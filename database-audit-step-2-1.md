# Аудит схемы базы данных - Этап 2.1

**Дата:** 22 января 2026  
**Цель:** Анализ текущей схемы БД, проверка целостности, нормализации и подготовка к миграциям

---

## 1. Инвентаризация таблиц

### Основные сущности

| Таблица | Entity класс | Назначение |
|---------|--------------|------------|
| `movies` | `Movie` | Основная таблица фильмов |
| `movie_users` | `User` | Пользователи системы |
| `persons` | `Person` | Режиссеры, сценаристы, операторы |
| `coordinates` | `Coordinates` | Координаты для фильмов |
| `locations` | `Location` | Местоположения персон |
| `notifications` | `Notification` | Уведомления о запросах админ-доступа |
| `importhistory` | `ImportHistory` | История импорта данных |
| `miniofiles` | `MinioFiles` | Файлы в MinIO хранилище |
| `movie_changes` | `MovieChange` | История изменений фильмов |
| `failedrequest` | `FailedRequest` | Журнал неудачных запросов |

### Справочники (Enum)
- `Color` - цвета глаз/волос
- `Country` - страны
- `MovieGenre` - жанры фильмов
- `MpaaRating` - рейтинги MPAA
- `ImportStatus` - статусы импорта

---

## 2. Анализ проблем и рекомендации

### 2.1. Проблемы целостности данных

#### ❌ **Проблема 1: Отсутствие ограничения уникальности email в User**

**Текущее состояние:**
```java
@Table(name = "movie_users")
@Entity
public class User implements UserDetails {
    private String email; // Нет @Column(unique = true)
}
```

**Риски:**
- Возможность создания нескольких пользователей с одинаковым email
- Ошибки при авторизации (какого пользователя выбирать?)
- Нарушение бизнес-логики

**Рекомендация:**
```java
@Column(unique = true, nullable = false, length = 255)
private String email;
```

---

#### ❌ **Проблема 2: Отсутствие foreign key между MinioFiles и ImportHistory**

**Текущее состояние:**
```java
@Entity
public class MinioFiles {
    private long historyId; // Обычное поле, не @ManyToOne
}
```

**Риски:**
- Возможность "висячих" ссылок (historyId указывает на несуществующую запись)
- Невозможность каскадного удаления
- Сложность поддержки ссылочной целостности

**Рекомендация:**
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "history_id", nullable = false, foreignKey = @ForeignKey(name = "fk_minio_files_history"))
private ImportHistory importHistory;
```

---

#### ❌ **Проблема 3: Отсутствие индексов на часто запрашиваемых полях**

**Таблицы без индексов:**
- `movie_users.email` - используется в WHERE при каждом запросе авторизации
- `notifications.user_email` - фильтрация по email
- `notifications.is_approved` - фильтрация по статусу
- `movies.name` - поиск по названию
- `movies.mpaa_rating` - фильтрация по рейтингу
- `movies.genre` - фильтрация по жанру
- `movies.creation_date` - сортировка по дате

**Рекомендация:** Добавить индексы через миграции (этап 2.2)

---

#### ❌ **Проблема 4: Несогласованность nullable полей**

**Location.java:**
```java
@Column(name = "LocationName", nullable = false) // nullable = false в БД
private String name; // Но нет @NotNull валидации
```

**Person.java:**
```java
@NotNull
@Column(name = "PersonEyeColor", nullable = false) // Дублирование
private Color eyeColor;
```

**Рекомендация:**
- Убрать дублирование: `@NotNull` на уровне валидации + `nullable = false` на уровне БД
- Добавить `@NotNull` для Location.name

---

### 2.2. Проблемы нормализации

#### ✅ **Первая нормальная форма (1NF):** Соблюдена
- Все значения атомарны
- Нет повторяющихся групп
- Нет массивов в полях

#### ✅ **Вторая нормальная форма (2NF):** Соблюдена
- Все таблицы имеют первичные ключи
- Нет частичных функциональных зависимостей

#### ⚠️ **Третья нормальная форма (3NF):** Частично нарушена

**Проблема: Дублирование данных Coordinates и Location**

Таблица `coordinates` содержит только `(x, y)`, но используется один раз на фильм.  
Таблица `locations` содержит `(x, y, z, name)`, используется для персон.

**Анализ:**
- Если координаты уникальны для каждого фильма → текущая структура приемлема
- Если несколько фильмов могут иметь одинаковые координаты → потенциальное дублирование

**Рекомендация:**
- Оставить текущую структуру, так как координаты фильма - это его неотъемлемое свойство
- Добавить композитный индекс `(x, y)` для поиска дубликатов

---

#### ⚠️ **Проблема: Person используется для трех ролей**

```java
@ManyToOne
private Person director; // Режиссер

@ManyToOne
private Person screenwriter; // Сценарист

@ManyToOne
private Person operator; // Оператор
```

**Анализ:**
- Одна и та же персона может быть режиссером одного фильма и сценаристом другого
- Текущая структура позволяет переиспользование Person → корректно
- Нарушений 3NF нет

**Рекомендация:** Оставить как есть, добавить индекс на `persons.name`

---

### 2.3. Проблемы типов данных

#### ❌ **Проблема 1: Несогласованность типов ID**

```java
// Movie.java
private long id; // примитив long

// User.java
private Long id; // обертка Long

// Person.java
private long id; // примитив long
```

**Рекомендация:**
- Использовать `Long` везде для возможности значения `null` и совместимости с JPA
- Примитивы не могут быть `null`, что может вызвать `NullPointerException`

---

#### ❌ **Проблема 2: Неоптимальные типы для дробных чисел**

```java
// Movie.java
private Float budget; // Обертка Float (32 бита)
private Double usaBoxOffice; // Обертка Double (64 бита)

// Person.java
private float height; // Примитив float (32 бита)

// Location.java
private Double x; // Обертка Double
private float z; // Примитив float
```

**Проблемы:**
- Несогласованность между `Float`/`Double` и `float`/`double`
- Для денежных сумм (budget, boxOffice) лучше использовать `BigDecimal` для точности

**Рекомендация:**
```java
@Column(precision = 15, scale = 2)
private BigDecimal budget;

@Column(precision = 15, scale = 2)
private BigDecimal usaBoxOffice;
```

---

#### ❌ **Проблема 3: Отсутствие ограничений длины строк**

```java
// User.java
private String email; // Нет length
private String password; // Нет length

// Movie.java
private String name; // Только @Size(min = 1)
private String tagline; // Нет length

// Notification.java
private String userEmail; // Нет length
```

**Рекомендация:**
```java
@Column(length = 255, unique = true, nullable = false)
private String email;

@Column(length = 255, nullable = false)
private String password; // BCrypt hash обычно 60 символов

@Column(length = 500, nullable = false)
private String name;

@Column(length = 1000, nullable = false)
private String tagline;
```

---

### 2.4. Отсутствие аудита

#### ❌ **Проблема: Нет полей created_at, updated_at, created_by, updated_by**

**Текущее состояние:**
- Только `Movie` имеет `creationDate`
- Остальные таблицы не отслеживают время создания/изменения
- Нет информации о том, кто внес изменения

**Рекомендация:** Создать базовый класс `Auditable`:

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;
    
    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;
    
    // getters/setters
}
```

**Применить к сущностям:**
```java
@Entity
public class Movie extends Auditable {
    // existing fields
}

@Entity
public class User extends Auditable implements UserDetails {
    // existing fields
}
```

---

### 2.5. Проблемы каскадных операций

#### ⚠️ **Проблема: Потенциальные неожиданные каскады**

```java
@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
@JoinColumn(name = "person_id")
private Person director;
```

**Анализ:**
- `CascadeType.PERSIST` - при сохранении фильма автоматически сохраняется новый Person
- `CascadeType.MERGE` - при обновлении фильма обновляется Person

**Потенциальная проблема:**
- Если один Person связан с несколькими Movie как режиссер
- Изменение Person через один Movie повлияет на все фильмы

**Рекомендация:**
- Убрать `CascadeType.MERGE` для Person/Coordinates/Location
- Управлять ими отдельно через соответствующие сервисы
- Оставить только `CascadeType.PERSIST` для удобства создания

```java
@ManyToOne(cascade = CascadeType.PERSIST)
@JoinColumn(name = "person_id", foreignKey = @ForeignKey(name = "fk_movie_director"))
private Person director;
```

---

## 3. Проверка каскадных удалений

### ❌ **Проблема: Нет стратегии onDelete для критических связей**

**Movie → User (creator):**
```java
@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
@JoinColumn(name = "creator_id", nullable = false)
private User creator;
```

**Вопрос:** Что делать при удалении пользователя?
- **Вариант 1:** Запретить удаление (ON DELETE RESTRICT) - если есть фильмы
- **Вариант 2:** Каскадное удаление фильмов (ON DELETE CASCADE) - потеря данных
- **Вариант 3:** Установить NULL (ON DELETE SET NULL) - но `nullable = false`

**Рекомендация:**
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(
    name = "creator_id", 
    nullable = false,
    foreignKey = @ForeignKey(
        name = "fk_movie_creator",
        foreignKeyDefinition = "FOREIGN KEY (creator_id) REFERENCES movie_users(id) ON DELETE RESTRICT"
    )
)
private User creator;
```

---

## 4. Рекомендации по оптимизации

### 4.1. Fetch стратегии

**Текущее:** Все связи по умолчанию `FetchType.EAGER` для `@ManyToOne`

**Проблема:** N+1 запросы при загрузке списка фильмов

```java
// Запрос 1: SELECT * FROM movies
// Запрос 2: SELECT * FROM users WHERE id = ?
// Запрос 3: SELECT * FROM coordinates WHERE id = ?
// ... и так для каждого фильма
```

**Рекомендация:**
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "creator_id", nullable = false)
private User creator;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "coordinates_id")
private Coordinates coordinates;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "person_id")
private Person director;
```

И использовать `@EntityGraph` в репозиториях для явной загрузки:

```java
@EntityGraph(attributePaths = {"creator", "coordinates", "director"})
List<Movie> findAllWithDetails();
```

---

### 4.2. Индексы (для этапа 2.2)

| Таблица | Поле | Тип индекса | Причина |
|---------|------|-------------|---------|
| `movie_users` | `email` | UNIQUE INDEX | Уникальность + быстрый поиск при авторизации |
| `notifications` | `user_email` | INDEX | Фильтрация уведомлений по пользователю |
| `notifications` | `is_approved` | INDEX | Фильтрация по статусу |
| `movies` | `name` | INDEX | Поиск фильмов по названию |
| `movies` | `creation_date` | INDEX | Сортировка по дате |
| `movies` | `mpaa_rating` | INDEX | Фильтрация по рейтингу |
| `movies` | `genre` | INDEX | Фильтрация по жанру |
| `movies` | `creator_id` | INDEX (FK) | Поиск фильмов пользователя |
| `persons` | `name` | INDEX | Поиск персон по имени |
| `coordinates` | `(x, y)` | COMPOSITE INDEX | Поиск дубликатов координат |
| `import_history` | `username` | INDEX | Фильтрация истории по пользователю |
| `minio_files` | `history_id` | INDEX (FK) | Join с import_history |

---

## 5. План изменений для следующих этапов

### Этап 2.2: Настройка Flyway/Liquibase и создание миграций

**Миграция V1:** Baseline - текущее состояние БД

**Миграция V2:** Исправление типов данных
- Изменить `long id` → `Long id` (уже сделано в коде, отразить в БД)
- Изменить `Float budget` → `BigDecimal budget`
- Изменить `Double usaBoxOffice` → `BigDecimal usaBoxOffice`
- Добавить ограничения длины для строк

**Миграция V3:** Добавление ограничений целостности
- `ALTER TABLE movie_users ADD CONSTRAINT uk_email UNIQUE (email);`
- `ALTER TABLE movie_users ALTER COLUMN email SET NOT NULL;`
- `ALTER TABLE minio_files ADD CONSTRAINT fk_minio_history FOREIGN KEY (history_id) REFERENCES importhistory(id);`

**Миграция V4:** Добавление аудит-полей
- Добавить `created_at`, `updated_at`, `created_by`, `updated_by` во все таблицы
- Установить значения по умолчанию для существующих записей

**Миграция V5:** Создание индексов
- Создать все индексы из таблицы 4.2

**Миграция V6:** Оптимизация foreign keys
- Добавить именованные `@ForeignKey` с правильными `ON DELETE` стратегиями

---

## 6. Итоговая оценка

### ✅ Сильные стороны текущей схемы:
- Корректная нормализация (соблюдение 3NF)
- Использование ENUM для справочников
- Разделение сущностей (Person, Coordinates, Location)
- Наличие истории изменений (MovieChange)

### ❌ Проблемы, требующие исправления:
1. Отсутствие UNIQUE на `movie_users.email`
2. Нет foreign key для `MinioFiles.historyId`
3. Отсутствие индексов на критических полях
4. Несогласованность типов данных (long/Long, Float/Double)
5. Отсутствие аудит-полей (created_at, updated_at)
6. Неоптимальные fetch стратегии (Eager везде)
7. Нет явных ограничений длины строк
8. Отсутствие ON DELETE стратегий

### 📊 Приоритет исправлений:
- **Критический:** Unique на email, foreign keys
- **Высокий:** Индексы, fetch стратегии
- **Средний:** Аудит-поля, типы данных
- **Низкий:** ON DELETE стратегии (зависит от бизнес-логики)

---

## 7. Следующие шаги

1. ✅ **Этап 2.1 завершен:** Аудит выполнен, проблемы документированы
2. **Этап 2.2:** Настроить Flyway, создать миграции для исправления проблем
3. **Этап 2.3:** Добавить индексы и протестировать производительность
4. **Этап 2.4:** Внедрить аудит через `@EntityListeners`
5. **Этап 2.5:** Оптимизировать запросы через `@EntityGraph` и `@Query`

---

**Документ создан:** 22 января 2026  
**Ответственный:** Команда рефакторинга  
**Статус:** ✅ Готов к переходу на этап 2.2
