# AGENTS.md

Guide for working on **amortization-api** — Kotlin/Spring Boot microservice
that computes and stores loan amortization schedules (companion of
`loans-api`). Gradle (Kotlin DSL), Spring Data JPA, PostgreSQL, Redis
(Redisson), Togglz.

## Architecture

Ports & adapters, same layering intent as the workspace Java APIs (package
names differ — this repo predates `java-maven-api-template`):

```
com.progmise.amortization
├── application/
│   ├── config/         Bean wiring, Togglz (JDBC-backed), Redisson
│   └── controller/     ScheduleResource — @RestController implementation
├── delivery/           Input adapter (≈ infrastructure/adapters/input/rest)
│   ├── controller/     Controller interfaces (the input ports)
│   ├── dto/            request/ (+ builders) and response/ DTOs
│   ├── exception/      ExceptionCode + @RestControllerAdvice handler
│   └── validator/      Field validators
├── domain/
│   ├── entity/         Schedule, Installment, ScheduleCriteria
│   ├── enums/          AmortizationSystem, FeatureToggle, ErrorLevel, LinkRef
│   ├── exception/      ScheduleNotFoundException
│   ├── repository/     ScheduleRepository (output port)
│   └── service/        AmortizationCalculator (pure domain math)
├── infrastructure/     Output adapters
│   ├── cache/          Cache iface + Redis impl (toggle-gated, fail-open)
│   ├── persistence/    JPA entities, Spring Data repo, ScheduleRepositoryImpl
│   └── togglz/         FeatureToggleHelper
└── utils/              Constants, extension functions, validators
```

Rules:
- Controllers (`delivery/`) never contain business logic — the work lives in
  `domain/service/` and the resource layer.
- Errors follow the shared contract `{"errors":[{code,message,level,description}]}`
  (local `delivery/exception/` handler — pending migration to `api-commons`).
- Feature toggles: Togglz on the `FEATURE_TOGGLE` table; `SCHEDULE_CACHE_ON`
  and `GERMAN_AMORTIZATION_ON` are `@EnabledByDefault` — flip via DB, not code.
- Generic candidates (validators, mappers used by 2+ services) belong in
  `api-commons` — promote, don't duplicate.
- This repo consumes `api-commons` via **JitPack pin** — migration to the
  Central artifact is pending; don't add a second source of truth.

## Conventions

- Kotlin, Gradle wrapper (`./gradlew`), JDK 21.
- Config only through env vars — never commit secrets. New vars go in
  `.env.example` (no values) + README table. Vars: `DB_URL`, `DB_USERNAME`,
  `DB_PASSWORD`, `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`, `REDIS_SSL`,
  `CACHE_SCHEDULES_TTL`, `PORT` (platform-injected).
- Local dev: `docker compose up -d` (Postgres + Redis), `./gradlew bootRun`.
- Tests are hermetic — H2, mocked Redis; no Docker required.
- API contract in `docs/swagger.yaml` — keep in sync with controllers.
- **No CI yet** — `.github/workflows/` is missing; pending migration to the
  `app-*` thin callers from `progmise/reusable-workflows` (`@v1`,
  `secrets: inherit`), matching the rest of the org.

## Verify before done

```bash
./gradlew test
docker compose config   # when touching compose/runtime config
```

## Branches

GitFlow: `main` is the default branch and holds releases; `development` is
the integration branch. Work lands on `<type>/<snake_description>` → PR to
`development` → PR to `main`. Types: `feature/`, `fix/`, `hotfix/`, `chore/`,
`docs/`, `refactor/`.

## Release

Once CI lands: bump the version in `build.gradle.kts`, merge
`development` → `main`, run the **Release** workflow on `main`. Deploys go
through `deploy-manifest` (`deploy.yml` dispatch).
