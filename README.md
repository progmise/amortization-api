# amortization-api

REST API that computes and stores loan amortization schedules — the calculation
engine companion of [`loans-api`](https://github.com/progmise/loans-api).
Kotlin, Spring Boot, Spring Data JPA, PostgreSQL, Redis (Redisson), Togglz.

## Architecture

Ports & adapters, following the same layering as the rest of the workspace APIs:

```
com.progmise.amortization
├── application/
│   ├── config/         Bean wiring, Togglz (JDBC-backed), Redisson
│   └── controller/     ScheduleResource — @RestController implementation
├── delivery/           Input adapter
│   ├── controller/     Controller interfaces
│   ├── dto/            request/ (+ builders) and response/ DTOs
│   ├── exception/      ExceptionCode + @RestControllerAdvice handler
│   └── validator/      Field validators (numeric, decimal, date, enum, ...)
├── domain/
│   ├── entity/         Schedule, Installment, ScheduleCriteria
│   ├── enums/          AmortizationSystem, FeatureToggle, ErrorLevel, LinkRef
│   ├── exception/      ScheduleNotFoundException
│   ├── repository/     ScheduleRepository (output port)
│   └── service/        AmortizationCalculator (pure domain math)
├── infrastructure/
│   ├── cache/          Cache interface + Redis impl (toggle-gated, fail-open)
│   ├── mapper/         domain <-> entity mapper
│   ├── persistence/    JPA entities, Spring Data repo, ScheduleRepositoryImpl
│   └── togglz/         FeatureToggleHelper
└── utils/              Constants, extension functions, exceptions, validators
```

## Endpoints

| Method | Path                    | Description                                        |
|--------|-------------------------|----------------------------------------------------|
| POST   | `/api/1.0/schedules`    | Compute and persist a schedule (FRENCH or GERMAN)  |
| GET    | `/api/1.0/schedules`    | Paginated list (`_offset`, `_limit` + HAL links)   |
| GET    | `/api/1.0/schedules/{id}` | Full schedule with every installment             |

Errors follow the shared contract: `{"errors": [{"code", "message", "level", "description"}]}`.

## Feature toggles

Togglz backed by the `FEATURE_TOGGLE` table (auto-created in PostgreSQL).
Both features default to **enabled** (`@EnabledByDefault`); flip them by
updating `FEATURE_ENABLED` in the table:

| Toggle                    | Effect                                                   |
|---------------------------|----------------------------------------------------------|
| `SCHEDULE_CACHE_ON`       | Redis cache-aside on `GET /schedules/{id}`               |
| `GERMAN_AMORTIZATION_ON`  | Rejects `system=GERMAN` with `feature.disabled.*` (400)  |

## Run

```bash
docker compose up -d       # PostgreSQL + Redis
./gradlew bootRun          # API on :8080, mounted under /api/1.0
```

## Configuration

Everything is env-var driven — point the same variables at Supabase
(Postgres) and Upstash (Redis over TLS) without code changes. See
`.env.example`.

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` — datasource
- `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`, `REDIS_SSL` — cache
- `CACHE_SCHEDULES_TTL` — cache TTL in seconds (default 300)

## Test

```bash
./gradlew test
```

Self-contained: pure unit tests for the calculator, validators and builders,
`@WebMvcTest` for the resource, mocked cache/JPA for the repository, and a
`@SpringBootTest` context test on H2. No Docker required.

## Docs

API contract: `docs/swagger.yaml` (OpenAPI 3.0).
