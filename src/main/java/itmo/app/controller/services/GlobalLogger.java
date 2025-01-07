package itmo.app.controller.services;

import itmo.app.model.entity.FailedRequest;
import itmo.app.model.repository.FailedRequestRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalLogger {
	@Autowired
	private FailedRequestRepository failedRequestRepository;
	private static final Logger logger = LoggerFactory.getLogger(GlobalLogger.class);
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
			logger.warn("Validation error on field '{}': {}", fieldName, errorMessage);
		});
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(Exception.class)
	public void handleAllException(Exception ex, HttpServletRequest request) throws IOException {
		System.out.println("пробуем сохранить");
		logger.error("error occured: {}", ex.getMessage(), ex);
		
		if (request instanceof ContentCachingRequestWrapper cachingRequest) {
			String requestBody = new String(cachingRequest.getContentAsByteArray(), cachingRequest.getCharacterEncoding());
			saveFailedReuest(String.valueOf(cachingRequest.getRequestURL()), cachingRequest.getMethod(), requestBody);
		}
		else {
			System.out.println("не получилось закэшировать");
		}
		System.exit(1);
	}
	
	private void saveFailedReuest(String url, String method, String body) {
		System.out.println("Начало сохранения");
		
		FailedRequest failedRequest = new FailedRequest();
		failedRequest.setUrl(url);
		failedRequest.setMethod(method);
		failedRequest.setCreatedAt(LocalDateTime.now());
		failedRequest.setBody(body);
		
		failedRequestRepository.save(failedRequest);
		System.out.println("Конец сохранения");
	}
	
	public static Logger getLogger() {
		return logger;
	}
}
