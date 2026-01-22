package itmo.app.service;

import itmo.app.dto.request.MovieCreateRequest;
import itmo.app.dto.request.MovieUpdateRequest;
import itmo.app.dto.response.MovieResponse;
import itmo.app.dto.response.PageResponse;
import itmo.app.exception.BusinessException;
import itmo.app.exception.ResourceNotFoundException;
import itmo.app.model.entity.*;
import itmo.app.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieService {
	
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
	
	private User getCurrentUser() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
	}
	
	@Retryable(
			retryFor = { CannotAcquireLockException.class },
			maxAttempts = 5,
			backoff = @Backoff(delay = 2000)
	)
	@Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
	public MovieResponse createMovie(MovieCreateRequest request) {
		Movie movie = new Movie();
		mapRequestToMovie(request, movie);
		
		if (!checkUnique(movie)) {
			throw new BusinessException("Please check unique constraint: " +
					"movies with same coordinates must be with different names, " +
					"workers must be different people");
		}
		
		// Handle existing entities
		handleExistingEntities(movie);
		
		// Set creator
		movie.setCreator(getCurrentUser());
		movie.setCreationDate(LocalDateTime.now());
		
		Movie savedMovie = movieRepository.save(movie);
		return new MovieResponse(savedMovie);
	}
	
	@Transactional(readOnly = true)
	public PageResponse<MovieResponse> getMovies(int start, int size) {
		// Оптимизированная пагинация с EntityGraph для предотвращения N+1
		int page = start / size;
		Pageable pageable = PageRequest.of(page, size);
		
		Page<Movie> moviePage = movieRepository.findAllWithDetails(pageable);
		
		List<MovieResponse> content = moviePage.getContent().stream()
				.map(MovieResponse::new)
				.collect(Collectors.toList());
		
		return new PageResponse<>(content, page, size, moviePage.getTotalElements());
	}
	
	@Transactional(readOnly = true)
	public long getMoviesCount() {
		return movieRepository.count();
	}
	
	@Transactional(readOnly = true)
	public MovieResponse getMovieById(Long id) {
		// Используем оптимизированный метод с загрузкой связанных сущностей
		Movie movie = movieRepository.findWithDetailsById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Movie", "id", id));
		return new MovieResponse(movie);
	}
	
	@Retryable(
			retryFor = { CannotAcquireLockException.class },
			maxAttempts = 5,
			backoff = @Backoff(delay = 2000)
	)
	@Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
	public MovieResponse updateMovie(Long id, MovieUpdateRequest request) {
		Movie movie = movieRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Movie", "id", id));
		
		User currentUser = getCurrentUser();
		
		// Check permissions
		if (!movie.getCreator().getEmail().equals(currentUser.getEmail()) 
				&& !currentUser.isApprovedAdmin()) {
			throw new AccessDeniedException("You don't have permission to update this movie");
		}
		
		// Update fields if provided
		if (request.getName() != null) {
			movie.setName(request.getName());
		}
		if (request.getCoordinates() != null) {
			movie.setCoordinates(request.getCoordinates());
		}
		if (request.getOscarsCount() != null) {
			movie.setOscarsCount(request.getOscarsCount());
		}
		if (request.getBudget() != null) {
			movie.setBudget(request.getBudget());
		}
		if (request.getTotalBoxOffice() != null) {
			movie.setTotalBoxOffice(request.getTotalBoxOffice());
		}
		if (request.getMpaaRating() != null) {
			movie.setMpaaRating(request.getMpaaRating());
		}
		if (request.getDirector() != null) {
			movie.setDirector(request.getDirector());
		}
		if (request.getScreenwriter() != null) {
			movie.setScreenwriter(request.getScreenwriter());
		}
		if (request.getOperator() != null) {
			movie.setOperator(request.getOperator());
		}
		if (request.getLength() != null) {
			movie.setLength(request.getLength());
		}
		if (request.getGoldenPalmCount() != null) {
			movie.setGoldenPalmCount(request.getGoldenPalmCount());
		}
		if (request.getUsaBoxOffice() != null) {
			movie.setUsaBoxOffice(request.getUsaBoxOffice());
		}
		if (request.getTagline() != null) {
			movie.setTagline(request.getTagline());
		}
		if (request.getGenre() != null) {
			movie.setGenre(request.getGenre());
		}
		
		if (!checkUnique(movie)) {
			throw new BusinessException("Please check unique constraint");
		}
		
		handleExistingEntities(movie);
		
		Movie updatedMovie = movieRepository.save(movie);
		return new MovieResponse(updatedMovie);
	}
	
	@Retryable(
			retryFor = { CannotAcquireLockException.class },
			maxAttempts = 5,
			backoff = @Backoff(delay = 2000)
	)
	@Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
	public void deleteMovie(Long id) {
		Movie movie = movieRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Movie", "id", id));
		
		User currentUser = getCurrentUser();
		
		// Check permissions
		if (!movie.getCreator().getEmail().equals(currentUser.getEmail()) 
				&& !currentUser.isApprovedAdmin()) {
			throw new AccessDeniedException("You don't have permission to delete this movie");
		}
		
		// Delete related movie changes
		List<MovieChange> moviesWithSameMovieChange = movieChangeRepository.findByMovie(movie);
		if (!moviesWithSameMovieChange.isEmpty()) {
			movieChangeRepository.deleteAll(moviesWithSameMovieChange);
		}
		
		movieRepository.deleteById(id);
	}
	
	@Transactional
	public void deleteAllMovies() {
		User currentUser = getCurrentUser();
		
		if (!currentUser.isApprovedAdmin()) {
			throw new AccessDeniedException("Only admin can delete all movies");
		}
		
		// Set all foreign keys to null before deleting
		List<Movie> allMovies = movieRepository.findAll();
		allMovies.forEach(movie -> {
			movie.setCoordinates(null);
			movie.setDirector(null);
			movie.setScreenwriter(null);
			movie.setOperator(null);
		});
		movieRepository.saveAll(allMovies);
		
		List<Person> allPersons = personRepository.findAll();
		allPersons.forEach(person -> person.setLocation(null));
		personRepository.saveAll(allPersons);
		
		// Delete all entities
		movieRepository.deleteAll();
		coordinatesRepository.deleteAll();
		personRepository.deleteAll();
		locationRepository.deleteAll();
		movieChangeRepository.deleteAll();
	}
	
	// Helper methods
	
	private void mapRequestToMovie(MovieCreateRequest request, Movie movie) {
		movie.setName(request.getName());
		movie.setCoordinates(request.getCoordinates());
		movie.setOscarsCount(request.getOscarsCount());
		movie.setBudget(request.getBudget());
		movie.setTotalBoxOffice(request.getTotalBoxOffice());
		movie.setMpaaRating(request.getMpaaRating());
		movie.setDirector(request.getDirector());
		movie.setScreenwriter(request.getScreenwriter());
		movie.setOperator(request.getOperator());
		movie.setLength(request.getLength());
		movie.setGoldenPalmCount(request.getGoldenPalmCount());
		movie.setUsaBoxOffice(request.getUsaBoxOffice());
		movie.setTagline(request.getTagline());
		movie.setGenre(request.getGenre());
	}
	
	private void handleExistingEntities(Movie movie) {
		// Handle coordinates
		if (movie.getCoordinates() != null && movie.getCoordinates().getId() != null && movie.getCoordinates().getId() != 0) {
			Optional<Coordinates> existingCoordinates = coordinatesRepository.findById(movie.getCoordinates().getId());
			existingCoordinates.ifPresent(movie::setCoordinates);
		}
		
		// Handle persons
		handleExistingPerson(movie.getDirector());
		handleExistingPerson(movie.getScreenwriter());
		handleExistingPerson(movie.getOperator());
		
		// Handle locations for persons
		handleExistingLocation(movie.getDirector());
		handleExistingLocation(movie.getScreenwriter());
		handleExistingLocation(movie.getOperator());
	}
	
	private void handleExistingPerson(Person person) {
		if (person != null && person.getId() != null && person.getId() != 0) {
			Optional<Person> existingPerson = personRepository.findById(person.getId());
			if (existingPerson.isPresent()) {
				// Update reference but keep the person object structure
			}
		}
	}
	
	private void handleExistingLocation(Person person) {
		if (person != null && person.getLocation() != null && person.getLocation().getId() != null && person.getLocation().getId() != 0) {
			Optional<Location> existingLocation = locationRepository.findById(person.getLocation().getId());
			if (existingLocation.isPresent()) {
				person.setLocation(existingLocation.get());
			}
		}
	}
	
	private boolean checkUnique(Movie movie) {
		return checkName(movie) && checkPeoples(movie);
	}
	
	private boolean checkName(Movie movie) {
		List<Movie> listMovieWithSameCoordinates = movieRepository.findByCoordinatesXAndCoordinatesY(
				movie.getCoordinates().getX(), 
				movie.getCoordinates().getY()
		);
		
		for (Movie m : listMovieWithSameCoordinates) {
			if (movie.getId() != m.getId() && movie.getName().equals(m.getName())) {
				return false;
			}
		}
		return true;
	}
	
	private boolean checkPeoples(Movie movie) {
		Long director = movie.getDirector() != null ? movie.getDirector().getId() : null;
		Long screenwriter = movie.getScreenwriter() != null ? movie.getScreenwriter().getId() : null;
		Long operator = movie.getOperator() != null ? movie.getOperator().getId() : null;
		
		// If any person is null, they can't conflict
		if (director == null || screenwriter == null || operator == null) {
			return true;
		}
		
		return (!director.equals(screenwriter) && !director.equals(operator) && !operator.equals(screenwriter)) ||
				(director + screenwriter == 0) ||
				(director + operator == 0) ||
				(screenwriter + operator == 0);
	}
	
	// Special query methods
	
	@Transactional(readOnly = true)
	public List<Coordinates> getAllCoordinates() {
		return coordinatesRepository.findAll();
	}
	
	@Transactional(readOnly = true)
	public List<Person> getAllPersons() {
		return personRepository.findAll();
	}
	
	@Transactional(readOnly = true)
	public List<Location> getAllLocations() {
		return locationRepository.findAll();
	}
	
	@Transactional(readOnly = true)
	public Person getMovieWithMinDirector() {
		return movieRepositoryImpl.findMovieWithMinDirector();
	}
	
	@Transactional(readOnly = true)
	public List<Movie> getMoviesWithTaglineGreaterThan(String tagline) {
		return movieRepositoryImpl.findMoviesWithTaglineGreaterThan(tagline);
	}
	
	@Transactional(readOnly = true)
	public java.util.Set<Double> getUniqueUsaBoxOffice() {
		return movieRepositoryImpl.findUniqueUsaBoxOfficeValues();
	}
	
	@Transactional(readOnly = true)
	public List<Person> getOperatorsWithNoOscars() {
		return movieRepositoryImpl.findOperatorsWithNoOscars();
	}
	
	@Transactional
	public void addOscarToRRatedMovies() {
		movieRepositoryImpl.addOscarToRRatedMovies();
	}
}
