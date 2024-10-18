package itmo.app.controller;

import itmo.app.model.Movie;
import itmo.app.model.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin(origins = "http://localhost:4200")
public class MovieController {
	@Autowired
	private MovieRepository movieRepository;
	
	@PostMapping("/action")
	public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
		Movie newMovie = movieRepository.save(movie);
		return new ResponseEntity<>(newMovie, HttpStatus.CREATED);
	}
	
	@GetMapping("/action")
	public ResponseEntity<List<Movie>> getAllMovies() {
		return new ResponseEntity<>(movieRepository.findAll(), HttpStatus.OK);
	}
	
	@DeleteMapping("/action/{id}")
	public ResponseEntity<List<Movie>> deleteMovie(@PathVariable long id) {
		movieRepository.deleteById(id);
		return new ResponseEntity<>(movieRepository.findAll(), HttpStatus.OK);
	}
	
	@PutMapping("/action")
	public ResponseEntity<List<Movie>> updateMovie(@RequestBody Movie movie) {
		movieRepository.save(movie);
		return new ResponseEntity<>(movieRepository.findAll(), HttpStatus.OK);
	}
	
	@GetMapping("/action/{id}")
	public ResponseEntity<Movie> getMovieById(@PathVariable long id) {
		Optional<Movie> movie = movieRepository.findById(id);
		return movie.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElse(null);
	}
}
