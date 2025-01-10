package itmo.app.controller.services;

import itmo.app.model.entity.FailedRequest;
import itmo.app.model.repository.FailedRequestRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.List;

@Component
public class FailedRequestProcessor {
	@Autowired
	private FailedRequestRepository repository;

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private FailedRequestRepository failedRequestRepository;
	
	@EventListener(ApplicationReadyEvent.class)
	public void processFailedRequest() {
		Logger logger = GlobalLogger.getLogger();
		logger.info("пробуем восстановить реквесты");
		List<FailedRequest> failedRequest = failedRequestRepository.findAll();
		for (FailedRequest request: failedRequest) {
			logger.info("восстанавливаем реквест");
			try {
				if ("POST".equalsIgnoreCase(request.getMethod())) {
					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_JSON);
					HttpEntity<String> entity = new HttpEntity<>(request.getBody(), headers);
					restTemplate.postForEntity(request.getUrl(), entity, String.class);
				} else if ("GET".equalsIgnoreCase(request.getMethod())) {
					restTemplate.getForEntity(request.getUrl(), String.class);
				}
				failedRequestRepository.delete(request);
			} catch (Exception e) {
				System.err.println("Ошибка при обработке повторного запроса" + e.getMessage());
			}
		}
	}
}
