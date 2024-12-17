package itmo.app.model.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table
public class MinioFiles {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private long historyId;
	private String fileName;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getHistoryId() {
		return historyId;
	}
	
	public void setHistoryId(long historyId) {
		this.historyId = historyId;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
