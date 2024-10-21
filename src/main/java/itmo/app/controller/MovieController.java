package itmo.app.controller;

import itmo.app.model.entity.Coordinates;
import itmo.app.model.entity.Movie;
import itmo.app.model.entity.Person;
import itmo.app.model.repository.CoordinatesRepository;
import itmo.app.model.repository.MovieRepository;
import itmo.app.model.repository.PersonRepository;
import org.hibernate.PersistentObjectException;
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
	
	@Autowired
	private PersonRepository personRepository;
	
	@Autowired
	private CoordinatesRepository coordinatesRepository;
	
	@PostMapping("/action")
	public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
		Optional<Coordinates> existingCoordinates = coordinatesRepository.findById(movie.getCoordinates().getId());
		
		existingCoordinates.ifPresent(movie::setCoordinates);
		
		Movie newMovie = movieRepository.save(movie);
		return new ResponseEntity<>(newMovie, HttpStatus.CREATED);
	}
	
	@GetMapping("/action")
	public ResponseEntity<List<Movie>> getAllMovies() {
		return new ResponseEntity<>(movieRepository.findAll(), HttpStatus.OK);
	}
	
	@DeleteMapping("/action/{id}")
	public ResponseEntity<List<Movie>> deleteMovie(@PathVariable long id) {
		Optional<Movie> movieToDelete = movieRepository.findById(id);
		
		if (movieToDelete.isPresent()) {
			Coordinates coordinates = movieToDelete.get().getCoordinates();
			movieRepository.deleteById(id);
			
			List<Movie> moviesWithSameCoordinates = movieRepository.findByCoordinates(coordinates);
			if (moviesWithSameCoordinates.isEmpty()) {
				coordinatesRepository.delete(coordinates);
			}
		}
		
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
	
	@GetMapping("/coordinates")
	public ResponseEntity<List<Coordinates>> getAllCoordinates() {
		// Здесь будет запрос к репозиторию координат (если он есть)
		List<Coordinates> coordinates = coordinatesRepository.findAll();
		return new ResponseEntity<>(coordinates, HttpStatus.OK);
	}
	
	@GetMapping("/persons")
	public ResponseEntity<List<Person>> getAllPersons() {
		List<Person> persons = personRepository.findAll();
		return new ResponseEntity<>(persons, HttpStatus.OK);
	}
}
