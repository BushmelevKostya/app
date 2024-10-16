package itmo.app.controller;

import itmo.app.model.Movie;
import itmo.app.model.collections.MovieCollection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin(origins = "http://localhost:4200")
public class MovieController {
	private MovieCollection movieCollection = MovieCollection.getInstance();
	
	@PostMapping("/action")
	public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
		movieCollection.add(movie);
		movieCollection.printCollection();
		return new ResponseEntity<>(movie, HttpStatus.CREATED);
	}
	
	@GetMapping("/action")
	public ResponseEntity<ArrayList<Movie>> getAllMovies() {
		return new ResponseEntity<>(movieCollection.getCollection(), HttpStatus.OK);
	}
}
