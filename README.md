# Warehouse Management System

## 📝 Описание
CRUD-приложение для управления товарами на складе с использованием:
- Spring Boot 3
- PostgreSQL/H2 Database
- Docker

## 🚀 Запуск приложения

### Вариант 1: С PostgreSQL (Docker)

```bash
# 1. Остановите существующие контейнеры (если есть)
docker-compose down

# 2. Удалите старые данные (опционально)
docker volume rm warehouse_pgdata

# 3. Соберите и запустите контейнеры
docker-compose up --build

# 4. Проверьте работу API
curl http://localhost:8080/api/products

# 5. Доступ к PostgreSQL (в другом терминале)
docker-compose exec db psql -U postgres warehouse

# В psql выполните:
\dt             # Показать таблицы
SELECT * FROM products;  # Просмотр товаров
```

### Вариант 2: С H2 (для разработки)

```bash
# 1. Запустите приложение с профилем 'local'
./gradlew bootRun --args='--spring.profiles.active=local'

# 2. Доступ к H2 Console:
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:file:./movchandb
User: sa
Password: (оставьте пустым)
```

## 🌐 API Endpoints

### Товары
- `GET    /api/products` - Получить все товары
- `POST   /api/products` - Создать товар
- `GET    /api/products/{id}` - Получить товар по ID
- `PUT    /api/products/{id}` - Обновить товар
- `DELETE /api/products/{id}` - Удалить товар

Пример запроса:
```bash
curl -X POST -H "Content-Type: application/json" -d '{
  "name": "MacBook Pro",
  "article": "MBP2023",
  "category": "ELECTRONICS",
  "price": 2999.99,
  "quantity": 10
}' http://localhost:8080/api/products
```

## 🛠 Технические детали

### Конфигурация БД
- **PostgreSQL** (production):
  ```properties
  spring.datasource.url=jdbc:postgresql://db:5432/warehouse
  spring.datasource.username=postgres
  spring.datasource.password=password
  ```

- **H2** (development):
  ```properties
  spring.datasource.url=jdbc:h2:file:./movchandb
  spring.datasource.driver-class-name=org.h2.Driver
  spring.h2.console.enabled=true
  ```

### Docker
- **Порт приложения**: 8080
- **Порт PostgreSQL**: 5432
- **Volume для данных**: warehouse_pgdata

## 🧪 Тестирование
```bash
# Запуск тестов
./gradlew test

# Генерация Javadoc
./gradlew javadoc
open build/docs/javadoc/index.html
# Результат: build/docs/javadoc/
```

## 🔧 Устранение проблем

### Если порт 8080 занят:
```bash
# Найти процесс
lsof -i :8080
# Остановить процесс
kill -9 <PID>
```

### Если таблицы не создаются:
1. Проверьте логи Spring Boot:
```bash
docker-compose logs app | grep "SQL create"
```
2. Убедитесь, что в настройках:
```properties
spring.jpa.hibernate.ddl-auto=update
```

## 🧪 Swagger
# Запуск
http://localhost:8080/swagger-ui/index.html