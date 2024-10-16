package itmo.app.model.collections;

import itmo.app.model.Movie;
import itmo.app.model.Person;

import java.util.ArrayList;

public class MovieCollection {
	private static MovieCollection instance;
	private ArrayList<Movie> collection = new ArrayList<>();
	
	private MovieCollection() {
	}
	
	public static MovieCollection getInstance() {
		if (instance == null) {
			instance = new MovieCollection();
		}
		return instance;
	}
	
	public void add(Movie movie) {
		collection.add(movie);
	}
	
	public void remove(Movie movie) {
		collection.remove(movie);
	}
	
	public void clear() {
		collection.clear();
	}
	
	public ArrayList<Movie> getCollection() {
		return collection;
	}
	
	public void setCollection(ArrayList<Movie> collection) {
		this.collection = collection;
	}
	
	public void printCollection() {
		if (collection.isEmpty()) {
			System.out.println("The collection is empty.");
			return;
		}
		
		for (Movie movie : collection) {
			System.out.println("=================================");
			System.out.println("ID: " + movie.getId());
			System.out.println("Name: " + movie.getName());
			System.out.println("Coordinates: X = " + movie.getCoordinates().getX() + ", Y = " + movie.getCoordinates().getY());
			System.out.println("Creation Date: " + movie.getCreationDate());
			System.out.println("Oscar Count: " + movie.getOscarsCount());
			System.out.println("Budget: " + movie.getBudget());
			System.out.println("Total Box Office: " + movie.getTotalBoxOffice());
			System.out.println("MPAA Rating: " + movie.getMpaaRating());
			System.out.println("Length: " + movie.getLength());
			System.out.println("Golden Palm Count: " + movie.getGoldenPalmCount());
			System.out.println("USA Box Office: " + (movie.getUsaBoxOffice() != null ? movie.getUsaBoxOffice() : "Not specified"));
			System.out.println("Tagline: " + movie.getTagline());
			System.out.println("Genre: " + (movie.getGenre() != null ? movie.getGenre() : "Not specified"));
			
			System.out.println("Director: ");
			printPerson(movie.getDirector());
			
			System.out.println("Screenwriter: ");
			printPerson(movie.getScreenwriter());
			
			System.out.println("Operator: ");
			printPerson(movie.getOperator());
		}
	}
	
	private void printPerson(Person person) {
		if (person == null) {
			System.out.println("Not specified");
			return;
		}
		
		System.out.println("Name: " + person.getName());
		System.out.println("Eye Color: " + person.getEyeColor());
		System.out.println("Hair Color: " + person.getHairColor());
		System.out.println("Height: " + person.getHeight());
		System.out.println("Nationality: " + person.getNationality());
	}
}
