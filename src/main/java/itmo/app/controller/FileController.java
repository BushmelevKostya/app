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
	
	@Autowired
	private ImportHistoryRepository importHistoryRepository;
	
	@PostMapping("/upload/{email}")
	@Transactional
	public ResponseEntity<ImportHistory> createMoviesFromFile(@RequestBody @Valid List<Movie> movies, @PathVariable String email) {
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
		
		ImportHistory importHistory = new ImportHistory();
		importHistory.setUsername(email);
		importHistory.setStatus(ImportStatus.OK);
		importHistory.setCountObjects(movies.size());
		ImportHistory ih = importHistoryRepository.save(importHistory);
		
		notifyClients();
		return new ResponseEntity<>(ih, HttpStatus.OK);
	}
	
	private void notifyClients() {
		try {
			movieWebSocketHandler.sendToAllSessions();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
