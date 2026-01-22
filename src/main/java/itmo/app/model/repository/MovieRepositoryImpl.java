package itmo.app.model.repository;

import itmo.app.model.entity.Movie;
import itmo.app.model.entity.MpaaRating;
import itmo.app.model.entity.Person;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MovieRepositoryImpl implements MovieRepositoryCustom{
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	@Lazy
	MovieRepository movieRepository;
	
	@Override
	public Person findMovieWithMinDirector() {
		// Оптимизированный запрос: сортировка на уровне БД + LIMIT 1
		String jpql = "SELECT m FROM Movie m " +
		              "JOIN FETCH m.director d " +
		              "ORDER BY d.name ASC";
		
		List<Movie> results = entityManager.createQuery(jpql, Movie.class)
				.setMaxResults(1)
				.getResultList();
		
		return results.isEmpty() ? null : results.get(0).getDirector();
	}
	
	@Override
	public List<Movie> findMoviesWithTaglineGreaterThan(String tagline) {
		// Оптимизированный запрос: фильтрация на уровне БД
		String jpql = "SELECT m FROM Movie m " +
		              "WHERE m.tagline > :tagline";
		
		return entityManager.createQuery(jpql, Movie.class)
				.setParameter("tagline", tagline)
				.getResultList();
	}
	
	@Override
	public Set<Double> findUniqueUsaBoxOfficeValues() {
		// Оптимизированный запрос: DISTINCT на уровне БД
		String jpql = "SELECT DISTINCT m.usaBoxOffice FROM Movie m";
		
		return entityManager.createQuery(jpql, Double.class)
				.getResultStream()
				.collect(Collectors.toSet());
	}
	
	@Override
	public List<Person> findOperatorsWithNoOscars() {
		// Оптимизированный запрос: фильтрация + DISTINCT на уровне БД
		String jpql = "SELECT DISTINCT m.operator FROM Movie m " +
		              "WHERE m.oscarsCount = 0 AND m.operator IS NOT NULL";
		
		return entityManager.createQuery(jpql, Person.class)
				.getResultList();
	}
	
	@Override
	public void addOscarToRRatedMovies() {
		// Оптимизированный запрос: batch update на уровне БД
		String jpql = "UPDATE Movie m SET m.oscarsCount = m.oscarsCount + 1 " +
		              "WHERE m.mpaaRating = :rating";
		
		int updatedCount = entityManager.createQuery(jpql)
				.setParameter("rating", MpaaRating.R)
				.executeUpdate();
	}
}
