package itmo.app.model.repository;

import itmo.app.model.entity.Coordinates;
import itmo.app.model.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {}

