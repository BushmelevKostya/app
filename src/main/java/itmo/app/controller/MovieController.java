package itmo.app.controller;

import itmo.app.model.Movie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class MovieController {
	@PostMapping("/create")
	public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
		System.out.println("Received movie: " + movie.getDirector().getLocation().getName());
		return new ResponseEntity<>(movie, HttpStatus.CREATED);
	}
}
