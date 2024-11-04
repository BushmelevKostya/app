package itmo.app.controller.services;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	
	private final MovieWebSocketHandler movieWebSocketHandler;
	
	public WebSocketConfig(MovieWebSocketHandler movieWebSocketHandler) {
		this.movieWebSocketHandler = movieWebSocketHandler;
	}
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(movieWebSocketHandler, "/socket").setAllowedOrigins("*");
	}
}
