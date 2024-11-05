package itmo.app.model.repository;

import itmo.app.model.entity.Coordinates;
import itmo.app.model.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CoordinatesRepository extends JpaRepository<Coordinates, Long> {
	@Modifying
	@Transactional
	@Query("DELETE FROM Coordinates c WHERE c.id NOT IN (SELECT m.coordinates.id FROM Movie m)")
	int deleteOrphanCoordinates();
}
