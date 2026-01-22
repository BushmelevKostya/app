package itmo.app.dto.response;

import itmo.app.model.entity.*;

import java.time.LocalDateTime;

public class MovieResponse {
	
	private Long id;
	private String name;
	private Coordinates coordinates;
	private LocalDateTime creationDate;
	private Integer oscarsCount;
	private Float budget;
	private Integer totalBoxOffice;
	private MpaaRating mpaaRating;
	private Person director;
	private Person screenwriter;
	private Person operator;
	private Integer length;
	private Long goldenPalmCount;
	private Double usaBoxOffice;
	private String tagline;
	private MovieGenre genre;
	private String creatorEmail;
	
	public MovieResponse() {
	}
	
	public MovieResponse(Movie movie) {
		this.id = movie.getId();
		this.name = movie.getName();
		this.coordinates = movie.getCoordinates();
		this.creationDate = movie.getCreationDate();
		this.oscarsCount = movie.getOscarsCount();
		this.budget = movie.getBudget();
		this.totalBoxOffice = movie.getTotalBoxOffice();
		this.mpaaRating = movie.getMpaaRating();
		this.director = movie.getDirector();
		this.screenwriter = movie.getScreenwriter();
		this.operator = movie.getOperator();
		this.length = movie.getLength();
		this.goldenPalmCount = movie.getGoldenPalmCount();
		this.usaBoxOffice = movie.getUsaBoxOffice();
		this.tagline = movie.getTagline();
		this.genre = movie.getGenre();
		this.creatorEmail = movie.getCreator() != null ? movie.getCreator().getEmail() : null;
	}
	
	// Getters and Setters
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Coordinates getCoordinates() {
		return coordinates;
	}
	
	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}
	
	public LocalDateTime getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}
	
	public Integer getOscarsCount() {
		return oscarsCount;
	}
	
	public void setOscarsCount(Integer oscarsCount) {
		this.oscarsCount = oscarsCount;
	}
	
	public Float getBudget() {
		return budget;
	}
	
	public void setBudget(Float budget) {
		this.budget = budget;
	}
	
	public Integer getTotalBoxOffice() {
		return totalBoxOffice;
	}
	
	public void setTotalBoxOffice(Integer totalBoxOffice) {
		this.totalBoxOffice = totalBoxOffice;
	}
	
	public MpaaRating getMpaaRating() {
		return mpaaRating;
	}
	
	public void setMpaaRating(MpaaRating mpaaRating) {
		this.mpaaRating = mpaaRating;
	}
	
	public Person getDirector() {
		return director;
	}
	
	public void setDirector(Person director) {
		this.director = director;
	}
	
	public Person getScreenwriter() {
		return screenwriter;
	}
	
	public void setScreenwriter(Person screenwriter) {
		this.screenwriter = screenwriter;
	}
	
	public Person getOperator() {
		return operator;
	}
	
	public void setOperator(Person operator) {
		this.operator = operator;
	}
	
	public Integer getLength() {
		return length;
	}
	
	public void setLength(Integer length) {
		this.length = length;
	}
	
	public Long getGoldenPalmCount() {
		return goldenPalmCount;
	}
	
	public void setGoldenPalmCount(Long goldenPalmCount) {
		this.goldenPalmCount = goldenPalmCount;
	}
	
	public Double getUsaBoxOffice() {
		return usaBoxOffice;
	}
	
	public void setUsaBoxOffice(Double usaBoxOffice) {
		this.usaBoxOffice = usaBoxOffice;
	}
	
	public String getTagline() {
		return tagline;
	}
	
	public void setTagline(String tagline) {
		this.tagline = tagline;
	}
	
	public MovieGenre getGenre() {
		return genre;
	}
	
	public void setGenre(MovieGenre genre) {
		this.genre = genre;
	}
	
	public String getCreatorEmail() {
		return creatorEmail;
	}
	
	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}
}
