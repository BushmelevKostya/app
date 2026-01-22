package itmo.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Movies", description = "API для управления фильмами")
@SecurityRequirement(name = "bearerAuth")
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
	@Operation(
			summary = "Создать новый фильм",
			description = "Создает новый фильм с заданными параметрами и отправляет уведомление всем подключенным клиентам через WebSocket"
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "201",
					description = "Фильм успешно создан",
					content = @Content(schema = @Schema(implementation = MovieResponse.class))
			),
			@ApiResponse(
					responseCode = "400",
					description = "Невалидные данные фильма",
					content = @Content
			),
			@ApiResponse(
					responseCode = "401",
					description = "Требуется аутентификация",
					content = @Content
			)
	})
	public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieCreateRequest request) {
		MovieResponse response = movieService.createMovie(request);
		notifyClients();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@GetMapping
	@Operation(
			summary = "Получить список фильмов с пагинацией",
			description = "Возвращает страницу фильмов с указанным смещением и размером"
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Список фильмов успешно получен",
					content = @Content(schema = @Schema(implementation = PageResponse.class))
			),
			@ApiResponse(
					responseCode = "401",
					description = "Требуется аутентификация",
					content = @Content
			)
	})
	public ResponseEntity<PageResponse<MovieResponse>> getMovies(
			@Parameter(description = "Начальная позиция (смещение)", example = "0")
			@RequestParam(defaultValue = "0") int start,
			@Parameter(description = "Количество элементов на странице", example = "10")
			@RequestParam(defaultValue = "10") int size) {
		PageResponse<MovieResponse> response = movieService.getMovies(start, size);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/count")
	@Operation(
			summary = "Получить общее количество фильмов",
			description = "Возвращает общее количество фильмов в базе данных"
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Количество фильмов получено"
			),
			@ApiResponse(
					responseCode = "401",
					description = "Требуется аутентификация",
					content = @Content
			)
	})
	public ResponseEntity<Long> getMoviesCount() {
		return ResponseEntity.ok(movieService.getMoviesCount());
	}
	
	@GetMapping("/{id}")
	@Operation(
			summary = "Получить фильм по ID",
			description = "Возвращает детальную информацию о фильме по его уникальному идентификатору"
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Фильм найден",
					content = @Content(schema = @Schema(implementation = MovieResponse.class))
			),
			@ApiResponse(
					responseCode = "404",
					description = "Фильм не найден",
					content = @Content
			),
			@ApiResponse(
					responseCode = "401",
					description = "Требуется аутентификация",
					content = @Content
			)
	})
	public ResponseEntity<MovieResponse> getMovieById(
			@Parameter(description = "ID фильма", example = "1")
			@PathVariable Long id) {
		MovieResponse response = movieService.getMovieById(id);
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/{id}")
	@Operation(
			summary = "Обновить фильм",
			description = "Обновляет информацию о существующем фильме и отправляет уведомление всем клиентам"
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Фильм успешно обновлен",
					content = @Content(schema = @Schema(implementation = MovieResponse.class))
			),
			@ApiResponse(
					responseCode = "404",
					description = "Фильм не найден",
					content = @Content
			),
			@ApiResponse(
					responseCode = "400",
					description = "Невалидные данные",
					content = @Content
			),
			@ApiResponse(
					responseCode = "401",
					description = "Требуется аутентификация",
					content = @Content
			)
	})
	public ResponseEntity<MovieResponse> updateMovie(
			@Parameter(description = "ID фильма для обновления", example = "1")
			@PathVariable Long id,
			@Valid @RequestBody MovieUpdateRequest request) {
		MovieResponse response = movieService.updateMovie(id, request);
		notifyClients();
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/{id}")
	@Operation(
			summary = "Удалить фильм",
			description = "Удаляет фильм из базы данных и отправляет уведомление всем клиентам"
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Фильм успешно удален"
			),
			@ApiResponse(
					responseCode = "404",
					description = "Фильм не найден",
					content = @Content
			),
			@ApiResponse(
					responseCode = "401",
					description = "Требуется аутентификация",
					content = @Content
			)
	})
	public ResponseEntity<Void> deleteMovie(
			@Parameter(description = "ID фильма для удаления", example = "1")
			@PathVariable Long id) {
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
