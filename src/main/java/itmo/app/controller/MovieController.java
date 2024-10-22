package itmo.app.controller;

import itmo.app.controller.enums.Command;
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
		
		deleteSingletoneEntities(movieToDelete, new Movie(), id, Command.DELETE);
		
		return new ResponseEntity<>(movieRepository.findAll(), HttpStatus.OK);
	}
	
	
	@PutMapping("/action/{id}")
	public ResponseEntity<List<Movie>> updateMovie(@PathVariable long id, @RequestBody Movie movie) {
		Optional<Movie> existingMovieOpt = movieRepository.findById(id);
		
		if (existingMovieOpt.isPresent()) {
			deleteSingletoneEntities(existingMovieOpt, movie, id, Command.UPDATE);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
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
	
	public void deleteSingletoneEntities(Optional<Movie> optionalMovie, Movie movie, long id, Command command) {
		if (optionalMovie.isPresent()) {
			Coordinates coordinates = optionalMovie.get().getCoordinates();
			Person director = optionalMovie.get().getDirector();
			Person screenwriter = optionalMovie.get().getScreenwriter();
			Person operator = optionalMovie.get().getOperator();
			
			if (command.equals(Command.UPDATE)){
				setFields(optionalMovie, movie);
			} else if (command.equals(Command.DELETE)){
				movieRepository.deleteById(id);
			}
			
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
	}
	
	public void setFields(Optional<Movie> optionalMovie, Movie movie) {
		Movie existingMovie = optionalMovie.get();
		
		existingMovie.setName(movie.getName());
		existingMovie.setCoordinates(movie.getCoordinates());
		existingMovie.setOscarsCount(movie.getOscarsCount());
		existingMovie.setBudget(movie.getBudget());
		existingMovie.setTotalBoxOffice(movie.getTotalBoxOffice());
		existingMovie.setMpaaRating(movie.getMpaaRating());
		existingMovie.setLength(movie.getLength());
		existingMovie.setGoldenPalmCount(movie.getGoldenPalmCount());
		existingMovie.setUsaBoxOffice(movie.getUsaBoxOffice());
		existingMovie.setTagline(movie.getTagline());
		existingMovie.setGenre(movie.getGenre());
		existingMovie.setDirector(movie.getDirector());
		existingMovie.setScreenwriter(movie.getScreenwriter());
		existingMovie.setOperator(movie.getOperator());
		
		movieRepository.save(existingMovie);
	}
}
