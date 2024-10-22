package itmo.app.model.repository;

import itmo.app.model.entity.Location;
import itmo.app.model.entity.Movie;
import itmo.app.model.entity.MovieChange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieChangeRepository extends JpaRepository<MovieChange, Long> {
	List<MovieChange> findByMovie(Movie movie);
}
