package itmo.app.model.repository;

import itmo.app.model.entity.Coordinates;
import itmo.app.model.entity.Movie;
import itmo.app.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
	List<Movie> findByCoordinates(Coordinates coordinates);
	
	List<Movie> findByDirector(Person director);
	
	List<Movie> findByScreenwriter(Person screenwriter);
	
	List<Movie> findByOperator(Person operator);
}
