package itmo.app.controller;

import itmo.app.controller.services.MovieWebSocketHandler;
import itmo.app.model.entity.*;
import itmo.app.model.repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
	
	@PostMapping("/upload/{email}")
	@Retryable(
			value = { CannotAcquireLockException.class },
			maxAttempts = 5,
			backoff = @Backoff(delay = 4000)
	)
	@Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
	public ResponseEntity<Object> createMoviesFromFile(@RequestBody @Valid List<Movie> movies, @PathVariable String email) {
		List<Movie> validatedMovies = new ArrayList<>();
		if (!checkImport(email)) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("{\"message\":\"you can't start two imports together\"}");
		}
		for (Movie movie : movies) {
			if (!checkUnique(movie, validatedMovies)) {
				redisTemplate.opsForValue().set("lock", "");
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body("{\"message\":\"Please check unique constraint: " +
						 "movies with same coordinates must be with different names," +
						  " workers must be different people\"}");
			}
			movie.setCreator(userRepository.findByEmail(email).get());
			validatedMovies.add(movie);
		}
		
		movieRepository.saveAll(validatedMovies);
		movieRepository.flush();
		
		ImportHistory importHistory = new ImportHistory();
		importHistory.setUsername(email);
		importHistory.setStatus(ImportStatus.OK);
		importHistory.setCountObjects(movies.size());
		ImportHistory ih = importHistoryRepository.save(importHistory);
		
		redisTemplate.opsForValue().set("lock", "");
		
		notifyClients();
		return new ResponseEntity<>(ih, HttpStatus.OK);
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
		String value = redisTemplate.opsForValue().get("lock");
		if (Objects.equals(value, "") || value == null || !value.equals(email)) {
			redisTemplate.opsForValue().set("lock", email);
			return true;
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
}
