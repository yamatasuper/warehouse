# 🏭 Warehouse Management System

## 📖 Описание

CRUD-приложение для управления товарами на складе с использованием современных технологий:

- **Spring Boot 3** - основной фреймворк приложения
- **PostgreSQL/H2 Database** - системы хранения данных
- **Docker** - контейнеризация приложения
- **Kubernetes** - оркестрация контейнеров
- **Kafka** - асинхронная обработка сообщений
- **Camunda/Zeebe** - workflow orchestration
- **AWS S3/MinIO** - хранение изображений товаров

---

## 🚀 Быстрый старт

### Вариант 1: Запуск с PostgreSQL (Docker Compose)

```bash
# 1. Остановите существующие контейнеры (если есть)
docker-compose down

# 2. Удалите старые данные (опционально)
docker volume rm warehouse_pgdata

# 3. Соберите и запустите контейнеры
docker-compose up --build

# 4. Проверьте работу API
curl http://localhost:8082/api/products

# 5. Доступ к PostgreSQL (в другом терминале)
docker-compose exec db psql -U postgres warehouse

# В psql выполните:
\dt                    # Показать таблицы
SELECT * FROM products; # Просмотр товаров
```

### Вариант 2: Запуск с H2 (для разработки)

```bash
# 1. Запустите приложение с профилем 'local'
./gradlew bootRun --args='--spring.profiles.active=local'

# 2. Доступ к H2 Console:
#    URL: http://localhost:8082/h2-console
#    JDBC URL: jdbc:h2:file:./movchandb
#    User: sa
#    Password: (оставьте пустым)
```

---

## ☸️ Kubernetes развертывание

```bash
# Используйте скрипт деплоя
./deploy.sh

# Или вручную:
kubectl apply -f namespace.yaml
kubectl apply -f pvc.yaml
kubectl apply -f deployments.yaml
kubectl apply -f services.yaml
kubectl apply -f ingress.yaml

# Проброс портов для доступа
kubectl port-forward -n warehouse-app svc/warehouse-app 8080:8080
```

---

## 📡 API Endpoints

### 🎯 Товары

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| `GET` | `/api/products` | Получить все товары |
| `POST` | `/api/products` | Создать товар |
| `GET` | `/api/products/{id}` | Получить товар по ID |
| `PUT` | `/api/products/{id}` | Обновить товар |
| `DELETE` | `/api/products/{id}` | Удалить товар |

### 📦 Заказы

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| `POST` | `/api/orders` | Создать заказ |
| `GET` | `/api/orders/{orderId}` | Получить заказ по ID |
| `DELETE` | `/api/orders/{orderId}` | Отменить заказ |
| `PATCH` | `/api/orders/{orderId}/status` | Обновить статус заказа |
| `POST` | `/api/orders/confirm/{orderId}` | Запустить процесс подтверждения заказа |

### 🖼️ Изображения товаров

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| `GET` | `/api/products/{productId}/images` | Получить изображения товара |
| `POST` | `/api/dev/seed-images` | Сгенерировать тестовые изображения |

### 🔄 Kafka тестирование

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| `POST` | `/api/kafka-test` | Отправить тестовое сообщение в Kafka |

---

## 🏗️ Технические детали

### Архитектура

- **Основное приложение**: Spring Boot 3 с REST API
- **База данных**: PostgreSQL для production, H2 для разработки
- **Очереди**: Kafka для асинхронной обработки сообщений
- **Оркестрация**: Camunda/Zeebe для workflow управления заказами
- **Хранилище**: MinIO (S3-совместимое) для изображений товаров
- **Интеграции**: Внешние сервисы для получения данных аккаунтов и клиентов

### Конфигурация БД

**PostgreSQL (production):**
```properties
spring.datasource.url=jdbc:postgresql://db:5432/warehouse
spring.datasource.username=postgres
spring.datasource.password=postgres
```

**H2 (development):**
```properties
spring.datasource.url=jdbc:h2:file:./movchandb
spring.datasource.driver-class-name=org.h2.Driver
spring.h2.console.enabled=true
```

### Docker

- **Порт приложения**: 8082 (8080 в контейнере)
- **Порт PostgreSQL**: 5432
- **Порт Kafka UI**: 9081
- **Порт Zeebe**: 26500
- **Volume для данных**: warehouse_pgdata

---

## 🧪 Тестирование

```bash
# Запуск тестов
./gradlew test

# Генерация Javadoc
./gradlew javadoc

# Доступ к документации (Mac)
open build/docs/javadoc/index.html

# Доступ к документации (Linux)
cd build/docs/javadoc && python -m http.server 8000
# Открыть http://localhost:8000
```

---

## 📊 Мониторинг и администрирование

- **Kafka UI**: http://localhost:9081
- **H2 Console (development)**: http://localhost:8082/h2-console
- **Swagger/OpenAPI**: http://localhost:8082/swagger-ui/index.html

---

## 🔧 Устранение проблем

```bash
# Найти процесс
lsof -i :8082

# Остановить процесс
kill -9 <PID>

# Если таблицы не создаются:
# Проверьте логи Spring Boot:
docker-compose logs app | grep "SQL create"

# Убедитесь, что в настройках:
spring.jpa.hibernate.ddl-auto=validate
```

---

## 🧹 Очистка окружения

```bash
# Полная очистка Docker
docker stop $(docker ps -aq)
docker rm $(docker ps -aq)
docker rmi -f $(docker images -q)
docker volume rm $(docker volume ls -q)

# Очистка Kubernetes
kubectl delete all --all -n warehouse-app
kubectl delete pvc --all -n warehouse-app
kubectl delete configmap --all -n warehouse-app
kubectl delete namespace warehouse-app
```

---

## 📁 Структура проекта

```
warehouse/
├── src/main/java/com/example/warehouse/
│   ├── camunda/           # Camunda BPM конфигурация и обработчики
│   ├── config/            # Конфигурационные классы Spring
│   ├── controller/        # REST контроллеры
│   ├── currency/          # Работа с валютами и курсами
│   ├── enums/            # Перечисления (enum classes)
│   ├── exception/         # Обработка исключений
│   ├── kafka/            # Kafka producers/consumers
│   ├── metrics/          # Метрики и мониторинг
│   ├── orders/           # Логика заказов
│   ├── ordersInfo/       # Информация о заказах
│   ├── persistence/      # Репозитории и entity классы
│   ├── S3Images/         # Работа с S3/MinIO для изображений
│   ├── schedulers/       # Планировщики задач
│   ├── search/           # Поисковые функции
│   ├── service/          # Бизнес-логика сервисов
│   └── WarehouseApplication.java # Главный класс приложения
├── src/main/resources/
│   ├── application.yml    # Основная конфигурация
│   ├── application-docker.yml # Конфигурация для Docker
│   ├── application-local.yml # Конфигурация для локальной разработки
│   └── db/changelog/      # Миграции базы данных (Liquibase)
├── config/                # Дополнительные конфигурационные файлы
│   └── application-docker.yml
├── kubernetes/           # Файлы для развертывания в Kubernetes
│   ├── namespace.yaml
│   ├── configmap.yaml
│   ├── pvc.yaml
│   ├── deployments.yaml
│   ├── services.yaml
│   └── ingress.yaml
├── docker-compose.yml    # Docker компоновка для локального развития
├── Dockerfile           # Сборка Docker образа приложения
├── init-db.sh          # Скрипт инициализации БД
├── deploy.sh           # Скрипт деплоя в Kubernetes
├── build.gradle.kts    # Конфигурация Gradle
├── settings.gradle.kts # Настройки проекта Gradle
└── gradlew             # Gradle wrapper
```