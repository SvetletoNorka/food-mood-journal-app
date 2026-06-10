# Food Mood Journal

A web application for tracking food intake and mood after meals. Users can log foods, meals, and wellness scores to discover which foods make them feel their best.

> Project from the SoftUni **Spring Fundamentals** course.

## Description

**Food Mood Journal** connects food logging with how you feel after eating. The idea is simple: record what you ate, rate your mood and energy, and let the app help you spot patterns between diet and well-being.

## Features

- User registration (username, email, password)
- Login and logout
- Session-based authentication (`UserSession`)
- Password hashing (BCrypt)
- Landing, login, and register pages
- Protected home page with user profile
- Food management (Food)
- Meal logging (Meal, MealEntry)
- Post-meal wellness log (mood score, energy score, notes)
- Statistics and reports

## Tech Stack

| Category | Technology |
|----------|------------|
| Language | Java 17 |
| Framework | Spring Boot 4.0.6 |
| Web | Spring MVC, Thymeleaf |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL |
| Other | Lombok, Bean Validation, Spring Actuator, DevTools |

## Prerequisites

- **JDK 17+**
- **Maven 3.9+** (or the included `./mvnw` wrapper)
- **MySQL 8+** running on `localhost:3306`

## Getting Started

### 1. Database configuration

Edit `src/main/resources/application.properties` and set your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/food-mood-journal-app?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

The database is created automatically on first run (`createDatabaseIfNotExist=true`).

Optional Spring profiles are available in `application-dev.properties` and `application-prod.properties`. Activate one with:

```bash
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

### 2. Build and run

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Alternatively:

```bash
mvn clean package
java -jar target/food-mood-journal-app-0.0.1-SNAPSHOT.jar
```

The application is available at: **http://localhost:8080**

### 3. Tests

```bash
.\mvnw.cmd test
```

## Routes

| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | Landing page |
| GET | `/login` | Login form |
| POST | `/login` | User login |
| GET | `/register` | Registration form |
| POST | `/register` | User registration |
| GET | `/home` | Dashboard (requires login) |
| GET | `/logout` | Logout |

## Data Model

```
User
 ├── Food (calories, protein, fat, carbs per 100g)
 └── Meal (type: BREAKFAST, LUNCH, DINNER, SNACK)
      ├── MealEntry (quantity in grams + Food reference)
      └── WellnessLog (moodScore, energyScore, notes)
```

### User Roles

- `USER` — standard user
- `ADMIN` — administrator role (defined in the model)

## Project Structure

```
src/main/java/app/
├── config/          # Bean configuration, UserSession
├── mapper/          # Entity ↔ DTO mapping
├── model/
│   ├── dto/         # Request/response objects
│   └── entity/      # JPA entities
├── repository/      # Spring Data repositories
├── service/         # Business logic
└── web/             # MVC controllers

src/main/resources/
├── static/css/      # Stylesheets
├── templates/       # Thymeleaf HTML templates
└── application.properties
```

## Authentication

The application uses **session-based authentication** via the `@SessionScope` `UserSession` component, not a full Spring Security filter chain. Passwords are stored hashed using `BCryptPasswordEncoder`.

## License

This project is licensed under the [MIT License](LICENSE).
