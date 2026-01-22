package itmo.app.model.repository;

import itmo.app.model.entity.Coordinates;
import itmo.app.model.entity.Movie;
import itmo.app.model.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>, MovieRepositoryCustom {
	
	// Оптимизированные методы с EntityGraph для предотвращения N+1 запросов
	
	@EntityGraph(attributePaths = {"creator", "coordinates", "director", "screenwriter", "operator"})
	@Query("SELECT m FROM Movie m")
	Page<Movie> findAllWithDetails(Pageable pageable);
	
	@EntityGraph(attributePaths = {"creator", "coordinates", "director", "screenwriter", "operator"})
	Optional<Movie> findWithDetailsById(Long id);
	
	@EntityGraph(attributePaths = {"creator", "coordinates"})
	List<Movie> findByCoordinates(Coordinates coordinates);
	
	@EntityGraph(attributePaths = {"creator", "director"})
	List<Movie> findByDirector(Person director);
	
	@EntityGraph(attributePaths = {"creator", "screenwriter"})
	List<Movie> findByScreenwriter(Person screenwriter);
	
	@EntityGraph(attributePaths = {"creator", "operator"})
	List<Movie> findByOperator(Person operator);
	
	@EntityGraph(attributePaths = {"creator", "coordinates"})
	List<Movie> findByCoordinatesXAndCoordinatesY(float x, int y);
}
