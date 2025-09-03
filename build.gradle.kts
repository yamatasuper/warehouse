plugins {
	java
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.springdoc.openapi-gradle-plugin") version "1.8.0" // Плагин для генерации OpenAPI
}

group = "com.warehouse"
version = "1.0.0"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
	mavenLocal()
	flatDir {
		dirs("libs")
	}
}

dependencies {
	// Spring Boot Starters
	implementation("org.springframework.boot:spring-boot-starter-web") // Веб-функциональность (MVC)
	implementation("org.springframework.boot:spring-boot-starter-data-jpa") // JPA + Hibernate
	implementation("org.springframework.boot:spring-boot-starter-validation") // Валидация (@Valid и другие аннотации)

	implementation ("org.springframework.boot:spring-boot-starter-webflux")
	implementation ("org.springframework.boot:spring-boot-starter-cache")
	implementation("com.github.ben-manes.caffeine:caffeine")

	// База данных
	runtimeOnly("com.h2database:h2") // Драйвер PostgreSQL
	runtimeOnly("org.postgresql:postgresql")

	// Документация API
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0") // Swagger UI + OpenAPI 3

	// Утилиты
	compileOnly("org.projectlombok:lombok") // Генерация boilerplate-кода
	annotationProcessor("org.projectlombok:lombok") // Обработка аннотаций Lombok
	implementation("org.mapstruct:mapstruct:1.5.5.Final") // Маппинг DTO <-> Entity
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final") // Генерация мапперов

	// Тестирование
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("org.junit.jupiter:junit-jupiter-api") // JUnit 5
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine") // Движок JUnit 5
	testRuntimeOnly("org.junit.platform:junit-platform-launcher") // Запуск тестов в IDE
	testImplementation("com.h2database:h2") // In-memory БД для тестов
	testImplementation("org.mockito:mockito-core:5.11.0") // Mockito для моков

	// Add these for better testing support
	testImplementation("org.mockito:mockito-junit-jupiter") // Better Mockito integration with JUnit 5
	testImplementation("org.assertj:assertj-core") // Fluent assertions for tests
	testImplementation("org.hamcrest:hamcrest-library") // Hamcrest matchers

	implementation("org.springframework:spring-test")

	// For JSON testing in MockMvc
	testImplementation("org.springframework:spring-test")
	testImplementation("com.jayway.jsonpath:json-path")

	// For logging in tests (if you want to use SLF4J)
	testImplementation("org.slf4j:slf4j-api")
	testRuntimeOnly("ch.qos.logback:logback-classic")

	// For testing with @AutoConfigureMockMvc
	testImplementation("org.springframework.boot:spring-boot-test-autoconfigure")

	// Micrometer for metrics
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-core")
	implementation("io.micrometer:micrometer-registry-prometheus")

	// db changelog
	implementation("org.liquibase:liquibase-core:4.22.0")

	// Загрузка переменных из .env
	implementation("io.github.cdimascio:dotenv-java:3.0.0")

	// Kafka
	implementation("org.springframework.kafka:spring-kafka")
	implementation("org.apache.kafka:kafka-clients:3.5.1")

	// JSON serialization
	implementation("com.fasterxml.jackson.core:jackson-databind")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

	implementation("org.springframework.boot:spring-boot-autoconfigure")
	implementation("software.amazon.awssdk:s3:2.20.0")
	implementation("software.amazon.awssdk:aws-core:2.20.0")

	// Spring Boot Test Starter (includes autoconfigure for testing)
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-test-autoconfigure")

//	implementation("com.example:exception-handler-starter")

	// Для работы с Multipart файлами
	implementation("commons-io:commons-io:2.11.0")

	// Camunda BPM (для ProcessEngine, RuntimeService, TaskService)
	implementation("org.camunda.bpm:camunda-engine-spring:7.20.0")
	implementation("org.camunda.bpm:camunda-engine:7.20.0")

// Zeebe (для работы с Camunda Cloud/Zeebe)
	implementation("io.camunda:spring-zeebe-starter:8.5.0")
	implementation("io.camunda:zeebe-client-java:8.5.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.bootJar {
	archiveFileName.set("warehouse-app.jar")
	mainClass.set("com.example.warehouse.WarehouseApplication")
}

// Для Spring Boot приложения через bootRun
tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	jvmArgs = listOf("-Xmx500m", "-Xms500m")
}


tasks {
	// Конфигурация Javadoc
	withType<Javadoc> {
		title = "Warehouse API Documentation"
		setDestinationDir(file("${buildDir}/docs/javadoc"))
		options {
			encoding = "UTF-8"
			this as StandardJavadocDocletOptions
			addStringOption("Xdoclint:none", "-quiet") // Отключаем некоторые проверки
			links("https://docs.oracle.com/en/java/javase/17/docs/api/")
			links("https://docs.spring.io/spring-framework/docs/current/javadoc-api/")
			links("https://javadoc.io/doc/org.springdoc/springdoc-openapi-starter-webmvc-ui/latest/")
		}
	}

	// Задача для генерации Javadoc вместе со сборкой
	build {
		dependsOn("javadoc")
	}
}