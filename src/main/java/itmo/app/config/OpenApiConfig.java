package itmo.app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:2580}")
    private String serverPort;

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Development server"),
                        new Server()
                                .url("https://api.yourdomain.com")
                                .description("Production server")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT authentication token. Format: 'Bearer {token}'")
                        )
                );
    }

    private Info apiInfo() {
        return new Info()
                .title("Movie Database Management API")
                .description("""
                        REST API для управления базой данных фильмов.
                        
                        ## Основные возможности:
                        
                        - **Аутентификация и авторизация** через JWT токены
                        - **Управление фильмами**: создание, редактирование, удаление, поиск
                        - **Управление персонами**: режиссеры, операторы и их роли
                        - **Импорт данных** из JSON файлов
                        - **Хранение файлов** в MinIO
                        - **Real-time уведомления** через WebSocket
                        - **Кэширование** с помощью Redis
                        
                        ## Аутентификация:
                        
                        1. Зарегистрируйтесь через `/api/auth/register`
                        2. Получите JWT токен через `/api/auth/login`
                        3. Используйте кнопку 'Authorize' для ввода токена
                        4. Все последующие запросы будут автоматически содержать токен
                        
                        ## Формат токена:
                        
                        ```
                        Authorization: Bearer <your-jwt-token>
                        ```
                        
                        ## Роли пользователей:
                        
                        - **USER**: Базовые права (просмотр, создание объектов)
                        - **ADMIN**: Полные права (удаление, модерация)
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("ИТМО - Рефакторинг БД и приложений")
                        .email("student@itmo.ru")
                        .url("https://itmo.ru"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }
}
