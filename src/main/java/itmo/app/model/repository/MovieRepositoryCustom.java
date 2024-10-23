package itmo.app.model.repository;

import itmo.app.model.entity.Movie;
import itmo.app.model.entity.Person;

import java.util.List;
import java.util.Set;

public interface MovieRepositoryCustom {
	Person findMovieWithMinDirector();
	
	List<Movie> findMoviesWithTaglineGreaterThan(String tagline);
	
	Set<Double> findUniqueUsaBoxOfficeValues();
	
	List<Person> findOperatorsWithNoOscars();
	
	void addOscarToRRatedMovies();
}
