# Diff Step 1: Внедрение Spring Security и JWT-аутентификации

**Дата:** 21 декабря 2025  
**Этап:** 1.1 - Архитектурный рефакторинг и безопасность

---

## Цель
Заменить примитивную аутентификацию на JWT-based с использованием Spring Security

---

## Выполненные изменения

### 1. Добавлены зависимости

**Файл:** `build.gradle`

```gradle
// Spring Security
implementation 'org.springframework.boot:spring-boot-starter-security'

// JWT
implementation 'io.jsonwebtoken:jjwt-api:0.12.5'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.5'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.5'
```

**Причина:** Необходимы для реализации JWT-токенов и защиты API

---

## Создаваемые файлы

### Security Configuration
- [x] `src/main/java/itmo/app/security/SecurityConfig.java` - конфигурация Spring Security
- [x] `src/main/java/itmo/app/security/JwtAuthenticationFilter.java` - фильтр для проверки JWT
- [x] `src/main/java/itmo/app/security/JwtTokenProvider.java` - генерация и валидация токенов
- [x] `src/main/java/itmo/app/security/UserDetailsServiceImpl.java` - загрузка пользователей для Security

### DTO (Data Transfer Objects)
- [x] `src/main/java/itmo/app/dto/request/LoginRequest.java` - запрос авторизации
- [x] `src/main/java/itmo/app/dto/request/RegisterRequest.java` - запрос регистрации
- [x] `src/main/java/itmo/app/dto/response/AuthResponse.java` - ответ с JWT токеном
- [x] `src/main/java/itmo/app/dto/response/ErrorResponse.java` - стандартизированный ответ с ошибкой

### Services
- [x] `src/main/java/itmo/app/service/AuthenticationService.java` - сервис аутентификации
- [ ] `src/main/java/itmo/app/service/UserService.java` - сервис управления пользователями (следующий шаг)

### Controllers
- [x] `src/main/java/itmo/app/controller/AuthController.java` - новый контроллер для auth
- [ ] Обновление `UserController.java` - рефакторинг с делегированием в сервисы (следующий шаг)

### Exception Handling
- [x] `src/main/java/itmo/app/exception/BusinessException.java` - базовый класс исключений
- [x] `src/main/java/itmo/app/exception/ResourceNotFoundException.java` - ресурс не найден
- [x] `src/main/java/itmo/app/exception/UnauthorizedException.java` - ошибка авторизации
- [x] `src/main/java/itmo/app/controller/GlobalExceptionHandler.java` - глобальная обработка ошибок

### Configuration
- [x] `src/main/resources/application.yml` - замена application.properties с JWT настройками

---

## Изменения в существующих файлах

### 1. User.java (Entity)
- [x] Реализовать интерфейс `UserDetails` для Spring Security
- [x] Добавить методы: `getAuthorities()`, `getUsername()`, `isAccountNonExpired()`, etc.
- [x] Добавлены аннотации `@JsonIgnore` для методов UserDetails

### 2. UserRepository.java
- [x] Метод `Optional<User> findByEmail(String email)` - уже существовал
- [x] Метод `boolean existsByEmail(String email)` - уже существовал

### 3. UserController.java
- Удалить методы `/register` и `/login` - переносим в AuthController
- Рефакторинг остальных методов с использованием UserService

---

## Ключевые архитектурные решения

### 1. JWT Token Structure
```json
{
  "sub": "user@example.com",
  "roles": ["ROLE_USER", "ROLE_ADMIN"],
  "iat": 1703174400,
  "exp": 1703260800
}
```

### 2. Security Flow
```
Client → JWT Filter → Security Context → Controller → Service → Repository
         ↓ (валидация токена)
         ↓ (если токен валиден, устанавливаем Authentication)
```

### 3. Endpoint Protection
- **Публичные:** `/api/auth/login`, `/api/auth/register`
- **Требуют аутентификации:** `/api/movies/**`, `/api/users/**`, `/api/notifications/**`
- **Только для админов:** `/api/admin/**`

---

## Удаляемые классы/методы

### Устаревшие компоненты
- ❌ `PasswordUtil.java` - заменяется на `BCryptPasswordEncoder` из Spring Security
- ❌ `UserValidationService.java` - логика переносится в сервисы
- ❌ `UserContext.java` - заменяется на `SecurityContextHolder`
- ❌ Методы в UserController: `registerUser()`, `loginUser()` - переезжают в AuthController

---

## CORS Configuration

**Было:** Аннотации `@CrossOrigin` на каждом контроллере  
**Стало:** Глобальная настройка в SecurityConfig

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    // ...
}
```

---

## Конфигурация приложения

### application.yml (новая структура)
```yaml
spring:
  application:
    name: app
  profiles:
    active: ${SPRING_PROFILE:dev}

# JWT Settings
jwt:
  secret: ${JWT_SECRET:your-256-bit-secret-key-here-change-in-production}
  expiration: 86400000 # 24 hours in milliseconds

# CORS Settings
cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:4200}
```

---

## Статус выполнения: ✅ 90% завершено

### Выполнено:
- ✅ Добавлены зависимости Spring Security и JWT (jjwt 0.12.5)
- ✅ Созданы все DTO классы (LoginRequest, RegisterRequest, AuthResponse, ErrorResponse)
- ✅ Созданы все Exception классы (BusinessException, ResourceNotFoundException, UnauthorizedException)
- ✅ Реализован JwtTokenProvider для генерации и валидации токенов
- ✅ Создан UserDetailsServiceImpl для загрузки пользователей
- ✅ Реализован JwtAuthenticationFilter для проверки токенов в запросах
- ✅ Настроен SecurityConfig с CORS, CSRF, session management
- ✅ Создан AuthenticationService с логикой регистрации и логина
- ✅ Создан AuthController с endpoint'ами `/api/auth/login` и `/api/auth/register`
- ✅ Создан GlobalExceptionHandler для централизованной обработки ошибок
- ✅ Обновлен User entity - реализован интерфейс UserDetails
- ✅ Создан application.yml с конфигурацией JWT, CORS, и всех сервисов
- ✅ Добавлен spring-security-test в зависимости для тестирования

### Осталось сделать:
- [ ] Удалить устаревшие классы (PasswordUtil, UserValidationService, UserContext)
- [ ] Рефакторинг UserController - удалить методы login/register
- [ ] Обновить MovieController - убрать `{email}` из URL, использовать SecurityContextHolder
- [ ] Создать .env.example с документацией переменных окружения
- [ ] Протестировать работу JWT авторизации

---

## Следующие шаги (после завершения 1.1)

- [ ] Обновить frontend для работы с JWT (Interceptor)
- [ ] Добавить Refresh Token механизм (опционально)
- [ ] Реализовать logout на backend (blacklist токенов через Redis)
- [ ] Добавить rate limiting для защиты от brute-force атак

---

## Заметки и решения проблем

### Проблема: Циклические зависимости
**Решение:** UserDetailsService должен работать только с UserRepository, без других сервисов

### Проблема: Пароли в plain text
**Решение:** Все существующие пароли нужно перехешировать с помощью BCrypt

### Проблема: Email в URL (`/api/action/{email}`)
**Решение:** Получаем email из JWT токена через SecurityContextHolder

---

_Документ обновляется по мере выполнения задач_
