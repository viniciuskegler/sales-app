# Sales App

A marketplace application with a mock payment gateway, built as a full-stack learning project.

## Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 4, Java 21, PostgreSQL 16 |
| Frontend | Angular 21, Tailwind CSS 4, SSR |
| Auth | JWT (JJWT) |
| Cache | Redis (local) / ElastiCache Serverless (prod) |
| Messaging | RabbitMQ (local) / SQS (prod) |
| Migrations | Liquibase |
| Infrastructure | AWS CDK (Java) |

## Architecture

```
Frontend (Angular SSR)
        │
        ▼
Backend (Spring Boot)  ──►  PostgreSQL
        │              ──►  Redis (cache)
        │              ──►  RabbitMQ / SQS (payment events)
        ▼
Lambda (mock payment processor)
```

### Backend — domain-driven packages

```
com.viniciuskegler.salesapp
├── auth/        JWT filter, security config, login/register
├── user/        User entity and service
├── customer/    Customer profile endpoints
├── product/     Products, reviews, categories, caching
└── shared/      Exception handling, Redis config, pagination
```

Public endpoints: `POST /api/auth/login`, `POST /api/auth/register-customer`, `GET /api/products/**`

All other endpoints require a Bearer JWT.

### Frontend — feature-based structure

```
src/app
├── core/        Layout, services, interceptors, guards
├── features/    Products, auth, cart, filters, search
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

**Backend** — requires a `backend/.env` file with `JWT_SECRET` and `JWT_EXPIRATION`:
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

## Infrastructure (AWS)

Infrastructure is defined as code in `infra/` using AWS CDK (Java). The stack provisions:

- **App Runner** — hosts the containerized backend, auto-deploys on new ECR image push
- **RDS PostgreSQL t4g.micro** — single-AZ, accessible only via VPC connector
- **ElastiCache Serverless** — Redis, pay-per-use
- **SQS** — payment event queue (`salesapp-payment-events`)
- **ECR** — Docker image repository (`salesapp-backend`)
- **Secrets Manager** — DB password and JWT secret, injected at runtime

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

Once `InfraStack` completes, it outputs the ECR URI. Build and push the backend image:

```bash
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <ECR_URI>

cd backend
docker build -t salesapp-backend .
docker tag salesapp-backend:latest <ECR_URI>:latest
docker push <ECR_URI>:latest
```

```bash
# Step 2 — deploy App Runner (requires the image to exist in ECR)
cd infra
cdk deploy AppStack
```

Subsequent backend deploys only require pushing a new image:

```bash
./publish.sh
```

This script builds the image, authenticates to ECR, and pushes — App Runner redeploys automatically.

## Environment variables (production)

Set as App Runner environment variables. Secrets are injected from Secrets Manager.

| Variable | Source |
|---|---|
| `DB_HOST` | RDS endpoint (CDK output) |
| `DB_NAME` | `salesapp` |
| `DB_USER` | `salesapp` |
| `DB_PASSWORD` | Secrets Manager |
| `REDIS_HOST` | ElastiCache endpoint (CDK output) |
| `JWT_SECRET` | Secrets Manager |
| `JWT_EXPIRATION` | `36000` |
| `SQS_QUEUE_URL` | SQS queue URL (CDK output) |

## Payment flow (planned)

```
POST /api/orders
        │
        ▼
Backend publishes OrderCreatedEvent → SQS
        │
        ▼
Lambda polls SQS, simulates processing, calls back POST /api/orders/{id}/payment-result
        │
        ▼
Backend updates order status
```

Locally, RabbitMQ is used instead of SQS. The active Spring profile (`dev` vs `prod`) determines which implementation is wired.
