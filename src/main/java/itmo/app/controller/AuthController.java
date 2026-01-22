package itmo.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import itmo.app.dto.request.LoginRequest;
import itmo.app.dto.request.RegisterRequest;
import itmo.app.dto.response.AuthResponse;
import itmo.app.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API для аутентификации и регистрации пользователей")
public class AuthController {
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@PostMapping("/register")
	@Operation(
			summary = "Регистрация нового пользователя",
			description = "Создает нового пользователя и возвращает JWT токен для дальнейшей аутентификации"
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Пользователь успешно зарегистрирован",
					content = @Content(schema = @Schema(implementation = AuthResponse.class))
			),
			@ApiResponse(
					responseCode = "400",
					description = "Невалидные данные или пользователь уже существует",
					content = @Content
			)
	})
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		AuthResponse response = authenticationService.register(request);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/login")
	@Operation(
			summary = "Вход в систему",
			description = "Аутентифицирует пользователя и возвращает JWT токен. Поддерживает вход как обычного пользователя и администратора"
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Успешная аутентификация",
					content = @Content(schema = @Schema(implementation = AuthResponse.class))
			),
			@ApiResponse(
					responseCode = "401",
					description = "Неверный email или пароль",
					content = @Content
			)
	})
	public ResponseEntity<AuthResponse> login(
			@Valid @RequestBody LoginRequest request,
			@RequestParam(defaultValue = "false") boolean isAdminLogin) {
		AuthResponse response = authenticationService.login(request, isAdminLogin);
		return ResponseEntity.ok(response);
	}
}
