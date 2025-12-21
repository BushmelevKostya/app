package itmo.app.service;

import itmo.app.exception.BusinessException;
import itmo.app.model.entity.MinioFiles;
import itmo.app.model.repository.MinioFilesRepository;
import io.minio.*;
import io.minio.messages.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {
	
	@Mock
	private MinioClient minioClient;
	
	@Mock
	private MinioFilesRepository minioFilesRepository;
	
	@Mock
	private UserService userService;
	
	@Mock
	private MultipartFile multipartFile;
	
	@InjectMocks
	private FileService fileService;
	
	private MinioFiles testFile;
	
	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(fileService, "bucketName", "test-bucket");
		
		testFile = new MinioFiles();
		testFile.setId(1L);
		testFile.setFileName("test-file.json");
	}
	
	@Test
	void uploadFile_ShouldUploadSuccessfully_WhenBucketExists() throws Exception {
		// Arrange
		when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
		when(multipartFile.getOriginalFilename()).thenReturn("test.json");
		when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
		when(multipartFile.getSize()).thenReturn(4L);
		when(multipartFile.getContentType()).thenReturn("application/json");
		when(minioFilesRepository.save(any(MinioFiles.class))).thenReturn(testFile);
		
		// Act
		MinioFiles result = fileService.uploadFile(multipartFile);
		
		// Assert
		assertNotNull(result);
		verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
		verify(minioFilesRepository, times(1)).save(any(MinioFiles.class));
	}
	
	@Test
	void uploadFile_ShouldCreateBucket_WhenBucketDoesNotExist() throws Exception {
		// Arrange
		when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);
		when(multipartFile.getOriginalFilename()).thenReturn("test.json");
		when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
		when(multipartFile.getSize()).thenReturn(4L);
		when(multipartFile.getContentType()).thenReturn("application/json");
		when(minioFilesRepository.save(any(MinioFiles.class))).thenReturn(testFile);
		
		// Act
		MinioFiles result = fileService.uploadFile(multipartFile);
		
		// Assert
		assertNotNull(result);
		verify(minioClient, times(1)).makeBucket(any(MakeBucketArgs.class));
		verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
	}
	
	@Test
	void uploadFile_ShouldThrowException_WhenUploadFails() throws Exception {
		// Arrange
		when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
		when(multipartFile.getInputStream()).thenThrow(new RuntimeException("Upload failed"));
		
		// Act & Assert
		assertThrows(BusinessException.class, () -> fileService.uploadFile(multipartFile));
	}
	
	@Test
	void downloadFile_ShouldReturnInputStream_WhenFileExists() throws Exception {
		// Arrange
		GetObjectResponse mockResponse = mock(GetObjectResponse.class);
		when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(mockResponse);
		
		// Act
		InputStream result = fileService.downloadFile("test-file.json");
		
		// Assert
		assertNotNull(result);
		verify(minioClient, times(1)).getObject(any(GetObjectArgs.class));
	}
	
	@Test
	void downloadFile_ShouldThrowException_WhenDownloadFails() throws Exception {
		// Arrange
		when(minioClient.getObject(any(GetObjectArgs.class)))
				.thenThrow(new RuntimeException("Download failed"));
		
		// Act & Assert
		assertThrows(BusinessException.class, () -> fileService.downloadFile("nonexistent.json"));
	}
	
	@Test
	void deleteFile_ShouldDeleteSuccessfully_WhenFileExists() throws Exception {
		// Arrange
		when(minioFilesRepository.findById(1L)).thenReturn(Optional.of(testFile));
		
		// Act
		fileService.deleteFile(1L);
		
		// Assert
		verify(minioClient, times(1)).removeObject(any(RemoveObjectArgs.class));
		verify(minioFilesRepository, times(1)).delete(testFile);
	}
	
	@Test
	void deleteFile_ShouldThrowException_WhenFileNotFound() throws Exception {
		// Arrange
		when(minioFilesRepository.findById(anyLong())).thenReturn(Optional.empty());
		
		// Act & Assert
		assertThrows(BusinessException.class, () -> fileService.deleteFile(999L));
		verify(minioClient, never()).removeObject(any(RemoveObjectArgs.class));
	}
}
