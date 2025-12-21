package itmo.app.dto.request;

import itmo.app.model.entity.Coordinates;
import itmo.app.model.entity.MpaaRating;
import itmo.app.model.entity.MovieGenre;
import itmo.app.model.entity.Person;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MovieCreateRequest {
	
	@NotNull
	@Size(min = 1)
	private String name;
	
	@NotNull
	private Coordinates coordinates;
	
	@NotNull
	@Min(1)
	private Integer oscarsCount;
	
	@NotNull
	@Min(1)
	private Float budget;
	
	@Min(1)
	private Integer totalBoxOffice;
	
	@NotNull
	private MpaaRating mpaaRating;
	
	@NotNull
	private Person director;
	
	@NotNull
	private Person screenwriter;
	
	@NotNull
	private Person operator;
	
	@NotNull
	@Min(1)
	private Integer length;
	
	@Min(1)
	private Long goldenPalmCount;
	
	@NotNull
	@Min(1)
	private Double usaBoxOffice;
	
	@NotNull
	private String tagline;
	
	private MovieGenre genre;
	
	public MovieCreateRequest() {
	}
	
	// Getters and Setters
	
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
}
