package itmo.app.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "coordinates")
public class Coordinates {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable = false) // ���� �� ����� ���� null
	private float x;
	
	@Column(nullable = false) // ���� �� ����� ���� null
	private int y;
	
	@OneToOne(mappedBy = "coordinates")
	private Movie movie;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public float getX() {
		return x;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}