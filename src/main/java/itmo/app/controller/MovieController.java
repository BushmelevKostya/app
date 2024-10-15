package itmo.app.controller;

import itmo.app.model.Movie;
import itmo.app.model.collections.MovieCollection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin(origins = "http://localhost:4200")
public class MovieController {
	private MovieCollection movieCollection = MovieCollection.getInstance();
	@PostMapping("/create")
	public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
		movieCollection.add(movie);
		movieCollection.printCollection();
		return new ResponseEntity<>(movie, HttpStatus.CREATED);
	}
}
