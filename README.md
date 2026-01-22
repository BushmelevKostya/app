# Movie Database Management System

Веб-приложение для управления базой данных фильмов. Проект курса "Рефакторинг баз данных и приложений" (ИТМО).

## Стек технологий

**Backend:** Java 17, Spring Boot 3.3.4, PostgreSQL 16, Redis, MinIO  
**Frontend:** Angular 19, TypeScript 5.8  
**DevOps:** Docker Compose, GitHub Actions, JaCoCo, SpotBugs, OWASP Dependency Check

## Возможности

- JWT-аутентификация с ролями (USER/ADMIN)
- CRUD операции для фильмов и персон
- Пагинация, фильтрация, кэширование
- Импорт данных из JSON
- Swagger UI документация
- Spring Boot Actuator мониторинг

## Быстрый старт

**Требования:** Docker и Docker Compose (рекомендуется)

### Docker Compose

```bash
git clone <repository-url>
cd app
docker-compose up -d
```

**Доступные сервисы:**
- Backend: http://localhost:2580
- Swagger UI: http://localhost:2580/swagger-ui.html
- pgAdmin: http://localhost:5050 (admin@admin.com / admin)
- Redis Commander: http://localhost:8081

### Локальная разработка

```bash
# Запустить БД и зависимости
docker-compose up -d postgres redis minio

# Backend
./gradlew bootRun

# Frontend
cd frontend
npm install
npm start
```

Frontend: http://localhost:4200  
Backend: http://localhost:2580

## Документация

- **Swagger UI**: http://localhost:2580/swagger-ui.html
- **OpenAPI JSON**: http://localhost:2580/v3/api-docs
- **Health Check**: http://localhost:2580/actuator/health

Дополнительно: [API.md](docs/API.md), [DEPLOYMENT.md](docs/DEPLOYMENT.md)

## Аутентификация

### Регистрация
```bash
curl -X POST http://localhost:2580/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "password": "password123"}'
```

### Вход
```bash
curl -X POST http://localhost:2580/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "password": "password123"}'
```

Используйте полученный JWT токен в заголовке:
```bash
curl -X GET http://localhost:2580/api/movies \
  -H "Authorization: Bearer <your-jwt-token>"
```

## Тестирование

```bash
# Unit-тесты
./gradlew test

# Отчет покрытия
./gradlew jacocoTestReport
# Отчет: build/reports/jacoco/test/html/index.html

# Все проверки
./gradlew check
```

## Сборка

```bash
# JAR файл
./gradlew bootJar

# Docker образ
docker build -t movie-app:latest .
```

---

**Статус рефакторинга:** Все этапы завершены (1, 2, 3.1-3.4)  
**Последнее обновление:** 23 января 2026
