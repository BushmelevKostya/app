package itmo.app.controller;

import itmo.app.model.entity.Coordinates;
import itmo.app.model.entity.Location;
import itmo.app.model.entity.Movie;
import itmo.app.model.entity.Person;
import itmo.app.model.repository.CoordinatesRepository;
import itmo.app.model.repository.LocationRepository;
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
	
	@Autowired
	private LocationRepository locationRepository;
	
	@PostMapping("/action")
	public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
		Optional<Coordinates> existingCoordinates = coordinatesRepository.findById(movie.getCoordinates().getId());
		existingCoordinates.ifPresent(movie::setCoordinates);
		
		Optional<Person> existingPersons = personRepository.findById(movie.getDirector().getId());
		existingPersons.ifPresent(movie::setDirector);
		
		existingPersons = personRepository.findById(movie.getScreenwriter().getId());
		existingPersons.ifPresent(movie::setScreenwriter);
		
		existingPersons = personRepository.findById(movie.getOperator().getId());
		existingPersons.ifPresent(movie::setOperator);
		
		Optional<Location> existingLocations = locationRepository.findById(movie.getDirector().getLocation().getId());
		if (existingLocations.isPresent()) {
			Person person = movie.getDirector();
			person.setLocation(existingLocations.get());
			movie.setDirector(person);
		}
		
		existingLocations = locationRepository.findById(movie.getScreenwriter().getLocation().getId());
		if (existingLocations.isPresent()) {
			Person person = movie.getScreenwriter();
			person.setLocation(existingLocations.get());
			movie.setScreenwriter(person);
		}
		
		existingLocations = locationRepository.findById(movie.getOperator().getLocation().getId());
		if (existingLocations.isPresent()) {
			Person person = movie.getOperator();
			person.setLocation(existingLocations.get());
			movie.setOperator(person);
		}
		
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
			Person director = movieToDelete.get().getDirector();
			Person screenwriter = movieToDelete.get().getScreenwriter();
			Person operator = movieToDelete.get().getOperator();
			movieRepository.deleteById(id);
			
			List<Movie> moviesWithSameCoordinates = movieRepository.findByCoordinates(coordinates);
			List<Movie> moviesWithSameDirector = movieRepository.findByDirector(director);
			List<Movie> moviesWithSameScreenwriter = movieRepository.findByScreenwriter(screenwriter);
			List<Movie> moviesWithSameOperator = movieRepository.findByOperator(operator);
			
			
			if (moviesWithSameCoordinates.isEmpty()) {
				coordinatesRepository.delete(coordinates);
			}
			
			Location location;
			if (moviesWithSameDirector.isEmpty()) {
				location = director.getLocation();
				personRepository.delete(director);
				
				List<Person> personsWithSameLocation = personRepository.findByLocation(location);
				if (personsWithSameLocation.isEmpty()) {
					locationRepository.delete(location);
				}
			}
			if (moviesWithSameScreenwriter.isEmpty()) {
				location = screenwriter.getLocation();
				personRepository.delete(screenwriter);
				
				List<Person> personsWithSameLocation = personRepository.findByLocation(location);
				if (personsWithSameLocation.isEmpty()) {
					locationRepository.delete(location);
				}
			}
			if (moviesWithSameOperator.isEmpty()) {
				location = operator.getLocation();
				personRepository.delete(operator);
				
				List<Person> personsWithSameLocation = personRepository.findByLocation(location);
				if (personsWithSameLocation.isEmpty()) {
					locationRepository.delete(location);
				}
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
		List<Coordinates> coordinates = coordinatesRepository.findAll();
		return new ResponseEntity<>(coordinates, HttpStatus.OK);
	}
	
	@GetMapping("/persons")
	public ResponseEntity<List<Person>> getAllPersons() {
		List<Person> persons = personRepository.findAll();
		return new ResponseEntity<>(persons, HttpStatus.OK);
	}
	
	@GetMapping("/locations")
	public ResponseEntity<List<Location>> getAllLocations() {
		List<Location> locations = locationRepository.findAll();
		return new ResponseEntity<>(locations, HttpStatus.OK);
	}
}
