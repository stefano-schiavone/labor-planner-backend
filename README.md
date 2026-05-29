# Labor Planner — Backend

REST API and automated scheduling engine for the Labor Planner application. Built with **Spring Boot 3** and **OptaPlanner**, it solves weekly machine-job scheduling problems under hard and soft constraints and exposes a JWT-secured REST API consumed by the React frontend.

> This repository was originally developed in a university-hosted private GitLab instance and is published here for portfolio purposes.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Scheduling Engine | OptaPlanner 9.44 (Constraint Streams) |
| Security | Spring Security + JWT (stateless, BCrypt) |
| Persistence | Spring Data JPA + PostgreSQL 15 |
| Mapping | MapStruct 1.6 |
| Build | Gradle 8 |
| API Docs | SpringDoc OpenAPI / Swagger UI |
| Testing | JUnit 5, Spring Boot Test, H2 (in-memory) |
| Coverage | JaCoCo 0.8 |
| Code Quality | SonarQube |
| Containerization | Docker |
| CI/CD | GitLab CI |

---

## Architecture

The project follows a layered architecture with a deliberate separation between the domain model, JPA persistence, and the API surface:

```
Controller → Service interface → Service impl → Repository custom → JPA Entity → DB
                                      ↓
                              Domain Model (plain Java)
                                      ↓
                              OptaPlanner Solver
```

- **Controllers** handle HTTP concerns only — routing, request/response mapping, and status codes.
- **Services** implement domain logic against interfaces, keeping controllers thin and mockable.
- **Repositories** combine Spring Data JPA with per-entity custom interfaces (`BaseRepositoryCustom`) so all queries stay in one place and the domain layer never sees JPA annotations.
- **Domain models** are plain Java objects (no JPA annotations). **MapStruct** mappers convert between JPA entity ↔ domain model ↔ DTO at each boundary.
- **Exception handlers** per domain area (`UserExceptionHandler`, `MachineExceptionHandler`, etc.) produce consistent `ApiError` responses.

---

## Scheduling Engine

The standout feature is automated weekly schedule generation powered by **OptaPlanner**. Given a list of jobs (each with a deadline and required machine type) and a pool of available machines, the solver assigns each job a `startingTimeGrain` and a `machine`.

**Planning entities:** `ScheduledJob` (planning variables: `startingTimeGrain`, `machine`)  
**Problem facts:** `Job`, `Machine`, `TimeGrain`  
**Score type:** `HardSoftScore`  
**Solver termination:** 5 seconds (configurable via `optaplanner.solver.termination.spent-limit`)

### Constraints defined in `ScheduleConstraintProvider`

| Constraint | Type | Rule |
|---|---|---|
| `jobWithinAllowedHours` | Hard | Job must start between 07:00 and 18:00 |
| `jobMustFinishWithinDay` | Hard | Job cannot cross a day boundary |
| `machineTypeMismatch` | Hard | Job must be assigned to a machine that matches its required type |
| `machineConflict` | Hard | Two jobs on the same machine cannot overlap |
| `jobMustFinishBeforeDeadline` | Hard | Job end time must not exceed its deadline |
| `preferOneGrainGapBetweenJobs` | Hard | Minimum 1-grain gap is required between jobs on the same machine |
| `preferEarlyFinishOverall` | Soft | Penalises higher grain indices — prefer scheduling earlier in the week |
| `penalizeLargeGapBetweenJobs` | Soft | Penalises idle gaps between jobs on the same machine on the same day |
| `penalizeUnnecessaryLateStart` | Soft | Penalises a job starting late when no predecessor exists on that machine |

---

## Domain Model

```
User          ←→ AccountType
Job           →  MachineType (required), JobTemplate (optional)
Job           ↔  Job (self-referencing dependencies)
Schedule      →  List<ScheduledJob>, List<Machine>, List<TimeGrain>
ScheduledJob  →  Job, Machine (assigned), TimeGrain (start — planning variable)
Machine       →  MachineType, MachineStatus
```

---

## REST API

All endpoints except `/api/auth/**` require a `Bearer` JWT in the `Authorization` header.  
Full interactive documentation is available at **`http://localhost:8080/swagger-ui/index.html`**.

| Resource | Base Path |
|---|---|
| Authentication | `/api/auth` |
| Users | `/api/users` |
| Account Types | `/api/account-types` |
| Jobs | `/api/jobs` |
| Job Templates | `/api/job-templates` |
| Machines | `/api/machines` |
| Machine Types | `/api/machine-types` |
| Machine Statuses | `/api/machine-statuses` |
| Schedules | `/api/schedules` |

### Key Schedule Endpoints

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/schedules/for-week` | Fetch existing schedule for a given week (or candidate jobs if none exists) |
| `POST` | `/api/schedules/solve-for-week` | Trigger the OptaPlanner solver for the given week |
| `POST` | `/api/schedules/solve` | Solve an arbitrary `Schedule` problem object |
| `DELETE` | `/api/schedules/{uuid}` | Delete a schedule |

---

## Getting Started

### Prerequisites

- Java 21
- PostgreSQL 15+

### Environment Variables

Create a `.env` file in the project root — it is loaded automatically by `bootRun`:

```env
DEV_DB_URL=jdbc:postgresql://localhost:5432/laborplanner
DEV_DB_USER=your_db_user
DEV_DB_PASSWORD=your_db_password
```

### Run Locally

```bash
./gradlew bootRun
```

The application starts on **port 8080** with the `dev` Spring profile active.

### Run Tests

```bash
# Unit and integration tests with combined summary table
./gradlew test

# Tests + JaCoCo coverage report
./gradlew test jacocoTestReport
```

- Unit tests use H2 in-memory database — no external dependencies.
- Integration tests are tagged `@Tag("integration")` and run against H2 as well.
- Results: `build/test-results/` — JaCoCo HTML report: `build/reports/jacoco/`.

### Build JAR

```bash
./gradlew clean build -x test
```

Runnable JAR is output to `build/libs/*.jar`.

---

## Docker

Build and run:

```bash
docker build -t labor-planner-backend .

docker run -d -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e PROD_DB_URL=jdbc:postgresql://db-host:5432/laborplanner \
  -e PROD_DB_USER=lp_prod_user \
  -e PROD_DB_PASSWORD=your_password \
  labor-planner-backend
```

The Dockerfile uses a two-step approach: builds with Gradle then runs with a minimal JRE image. The application runs as an unprivileged `app` system user (no login shell, no home directory) following Docker security best practices.

---

## CI/CD Pipeline

The `.gitlab-ci.yml` defines a fully automated seven-stage pipeline that runs on every push and merge request. Each stage runs in an isolated Docker container.

| Stage | Image | What it does |
|---|---|---|
| `build` | `gradle:8.14-jdk21` | Compiles the project, produces the JAR artifact |
| `test` | `eclipse-temurin:21-jdk` | Runs unit + integration tests against H2; generates JaCoCo coverage |
| `sonar` | `gradle:8.14-jdk21` | Runs SonarQube static analysis and enforces the quality gate |
| `package` | — | Passes the JAR artifact forward to subsequent stages |
| `image` | `docker:24` (DinD) | Builds and pushes the Docker image to a private registry |
| `deploy-production` | `alpine` | SSH-deploys the new image to the production VM — **manual trigger on `main`** |

Gradle dependency cache is persisted per branch to keep pipelines fast. The production deploy injects database credentials as CI/CD variables — no secrets are stored in the repository.

---

## Configuration Profiles

| Profile | Database | When used |
|---|---|---|
| `dev` | PostgreSQL via `.env` | Local development |
| `test` | H2 in-memory | Automated tests |
| `staging` | PostgreSQL | Staging server |
| `prod` | PostgreSQL via env vars | Production server |

---

## Related Repository

[labor-planner-frontend](https://github.com/stefano-schiavone/labor-planner-frontend) — React + TypeScript frontend that consumes this API.
