package itmo.app.controller;

import itmo.app.model.entity.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin(origins = "http://localhost:4200")
public class FileController {
	
	private final MovieController movieController;
	
	public FileController(MovieController movieController) {
		this.movieController = movieController;
	}
	
	@PostMapping("/upload/{email}")
	public ResponseEntity<List<Movie>> createMoviesFromFile(@RequestBody List<Movie> movies, @PathVariable String email) {
		List<Movie> createdMovies = new ArrayList<>();
		
		for (Movie movie : movies) {
			ResponseEntity<Movie> response = movieController.createMovie(movie, email);
			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				createdMovies.add(response.getBody());
			}
		}
		
		return ResponseEntity.ok(createdMovies);
	}
}
