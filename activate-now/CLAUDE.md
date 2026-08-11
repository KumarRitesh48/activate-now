# CLAUDE.md

This file mirrors the repository conventions defined in AGENTS.md - see that file
for the source of truth. Kept as an identical copy (not a symlink, for cross-platform
safety) so Claude-specific tooling that looks for CLAUDE.md finds the same content.

Instructions for any AI coding agent (Claude, Copilot, Cursor, Codex, etc.) working in this repo.

## What this repo is

A full-stack "Activate Now" feature: a student/parent dashboard with a fee-financing
widget, and a modal form that submits KYC-style details (phone, PAN, name, email) to
activate the offer. Two apps:

```
activate-now/
├── backend/     Spring Boot 3 (Java 17) REST API + JPA + MySQL/H2
├── frontend/    Angular 18 standalone-components app
├── docker-compose.yml   MySQL for local/dev
└── README.md
```

## Structure & conventions

### Backend (`backend/src/main/java/com/zenda/activatenow/`)
- `model/` — JPA entities (`Student`, `Activation`)
- `dto/` — request/response payloads, never expose entities directly over the API
- `repository/` — Spring Data JPA interfaces only, no query logic beyond method names
- `service/` — business logic; controllers stay thin and delegate here
- `controller/` — `@RestController`s, one per resource, under `/api/...`
- `exception/` — custom exceptions + a single `@RestControllerAdvice` global handler

Validation rules live as Bean Validation annotations directly on `ActivationRequest`
(`@Pattern` for phone/PAN/email) — this is the source of truth. The Angular form
mirrors the same regexes for instant UX feedback, but the backend re-validates
independently; never trust client-side validation alone.

### Frontend (`frontend/src/app/`)
- `models/` — TypeScript interfaces that mirror backend DTOs 1:1
- `services/` — `StudentService` wraps all HTTP calls; components never call `HttpClient` directly
- `components/dashboard/` — dashboard screen
- `components/activate-modal/` — the "Activate Now" popup, a standalone component with its own `ReactiveFormsModule` form

Standalone components only (no `NgModule`s). Keep components presentational;
API calls and state transitions go through `StudentService`.

## Run / build / test

**Backend** (from `backend/`):
```bash
mvn spring-boot:run                                    # H2 in-memory, zero setup
mvn spring-boot:run -Dspring-boot.run.profiles=mysql    # MySQL (run docker-compose first)
mvn test
```

**Frontend** (from `frontend/`):
```bash
npm install
npm start        # ng serve, http://localhost:4200
npm run build    # production build -> dist/
```

**Full stack via Docker (MySQL only, apps run natively):**
```bash
docker-compose up -d      # starts MySQL on :3306
# then run backend with -Dspring-boot.run.profiles=mysql, and frontend with npm start
```

## Conventions an agent should follow when extending this repo

1. Any new form field or validation rule must be added in **three** places to stay in sync:
   `ActivationRequest.java` (backend truth), `activate-modal.component.ts` (Angular validators),
   and `models/dashboard.model.ts` (TS interface) if the payload shape changes.
2. Never hardcode dashboard/activation data in the frontend — it must come from the API.
   Seed data belongs in `backend/src/main/resources/data.sql`.
3. New REST endpoints go under `/api/...`, return DTOs (never entities), and get a
   corresponding method in `StudentService` (Angular) + `StudentService` (Spring) — note
   the naming overlap is intentional per-layer, not a conflict.
4. Run `mvn test` and `npm run build` before considering a change complete.
5. Keep commit messages descriptive (e.g. `Add: PAN format validation - backend + frontend`).
