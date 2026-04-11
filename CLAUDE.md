# Sales App

A marketplace application with a mockup payment gateway.

## References
- Zard UI component docs: https://zardui.com/llms — fetch when working on shared UI components

## Stack

- **Backend**: Spring Boot 4 / Java 21, PostgreSQL 16, Liquibase, JJWT
- **Frontend**: Angular 21, standalone components, Tailwind CSS 4, SSR enabled

## Running the project

### Dev scripts (from project root)

```bash
./dev.sh        # unit tests only (no Docker), then start backend — use for inner dev loop
./dev-full.sh   # all tests including IT (requires Docker) + frontend tests, then start backend
```

Both scripts abort immediately if any test fails (`set -e`).

### Manual

Requires a `.env` file in `backend/` with `JWT_SECRET` and `JWT_EXPIRATION`.

```bash
# Backend
cd backend && ./mvnw spring-boot:run   # http://localhost:8080

# Frontend
cd frontend && npm start               # http://localhost:4200, proxies /api/** to :8080

# Backend tests
cd backend && ./mvnw test              # all tests (unit + IT, IT requires Docker)
cd backend && ./mvnw test -Dtest='!*IT'  # unit tests only, no Docker needed
```

### Test setup note
`*IT.java` tests run via Surefire (not Failsafe) — all tests run together with `mvn test`. There is no separate `mvn verify` phase for IT tests.

## Architecture

### Backend — domain-driven package structure
One package per domain under `com.viniciuskegler.salesapp`:
- `auth/` — JWT filter, security config, auth endpoints
- `user/` — User entity, UserService (login + register)
- `customer/` — Customer entity and endpoints
- `product/` — Product + Review entities and endpoints
- `order/` — Order placement, history, status advancement, WebSocket publisher
- `payment/` — PaymentEventPublisher interface; `rabbitmq/` (dev profile), `sqs/` (prod profile)
- `shared/` — Cross-cutting concerns (exception handler, pagination, WebSocket config, rate limiter)

Public endpoints: `/api/auth/**`, `/api/products/**`. Everything else requires a Bearer JWT.

### Frontend — feature-based structure under `src/app`
- `core/` — layout, services, interceptors, guards
- `features/` — one folder per feature (products, auth, cart, etc.)
- `shared/` — Zard UI component library (custom, CVA-based)

Path aliases: `@core/*`, `@features/*`, `@shared/*`, `@layout/*`

## Conventions

### General
- Always use `{}` braces for `if`/`else if`/`else` blocks — no braceless one-liners, in any language

### Backend
- Constructor injection (no `@Autowired` on fields)
- Services are `@Validated`; controllers use `@Valid` on request bodies
- `@Transactional` on service methods that write to multiple tables
- DTOs live in a `dto/` sub-package within their domain

### Frontend
- Standalone components only — no NgModules (except legacy `HomeRoutingModule`)
- `ChangeDetectionStrategy.OnPush` on all components
- State via signals (`signal`, `computed`); async data via `rxResource`
- `inject()` for dependency injection, not constructor params
- `isPlatformBrowser(PLATFORM_ID)` required before any `localStorage` access (SSR)
- Class member order: inject → inputs → outputs → view/content queries → computed/linked signals → other properties → constructor → lifecycle hooks → methods
  - Exception: plain properties that are used as default values for `model`/`input` (e.g. `paginationOptions`) must be declared before those fields

## Testing

### Backend
- Integration tests: `RestTestClient` + TestContainers — use `.body()` not `.bodyValue()`
- Unit tests: `ReflectionTestUtils` to inject `@Value` fields without loading Spring context
- `CustomerRegisterRequestDTO` has no no-args constructor — Lombok treats `@Nonnull` fields as required args: `new CustomerRegisterRequestDTO(firstName, lastName, phoneNumber)`
- MockMvc tests with Spring Security **require** `.apply(springSecurity())` on `webAppContextSetup`, otherwise security filters don't run and all requests return 200
- `JwtAuthFilter` catches all JWT exceptions silently — invalid tokens leave the SecurityContext empty and Spring Security denies access naturally
- `ApplicationControllerAdvice` handles `BadCredentialsException`/`UsernameNotFoundException` → 401, `DataIntegrityViolationException` → 409

### Frontend
- Karma + Jasmine; run with `npm test`

## WebSocket / real-time updates

WebSocket (STOMP over SockJS) is used in dev for real-time payment status and notifications. **AWS App Runner does not support WebSocket connections** — enabling it would require a NAT gateway, an NLB, and a separate WebSocket proxy container, which is significant infrastructure overhead.

In production (`environment.wsEnabled = false`):
- `PaymentComponent` polls `GET /api/orders/{id}` every 3 seconds until the order reaches a terminal status
- `NotificationService` skips the WebSocket connection entirely — the notification bell is present but won't receive real-time updates

The `environment.wsEnabled` flag in `src/environments/environment*.ts` controls this. Set to `true` in dev, `false` in prod.
