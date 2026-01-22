# Deployment Guide

Руководство по развертыванию Movie Database Management System в различных окружениях.

## Содержание

- [Локальная разработка](#локальная-разработка)
- [Docker Development](#docker-development)
- [Production Deployment](#production-deployment)
- [Cloud Deployment](#cloud-deployment)
- [Troubleshooting](#troubleshooting)

---

## Локальная разработка

### Требования

- Java 17 или выше
- Node.js 20+ и npm 10+
- PostgreSQL 16
- Redis (latest)
- MinIO
- Gradle 8.x (или используйте wrapper)

### Шаги

1. **Клонировать репозиторий:**
```bash
git clone <repository-url>
cd app
```

2. **Настроить базу данных PostgreSQL:**
```bash
createdb is
```

3. **Настроить переменные окружения:**
```bash
cp .env.example .env
```

Отредактировать `.env`:
```env
DB_URL=jdbc:postgresql://localhost:5434/is
DB_USER=is
DB_PASSWORD=is
REDIS_HOST=localhost
MINIO_ENDPOINT=http://127.0.0.1:9000
```

4. **Запустить инфраструктурные сервисы через Docker:**
```bash
docker-compose up -d postgres redis minio
```

5. **Собрать и запустить backend:**
```bash
./gradlew bootRun
```

6. **Установить зависимости и запустить frontend:**
```bash
cd frontend
npm install
npm start
```

7. **Открыть приложение:**
- Frontend: http://localhost:4200
- Backend API: http://localhost:2580
- Swagger UI: http://localhost:2580/swagger-ui.html

---

## Docker Development

Самый простой способ для разработки с полным окружением.

### Требования

- Docker 20.10+
- Docker Compose 2.0+

### Шаги

1. **Клонировать репозиторий:**
```bash
git clone <repository-url>
cd app
```

2. **Запустить все сервисы:**
```bash
docker-compose up -d
```

Это запускает:
- ✅ PostgreSQL 16 (порт 5434)
- ✅ Redis (порт 6379)
- ✅ MinIO (порт 9000, консоль 9001)
- ✅ Backend приложение (порт 2580)
- ✅ pgAdmin (порт 5050)
- ✅ Redis Commander (порт 8081)

3. **Проверить статус:**
```bash
docker-compose ps
```

Все сервисы должны быть в статусе `healthy` или `running`.

4. **Просмотреть логи:**
```bash
# Все сервисы
docker-compose logs -f

# Только backend
docker-compose logs -f app

# Только база данных
docker-compose logs -f postgres
```

5. **Остановить сервисы:**
```bash
docker-compose down
```

6. **Полная очистка (включая volumes):**
```bash
docker-compose down -v
```

### Дополнительные инструменты в Docker

**pgAdmin (Управление PostgreSQL):**
- URL: http://localhost:5050
- Email: admin@admin.com
- Password: admin
- Добавить сервер:
  - Host: postgres
  - Port: 5432
  - Database: is
  - Username: is
  - Password: is

**Redis Commander:**
- URL: http://localhost:8081
- Автоматически подключен к Redis

**MinIO Console:**
- URL: http://localhost:9001
- Username: minioadmin
- Password: minioadmin

---

## Production Deployment

### Требования

- Docker & Docker Compose ИЛИ
- Java 17+ runtime
- PostgreSQL 16
- Redis
- MinIO
- Reverse proxy (Nginx/Traefik)
- SSL сертификат

### Вариант 1: Docker Compose (Рекомендуется)

#### 1. Подготовка сервера

```bash
# Обновить систему
sudo apt update && sudo apt upgrade -y

# Установить Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Установить Docker Compose
sudo apt install docker-compose-plugin -y
```

#### 2. Настроить production переменные

Создать `.env` файл:
```env
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/is
SPRING_DATASOURCE_USERNAME=is
SPRING_DATASOURCE_PASSWORD=<STRONG_PASSWORD_HERE>

# Redis
SPRING_DATA_REDIS_HOST=redis
SPRING_DATA_REDIS_PORT=6379

# MinIO
MINIO_ENDPOINT=http://minio:9000
MINIO_ACCESS_KEY=<CHANGE_ME>
MINIO_SECRET_KEY=<STRONG_SECRET_HERE>
MINIO_BUCKET_NAME=movies

# JWT
JWT_SECRET=<GENERATE_WITH_openssl_rand_-hex_32>
JWT_EXPIRATION=86400000

# CORS
CORS_ALLOWED_ORIGINS=https://yourdomain.com

# Spring
SPRING_PROFILES_ACTIVE=prod
HIBERNATE_DDL_AUTO=validate
HIBERNATE_SHOW_SQL=false
LOG_LEVEL_APP=INFO
LOG_LEVEL_SECURITY=WARN
```

**ВАЖНО:** Сгенерировать безопасные секреты:
```bash
# JWT Secret (256 бит)
openssl rand -hex 32

# MinIO Secret Key
openssl rand -base64 32
```

#### 3. Запустить production stack

Запустить БЕЗ `docker-compose.override.yml` (без development tools):
```bash
docker-compose -f docker-compose.yml up -d
```

#### 4. Проверить здоровье

```bash
# Проверить статус контейнеров
docker-compose ps

# Проверить health endpoint
curl http://localhost:2580/actuator/health
```

#### 5. Настроить Nginx reverse proxy

Создать `/etc/nginx/sites-available/movie-app`:
```nginx
server {
    listen 80;
    server_name yourdomain.com;

    # Redirect to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com;

    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;

    # Backend API
    location /api/ {
        proxy_pass http://localhost:2580;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # WebSocket
    location /ws {
        proxy_pass http://localhost:2580;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # Frontend
    location / {
        root /var/www/movie-app;
        try_files $uri $uri/ /index.html;
    }
}
```

Активировать:
```bash
sudo ln -s /etc/nginx/sites-available/movie-app /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### Вариант 2: Standalone JAR

#### 1. Собрать приложение

```bash
./gradlew bootJar
```

JAR файл: `build/libs/app-0.0.1-SNAPSHOT.jar`

#### 2. Создать systemd service

Создать `/etc/systemd/system/movie-app.service`:
```ini
[Unit]
Description=Movie Database Application
After=network.target postgresql.service redis.service

[Service]
Type=simple
User=movieapp
WorkingDirectory=/opt/movie-app
ExecStart=/usr/bin/java -jar /opt/movie-app/app.jar
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=movie-app

Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="DB_URL=jdbc:postgresql://localhost:5432/is"
Environment="DB_USER=is"
Environment="DB_PASSWORD=<PASSWORD>"
Environment="REDIS_HOST=localhost"
Environment="JWT_SECRET=<SECRET>"

[Install]
WantedBy=multi-user.target
```

#### 3. Запустить сервис

```bash
sudo systemctl daemon-reload
sudo systemctl enable movie-app
sudo systemctl start movie-app
sudo systemctl status movie-app
```

---

## Cloud Deployment

### AWS (Amazon Web Services)

#### Архитектура

- **ECS Fargate** - контейнеры приложения
- **RDS PostgreSQL** - управляемая БД
- **ElastiCache Redis** - управляемый кэш
- **S3** - хранилище файлов (вместо MinIO)
- **ALB** - балансировщик нагрузки
- **Route 53** - DNS
- **ACM** - SSL сертификаты

#### Шаги

1. **Создать RDS PostgreSQL:**
```bash
aws rds create-db-instance \
  --db-instance-identifier movie-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --engine-version 16 \
  --master-username admin \
  --master-user-password <PASSWORD> \
  --allocated-storage 20
```

2. **Создать ElastiCache Redis:**
```bash
aws elasticache create-cache-cluster \
  --cache-cluster-id movie-cache \
  --cache-node-type cache.t3.micro \
  --engine redis \
  --num-cache-nodes 1
```

3. **Создать S3 bucket:**
```bash
aws s3 mb s3://movie-app-files
```

4. **Отправить Docker image в ECR:**
```bash
# Создать репозиторий
aws ecr create-repository --repository-name movie-app

# Login
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin <account>.dkr.ecr.us-east-1.amazonaws.com

# Build и push
docker build -t movie-app .
docker tag movie-app:latest <account>.dkr.ecr.us-east-1.amazonaws.com/movie-app:latest
docker push <account>.dkr.ecr.us-east-1.amazonaws.com/movie-app:latest
```

5. **Создать ECS Task Definition** и **Service** через AWS Console или Terraform.

### Azure

TODO: Добавить инструкции для Azure App Service

### Google Cloud Platform

TODO: Добавить инструкции для GKE

---

## Troubleshooting

### Проблема: Приложение не запускается

**Проверить логи:**
```bash
docker-compose logs app
```

**Частые причины:**
- База данных не готова → проверить `docker-compose logs postgres`
- Неверные переменные окружения → проверить `.env`
- Порты заняты → проверить `netstat -tulpn | grep :2580`

### Проблема: База данных недоступна

```bash
# Проверить статус
docker-compose ps postgres

# Подключиться к PostgreSQL
docker-compose exec postgres psql -U is -d is

# Проверить логи
docker-compose logs postgres
```

### Проблема: Redis connection timeout

```bash
# Проверить Redis
docker-compose exec redis redis-cli ping

# Должен вернуть: PONG
```

### Проблема: MinIO недоступен

```bash
# Проверить MinIO
curl http://localhost:9000/minio/health/live

# Проверить логи
docker-compose logs minio
```

### Проблема: Frontend не может подключиться к backend

**Проверить CORS настройки:**
```env
CORS_ALLOWED_ORIGINS=http://localhost:4200
```

**Проверить proxy конфигурацию** в `frontend/proxy.conf.json`:
```json
{
  "/api": {
    "target": "http://localhost:2580",
    "secure": false
  }
}
```

### Проблема: Health check fails

```bash
# Проверить health endpoint
curl http://localhost:2580/actuator/health

# Проверить подробности
curl http://localhost:2580/actuator/health | jq
```

### Проблема: JWT токен не работает

- Проверить что `JWT_SECRET` одинаковый на всех инстансах
- Проверить время жизни токена `JWT_EXPIRATION`
- Проверить формат заголовка: `Authorization: Bearer <token>`

### Очистка и пересоздание

**Полная пересборка:**
```bash
# Остановить все
docker-compose down -v

# Удалить images
docker rmi $(docker images -q movie-app)

# Очистить build cache
docker builder prune -a

# Пересобрать
docker-compose build --no-cache
docker-compose up -d
```

---

## Мониторинг

### Логирование

**Docker Compose:**
```bash
# Real-time logs
docker-compose logs -f

# Last 100 lines
docker-compose logs --tail=100
```

**Production (systemd):**
```bash
journalctl -u movie-app -f
```

### Метрики

**Actuator endpoints:**
- Health: http://localhost:2580/actuator/health
- Metrics: http://localhost:2580/actuator/metrics
- Info: http://localhost:2580/actuator/info

### Backup

**PostgreSQL backup:**
```bash
docker-compose exec postgres pg_dump -U is is > backup.sql
```

**Restore:**
```bash
docker-compose exec -T postgres psql -U is is < backup.sql
```

---

## Безопасность

### Production Checklist

- [ ] Изменены все default пароли
- [ ] JWT_SECRET сгенерирован безопасно (256+ бит)
- [ ] HTTPS включен (SSL сертификат)
- [ ] CORS ограничен production доменом
- [ ] Database password надежный
- [ ] MinIO credentials изменены
- [ ] Firewall настроен (только 80/443 открыты)
- [ ] Регулярные backup настроены
- [ ] Логирование настроено
- [ ] Мониторинг настроен
- [ ] `.env` НЕ в git репозитории

---

**Последнее обновление:** 22 января 2026
