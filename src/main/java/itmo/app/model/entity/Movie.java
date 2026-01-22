package itmo.app.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "movies", indexes = {
	@Index(name = "idx_movie_name", columnList = "name"),
	@Index(name = "idx_movie_creation_date", columnList = "creation_date"),
	@Index(name = "idx_movie_mpaa_rating", columnList = "mpaa_rating"),
	@Index(name = "idx_movie_genre", columnList = "genre"),
	@Index(name = "idx_movie_creator", columnList = "creator_id")
})
public class Movie extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(
		name = "creator_id", 
		nullable = false,
		foreignKey = @ForeignKey(name = "fk_movie_creator")
	)
	private User creator;
	
	@NotNull
	@Size(min = 1, max = 500)
	@Column(nullable = false, length = 500)
	private String name;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(
		name = "coordinates_id",
		foreignKey = @ForeignKey(name = "fk_movie_coordinates")
	)
	private Coordinates coordinates;
	
	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(nullable = false, updatable = false)
	private java.time.LocalDateTime creationDate;
	
	@NotNull
	@Min(1)
	@Column
	private int oscarsCount;
	
	@NotNull
	@Min(1)
	@Column(nullable = false)
	private Float budget;
	
	@Min(1)
	@Column
	private int totalBoxOffice;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MpaaRating mpaaRating;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(
		name = "person_id",
		foreignKey = @ForeignKey(name = "fk_movie_director")
	)
	private Person director;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(
		name = "screenwriter_id",
		foreignKey = @ForeignKey(name = "fk_movie_screenwriter")
	)
	private Person screenwriter;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(
		name = "operator_id",
		foreignKey = @ForeignKey(name = "fk_movie_operator")
	)
	private Person operator;
	
	@NotNull
	@Min(1)
	@Column
	private int length;
	
	@Min(1)
	private long goldenPalmCount;
	
	@NotNull
	@Min(1)
	@Column
	private Double usaBoxOffice;
	
	@NotNull
	@Column(nullable = false, length = 1000)
	private String tagline;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private MovieGenre genre;
	
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
	
	public User getCreator() {
		return creator;
	}
	
	public void setCreator(User creator) {
		this.creator = creator;
	}
	
//	public Set<MovieChange> getChanges() {
//		return changes;
//	}
//
//	public void setChanges(Set<MovieChange> changes) {
//		this.changes = changes;
//	}
}
