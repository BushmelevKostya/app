package itmo.app.service;

import itmo.app.exception.BusinessException;
import itmo.app.model.entity.MinioFiles;
import itmo.app.model.repository.MinioFilesRepository;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {
	
	@Autowired
	private MinioClient minioClient;
	
	@Autowired
	private MinioFilesRepository minioFilesRepository;
	
	@Autowired
	private UserService userService;
	
	@Value("${minio.bucket.name}")
	private String bucketName;
	
	@Transactional
	public MinioFiles uploadFile(MultipartFile file) {
		try {
			// Ensure bucket exists
			boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
					.bucket(bucketName)
					.build());
			
			if (!bucketExists) {
				minioClient.makeBucket(MakeBucketArgs.builder()
						.bucket(bucketName)
						.build());
			}
			
			// Generate unique filename
			String originalFilename = file.getOriginalFilename();
			String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
			
			// Upload file to MinIO
			minioClient.putObject(PutObjectArgs.builder()
					.bucket(bucketName)
					.object(fileName)
					.stream(file.getInputStream(), file.getSize(), -1)
					.contentType(file.getContentType())
					.build());
			
			// Save metadata to database
			MinioFiles minioFile = new MinioFiles();
			minioFile.setFileName(fileName);
			// Note: MinioFiles entity doesn't have these fields yet, will be added later
			// minioFile.setOriginalFileName(originalFilename);
			// minioFile.setFileSize(file.getSize());
			// minioFile.setContentType(file.getContentType());
			// minioFile.setUploadDate(LocalDateTime.now());
			// minioFile.setUploadedBy(userService.getCurrentUser());
			
			return minioFilesRepository.save(minioFile);
			
		} catch (Exception e) {
			throw new BusinessException("Failed to upload file: " + e.getMessage(), e);
		}
	}
	
	@Transactional(readOnly = true)
	public InputStream downloadFile(String fileName) {
		try {
			return minioClient.getObject(GetObjectArgs.builder()
					.bucket(bucketName)
					.object(fileName)
					.build());
		} catch (Exception e) {
			throw new BusinessException("Failed to download file: " + e.getMessage(), e);
		}
	}
	
	@Transactional
	public void deleteFile(Long fileId) {
		MinioFiles file = minioFilesRepository.findById(fileId)
				.orElseThrow(() -> new BusinessException("File not found"));
		
		try {
			minioClient.removeObject(RemoveObjectArgs.builder()
					.bucket(bucketName)
					.object(file.getFileName())
					.build());
			
			minioFilesRepository.delete(file);
		} catch (Exception e) {
			throw new BusinessException("Failed to delete file: " + e.getMessage(), e);
		}
	}
	
	@Transactional(readOnly = true)
	public List<MinioFiles> getUserFiles() {
		return minioFilesRepository.findByUploadedBy(userService.getCurrentUser());
	}
	
	@Transactional(readOnly = true)
	public List<MinioFiles> getAllFiles() {
		return minioFilesRepository.findAll();
	}
}
