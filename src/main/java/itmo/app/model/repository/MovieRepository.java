package itmo.app.model.repository;

import itmo.app.model.entity.Coordinates;
import itmo.app.model.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
	List<Movie> findByCoordinates(Coordinates coordinates);
}
