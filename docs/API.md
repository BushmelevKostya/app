# API Documentation

REST API для Movie Database Management System с примерами запросов.

## Базовый URL

- Development: `http://localhost:2580`
- Production: `https://api.yourdomain.com`

## Swagger UI

Интерактивная документация доступна по адресу:
- http://localhost:2580/swagger-ui.html

OpenAPI спецификация (JSON):
- http://localhost:2580/v3/api-docs

---

## Аутентификация

API использует JWT (JSON Web Tokens) для аутентификации.

### Регистрация

```bash
curl -X POST http://localhost:2580/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "securePassword123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "user@example.com",
  "role": "USER"
}
```

### Вход

```bash
curl -X POST http://localhost:2580/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "securePassword123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "user@example.com",
  "role": "USER"
}
```

### Вход как администратор

```bash
curl -X POST "http://localhost:2580/api/auth/login?isAdminLogin=true" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "adminPassword123"
  }'
```

### Использование токена

Все последующие запросы требуют токен в заголовке `Authorization`:

```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Movies API

### Создать фильм

```bash
curl -X POST http://localhost:2580/api/movies \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Inception",
    "coordinates": {
      "x": 10,
      "y": 20.5
    },
    "creationDate": "2010-07-16T00:00:00",
    "oscarsCount": 4,
    "budget": 160000000,
    "totalBoxOffice": 829895144,
    "mpaaRating": "PG_13",
    "director": {
      "name": "Christopher Nolan",
      "birthday": "1970-07-30T00:00:00",
      "eyeColor": "BLUE",
      "hairColor": "BROWN",
      "location": {
        "x": 51.5074,
        "y": -0.1278,
        "z": 11,
        "name": "London"
      },
      "nationality": "BRITISH"
    },
    "screenwriter": {
      "name": "Christopher Nolan",
      "birthday": "1970-07-30T00:00:00",
      "eyeColor": "BLUE",
      "hairColor": "BROWN",
      "nationality": "BRITISH"
    },
    "operator": {
      "name": "Wally Pfister",
      "birthday": "1961-07-08T00:00:00",
      "eyeColor": "BROWN",
      "nationality": "AMERICAN"
    },
    "genre": "SCIENCE_FICTION"
  }'
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Inception",
  "coordinates": {
    "x": 10,
    "y": 20.5
  },
  "creationDate": "2010-07-16T00:00:00",
  "oscarsCount": 4,
  "budget": 160000000,
  "totalBoxOffice": 829895144,
  "mpaaRating": "PG_13",
  "director": {
    "id": 1,
    "name": "Christopher Nolan",
    ...
  },
  ...
}
```

### Получить список фильмов (с пагинацией)

```bash
curl -X GET "http://localhost:2580/api/movies?start=0&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Parameters:**
- `start` - Начальная позиция (default: 0)
- `size` - Количество элементов (default: 10)

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Inception",
      ...
    },
    {
      "id": 2,
      "name": "Interstellar",
      ...
    }
  ],
  "totalElements": 50,
  "totalPages": 5,
  "currentPage": 0,
  "pageSize": 10
}
```

### Получить фильм по ID

```bash
curl -X GET http://localhost:2580/api/movies/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**
```json
{
  "id": 1,
  "name": "Inception",
  "coordinates": {...},
  ...
}
```

### Обновить фильм

```bash
curl -X PUT http://localhost:2580/api/movies/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Inception (Director'\''s Cut)",
    "oscarsCount": 5
  }'
```

### Удалить фильм

```bash
curl -X DELETE http://localhost:2580/api/movies/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:** 200 OK (no content)

### Получить количество фильмов

```bash
curl -X GET http://localhost:2580/api/movies/count \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**
```json
157
```

### Подсчитать фильмы с oscarsCount больше заданного

```bash
curl -X GET "http://localhost:2580/api/movies/count-oscars-greater-than?oscarsCount=5" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**
```json
23
```

### Получить фильмы, отсортированные по названию

```bash
curl -X GET http://localhost:2580/api/movies/sorted-by-name \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Поиск фильма по названию режиссера

```bash
curl -X GET "http://localhost:2580/api/movies/search-by-director?directorName=Nolan" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Удалить один фильм по oscarsCount

```bash
curl -X DELETE "http://localhost:2580/api/movies/delete-by-oscars?oscarsCount=3" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Users API

### Получить текущего пользователя

```bash
curl -X GET http://localhost:2580/api/users/me \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**
```json
{
  "id": 1,
  "email": "user@example.com",
  "role": "USER",
  "createdAt": "2026-01-15T10:30:00"
}
```

### Получить всех пользователей (только ADMIN)

```bash
curl -X GET http://localhost:2580/api/users \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

---

## Notifications API

### Получить уведомления для текущего пользователя

```bash
curl -X GET http://localhost:2580/api/notifications \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**
```json
[
  {
    "id": 1,
    "message": "New movie added: Inception",
    "type": "MOVIE_CREATED",
    "approved": false,
    "createdAt": "2026-01-22T14:30:00"
  }
]
```

### Одобрить уведомление (ADMIN)

```bash
curl -X PUT http://localhost:2580/api/notifications/1/approve \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

### Удалить уведомление

```bash
curl -X DELETE http://localhost:2580/api/notifications/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Import API

### Импортировать данные из JSON файла

```bash
curl -X POST http://localhost:2580/api/import/upload \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@movies_data.json"
```

**Response:**
```json
{
  "status": "SUCCESS",
  "importedCount": 150,
  "failedCount": 3,
  "errors": [
    "Line 25: Invalid date format",
    "Line 67: Missing required field 'name'"
  ]
}
```

### Получить историю импорта

```bash
curl -X GET http://localhost:2580/api/import/history \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**
```json
[
  {
    "id": 1,
    "fileName": "movies_data.json",
    "status": "SUCCESS",
    "importedCount": 150,
    "uploadedAt": "2026-01-22T10:00:00",
    "userId": 1
  }
]
```

---

## File Storage API

### Загрузить файл в MinIO

```bash
curl -X POST http://localhost:2580/api/files/upload \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@poster.jpg"
```

**Response:**
```json
{
  "fileName": "poster_1234567890.jpg",
  "url": "http://localhost:9000/movies/poster_1234567890.jpg",
  "size": 245678
}
```

### Скачать файл

```bash
curl -X GET "http://localhost:2580/api/files/download?fileName=poster_1234567890.jpg" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  --output poster.jpg
```

### Удалить файл

```bash
curl -X DELETE "http://localhost:2580/api/files/delete?fileName=poster_1234567890.jpg" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Health Check & Monitoring

### Health Check

```bash
curl -X GET http://localhost:2580/actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "redis": {
      "status": "UP",
      "details": {
        "version": "7.2.4"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 500GB,
        "free": 250GB
      }
    }
  }
}
```

### Application Info

```bash
curl -X GET http://localhost:2580/actuator/info
```

### Metrics

```bash
curl -X GET http://localhost:2580/actuator/metrics
```

### Specific Metric

```bash
curl -X GET http://localhost:2580/actuator/metrics/jvm.memory.used
```

---

## Error Responses

API возвращает стандартизированные ошибки:

### 400 Bad Request

```json
{
  "timestamp": "2026-01-22T15:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "name",
      "message": "must not be blank"
    },
    {
      "field": "budget",
      "message": "must be greater than 0"
    }
  ],
  "path": "/api/movies"
}
```

### 401 Unauthorized

```json
{
  "timestamp": "2026-01-22T15:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is missing or invalid",
  "path": "/api/movies"
}
```

### 403 Forbidden

```json
{
  "timestamp": "2026-01-22T15:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied. Admin role required.",
  "path": "/api/users"
}
```

### 404 Not Found

```json
{
  "timestamp": "2026-01-22T15:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Movie with id 999 not found",
  "path": "/api/movies/999"
}
```

### 500 Internal Server Error

```json
{
  "timestamp": "2026-01-22T15:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "path": "/api/movies"
}
```

---

## Rate Limiting

API не имеет жесткого rate limiting, но рекомендуется:
- Не более 100 запросов в минуту от одного IP
- Не более 1000 запросов в час от одного пользователя

---

## WebSocket

### Подключение к WebSocket

```javascript
const socket = new WebSocket('ws://localhost:2580/ws');

socket.onopen = () => {
  console.log('WebSocket connected');
};

socket.onmessage = (event) => {
  console.log('Received:', JSON.parse(event.data));
  // Обновить UI с новыми данными
};

socket.onerror = (error) => {
  console.error('WebSocket error:', error);
};

socket.onclose = () => {
  console.log('WebSocket disconnected');
};
```

### События WebSocket

Сервер отправляет уведомления при:
- Создании нового фильма
- Обновлении фильма
- Удалении фильма

**Формат сообщения:**
```json
{
  "type": "MOVIE_CREATED",
  "movieId": 123,
  "timestamp": "2026-01-22T15:30:00"
}
```

---

## Pagination & Filtering

### Общий формат пагинации

```
GET /api/movies?start=0&size=10
```

**Parameters:**
- `start` - Offset (начальная позиция)
- `size` - Limit (количество элементов)

**Response structure:**
```json
{
  "content": [...],
  "totalElements": 100,
  "totalPages": 10,
  "currentPage": 0,
  "pageSize": 10
}
```

---

## Best Practices

1. **Всегда используйте HTTPS** в production
2. **Храните JWT токены безопасно** (HttpOnly cookies или secure storage)
3. **Обновляйте токены** перед истечением срока действия
4. **Обрабатывайте ошибки** gracefully на клиенте
5. **Используйте пагинацию** для больших списков
6. **Кэшируйте результаты** на клиенте где возможно
7. **Не отправляйте чувствительные данные** в query параметрах

---

**Последнее обновление:** 22 января 2026

Для интерактивного тестирования API используйте Swagger UI:
**http://localhost:2580/swagger-ui.html**
