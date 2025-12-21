package itmo.app.controller;

import itmo.app.controller.services.MovieWebSocketHandler;
import itmo.app.dto.request.MovieCreateRequest;
import itmo.app.dto.request.MovieUpdateRequest;
import itmo.app.dto.response.MovieResponse;
import itmo.app.dto.response.PageResponse;
import itmo.app.model.entity.Coordinates;
import itmo.app.model.entity.Location;
import itmo.app.model.entity.Movie;
import itmo.app.model.entity.Person;
import itmo.app.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/movies")
public class MovieController {
	
	@Autowired
	private MovieService movieService;
	
	@Autowired
	private MovieWebSocketHandler movieWebSocketHandler;
	
	private void notifyClients() {
		try {
			movieWebSocketHandler.sendToAllSessions();
		} catch (Exception e) {
			// Log error but don't fail the request
		}
	}
	
	@PostMapping
	public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieCreateRequest request) {
		MovieResponse response = movieService.createMovie(request);
		notifyClients();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@GetMapping
	public ResponseEntity<PageResponse<MovieResponse>> getMovies(
			@RequestParam(defaultValue = "0") int start,
			@RequestParam(defaultValue = "10") int size) {
		PageResponse<MovieResponse> response = movieService.getMovies(start, size);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/count")
	public ResponseEntity<Long> getMoviesCount() {
		return ResponseEntity.ok(movieService.getMoviesCount());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<MovieResponse> getMovieById(@PathVariable Long id) {
		MovieResponse response = movieService.getMovieById(id);
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<MovieResponse> updateMovie(
			@PathVariable Long id,
			@Valid @RequestBody MovieUpdateRequest request) {
		MovieResponse response = movieService.updateMovie(id, request);
		notifyClients();
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
		movieService.deleteMovie(id);
		notifyClients();
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteAllMovies() {
		movieService.deleteAllMovies();
		notifyClients();
		return ResponseEntity.ok().build();
	}
	
	// Special query endpoints
	
	@GetMapping("/coordinates")
	public ResponseEntity<List<Coordinates>> getAllCoordinates() {
		return ResponseEntity.ok(movieService.getAllCoordinates());
	}
	
	@GetMapping("/persons")
	public ResponseEntity<List<Person>> getAllPersons() {
		return ResponseEntity.ok(movieService.getAllPersons());
	}
	
	@GetMapping("/locations")
	public ResponseEntity<List<Location>> getAllLocations() {
		return ResponseEntity.ok(movieService.getAllLocations());
	}
	
	@GetMapping("/min-director")
	public ResponseEntity<Person> getMovieWithMinDirector() {
		return ResponseEntity.ok(movieService.getMovieWithMinDirector());
	}
	
	@GetMapping("/tagline-greater-than")
	public ResponseEntity<List<Movie>> getMoviesWithTaglineGreaterThan(@RequestParam String tagline) {
		return ResponseEntity.ok(movieService.getMoviesWithTaglineGreaterThan(tagline));
	}
	
	@GetMapping("/unique-usa-box-office")
	public ResponseEntity<java.util.Set<Double>> getUniqueUsaBoxOffice() {
		return ResponseEntity.ok(movieService.getUniqueUsaBoxOffice());
	}
	
	@GetMapping("/operators-no-oscars")
	public ResponseEntity<List<Person>> getOperatorsWithNoOscars() {
		return ResponseEntity.ok(movieService.getOperatorsWithNoOscars());
	}
	
	@PostMapping("/add-oscar-to-r-rated")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> addOscarToRRatedMovies() {
		movieService.addOscarToRRatedMovies();
		notifyClients();
		return ResponseEntity.ok().build();
	}
}
