package itmo.app.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies")
public class Movie {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id; //�������� ���� ������ ���� ������ 0, �������� ����� ���� ������ ���� ����������, �������� ����� ���� ������ �������������� �������������
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "creator_id", nullable = false)
	private User creator;
	
	@Column(nullable = false)
	private String name; //���� �� ����� ���� null, ������ �� ����� ���� ������
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "coordinates_id")
	private Coordinates coordinates; //���� �� ����� ���� null
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(nullable = false, updatable = false)
	private java.time.LocalDateTime creationDate; //���� �� ����� ���� null, �������� ����� ���� ������ �������������� �������������
	
	@Column
	private int oscarsCount; //�������� ���� ������ ���� ������ 0
	
	@Column(nullable = false)
	private Float budget; //�������� ���� ������ ���� ������ 0, ���� �� ����� ���� null
	
	@Column
	private int totalBoxOffice; //�������� ���� ������ ���� ������ 0
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MpaaRating mpaaRating; //���� �� ����� ���� null
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "person_id")
	private Person director; //���� ����� ���� null
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "screenwriter_id")
	private Person screenwriter;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "operator_id")
	private Person operator; //���� ����� ���� null
	
	@Column
	private int length; //�������� ���� ������ ���� ������ 0
	
	private long goldenPalmCount; //�������� ���� ������ ���� ������ 0
	@Column
	private Double usaBoxOffice; //���� ����� ���� null, �������� ���� ������ ���� ������ 0
	
	@Column(nullable = false)
	private String tagline; //���� �� ����� ���� null
	
	@Enumerated(EnumType.STRING)
	@Column
	private MovieGenre genre; //���� ����� ���� null
	
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
