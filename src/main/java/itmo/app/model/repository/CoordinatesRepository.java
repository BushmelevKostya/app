package itmo.app.model.repository;

import itmo.app.model.entity.Coordinates;
import itmo.app.model.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoordinatesRepository extends JpaRepository<Coordinates, Long> {}
