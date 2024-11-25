package itmo.app.controller;

import itmo.app.controller.services.MovieWebSocketHandler;
import itmo.app.model.entity.Coordinates;
import itmo.app.model.entity.Location;
import itmo.app.model.entity.Movie;
import itmo.app.model.entity.Person;
import itmo.app.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	
	@PostMapping("/upload/{email}")
	public ResponseEntity createMoviesFromFile(@RequestBody List<Movie> movies, @PathVariable String email) {
		for (Movie movie : movies) {
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
			
			movieRepository.save(movie);
		}
		
		notifyClients();
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	private void notifyClients() {
		try {
			movieWebSocketHandler.sendToAllSessions();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
