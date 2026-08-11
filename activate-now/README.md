# Activate Now — Full-Stack Feature

A student/parent dashboard with a fee-financing widget, and an "Activate Now" modal
that captures KYC-style details and persists them to a real database.

Built to match the provided Figma (mobile view): gradient dashboard header, profile
card, purple fee-financing widget, and a bottom-sheet activation form with live
validation (green tick on success, inline error on failure).

---

## Stack

| Layer    | Tech |
|----------|------|
| Frontend | Angular 18 (standalone components), Reactive Forms, SCSS |
| Backend  | Spring Boot 3.3 (Java 17), Spring Data JPA, Bean Validation |
| Database | MySQL 8 (via Docker) — H2 in-memory also wired up for zero-setup local review |

---

## Project structure

```
activate-now/
├── backend/                  Spring Boot API
│   └── src/main/java/com/zenda/activatenow/
│       ├── model/             JPA entities: Student, Activation
│       ├── dto/                Request/response payloads
│       ├── repository/       Spring Data JPA repositories
│       ├── service/            Business logic
│       ├── controller/       REST controllers
│       └── exception/       Global exception handling
├── frontend/                  Angular app
│   └── src/app/
│       ├── models/              TS interfaces mirroring backend DTOs
│       ├── services/           StudentService (all HTTP calls)
│       └── components/
│           ├── dashboard/        Dashboard screen
│           └── activate-modal/   "Activate Now" popup form
├── docker-compose.yml        MySQL for local/dev
├── AGENTS.md                  Agent instructions - source of truth
├── CLAUDE.md                  Identical copy of AGENTS.md, for Claude-specific tooling
├── .cursor/rules               Pointer + quick reference, for Cursor's auto-discovery
└── README.md                 This file
```

**On the multiple instruction files:** `AGENTS.md` is the source of truth. `CLAUDE.md`
and `.cursor/rules` exist as real files (not symlinks, for cross-platform safety on
Windows) so the same conventions are discoverable regardless of which AI coding tool
someone points at this repo, without maintaining three divergent copies — `CLAUDE.md`
mirrors `AGENTS.md` exactly, and `.cursor/rules` is a short pointer + quick reference
back to it.

---

## Running it locally

### Option A — MySQL (default; matches the challenge's "real DB" requirement)

```bash
# 1. Start MySQL
docker-compose up -d

# 2. Start backend against MySQL (this is now the default profile)
cd backend
mvn spring-boot:run
```

MySQL runs on `localhost:3306`, database `activatenow`, user `root` / password `root`
(see `docker-compose.yml` and `application-mysql.properties` — change credentials
before using this outside local development).

```bash
cd frontend
npm install
npm start
```
Frontend starts on `http://localhost:4200`.

### Option B — H2, zero Docker setup (for quick local iteration only)

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```
Backend starts on `http://localhost:8080`, using an in-memory H2 database seeded
with demo students (including Jessica John Jones, matching the Figma example).
Data resets on every restart — use Option A for anything you want to persist.

## Verification

This project was built and run locally end-to-end, not just written and assumed
to work:

- **Frontend:** Angular production build (`ng build`) completes with zero errors;
  `ng serve` confirmed running and serving on port 4200.
- **Backend:** run locally via IntelliJ/Maven against the H2 profile — confirmed
  clean startup, correct Hibernate schema generation, and the full activation
  flow (dashboard fetch → form submit → DB insert/update → dashboard reflecting
  `activated = true`) verified via actual Hibernate SQL logs.
- **H2 Console:** connected directly and confirmed submitted activation data is
  actually persisted in the `activations` table, and `students.activated` flips
  correctly.
- **Tests:** `mvn test` run locally — all backend unit, controller, and
  validation tests passing (38/38 after two real bugs the test suite itself
  caught and fixed: a `data.sql`/Hibernate startup-ordering issue, and an email
  regex that incorrectly rejected subdomain addresses).
- **MySQL profile:** provided via `docker-compose.yml` and `application-mysql.properties`;
  not separately re-verified after the most recent relationship/constraint changes
  below — run `docker-compose up -d` and `mvn spring-boot:run` to confirm on your
  machine before final submission.

---

## Testing

Backend tests live in `backend/src/test/java/com/zenda/activatenow/`:

| File | What it covers |
|---|---|
| `service/StudentServiceTest.java` | Business logic in isolation (Mockito-mocked repositories) — dashboard fetch, activation success, student-not-found handling, duplicate-activation rejection, and that the `Activation` entity + `Student.activated` flag are actually persisted correctly |
| `controller/StudentControllerTest.java` | HTTP layer via `MockMvc` — status codes (200/404/400/409), JSON response shape, and that invalid phone/PAN/email each trigger the expected `400` with field-level error messages |
| `dto/ActivationRequestValidationTest.java` | The Bean Validation rules directly — parameterized tests covering valid and invalid phone/PAN/email formats against the exact regex rules |
| `ActivateNowApplicationTests.java` | Smoke test that the full Spring context wires up correctly against the H2 profile |

Run everything with:
```bash
cd backend
mvn test
```

> The relationship/duplicate-activation changes below were added after the last
> full local test run — re-run `mvn test` once more before submission to confirm
> the new tests pass on your machine (they follow the same patterns as the
> already-verified tests above, so no surprises expected, but worth confirming).

---




### `GET /api/students/{id}/dashboard`

Returns dashboard data for a student — profile, school, and fee widget info.

**Response `200`:**
```json
{
  "studentId": 1,
  "schoolName": "Delhi Public School",
  "studentName": "Jessica John Jones",
  "classSection": "FS1 Acacia",
  "profilePhotoUrl": "https://i.pravatar.cc/150?img=47",
  "annualFee": 340000.00,
  "interestRatePercent": 0,
  "activated": false
}
```

**Response `404`** if student doesn't exist:
```json
{
  "timestamp": "2026-08-08T10:15:00",
  "success": false,
  "message": "Student not found with id: 99"
}
```

---

### `POST /api/students/{id}/activate`

Submits the "Activate Now" modal form and persists it.

**Request body:**
```json
{
  "phoneNumber": "+918329230390",
  "panNumber": "EEAPS6789R",
  "nameAsInPan": "KRISHNA KUMAR SINGH",
  "email": "xyz@gmail.com"
}
```

**Validation rules:**
| Field | Rule |
|---|---|
| `phoneNumber` | `+91` followed by exactly 10 digits |
| `panNumber` | Standard PAN format: 5 letters, 4 digits, 1 letter (e.g. `AAAAA9999A`) |
| `nameAsInPan` | Required, min 2 characters |
| `email` | Standard address ending in `.com` |

> **Email is intentionally restricted to `.com` domains** — this matches the
> challenge brief's stated rule (`...@...com`) exactly. It will reject otherwise
> valid addresses like `user@gmail.in` or `user@company.co.uk` by design, not
> by oversight.

**Response `200`** on success:
```json
{
  "success": true,
  "message": "Activation successful",
  "activated": true
}
```

**Response `400`** on validation failure:
```json
{
  "timestamp": "2026-08-08T10:16:00",
  "success": false,
  "message": "Validation failed",
  "errors": {
    "phoneNumber": "Phone must be in format +91XXXXXXXXXX (10 digits)"
  }
}
```

**Response `409`** if the student has already been activated (one activation per
student is enforced both here and via a DB-level unique constraint):
```json
{
  "timestamp": "2026-08-08T10:16:00",
  "success": false,
  "message": "Student with id 1 is already activated"
}
```

---

## Database Schema

**`students`**
| Column | Type | Notes |
|---|---|---|
| id | BIGINT (PK, auto) | |
| school_name | VARCHAR | |
| student_name | VARCHAR | |
| class_section | VARCHAR | e.g. "FS1 Acacia" |
| profile_photo_url | VARCHAR | |
| annual_fee | DECIMAL(12,2) | |
| interest_rate_percent | INT | |
| activated | BOOLEAN | flips to `true` once activation succeeds |

**`activations`**
| Column | Type | Notes |
|---|---|---|
| id | BIGINT (PK, auto) | |
| student_id | BIGINT | **FK** to `students.id`, with a **UNIQUE constraint** — enforces one activation per student at the database level, not just in application code |
| phone_number | VARCHAR | |
| pan_number | VARCHAR | |
| name_as_in_pan | VARCHAR | |
| email | VARCHAR | |
| submitted_at | TIMESTAMP | |

Modeled as a real JPA `@OneToOne` relationship (`Activation.student`), not a raw
foreign-key ID field — `Activation` holds a lazy reference to `Student`, matching
the actual one-activation-per-student business rule.

Schema is auto-generated by Hibernate (`spring.jpa.hibernate.ddl-auto=update`);
seed data lives in `backend/src/main/resources/data.sql`.

---

## Flow behavior (per the challenge's requirements)

1. Dashboard loads student profile, school, and fee widget data live from
   `GET /api/students/1/dashboard` — nothing is hardcoded in the frontend.
2. Clicking **Activate Now** opens the modal (only if not already activated).
3. Each field validates live as you type: a green tick appears once a field is
   valid, an inline error appears once a field is touched and invalid.
4. **Cancel** closes the modal and returns to the dashboard, no changes made.
5. **Activate Now** (in the modal) submits to `POST /api/students/1/activate`.
   On success, the modal closes, the dashboard re-fetches from the API, and the
   button now reads **Activated** (disabled, green) instead of **Activate Now** —
   this state is persisted in the DB, not just a local flag, so it survives a
   page refresh.

---

## How I used agentic/AI coding tools

This feature was built end-to-end in collaboration with Claude (Anthropic), working
directly inside a sandboxed dev container with real file, bash, and build access —
not just chat-based code suggestions.

**What the AI did:**
- Scaffolded the Angular app via `ng new` and the Spring Boot structure by hand
  (Maven wasn't preinstalled — it was installed via `apt-get` mid-session)
- Wrote all backend code (entities, DTOs, repositories, service, controller,
  global exception handler) and all frontend code (dashboard + modal components,
  reactive form validators, service layer, SCSS matching the Figma)
- **Actually ran `ng build` and `ng serve`** to verify the frontend compiles and
  boots — caught and fixed a real bug this way (Angular 18's new `@` control-flow
  syntax broke on a literal `@` character in the fee-widget copy; fixed by
  swapping to the `&#64;` HTML entity)
- Configured dual Spring profiles (H2 for instant local review, MySQL for the
  "real DB" requirement) so a reviewer isn't blocked on DB setup to try it

**What a human did:**
- Ran the full stack locally end-to-end (Angular + Spring Boot + H2), fixed a
  Run Configuration issue (a stray `SPRING_PROFILES_ACTIVE` environment variable
  was overriding the intended profile), and confirmed via H2 Console that
  activation data is genuinely persisted, not just displayed in the UI
- Ran `mvn test` locally and confirmed all backend tests pass

**What's still worth doing before final submission:**
- Run `docker-compose up -d` + `mvn spring-boot:run` once more against the MySQL
  profile specifically, to confirm the schema/relationship changes above (the
  `@OneToOne` constraint, duplicate-activation check) work against real MySQL,
  not just H2 — they follow standard JPA patterns so this should be a formality,
  but worth confirming rather than assuming
- Capture the actual screen recording of the flow (dashboard → activate → validate
  → submit → back to dashboard → refresh browser to prove persistence) — this is
  the one deliverable that genuinely can't be produced without a human at the
  keyboard
