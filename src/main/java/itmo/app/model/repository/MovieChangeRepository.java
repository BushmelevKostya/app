package itmo.app.model.repository;

import itmo.app.model.entity.Location;
import itmo.app.model.entity.Movie;
import itmo.app.model.entity.MovieChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieChangeRepository extends JpaRepository<MovieChange, Long> {
	List<MovieChange> findByMovie(Movie movie);
}
