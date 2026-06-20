# Food Mood Journal

A web application for tracking food intake and mood after meals. Users can log foods, meals, and wellness scores to discover which foods make them feel their best.

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
| GET | `/foods` | Food list (requires login) |
| GET | `/foods/new` | New food form (requires login) |
| POST | `/foods` | Create food (requires login) |
| GET | `/foods/{id}/edit` | Edit food form (requires login) |
| POST | `/foods/{id}` | Update food (requires login) |
| POST | `/foods/{id}/delete` | Delete food (requires login) |
| GET | `/meals` | Meal list (requires login) |
| GET | `/meals/new` | New meal form (requires login) |
| POST | `/meals` | Create meal (requires login) |
| GET | `/meals/{id}` | Meal details (requires login) |
| GET | `/meals/{id}/wellness` | Wellness log form (requires login) |
| POST | `/meals/{id}/wellness` | Save wellness log (requires login) |
| GET | `/statistics` | Statistics page (requires login) |
| GET | `/users` | User list (admin only) |
| PUT | `/users/{id}/status` | Toggle user status (admin only) |
| PUT | `/users/{id}/role` | Toggle user role (admin only) |

## Data Model

```
User
 ├── Food
 └── Meal
      ├── MealEntry → Food
      └── WellnessLog (OneToOne)
```

### Entities

| Entity | Fields |
|--------|--------|
| **User** | `username`, `email`, `password`, `role`, `isActive`, `createdOn`, `updatedOn` |
| **Food** | `name`, `caloriesPer100g`, `proteinPer100g`, `fatPer100g`, `carbsPer100g`, `owner` |
| **Meal** | `mealType`, `eatenAt`, `owner`, `entries`, `wellnessLog` |
| **MealEntry** | `quantityInGrams`, `meal`, `food` |
| **WellnessLog** | `moodScore`, `energyScore`, `notes`, `createdAt`, `meal` |

### Enums

| Enum | Values |
|------|--------|
| **UserRole** | `USER`, `ADMIN` |
| **MealType** | `BREAKFAST`, `LUNCH`, `DINNER`, `SNACK` |

## Project Structure

```
src/main/java/app/
├── config/              # BeanConfiguration, WebMvcConfiguration
├── mapper/              # Entity ↔ DTO mapping (food, meal, user)
├── model/
│   ├── dto/             # Request/response objects
│   └── entity/          # JPA entities (food, meal, user)
├── repository/          # Spring Data repositories
├── security/            # SessionInterceptor (auth guard)
├── service/             # Business logic (food, meal, statistics, user)
└── web/                 # MVC controllers (food, meal, statistics, user)

src/main/resources/
├── static/
│   ├── css/             # Stylesheets
│   └── images/          # Logo, mood icons, food images
├── templates/
│   └── fragments/       # Reusable Thymeleaf partials
└── application.properties
```

## Authentication

The application uses **session-based authentication** via the `@SessionScope` `UserSession` component, not a full Spring Security filter chain. Passwords are stored hashed using `BCryptPasswordEncoder`.

## License

This project is licensed under the [MIT License](LICENSE).
