# Movie Database Management System

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen?style=flat-square&logo=spring" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Angular-19-red?style=flat-square&logo=angular" alt="Angular 19">
  <img src="https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/Redis-Latest-red?style=flat-square&logo=redis" alt="Redis">
  <img src="https://img.shields.io/badge/Docker-Compose-blue?style=flat-square&logo=docker" alt="Docker">
</p>

## 📋 Описание

Полнофункциональное веб-приложение для управления базой данных фильмов с использованием современного стека технологий. Проект разработан в рамках курса "Рефакторинг баз данных и приложений" в Университете ИТМО.

### Ключевые возможности

- 🔐 **JWT-аутентификация** с ролевой моделью (USER/ADMIN)
- 🎬 **CRUD операции** для управления фильмами и персонами
- 📊 **Пагинация и фильтрация** данных
- 📁 **Хранение файлов** в MinIO
- 🔄 **Real-time обновления** через WebSocket
- ⚡ **Кэширование** с помощью Redis
- 📤 **Импорт данных** из JSON файлов
- 📖 **Swagger UI** для интерактивной документации API
- 🐳 **Docker Compose** для одного команды запуска
- 🔍 **Мониторинг** через Spring Boot Actuator

## 🏗️ Архитектура

### Backend
- **Java 17** + **Spring Boot 3.3.4**
- **Spring Security** с JWT токенами
- **Spring Data JPA** + **Hibernate**
- **PostgreSQL 16** - основная БД
- **Redis** - кэширование
- **MinIO** - объектное хранилище
- **WebSocket** - real-time коммуникация

### Frontend
- **Angular 19** (standalone компоненты)
- **TypeScript 5.8**
- **RxJS** - реактивное программирование
- **SCSS** - стилизация

### DevOps
- **Docker & Docker Compose** - контейнеризация
- **GitHub Actions** - CI/CD pipeline
- **JaCoCo** - покрытие тестами (минимум 60%)
- **SpotBugs** - статический анализ
- **OWASP Dependency Check** - проверка уязвимостей

## 📁 Структура проекта

```
app/
├── backend/                    # Backend Java код (legacy, перенесен в src/)
├── frontend/                   # Angular приложение
│   ├── src/
│   │   ├── app/
│   │   │   ├── auth/          # Аутентификация
│   │   │   ├── home/          # Главная страница с таблицей фильмов
│   │   │   ├── services/      # HTTP сервисы
│   │   │   └── visualization/ # Визуализация данных
│   │   └── assets/
│   └── package.json
├── src/main/                   # Backend source code
│   ├── java/itmo/app/
│   │   ├── config/            # Конфигурация (Security, CORS, OpenAPI)
│   │   ├── controller/        # REST контроллеры
│   │   ├── dto/               # Data Transfer Objects
│   │   │   ├── request/       # Request DTO
│   │   │   └── response/      # Response DTO
│   │   ├── model/             
│   │   │   ├── entity/        # JPA сущности
│   │   │   ├── enums/         # Перечисления
│   │   │   └── Auditable.java # Базовый класс для аудита
│   │   ├── repository/        # Spring Data JPA репозитории
│   │   ├── security/          # JWT фильтры и утилиты
│   │   ├── service/           # Бизнес-логика
│   │   └── exception/         # Кастомные исключения
│   └── resources/
│       ├── application.yml    # Конфигурация Spring
│       ├── db/migration/      # Flyway миграции (опционально)
│       └── static/            # Статические файлы фронтенда
├── .github/workflows/          # CI/CD pipelines
│   ├── ci-cd.yml              # Основной CI/CD
│   ├── pr-checks.yml          # Проверки PR
│   └── security-scan.yml      # Еженедельное сканирование
├── build.gradle               # Gradle сборка
├── docker-compose.yml         # Production конфигурация
├── docker-compose.override.yml # Development окружение
├── Dockerfile                 # Multi-stage Docker build
├── .env.example               # Шаблон переменных окружения
└── README.md                  # Этот файл
```

## 🚀 Быстрый старт

### Требования

- **Java 17+** (для локальной разработки)
- **Node.js 20+** и **npm 10+** (для фронтенда)
- **Docker** и **Docker Compose** (рекомендуется)
- **PostgreSQL 16** (если запуск без Docker)
- **Redis** (если запуск без Docker)
- **MinIO** (если запуск без Docker)

### Вариант 1: Docker Compose (Рекомендуется)

1. **Клонировать репозиторий:**
```bash
git clone <repository-url>
cd app
```

2. **Запустить все сервисы:**
```bash
docker-compose up -d
```

Это автоматически запустит:
- PostgreSQL (порт 5434)
- Redis (порт 6379)
- MinIO (порт 9000, консоль 9001)
- Backend приложение (порт 2580)
- pgAdmin (порт 5050) - для управления БД
- Redis Commander (порт 8081) - для просмотра кэша

3. **Открыть приложение:**
- Frontend: Соберите и запустите отдельно (см. ниже)
- Backend API: http://localhost:2580
- Swagger UI: http://localhost:2580/swagger-ui.html
- API Docs: http://localhost:2580/v3/api-docs
- pgAdmin: http://localhost:5050 (admin@admin.com / admin)
- Redis Commander: http://localhost:8081

### Вариант 2: Локальная разработка

1. **Настроить переменные окружения:**
```bash
cp .env.example .env
# Отредактировать .env под свое окружение
```

2. **Запустить PostgreSQL, Redis, MinIO:**
```bash
docker-compose up -d postgres redis minio
```

3. **Собрать и запустить backend:**
```bash
./gradlew bootRun
```

4. **Собрать и запустить frontend:**
```bash
cd frontend
npm install
npm start
```

5. **Открыть приложение:**
- Frontend: http://localhost:4200
- Backend: http://localhost:2580
- Swagger: http://localhost:2580/swagger-ui.html

## 📚 Документация

### API Documentation

- **Swagger UI**: [http://localhost:2580/swagger-ui.html](http://localhost:2580/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:2580/v3/api-docs](http://localhost:2580/v3/api-docs)
- **Примеры запросов**: [docs/API.md](docs/API.md)

### Дополнительная документация

- [Deployment Guide](docs/DEPLOYMENT.md) - Инструкции по развертыванию
- [CI/CD Documentation](.github/CI_CD_README.md) - Описание пайплайнов
- [Environment Variables](.env.example) - Переменные окружения
- [Refactoring Plan](REFACTORING_PLAN.md) - План рефакторинга

### Мониторинг и Health Checks

- **Health Check**: http://localhost:2580/actuator/health
- **Metrics**: http://localhost:2580/actuator/metrics
- **Info**: http://localhost:2580/actuator/info

## 🔐 Аутентификация

### Регистрация

```bash
curl -X POST http://localhost:2580/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### Вход

```bash
curl -X POST http://localhost:2580/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

Ответ содержит JWT токен:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "user@example.com",
  "role": "USER"
}
```

### Использование токена

Добавьте токен в заголовок `Authorization` всех последующих запросов:

```bash
curl -X GET http://localhost:2580/api/movies \
  -H "Authorization: Bearer <your-jwt-token>"
```

## 🧪 Тестирование

### Запуск unit-тестов

```bash
./gradlew test
```

### Генерация отчета покрытия

```bash
./gradlew jacocoTestReport
```

Отчет доступен в `build/reports/jacoco/test/html/index.html`

### Запуск всех проверок (тесты + статический анализ)

```bash
./gradlew check
```

## 🛠️ Разработка

### Настройка IDE

#### IntelliJ IDEA
1. File → Open → Выбрать папку проекта
2. Gradle автоматически импортирует зависимости
3. Установить Java 17 SDK
4. Включить Annotation Processing (для Lombok, если используется)

#### VS Code
1. Установить расширения:
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - Angular Language Service
2. Открыть папку проекта
3. VS Code автоматически обнаружит Gradle проект

### Hot Reload

#### Backend (Spring Boot DevTools)
При изменении Java классов приложение автоматически перезагрузится.

#### Frontend (Angular)
```bash
cd frontend
npm start
```
Angular автоматически перезагружает изменения.

### Remote Debugging

В `docker-compose.override.yml` включен remote debugging на порту 5005:

1. В IntelliJ IDEA: Run → Edit Configurations → Add New → Remote JVM Debug
2. Установить порт 5005
3. Запустить debug конфигурацию

## 📦 Production Build

### Сборка JAR

```bash
./gradlew bootJar
```

JAR файл: `build/libs/app-0.0.1-SNAPSHOT.jar`

### Сборка Docker образа

```bash
docker build -t movie-app:latest .
```

### Запуск в production

```bash
docker-compose -f docker-compose.yml up -d
```

## 🔧 Конфигурация

### Переменные окружения

Все конфигурируемые параметры вынесены в переменные окружения. См. [.env.example](.env.example) для полного списка.

Основные переменные:
- `DB_URL` - URL PostgreSQL
- `REDIS_HOST` - Хост Redis
- `MINIO_ENDPOINT` - Эндпоинт MinIO
- `JWT_SECRET` - Секретный ключ JWT (изменить в production!)
- `CORS_ALLOWED_ORIGINS` - Разрешенные origins

### Профили Spring

- `dev` - Разработка (DEBUG логи, показ SQL)
- `prod` - Production (INFO логи, оптимизации)

Установить через: `SPRING_PROFILES_ACTIVE=prod`

## 🤝 Вклад в проект

1. Создать feature branch (`git checkout -b feature/amazing-feature`)
2. Закоммитить изменения (`git commit -m 'feat: Add amazing feature'`)
3. Запушить в branch (`git push origin feature/amazing-feature`)
4. Создать Pull Request

### Commit Convention

Используем [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` - новая функциональность
- `fix:` - исправление бага
- `docs:` - изменения в документации
- `refactor:` - рефакторинг кода
- `test:` - добавление тестов
- `chore:` - обновление зависимостей и т.д.

## 📝 Лицензия

MIT License - см. [LICENSE](LICENSE)

## 👥 Авторы

Проект разработан студентами Университета ИТМО в рамках курса "Рефакторинг баз данных и приложений".

## 📞 Контакты

- Email: student@itmo.ru
- University: [ИТМО](https://itmo.ru)

---

**Статус рефакторинга:**

- ✅ Этап 1: Архитектурный рефакторинг и безопасность
- ✅ Этап 2: Рефакторинг базы данных  
- ✅ Этап 3.1: Настройка CI/CD
- ✅ Этап 3.2: Контейнеризация приложения
- ✅ Этап 3.3: Управление конфигурацией
- ✅ Этап 3.4: Документация API

**Последнее обновление:** 22 января 2026
