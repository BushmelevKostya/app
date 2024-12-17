package itmo.app.controller.services;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class MinioConfig {
	
	@Value("${minio.endpoint}")
	private String minioEndpoint;
	
	@Value("${minio.access.key}")
	private String minioAccessKey;
	
	@Value("${minio.secret.key}")
	private String minioSecretKey;
	
	@Value("${minio.bucket.name}")
	private String bucketName;
	
	@Bean
	public MinioClient minioClient() throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
		MinioClient minioClient = MinioClient.builder()
				.endpoint("http://localhost:9000")
				.credentials("minioadmin", "minioadmin")
				.build();
		
		String bucketName = "json-bucket";
		boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
		if (!isExist) {
			minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
		}
		
		return minioClient;
	}
	
	
}
