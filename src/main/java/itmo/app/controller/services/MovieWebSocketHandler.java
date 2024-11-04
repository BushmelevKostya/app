package itmo.app.controller.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import itmo.app.model.entity.Movie;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class MovieWebSocketHandler extends TextWebSocketHandler {
	private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
	private final ObjectMapper objectMapper;
	
	public MovieWebSocketHandler() {
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.add(session);
		session.sendMessage(new TextMessage("{\"message\": \"Соединение установлено\"}"));
	}
	
	public void sendToAllSessions(List<Movie> movies) {
		try {
			String message = objectMapper.writeValueAsString(movies);
			TextMessage textMessage = new TextMessage(message);
			System.out.println("отправляю сообщение");
			System.out.println(sessions);
			System.out.println(sessions.size());
			for (WebSocketSession session : sessions) {
				System.out.println(session.isOpen());
				if (session.isOpen()) {
					System.out.println("сообщение отправлено");
					session.sendMessage(textMessage);
				}
			}
		} catch (Exception e) {
			System.err.println("Ошибка при отправке сообщения: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
		System.out.println("Сессия закрыта: " + session.getId() + ", статус: " + status);
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		System.err.println("Ошибка транспортировки в сессии " + session.getId() + ": " + exception.getMessage());
		session.close(CloseStatus.SERVER_ERROR);
	}
}
