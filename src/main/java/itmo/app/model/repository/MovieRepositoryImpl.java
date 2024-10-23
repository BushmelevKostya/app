package itmo.app.model.repository;

import itmo.app.model.entity.Movie;
import itmo.app.model.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MovieRepositoryImpl implements MovieRepositoryCustom{
	@Autowired
	@Lazy
	MovieRepository movieRepository;
	
	@Override
	public Movie findMovieWithMinDirector() {
		return movieRepository.findAll()
				.stream()
				.min(Comparator.comparing(movie -> movie.getDirector().getName()))
				.orElse(null);
	}
	
	@Override
	public List<Movie> findMoviesWithTaglineGreaterThan(String tagline) {
		return movieRepository.findAll()
				.stream()
				.filter(movie -> movie.getTagline().compareTo(tagline) > 0)
				.collect(Collectors.toList());
	}
	
	@Override
	public Set<Double> findUniqueUsaBoxOfficeValues() {
		return movieRepository.findAll()
				.stream()
				.map(Movie::getUsaBoxOffice)
				.collect(Collectors.toSet());
	}
	
	@Override
	public List<Person> findOperatorsWithNoOscars() {
		return movieRepository.findAll()
				.stream()
				.filter(movie -> movie.getOscarsCount() == 0)
				.map(Movie::getOperator)
				.distinct()
				.collect(Collectors.toList());
	}
	
	@Override
	public void addOscarToRRatedMovies() {
		List<Movie> rRatedMovies = movieRepository.findAll()
				.stream()
				.filter(movie -> movie.getMpaaRating().equals("R"))
				.collect(Collectors.toList());
		rRatedMovies.forEach(movie -> movie.setOscarsCount(movie.getOscarsCount() + 1));
		movieRepository.saveAll(rRatedMovies);
	}
}
