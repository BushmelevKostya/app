package itmo.app.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class Movie {
	private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
	private String name; //Поле не может быть null, Строка не может быть пустой
	private Coordinates coordinates; //Поле не может быть null
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private java.time.LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
	private int oscarsCount; //Значение поля должно быть больше 0
	private Float budget; //Значение поля должно быть больше 0, Поле не может быть null
	private int totalBoxOffice; //Значение поля должно быть больше 0
	private MpaaRating mpaaRating; //Поле не может быть null
	private Person director; //Поле может быть null
	private Person screenwriter;
	private Person operator; //Поле может быть null
	private int length; //Значение поля должно быть больше 0
	private long goldenPalmCount; //Значение поля должно быть больше 0
	private Double usaBoxOffice; //Поле может быть null, Значение поля должно быть больше 0
	private String tagline; //Поле не может быть null
	private MovieGenre genre; //Поле может быть null
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
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
	
	public int getOscarsCount() {
		return oscarsCount;
	}
	
	public void setOscarsCount(int oscarsCount) {
		this.oscarsCount = oscarsCount;
	}
	
	public Float getBudget() {
		return budget;
	}
	
	public void setBudget(Float budget) {
		this.budget = budget;
	}
	
	public int getTotalBoxOffice() {
		return totalBoxOffice;
	}
	
	public void setTotalBoxOffice(int totalBoxOffice) {
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
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public long getGoldenPalmCount() {
		return goldenPalmCount;
	}
	
	public void setGoldenPalmCount(long goldenPalmCount) {
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
