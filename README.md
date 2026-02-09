# Labor Planner – Backend

Backend service for the **Labor Planner** application, built with a focus on clean architecture, scalability, and real-world backend practices.

This project demonstrates my experience with **Java, Spring Boot, REST APIs, security, persistence, and constraint-based scheduling**.

## Repository Origin

> ℹ️ This repository was originally developed within a university environment and later **imported from the university's private GitLab instance**.
> The project is published here for **portfolio and demonstration purposes**.

## Tech Stack

**Core**
- Java 21
- Spring Boot
- Gradle

**Backend & APIs**
- Spring Web (REST)
- Spring Validation
- Spring Security
- JWT authentication

**Persistence**
- Spring Data JPA
- Hibernate
- Relational database (environment-configurable)

**Scheduling & Optimization**
- OptaPlanner (constraint-based planning)

**Mapping & DTOs**
- MapStruct
- Layered DTO architecture

**Testing & Quality**
- JUnit 5
- Spring Boot Test
- JaCoCo (code coverage)

**DevOps & Tooling**
- Docker
- Environment-based configuration
- Git-based workflow

## What This Backend Does

The backend is responsible for:
- Authenticating users using JWT
- Managing jobs, machines, users, and schedules
- Validating domain rules and constraints
- Generating optimized schedules using OptaPlanner
- Exposing a clean REST API for frontend consumption

## Architecture

The application follows a **layered architecture** with clear separation of concerns.

```
controller/ → REST API layer
service/ → Business logic & scheduling
repository/ → Data access (JPA)
dto/ → Request/response models
model/ → Domain models
security/ → Authentication & authorization
exception/ → Centralized error handling
```

### Key Architectural Decisions

- **DTO-based API design** to avoid exposing domain models
- **MapStruct** for clean, type-safe mapping
- **JWT authentication** for stateless security
- **OptaPlanner** for constraint-based scheduling instead of hardcoded logic
- **Environment-specific configs** for dev, test, staging, and production

## Scheduling Engine

Scheduling is implemented using **OptaPlanner**, allowing:
- Hard constraints (e.g. invalid assignments are forbidden)
- Soft constraints (e.g. preferred machine usage)
- Extensible planning rules without changing core logic

The constraint logic is defined in:
`service/optaplanner/ScheduleConstraintProvider.java`

## Configuration

Multiple environments are supported:
- `application-dev.properties`
- `application-test.properties`
- `application-staging.properties`
- `application-prod.properties`

This mirrors real-world deployment setups.

## Running the Project

### Prerequisites
- Java 21
- Gradle
- Database instance

### Run locally
```bash
./gradlew bootRun
```

### Run tests
```bash
./gradlew test
```

### Docker
A Dockerfile is included to containerize the application.

## Related Repository
[labor-planner-frontend](https://github.com/stefano-schiavone/labor-planner-frontend) – Frontend application consuming this API

## Why This Project Matters

This project showcases:
- Enterprise-style backend architecture
- Real-world authentication and security
- Non-trivial scheduling logic
- Clean separation of concerns
- Production-aware configuration and tooling

It reflects how I approach backend development beyond coursework.
