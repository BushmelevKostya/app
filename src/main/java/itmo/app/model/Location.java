package itmo.app.model;

public class Location {
	private Double x; //���� �� ����� ���� null
	private Double y; //���� �� ����� ���� null
	private float z;
	private String name; //���� �� ����� ���� null
	
	public Double getX() {
		return x;
	}
	
	public void setX(Double x) {
		this.x = x;
	}
	
	public Double getY() {
		return y;
	}
	
	public void setY(Double y) {
		this.y = y;
	}
	
	public float getZ() {
		return z;
	}
	
	public void setZ(float z) {
		this.z = z;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
