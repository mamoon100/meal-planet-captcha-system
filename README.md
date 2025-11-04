# Meal Planet Captcha System

A lightweight backend-only CAPTCHA service built with Spring Boot. It generates captchas (image- and math-based), validates user answers, exposes analytics, and automatically cleans up expired captcha image files.


## 1) Setup & Run

### Prerequisites
- Java 17+
- Maven 3.9+

### Configuration
Default application configuration is in `src/main/resources/application.properties`:
- `server.port=8080`
- `captcha.lifespan=120` (seconds)
- `spring.datasource.url=jdbc:h2:file:~/testdb` (H2 file database for local/dev)
- H2 console enabled at `/h2-console`

Tests use an isolated in‑memory H2 DB via `src/test/resources/application-test.properties` and the `test` Spring profile.

### Run locally (dev)
``` BASH
./mvnw spring-boot:run
```
Then open: `http://localhost:8080`

### Build a jar
``` BASH
./mvnw -DskipTests package
```

## 2) API Quickstart

Base URL: `/api/v1`

- Generate captcha
```
POST /api/v1/captcha/generate?type=<image or math>
Response: { "id": "<uuid>", "expires_in": <seconds> }
```

- Fetch captcha image bytes (PNG)
```
GET /api/v1/captcha/{id}
Response: image/png
```

- Validate captcha
```
POST /api/v1/captcha/validate/{id}
Body: { "answer": "<user input>" }
Response: { "valid": true | false }
```

- Analytics
```
GET /api/v1/analytic
Response: {
  "image": { "correct": <long>, "incorrect": <long> },
  "math":  { "correct": <long>, "incorrect": <long> }
}
```

### Curl examples
```
# 1) Generate an IMAGE captcha
curl -s "http://localhost:8080/api/v1/captcha/generate?type=image"

# 2) Download the PNG (replace {id})
curl -Lo captcha.png "http://localhost:8080/api/v1/captcha/{id}"

# 3) Validate captcha (replace {id} and YOUR_ANSWER)
curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{"answer":"YOUR_ANSWER"}' \
  "http://localhost:8080/api/v1/captcha/validate/{id}"

# 4) Get analytics
curl -s "http://localhost:8080/api/v1/analytic" | jq
```


## 3) Architecture Overview

### High-level components
- Controllers
  - `CaptchaController` — endpoints for generate, fetch-by-id (PNG), and validate.
  - `AnalyticController` — endpoint to retrieve summary statistics.
- Services
  - `CaptchaService` — core captcha workflow (generation, retrieval, validation, status/expiration checks).
  - `AnalyticService` — aggregates VALID/INVALID counts per captcha type.
- Repository
  - `CaptchaRepo` — Spring Data JPA repo for `CaptchaEntity`.
- Scheduler
  - `CaptchaExpirationScheduler` — runs every minute (cron: `0 * * * * *`) to delete expired captcha image files and update DB flags.
- Utilities
  - `ExpressionGenerationUtil` — creates random strings and math expressions.
  - `ImageGenerationUtil` — renders and saves PNG images (`captcha/output` folder). 
  - `HashingUtil` — hashes answers using SHA‑256; only hashes are stored.
- Error handling
  - `ExceptionAdvisor` — centralized controller advice returning structured errors and helpful messages (e.g., missing/invalid params).

### Data model
- `CaptchaEntity`
  - `id: UUID`
  - `expiresAt: LocalDateTime`
  - `fileName: String` (path to generated PNG)
  - `answer: String` (SHA‑256 hash of the answer)
  - `type: CaptchaTypeEnum` (IMAGE, MATH)
  - `status: CaptchaStatusEnum` (NEW, VALID, INVALID)
  - `isExpired: boolean`

### Request flow (IMAGE)
1. `POST /captcha/generate?type=IMAGE` → generate random string → render PNG → save file → persist entity (`NEW`) with hashed answer and expiry.
2. `GET /captcha/{id}` → validate status is `NEW` and not expired → stream PNG bytes.
3. `POST /captcha/validate/{id}` → check status/expiry → compare hash with user answer → set `VALID` or `INVALID`.


## 4) Design Choices and Trade‑offs

- Spring Boot + H2
  - Chosen for simplicity and speed. File-based H2 in dev persists data between runs; tests use in‑memory H2 for isolation.
- Stateless API surface with server-side state
  - The server stores captcha metadata and answers (hashed). Clients only handle IDs. This simplifies validation but introduces DB I/O per request.
- Image generation to disk
  - Simpler than storing blobs in DB and keeps DB lean; enables easy inspection during development/tests. Trade‑off: requires filesystem access and cleanup.
- SHA‑256 hashing of answers
  - Never store plaintext answers. Improves security with minimal overhead.
- Strict lifecycle
  - Only `NEW` captchas are retrievable/validatable; once validated, further fetches are rejected. Minimizes abuse.
- Centralized exception handling (`ExceptionAdvisor`)
  - Predictable 400 responses with helpful messages on invalid/missing params; 500 with a generic message otherwise.
- Scheduling cadence: every minute
  - Good balance of timeliness and overhead for cleanup in typical loads.


## 5) Future Improvements

- Security & Abuse Mitigation
  - Rate limiting per IP/client and captcha generation quotas.
- Storage & Delivery
  - Option to store images in object storage (S3/GCS) with short-lived signed URLs; make disk storage pluggable.
  - Support SVG or WebP, configurable fonts/noise levels; accessibility options (audio captcha).
- Reliability & Scale
  - Introduce Redis for ephemeral storage and distributed rate limiting.
  - Horizontal scaling docs/sample manifests (Dockerfile, K8s manifests).
  - Observability: metrics (Micrometer/Prometheus), structured logs, tracing.
- API & DX
  - Swagger documentation.
  - More analytics (per time window, per client, response times).
  - Idempotency keys for generate endpoint.
- Testing
  - Add contract tests and performance tests; test coverage badges in CI.
- Configuration
  - Externalize `captcha/output` path and make retention configurable; feature flags for types.

## 6) Notes
- Captcha images are written to `captcha/output` (relative to the working directory). Ensure the process has write permissions.
- After validation, the same captcha cannot be fetched/validated again.
- If `type` is missing or invalid, the API returns HTTP 400 with a clear error message.
- `captcha.lifespan` uses seconds; the service adds a small correctness internally.
