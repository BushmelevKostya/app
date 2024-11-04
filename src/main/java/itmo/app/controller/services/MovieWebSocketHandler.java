package itmo.app.controller.services;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MovieWebSocketHandler extends TextWebSocketHandler {
	
	private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		TextMessage message = new TextMessage("{\"message\": \"Соединение установлено\"}");
		session.sendMessage(message);
		sessions.add(session);
	}

}
