package itmo.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import itmo.app.controller.services.GlobalLogger;
import itmo.app.controller.services.MovieWebSocketHandler;
import itmo.app.model.entity.*;
import itmo.app.model.repository.*;
import jakarta.annotation.PostConstruct;
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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
	
	@Autowired ObjectMapper objectMapper;
	
	@Retryable(
			value = {CannotAcquireLockException.class},
			maxAttempts = 5,
			backoff = @Backoff(delay = 4000)
	)
	@PostMapping("/uploadTransaction/{email}")
	@Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
	public ResponseEntity<Object> uploadTransaction(
			@RequestParam("file") MultipartFile file,
			@RequestParam("movies") String moviesJson,
			@PathVariable String email) {
		ImportHistory importHistory = null;
		try {
			List<Movie> movies = objectMapper.readValue(moviesJson, new TypeReference<>() {
			});
			
			importHistory = saveMoviesWithHistory(movies, email);
			
			saveFileToMinio(file, importHistory.getId());
			
			notifyClients();
			return ResponseEntity.ok(importHistory);
		} catch (Exception e) {
			GlobalLogger.getLogger().info(e.getMessage());
			if (importHistory != null) {
				rollbackTransaction(email, importHistory.getId());
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transaction failed: " + e.getMessage());
		}
	}
	
	private void rollbackTransaction(String email, Long historyId) {
		try {
			if (historyId != null) {
				importHistoryRepository.deleteById(historyId);
				minioFilesRepository.deleteByHistoryId(historyId);
			}
			
			redisTemplate.opsForValue().increment(email, 1);
		} catch (Exception e) {
			GlobalLogger.getLogger().info("Rollback failed: {}", e.getMessage());
		}
	}
	
	
	private ImportHistory saveMoviesWithHistory(List<Movie> movies, String email) {
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
		ImportHistory importHistory = new ImportHistory();
		importHistory.setUsername(email);
		importHistory.setStatus(ImportStatus.OK);
		importHistory.setCountObjects(movies.size());
		return importHistoryRepository.save(importHistory);
	}
	
	private void saveFileToMinio(MultipartFile file, Long historyId) {
		try {
			String filename = historyId + ".json";
			PutObjectArgs putObjectArgs = PutObjectArgs.builder()
					.bucket("json-bucket")
					.object(filename)
					.stream(file.getInputStream(), file.getSize(), -1)
					.contentType(file.getContentType())
					.build();
			minioClient.putObject(putObjectArgs);

			MinioFiles minioFile = new MinioFiles();
			minioFile.setHistoryId(historyId);
			minioFile.setFileName(filename);
			minioFilesRepository.save(minioFile);
		} catch (Exception e) {
			throw new RuntimeException("Error saving file: " + e.getMessage());
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
		objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
}
