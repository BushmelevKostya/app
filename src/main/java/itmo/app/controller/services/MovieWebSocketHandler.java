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
	
	public void sendToAllSessions() {
		try {
			String message = objectMapper.writeValueAsString(true);
			TextMessage textMessage = new TextMessage(message);
			for (WebSocketSession session : sessions) {
				if (session.isOpen()) {
					session.sendMessage(textMessage);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		session.close(CloseStatus.SERVER_ERROR);
	}
}
