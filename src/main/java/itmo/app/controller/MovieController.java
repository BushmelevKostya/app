package itmo.app.controller;

import itmo.app.controller.services.MovieWebSocketHandler;
import itmo.app.model.entity.*;
import itmo.app.model.repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MovieChangeRepository movieChangeRepository;
	
	@Autowired
	private MovieRepositoryImpl movieRepositoryImpl;
	
	@Autowired
	private MovieWebSocketHandler movieWebSocketHandler;
	
		@PostMapping("/action/{email}")
		public ResponseEntity<Movie> createMovie(@RequestBody @Valid Movie movie, @PathVariable String email) {
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
		
		movie.setCreator(userRepository.findByEmail(email).get());
		
		Movie newMovie = movieRepository.save(movie);
		notifyClients(movieRepository.findAll());
		return new ResponseEntity<>(newMovie, HttpStatus.CREATED);
	}
	
	@GetMapping("/action/{start}/{end}")
	public ResponseEntity<List<Movie>> getMovies(@PathVariable int start, @PathVariable int end) {
		return new ResponseEntity<>(movieRepository.findAll().subList(start, end), HttpStatus.OK);
	}
	
	@GetMapping("/action/count")
	public ResponseEntity<Long> getCountMovies() {
		return new ResponseEntity<>(movieRepository.count(), HttpStatus.OK);
	}
	
	@DeleteMapping("/action/{linkId}/{id}/{email}")
	public ResponseEntity<List<Movie>> deleteMovie(@PathVariable long linkId, @PathVariable long id, @PathVariable String email) {
		Optional<Movie> movieToDelete = movieRepository.findById(id);
		if (movieToDelete.isPresent()) {
			Movie movie = movieToDelete.get();
			Optional<User> user = userRepository.findByEmail(email);
			if (user.isPresent()) {
				User curUser = user.get();
				if (!movie.getCreator().getEmail().equals(email) && !curUser.isApprovedAdmin()) {
					return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
				}
			}
			Coordinates coordinates = movie.getCoordinates();
			Person director = movie.getDirector();
			Person screenwriter = movie.getScreenwriter();
			Person operator = movie.getOperator();
			
			List<MovieChange> moviesWithSameMovieChange = movieChangeRepository.findByMovie(movie);
			if (!moviesWithSameMovieChange.isEmpty()) {
				movieChangeRepository.deleteAll(moviesWithSameMovieChange);
			}
			if (linkId != 0) {
				if (user.isPresent()) {
					User curUser = user.get();
					Optional<Movie> optionalMovie = movieRepository.findById(linkId);
					if (optionalMovie.isPresent()) {
						Movie oldMovie = optionalMovie.get();
						oldMovie.setCoordinates(coordinates);
						oldMovie.setDirector(director);
						oldMovie.setScreenwriter(screenwriter);
						oldMovie.setOperator(operator);
						updateMovie(linkId, curUser.getEmail(), oldMovie);
					}
					else {
						return new ResponseEntity<>(HttpStatus.NOT_FOUND);
					}
				}
			}
			movieRepository.deleteById(id);
			return new ResponseEntity<>(movieRepository.findAll(), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@DeleteMapping("/action/{email}")
	public ResponseEntity<List<Movie>> deleteAllMovie(@PathVariable String email) {
		Optional<User> user = userRepository.findByEmail(email);
		if (user.isPresent()) {
			User curUser = user.get();
			if (!curUser.isApprovedAdmin()) {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		}
		movieRepository.deleteAll();
		coordinatesRepository.deleteAll();
		locationRepository.deleteAll();
		personRepository.deleteAll();
		movieChangeRepository.deleteAll();
		
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
	}
	
	@PutMapping("/action/{id}/{email}")
	public ResponseEntity<List<Movie>> updateMovie(@PathVariable long id, @PathVariable String email,
	                                               @RequestBody @Valid Movie movie) {
		Optional<Movie> existingMovieOpt = movieRepository.findById(id);
		
		if (existingMovieOpt.isPresent()) {
			Movie oldMovie = existingMovieOpt.get();
			Optional<User> optUser = userRepository.findByEmail(email);
			if (optUser.isPresent()) {
				User curUser = optUser.get();
				if (!existingMovieOpt.get().getCreator().getEmail().equals(email) && !curUser.isApprovedAdmin()) {
					return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
				}
			}
			
			setFields(oldMovie, movie);
			
			
			Optional<User> existingUserOpt = userRepository.findByEmail(email);
			if (existingUserOpt.isPresent()) {
				User user = existingUserOpt.get();
				MovieChange change = new MovieChange();
				change.setMovie(existingMovieOpt.get());
				change.setUser(user);
				change.setChangeTime(LocalDateTime.now());
				
				movieChangeRepository.save(change);
			}
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
	
	@GetMapping("/min-director")
	public ResponseEntity<Person> getMovieWithMinDirector() {
		Person person = movieRepositoryImpl.findMovieWithMinDirector();
		return new ResponseEntity<>(person, HttpStatus.OK);
	}
	
	@GetMapping("/tagline-greater-than")
	public ResponseEntity<List<Movie>> getMoviesWithTaglineGreaterThan(@RequestParam String tagline) {
		List<Movie> movies = movieRepositoryImpl.findMoviesWithTaglineGreaterThan(tagline);
		return new ResponseEntity<>(movies, HttpStatus.OK);
	}
	
	@GetMapping("/unique-usa-box-office")
	public ResponseEntity<Set<Double>> getUniqueUsaBoxOffice() {
		Set<Double> uniqueUsaBoxOffices = movieRepositoryImpl.findUniqueUsaBoxOfficeValues();
		return new ResponseEntity<>(uniqueUsaBoxOffices, HttpStatus.OK);
	}
	
	@GetMapping("/operators-no-oscars")
	public ResponseEntity<List<Person>> getOperatorsWithNoOscars() {
		List<Person> operators = movieRepositoryImpl.findOperatorsWithNoOscars();
		return new ResponseEntity<>(operators, HttpStatus.OK);
	}
	
	@PostMapping("/add-oscar-to-r-rated")
	public ResponseEntity<Void> addOscarToRRatedMovies() {
		movieRepositoryImpl.addOscarToRRatedMovies();
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	public void setFields(Movie oldMovie, Movie newMovie) {
		oldMovie.setName(newMovie.getName());
		oldMovie.setCreationDate(newMovie.getCreationDate() );
		oldMovie.setCoordinates(newMovie.getCoordinates());
		oldMovie.setOscarsCount(newMovie.getOscarsCount());
		oldMovie.setBudget(newMovie.getBudget());
		oldMovie.setTotalBoxOffice(newMovie.getTotalBoxOffice());
		oldMovie.setMpaaRating(newMovie.getMpaaRating());
		oldMovie.setLength(newMovie.getLength());
		oldMovie.setGoldenPalmCount(newMovie.getGoldenPalmCount());
		oldMovie.setUsaBoxOffice(newMovie.getUsaBoxOffice());
		oldMovie.setTagline(newMovie.getTagline());
		oldMovie.setGenre(newMovie.getGenre());
		oldMovie.setDirector(newMovie.getDirector());
		oldMovie.setScreenwriter(newMovie.getScreenwriter());
		oldMovie.setOperator(newMovie.getOperator());
		
		movieRepository.save(oldMovie);
	}
	
	private void notifyClients(List<Movie> movies) {
		try {
			movieWebSocketHandler.sendToAllSessions(movies);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
