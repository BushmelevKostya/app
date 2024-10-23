package itmo.app.model.repository;

import itmo.app.model.entity.Location;
import itmo.app.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
	List<Person> findByLocation(Location location);
}
