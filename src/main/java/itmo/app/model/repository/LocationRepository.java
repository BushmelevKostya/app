package itmo.app.model.repository;

import itmo.app.model.entity.Coordinates;
import itmo.app.model.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
	@Modifying
	@Transactional
	@Query("DELETE FROM Location l WHERE l.id NOT IN (SELECT p.location.id FROM Person p)")
	int deleteOrphanLocations();
}

