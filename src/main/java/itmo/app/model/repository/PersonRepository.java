package itmo.app.model.repository;

import itmo.app.model.entity.Location;
import itmo.app.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
	List<Person> findByLocation(Location location);
}
