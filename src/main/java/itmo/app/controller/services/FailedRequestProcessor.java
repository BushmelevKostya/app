package itmo.app.controller.services;

import itmo.app.model.entity.FailedRequest;
import itmo.app.model.repository.FailedRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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
		System.out.println("пробуем восстановить реквесты");
		List<FailedRequest> failedRequest = failedRequestRepository.findAll();
		for (FailedRequest request: failedRequest) {
			System.out.println("восстанавливаем реквест");
			try {
				if ("POST".equalsIgnoreCase(request.getMethod())) {
					restTemplate.postForEntity(request.getUrl(), request.getBody(), String.class);
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
