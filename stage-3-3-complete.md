# Этап 3.3: Управление конфигурацией - Завершено

**Дата:** 22 января 2026  
**Статус:** ✅ Выполнено

## Выполненные задачи

### 1. Создан .env.example - Шаблон переменных окружения

Создан подробный шаблон со всеми переменными окружения приложения, сгруппированными по категориям:

#### Категории переменных:

**Server Configuration:**
- `SERVER_PORT` - Порт приложения (default: 2580)
- `SPRING_PROFILES_ACTIVE` - Активный профиль (dev/test/prod)

**Database (PostgreSQL):**
- `DB_URL` / `SPRING_DATASOURCE_URL` - URL подключения к БД
- `DB_USER` / `SPRING_DATASOURCE_USERNAME` - Пользователь БД
- `DB_PASSWORD` / `SPRING_DATASOURCE_PASSWORD` - Пароль БД
- `HIBERNATE_DDL_AUTO` - Режим DDL (update/validate/none)
- `HIBERNATE_SHOW_SQL` - Показывать SQL запросы в логах

**Redis:**
- `REDIS_HOST` / `SPRING_DATA_REDIS_HOST` - Хост Redis
- `REDIS_PORT` / `SPRING_DATA_REDIS_PORT` - Порт Redis
- `REDIS_PASSWORD` - Пароль Redis (опционально)
- `REDIS_TIMEOUT` - Таймаут подключения
- `REDIS_POOL_*` - Настройки connection pool

**MinIO (Object Storage):**
- `MINIO_ENDPOINT` - URL эндпоинта MinIO
- `MINIO_ACCESS_KEY` - Access key (username)
- `MINIO_SECRET_KEY` - Secret key (password)
- `MINIO_BUCKET_NAME` - Название bucket для файлов

**JWT Authentication:**
- `JWT_SECRET` - Секретный ключ для подписи токенов (минимум 256 бит)
- `JWT_EXPIRATION` - Время жизни токена в миллисекундах

**CORS:**
- `CORS_ALLOWED_ORIGINS` - Разрешенные origins (через запятую)

**File Upload:**
- `MULTIPART_ENABLED` - Включить загрузку файлов
- `MULTIPART_MAX_FILE_SIZE` - Максимальный размер файла
- `MULTIPART_MAX_REQUEST_SIZE` - Максимальный размер запроса

**Logging:**
- `LOG_LEVEL_APP` - Уровень логирования приложения
- `LOG_LEVEL_SECURITY` - Уровень логирования Spring Security
- `LOG_LEVEL_HIBERNATE` - Уровень логирования Hibernate

**Actuator (Health Checks):**
- `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE` - Какие endpoints экспонировать
- `MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS` - Детальность health информации

#### Особенности .env.example:

✅ **Детальная документация** каждой переменной с комментариями  
✅ **Значения по умолчанию** для быстрого старта  
✅ **Примеры** для разных окружений (dev/docker/production)  
✅ **Production Security Checklist** - чеклист безопасности перед деплоем  
✅ **Группировка** по функциональным категориям  
✅ **Генерация секретов** - команды для создания безопасных ключей

### 2. Обновлен application.yml

Все хардкод значения заменены на паттерн `${ENV_VAR:default}`:

#### Поддержка двойной конфигурации:

Для совместимости с Docker и стандартными Spring Boot переменными используется fallback паттерн:

```yaml
datasource:
  url: ${SPRING_DATASOURCE_URL:${DB_URL:jdbc:postgresql://localhost:5434/is}}
  username: ${SPRING_DATASOURCE_USERNAME:${DB_USER:is}}
  password: ${SPRING_DATASOURCE_PASSWORD:${DB_PASSWORD:is}}
```

Это позволяет использовать:
- **Docker переменные:** `SPRING_DATASOURCE_URL`, `SPRING_DATA_REDIS_HOST`
- **Короткие переменные:** `DB_URL`, `REDIS_HOST`
- **Значения по умолчанию** если переменные не заданы

#### Что выведено в переменные:

**Before:**
```yaml
jpa:
  hibernate:
    ddl-auto: update  # Хардкод
  show-sql: true      # Хардкод

data:
  redis:
    timeout: 2000     # Хардкод
    lettuce:
      pool:
        max-active: 8 # Хардкод
```

**After:**
```yaml
jpa:
  hibernate:
    ddl-auto: ${HIBERNATE_DDL_AUTO:update}
  show-sql: ${HIBERNATE_SHOW_SQL:true}

data:
  redis:
    timeout: ${REDIS_TIMEOUT:2000}
    lettuce:
      pool:
        max-active: ${REDIS_POOL_MAX_ACTIVE:8}
```

#### Добавлены новые секции:

**Actuator Configuration:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: ${MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE:health,info,metrics}
  endpoint:
    health:
      show-details: ${MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS:always}
```

**Logging Configuration:**
```yaml
logging:
  level:
    itmo.app: ${LOG_LEVEL_APP:DEBUG}
    org.springframework.security: ${LOG_LEVEL_SECURITY:DEBUG}
    org.hibernate: ${LOG_LEVEL_HIBERNATE:INFO}
```

### 3. Создан docker-compose.override.yml

Файл автоматически применяется при запуске `docker-compose up` и переопределяет настройки для локальной разработки.

#### Ключевые возможности:

**Development-friendly настройки:**
```yaml
app:
  environment:
    SPRING_PROFILES_ACTIVE: dev
    LOG_LEVEL_APP: DEBUG
    LOG_LEVEL_SECURITY: DEBUG
    HIBERNATE_SHOW_SQL: "true"
    HIBERNATE_DDL_AUTO: update
    MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS: always
```

**Remote Debugging:**
```yaml
ports:
  - "2580:2580"
  - "5005:5005"  # Remote debugging port

command: >
  java
  -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
  ...
```

**Дополнительные инструменты разработки:**

1. **pgAdmin** - Веб-интерфейс для PostgreSQL
   - URL: http://localhost:5050
   - Login: admin@admin.com
   - Password: admin

2. **Redis Commander** - Веб-интерфейс для Redis
   - URL: http://localhost:8081

#### Как использовать:

```bash
# Запуск с override (по умолчанию)
docker-compose up -d

# Запуск БЕЗ override (production-like)
docker-compose -f docker-compose.yml up -d

# Явное указание обоих файлов
docker-compose -f docker-compose.yml -f docker-compose.override.yml up -d
```

### 4. Обновлен .gitignore

Добавлена секция для управления .env файлами:

```gitignore
### Environment Variables ###
.env
.env.local
.env.*.local
src/main/resources/env.properties
# Keep .env.example in version control
!.env.example
```

**Правила:**
- ✅ `.env.example` **коммитится** в репозиторий (шаблон)
- ❌ `.env` **игнорируется** (локальные значения)
- ❌ `.env.local` **игнорируется** (локальные переопределения)
- ❌ `env.properties` **игнорируется** (устаревший файл)

## Сравнение: До vs После

### Управление конфигурацией

| Аспект | До | После |
|--------|----|----|
| **Конфигурация БД** | Хардкод в application.yml | Переменные окружения |
| **Секреты** | В коде (JWT_SECRET) | Переменные окружения |
| **Документация** | ❌ Нет | ✅ .env.example с описаниями |
| **Разные окружения** | Копирование файлов | Изменение .env |
| **Docker vs Local** | Конфликты | Поддержка обеих конфигураций |
| **Development tools** | ❌ Нет | ✅ pgAdmin, Redis Commander |
| **Remote debugging** | ❌ Нет | ✅ Порт 5005 |
| **Безопасность** | Секреты в репозитории | .env в .gitignore |

### Файловая структура

**До:**
```
src/main/resources/
  ├── application.yml        (хардкод значения)
  ├── application.properties (дублирование)
  └── env.properties         (в репозитории)
```

**После:**
```
├── .env.example              (шаблон с документацией)
├── .env                      (локальный, в .gitignore)
├── docker-compose.yml        (production)
├── docker-compose.override.yml (development)
└── src/main/resources/
    └── application.yml       (только ${ENV_VAR:default})
```

## Использование

### Для локальной разработки (без Docker)

1. Создать локальный `.env` файл:
```bash
cp .env.example .env
```

2. Настроить переменные для локального окружения:
```env
SERVER_PORT=2580
DB_URL=jdbc:postgresql://localhost:5434/is
REDIS_HOST=localhost
MINIO_ENDPOINT=http://127.0.0.1:9000
```

3. Запустить приложение (Spring Boot автоматически подхватит .env через application.yml)

### Для разработки с Docker

1. Запустить все сервисы:
```bash
docker-compose up -d
```

2. Автоматически применяется `docker-compose.override.yml` с:
   - pgAdmin на http://localhost:5050
   - Redis Commander на http://localhost:8081
   - Remote debugging на порту 5005

### Для Production

1. Создать `.env` файл на сервере с production значениями:
```env
# ВАЖНО: Изменить все секреты!
DB_PASSWORD=strong-production-password
JWT_SECRET=$(openssl rand -hex 32)
MINIO_SECRET_KEY=production-minio-secret
HIBERNATE_DDL_AUTO=validate
HIBERNATE_SHOW_SQL=false
LOG_LEVEL_APP=INFO
CORS_ALLOWED_ORIGINS=https://yourdomain.com
```

2. Запустить БЕЗ override файла:
```bash
docker-compose -f docker-compose.yml up -d
```

3. Или использовать специфический production compose:
```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

## Production Security Checklist

Перед деплоем на production убедитесь:

- [ ] Изменен `DB_PASSWORD` на strong password
- [ ] Сгенерирован новый `JWT_SECRET` (минимум 256 бит)
  ```bash
  openssl rand -hex 32
  ```
- [ ] Изменены `MINIO_ACCESS_KEY` и `MINIO_SECRET_KEY`
- [ ] Установлен `HIBERNATE_DDL_AUTO=validate` или `none`
- [ ] Отключен `HIBERNATE_SHOW_SQL=false`
- [ ] Уровни логирования: `LOG_LEVEL_APP=INFO`, `LOG_LEVEL_SECURITY=WARN`
- [ ] Обновлен `CORS_ALLOWED_ORIGINS` на production домен
- [ ] Установлен `MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=when-authorized`
- [ ] Файл `.env` добавлен в `.gitignore` (не коммитить!)
- [ ] Использован secrets manager (AWS Secrets Manager, HashiCorp Vault, Kubernetes Secrets)

## Преимущества нового подхода

### 1. Безопасность
✅ Секреты не попадают в репозиторий  
✅ Разные секреты для разных окружений  
✅ Production checklist для предотвращения утечек

### 2. Гибкость
✅ Одна кодовая база для dev/test/prod  
✅ Легкое переключение между окружениями  
✅ Поддержка Docker и локального запуска

### 3. Документация
✅ Полное описание всех переменных в `.env.example`  
✅ Примеры значений для разных окружений  
✅ Встроенные комментарии с рекомендациями

### 4. Developer Experience
✅ `docker-compose.override.yml` с готовыми инструментами  
✅ Remote debugging из коробки  
✅ pgAdmin и Redis Commander для удобства

### 5. Соответствие 12-Factor App
✅ [III. Config](https://12factor.net/config) - Store config in the environment  
✅ Строгое разделение конфигурации и кода  
✅ Одна кодовая база, множество деплоев

## Метрики

| Метрика | Значение |
|---------|----------|
| Переменных окружения | 25+ |
| Категорий конфигурации | 9 |
| Строк документации в .env.example | 200+ |
| Production security checklist items | 9 |
| Дополнительных dev tools | 2 (pgAdmin, Redis Commander) |

## Следующие шаги

Этап 3.3 **завершен** согласно плану. Готовы к:

- **Этап 3.4**: Документация API (Swagger/OpenAPI аннотации)

## Дополнительные улучшения (опционально)

В будущем можно добавить:

- [ ] Spring Cloud Config Server для централизованной конфигурации
- [ ] HashiCorp Vault интеграция для secrets management
- [ ] Environment-specific профили (application-dev.yml, application-prod.yml)
- [ ] Encrypted properties с Jasypt
- [ ] Configuration validation на старте приложения
- [ ] Metrics и мониторинг конфигурации через Actuator

---

**Итог:** Вся конфигурация вынесена в переменные окружения с подробной документацией в `.env.example`. Созданы удобные инструменты для разработки через `docker-compose.override.yml`. Приложение готово к деплою в любое окружение путем простой смены переменных окружения.
