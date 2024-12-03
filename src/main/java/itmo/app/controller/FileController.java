package itmo.app.controller;

import itmo.app.controller.services.MovieWebSocketHandler;
import itmo.app.model.entity.*;
import itmo.app.model.repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
	
	@PostMapping("/upload/{email}")
	@Transactional
	public ResponseEntity<Object> createMoviesFromFile(@RequestBody @Valid List<Movie> movies, @PathVariable String email) {
		List<Movie> validatedMovies = new ArrayList<>();
		for (Movie movie : movies) {
			if (!checkUnique(movie, validatedMovies)) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body("{\"message\":\"Film with the same coordinates and name already exists\"}");
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
		notifyClients();
		return new ResponseEntity<>(ih, HttpStatus.OK);
	}
	
	private boolean checkUnique(Movie movie, List<Movie> validatedMovies) {
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
	
	private void notifyClients() {
		try {
			movieWebSocketHandler.sendToAllSessions();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
