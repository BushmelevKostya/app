package itmo.app.model.repository;

import itmo.app.model.entity.Location;
import itmo.app.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
	List<Person> findByLocation(Location location);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM Person p WHERE p.id NOT IN (SELECT m.director.id FROM Movie m) AND " +
			"p.id NOT IN (SELECT m.screenwriter.id FROM Movie m) AND " +
			"p.id NOT IN (SELECT m.operator.id FROM Movie m)")
	int deleteOrphanPersons();
}
