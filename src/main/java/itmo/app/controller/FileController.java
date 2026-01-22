package itmo.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.minio.*;
import io.minio.messages.Item;
import itmo.app.controller.services.GlobalLogger;
import itmo.app.controller.services.MovieWebSocketHandler;
import itmo.app.model.entity.*;
import itmo.app.model.repository.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin(origins = "http://localhost:4200")
public class FileController {
	@Autowired
	private MovieRepository movieRepository;
	
	@Autowired
	private PersonRepository personRepository;
	
	@Autowired
	private CoordinatesRepository coordinatesRepository;
	
	@Autowired
	private LocationRepository locationRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MovieWebSocketHandler movieWebSocketHandler;
	
	@Autowired
	private ImportHistoryRepository importHistoryRepository;
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Autowired
	private MinioClient minioClient;
	
	@Autowired
	private MinioFilesRepository minioFilesRepository;
	
	@Autowired
	ObjectMapper objectMapper;
	
	Logger logger = GlobalLogger.getLogger();
	
//	@Retryable(
//			value = {CannotAcquireLockException.class},
//			maxAttempts = 5,
//			backoff = @Backoff(delay = 4000)
//	)
	@PostMapping("/uploadTransaction/{email}")
//	@Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
	public ResponseEntity<Object> uploadTransaction(
			@RequestParam("file") MultipartFile file,
			@RequestParam("movies") String moviesJson,
			@PathVariable String email) {
		String currentTime = String.valueOf(LocalDateTime.now());
		logger.info("Начало транзакции");
		List<Movie> movies = new ArrayList<>();
		String filename = currentTime + "_" + "temp" + ".json";
		
		try {
			movies = objectMapper.readValue(moviesJson, new TypeReference<>() {
			});
			
			saveFileToMinio(file, filename);
			
			saveMoviesToDatabase(movies, email);
			
		} catch (Exception e) {
			GlobalLogger.getLogger().info(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transaction failed: " + e.getMessage());
		} finally {
			ImportHistory importHistory = finalizeImportHistoryAndMinioFile(movies, email, filename);
			notifyClients();
			logger.info("Конец транзакции");
			return ResponseEntity.ok(importHistory);
		}
	}

	
	private void saveMoviesToDatabase(List<Movie> movies, String email) {
		try {
			List<Movie> validatedMovies = new ArrayList<>();
			
			for (Movie movie : movies) {
				if (checkUnique(movie, validatedMovies)) {
					movie.setCreator(userRepository.findByEmail(email).get());
					validatedMovies.add(movie);
				} else {
					throw new RuntimeException("Movie validation failed: " + movie.getName());
				}
			}
			
			movieRepository.saveAll(validatedMovies);
			logger.info("фильмы сохранены в бд");
		} catch (Exception e) {
			throw new RuntimeException("Movie validation failed: " + e.getMessage());
		}
	}
	
	private void saveFileToMinio(MultipartFile file, String filename) {
		try {
			PutObjectArgs putObjectArgs = PutObjectArgs.builder()
					.bucket("json-bucket")
					.object(filename)
					.stream(file.getInputStream(), file.getSize(), -1)
					.contentType(file.getContentType())
					.build();
			minioClient.putObject(putObjectArgs);
			logger.info("файл сохранен в minio");
		} catch (Exception e) {
			throw new RuntimeException("Error saving file: " + e.getMessage());
		}
	}
	
	private ImportHistory finalizeImportHistoryAndMinioFile(List<Movie> movies, String email, String filename) {
		ImportHistory importHistory = new ImportHistory();
		importHistory.setUsername(email);
		importHistory.setStatus(ImportStatus.OK);
		importHistory.setCountObjects(movies.size());
		importHistory = importHistoryRepository.save(importHistory);
		
		MinioFiles minioFile = new MinioFiles();
		minioFile.setImportHistory(importHistory);
		minioFile.setFileName(filename);
		minioFilesRepository.save(minioFile);
		logger.info("importHistory и minioFile сохранены в бд");
		return importHistory;
	}
	
	@Scheduled(fixedRate = 60000)
	private void synchronizeMinioAndDatabase() {
		try {
			List<MinioFiles> databaseFiles = minioFilesRepository.findAll();
			List<String> databaseFileNames = new ArrayList<>();
			for (MinioFiles file : databaseFiles) {
				databaseFileNames.add(file.getFileName());
			}
			
			Iterable<Result<Item>> minioObjects = minioClient.listObjects(
					ListObjectsArgs.builder().bucket("json-bucket").build()
			);
			
			List<String> minioFileNames = new ArrayList<>();
			for (Result<Item> result : minioObjects) {
				Item item = result.get();
				minioFileNames.add(item.objectName());
			}
			
			List<String> filesToDeleteFromDb = new ArrayList<>(databaseFileNames);
			filesToDeleteFromDb.removeAll(minioFileNames);
			
			List<String> filesToDeleteFromMinio = new ArrayList<>(minioFileNames);
			filesToDeleteFromMinio.removeAll(databaseFileNames);
			
			for (String fileName : filesToDeleteFromDb) {
				MinioFiles file = minioFilesRepository.findByFileName(fileName);
				if (file != null) {
					minioFilesRepository.delete(file);
					logger.info("Файл {} удален из базы данных", fileName);
				}
			}
			for (String fileName : filesToDeleteFromMinio) {
				minioClient.removeObject(
						RemoveObjectArgs.builder().bucket("json-bucket").object(fileName).build()
				);
				logger.info("Файл {} удален из Minio", fileName);
			}
			
			logger.info("Синхронизация Minio и базы данных завершена");
		} catch (Exception e) {
			logger.error("Ошибка во время синхронизации Minio и базы данных: {}", e.getMessage());
		}
	}
	
	private boolean checkUnique(Movie movie, List<Movie> validatedMovies) {
		return checkName(movie, validatedMovies) & checkPeoples(movie);
	}
	
	private boolean checkName(Movie movie, List<Movie> validatedMovies) {
		List<Movie> listMovieWithSameCoordinates = movieRepository.findByCoordinatesXAndCoordinatesY(movie.getCoordinates().getX(), movie.getCoordinates().getY());
		for (Movie m : listMovieWithSameCoordinates) {
			if (movie.getName().equals(m.getName())) {
				return false;
			}
		}
		for (Movie m : validatedMovies) {
			if (movie.getName().equals(m.getName())) {
				return false;
			}
		}
		return true;
	}
	
	private boolean checkPeoples(Movie movie) {
		Long director = movie.getDirector().getId();
		Long screenwriter = movie.getScreenwriter().getId();
		Long operator = movie.getOperator().getId();
		return
				(!director.equals(screenwriter) && !director.equals(operator) && !operator.equals(screenwriter)) ||
						(director + screenwriter == 0) ||
						(director + operator == 0) ||
						(screenwriter + operator == 0);
	}
	
	private boolean checkImport(String email) {
		String value = redisTemplate.opsForValue().get(email);
		if (value == null) {
			redisTemplate.opsForValue().set(email, "1");
			return true;
		} else {
			int countImports = Integer.parseInt(value);
			if (countImports < 10) {
				redisTemplate.opsForValue().set(email, String.valueOf(countImports + 1));
				return true;
			}
		}
		return false;
	}
	
	private void notifyClients() {
		try {
			movieWebSocketHandler.sendToAllSessions();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@GetMapping("/download/{fileId}")
	public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
		try {
			String bucketName = "json-bucket";
			String fileName = fileId + ".json";
			
			MinioClient minioClient = MinioClient.builder()
					.endpoint("http://localhost:9000")
					.credentials("minioadmin", "minioadmin")
					.build();
			
			InputStream stream = minioClient.getObject(GetObjectArgs.builder()
					.bucket(bucketName)
					.object(fileName)
					.build());
			
			ByteArrayResource resource = new ByteArrayResource(stream.readAllBytes());
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
					.contentType(MediaType.APPLICATION_JSON)
					.body(resource);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostConstruct
	public void init() {
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
}
