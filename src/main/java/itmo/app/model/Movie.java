package itmo.app.model;

public class Movie {
	private Integer id; //���� �� ����� ���� null, �������� ���� ������ ���� ������ 0, �������� ����� ���� ������ ���� ����������, �������� ����� ���� ������ �������������� �������������
	private String name; //���� �� ����� ���� null, ������ �� ����� ���� ������
	private Coordinates coordinates; //���� �� ����� ���� null
	private java.time.LocalDateTime creationDate; //���� �� ����� ���� null, �������� ����� ���� ������ �������������� �������������
	private long oscarsCount; //�������� ���� ������ ���� ������ 0
	private int budget; //�������� ���� ������ ���� ������ 0
	private Float totalBoxOffice; //���� ����� ���� null, �������� ���� ������ ���� ������ 0
	private MpaaRating mpaaRating; //���� �� ����� ���� null
	private Person director; //���� ����� ���� null
	private Person screenwriter;
	private Person operator; //���� ����� ���� null
	private long length; //�������� ���� ������ ���� ������ 0
	private Integer goldenPalmCount; //�������� ���� ������ ���� ������ 0, ���� ����� ���� null
	private long usaBoxOffice; //�������� ���� ������ ���� ������ 0
	private String tagline; //���� �� ����� ���� null
	private MovieGenre genre; //���� ����� ���� null
}
