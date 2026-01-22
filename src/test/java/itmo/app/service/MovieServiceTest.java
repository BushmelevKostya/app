package itmo.app.service;

import itmo.app.dto.response.MovieResponse;
import itmo.app.dto.response.PageResponse;
import itmo.app.exception.ResourceNotFoundException;
import itmo.app.model.entity.*;
import itmo.app.model.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {
	
	@Mock
	private MovieRepository movieRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private MovieService movieService;
	
	private User testUser;
	private Movie testMovie;
	
	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setId(1L);
		testUser.setEmail("test@example.com");
		
		testMovie = new Movie();
		testMovie.setId(1L);
		testMovie.setName("Test Movie");
		testMovie.setCreator(testUser);
		testMovie.setCreationDate(LocalDateTime.now());
	}
	
	@Test
	void getMoviesCount_ShouldReturnCount() {
		// Arrange
		when(movieRepository.count()).thenReturn(5L);
		
		// Act
		long count = movieService.getMoviesCount();
		
		// Assert
		assertEquals(5L, count);
		verify(movieRepository, times(1)).count();
	}
	
	@Test
	void getMovieById_ShouldReturnMovie_WhenMovieExists() {
		// Arrange
		when(movieRepository.findWithDetailsById(1L)).thenReturn(Optional.of(testMovie));
		
		// Act
		MovieResponse response = movieService.getMovieById(1L);
		
		// Assert
		assertNotNull(response);
		assertEquals("Test Movie", response.getName());
		verify(movieRepository, times(1)).findWithDetailsById(1L);
	}
	
	@Test
	void getMovieById_ShouldThrowException_WhenMovieNotFound() {
		// Arrange
		when(movieRepository.findWithDetailsById(999L)).thenReturn(Optional.empty());
		
		// Act & Assert
		assertThrows(ResourceNotFoundException.class, () -> movieService.getMovieById(999L));
		verify(movieRepository, times(1)).findWithDetailsById(999L);
	}
	
	@Test
	void getMovies_ShouldReturnPaginatedMovies() {
		// Arrange
		Movie movie2 = new Movie();
		movie2.setId(2L);
		movie2.setName("Movie 2");
		movie2.setCreator(testUser);
		movie2.setCreationDate(LocalDateTime.now());
		
		List<Movie> allMovies = Arrays.asList(testMovie, movie2);
		Pageable pageable = PageRequest.of(0, 10);
		Page<Movie> moviePage = new PageImpl<>(allMovies, pageable, allMovies.size());
		
		when(movieRepository.findAllWithDetails(any(Pageable.class))).thenReturn(moviePage);
		
		// Act
		PageResponse<MovieResponse> response = movieService.getMovies(0, 10);
		
		// Assert
		assertNotNull(response);
		assertEquals(2, response.getContent().size());
		assertEquals(2, response.getTotalElements());
		verify(movieRepository, times(1)).findAllWithDetails(any(Pageable.class));
	}
}
