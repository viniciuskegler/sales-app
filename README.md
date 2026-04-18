# Sales App

A marketplace application with a mock payment gateway, built as a full-stack learning project.

## Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 4, Java 21, PostgreSQL 16 |
| Frontend | Angular 21, Tailwind CSS 4, SSR |
| Auth | JWT (JJWT) |
| Cache | Redis (local) / ElastiCache t4g.micro (prod) |
| Messaging | RabbitMQ (local) / SQS (prod) |
| Migrations | Liquibase |
| Infrastructure | AWS CDK (Java) |

## Architecture

```
Browser
   │
   ▼
Frontend App Runner (Angular SSR / Express)
   │  /api/**  →  proxy
   ▼
Backend App Runner (Spring Boot)  ──►  PostgreSQL (RDS)
                                  ──►  Redis (ElastiCache)
                                  ──►  SQS (payment events)
                                  ▲
EventBridge (every 2 min) ──► Lambda ──► POST /api/internal/advance-statuses
```

> **WebSocket note**: App Runner does not support WebSocket connections. In production, the payment page polls the order status every 3 seconds instead of using WebSocket push. The notification bell is visible but does not receive real-time updates. Supporting WebSocket on App Runner would require a NAT gateway, an NLB, and a separate proxy container — not implemented.

### Backend — domain-driven packages

```
com.viniciuskegler.salesapp
├── auth/        JWT filter, security config, login/register
├── user/        User entity and service
├── customer/    Customer profile endpoints
├── product/     Products, reviews, categories, caching
├── order/       Order placement, history, status advancement
├── payment/
│   ├── rabbitmq/    RabbitMQ config, publisher, simulation consumer (dev)
│   └── sqs/         SQS config, publisher, simulation consumer (prod)
└── shared/      Exception handling, Redis config, pagination, WebSocket config
```

Public endpoints: `POST /api/auth/login`, `POST /api/auth/register-customer`, `GET /api/products/**`

Internal endpoint (secret-header auth, used by Lambda): `POST /api/internal/advance-statuses`

Dev-only endpoint (no auth, inactive in prod): `POST /api/dev/advance-statuses`

All other endpoints require a Bearer JWT.

### Frontend — feature-based structure

```
src/app
├── core/        Layout, services, interceptors, guards
├── features/    Products, auth, cart, orders, payment, notifications
└── shared/      Zard UI component library (CVA-based)
```

## Running locally

### Prerequisites

- Java 21
- Node.js 20+
- Docker (required for integration tests and local services)

### Dev scripts (from project root)

```bash
./dev.sh        # unit tests only (no Docker), then start backend
./dev-full.sh   # all tests including integration tests, then start backend
```

Both scripts abort if any test fails.

### Manual

**Start services**
```bash
docker compose up -d
```

**Backend** — requires a `backend/.env` file with `JWT_SECRET`, `JWT_EXPIRATION`, and `INTERNAL_API_SECRET`:
```bash
cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
# http://localhost:8080
```

**Frontend**
```bash
cd frontend && npm start
# http://localhost:4200 — proxies /api/** to :8080
```

**Backend tests**
```bash
# Unit tests only (no Docker needed)
cd backend && ./mvnw test -Dtest='!*IT'

# All tests including integration tests (requires Docker)
cd backend && ./mvnw test
```

**Advancing order statuses in dev**

With the backend running, call the dev endpoint directly (no auth required):
```bash
curl -X POST http://localhost:8080/api/dev/advance-statuses
```

## Infrastructure (AWS)

Infrastructure is defined as code in `infra/` using AWS CDK (Java). The stack provisions:

- **App Runner (backend)** — hosts the Spring Boot container, VPC connector for DB/Redis access, auto-deploys on ECR push
- **App Runner (frontend)** — hosts the Angular SSR/Express container, proxies `/api/**` and `/ws` to the backend
- **RDS PostgreSQL t4g.micro** — single-AZ, accessible only via VPC connector
- **ElastiCache t4g.micro** — provisioned Redis node (cheaper than Serverless at low traffic)
- **SQS** — payment event queue (`salesapp-payment-events`)
- **Lambda + EventBridge** — advances order statuses every 2 minutes (`CONFIRMED→SHIPPED→DELIVERED`)
- **ECR** — two image repositories: `salesapp-backend` and `salesapp-frontend`
- **Secrets Manager** — DB password, JWT secret, and internal API secret, injected at runtime

No NAT gateway — all resources use public subnets with security group restrictions to keep costs low.

### Deploy

The infrastructure is split into two stacks to avoid a chicken-and-egg problem where App Runner needs an image that doesn't exist yet.

```bash
cd infra

# First time only
cdk bootstrap

# Step 1 — provision base infrastructure (VPC, RDS, Cache, SQS, ECR, IAM)
cdk deploy InfraStack
```

Once `InfraStack` completes, it outputs the ECR URIs. Build and push both images:

```bash
./publish.sh
```

```bash
# Step 2 — deploy App Runner services and Lambda (requires images to exist in ECR)
cd infra
cdk deploy AppStack
```

Subsequent deploys only require pushing new images — `publish.sh` handles both:

```bash
./publish.sh
```

App Runner detects new images and redeploys automatically.

### CI/CD (GitHub Actions)

A workflow at `.github/workflows/ci.yml` automates testing and deployment:

- **On every push/PR to `main`** — runs backend unit tests
- **On push to `main` only** — if tests pass, builds and pushes both Docker images to ECR and triggers App Runner deployments

Required GitHub repository secrets:

| Secret | Description |
|---|---|
| `AWS_ACCESS_KEY_ID` | IAM user access key with ECR push permissions |
| `AWS_SECRET_ACCESS_KEY` | IAM user secret key |

Add them under **Settings → Secrets and variables → Actions**.

## Environment variables (production)

Set as App Runner environment variables. Secrets are injected from Secrets Manager.

**Backend**

| Variable | Source                            |
|---|-----------------------------------|
| `SPRING_PROFILES_ACTIVE` | `prod` (set by CDK)               |
| `DB_HOST` | RDS endpoint (CDK output)         |
| `DB_NAME` | `salesapp`                        |
| `DB_USER` | `salesapp`                        |
| `DB_PASSWORD` | Secrets Manager                   |
| `REDIS_HOST` | ElastiCache endpoint (CDK output) |
| `JWT_SECRET` | Secrets Manager                   |
| `JWT_EXPIRATION` | `86400000`                         |
| `SQS_QUEUE_URL` | SQS queue URL (CDK output)        |
| `INTERNAL_API_SECRET` | Secrets Manager                   |

**Frontend**

| Variable | Source |
|---|---|
| `BACKEND_URL` | Backend App Runner URL (CDK output) |
| `PORT` | `4000` (default, set by App Runner config) |

## Payment flow

```
POST /api/orders
        │
        ▼
Backend publishes PaymentEvent → RabbitMQ (local) / SQS (prod)
        │
        ▼
PaymentSimulationConsumer processes event (simulates ~4s delay, 80% approval rate)
        │
        ▼
Order status updated → CONFIRMED or CANCELLED
        │
        ▼
OrderStatusPublisher pushes to:
  ├── /topic/orders/{id}          → payment page (WebSocket in dev, polling in prod)
  └── /topic/users/{userId}/notifications → notification bell (WebSocket dev only)
```

Locally, RabbitMQ is used instead of SQS. The active Spring profile (`dev` vs `prod`) determines which implementation is wired.

## Order status advancement

After payment confirmation, orders progress through further statuses via a scheduled job:

```
EventBridge (every 2 min)
        │
        ▼
Lambda → POST /api/internal/advance-statuses (X-Internal-Secret header)
        │
        ▼
OrderStatusAdvancerService advances: CONFIRMED → SHIPPED → DELIVERED
        │
        ▼
OrderStatusPublisher pushes updates (WebSocket in dev, polling picks them up in prod)
```