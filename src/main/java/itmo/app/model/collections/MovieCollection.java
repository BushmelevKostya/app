package itmo.app.model.collections;

import itmo.app.model.Movie;

import java.util.ArrayList;

public class MovieCollection {
	private static ArrayList<Movie> collection = new ArrayList<>();
	
	public MovieCollection() {}
	
	public void add(Movie movie) {
		collection.add(movie);
	}
	
	public void remove(Movie movie) {
		collection.remove(movie);
	}
	
	public void clear() {
		collection.clear();
	}
	
	public static ArrayList<Movie> getCollection() {
		return collection;
	}
	
	public void setCollection(ArrayList<Movie> collection) {
		this.collection = collection;
	}
}
