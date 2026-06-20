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

## Screenshots
<img width="1908" height="965" alt="Screenshot 2026-06-20 163609" src="https://github.com/user-attachments/assets/e4683c15-cf93-4926-9f88-9b7d87ba2719" />
<img width="1911" height="962" alt="Screenshot 2026-06-20 165904" src="https://github.com/user-attachments/assets/c510e30c-bfae-4eef-b7f4-c9984bc0b4eb" />
<img width="1895" height="961" alt="Screenshot 2026-06-20 165845" src="https://github.com/user-attachments/assets/8338a464-0211-41da-b589-c6c565ec8766" />
<img width="1917" height="967" alt="Screenshot 2026-06-20 165454" src="https://github.com/user-attachments/assets/ca9b64bc-6fcb-443b-9d1e-d99f6dd773bd" />
<img width="1912" height="971" alt="Screenshot 2026-06-20 165517" src="https://github.com/user-attachments/assets/2c880adc-bf5d-4fdc-a027-51994b1e129e" />
<img width="1891" height="959" alt="Screenshot 2026-06-20 165535" src="https://github.com/user-attachments/assets/d90168ae-81cf-4e50-adf5-1567bc191f09" />
<img width="1891" height="950" alt="Screenshot 2026-06-20 165557" src="https://github.com/user-attachments/assets/6fd11488-6c2c-4957-a6d4-0090ef5b2952" />
<img width="1920" height="1080" alt="Screenshot (2)" src="https://github.com/user-attachments/assets/48b8cf9b-3ec1-44f5-90f1-a510f032902f" />
<img width="1909" height="969" alt="Screenshot 2026-06-20 165701" src="https://github.com/user-attachments/assets/1fd912fb-04b6-4b4a-b064-0b97a4af3187" />

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
