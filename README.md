# Devstream Portfolio
A compact showcase of a Spring Boot application and accompanying infrastructure patterns (Docker, Helm, Terraform) used to demonstrate modern Java + DevOps practices.

## Features
- A **Spring Boot** application exposes REST APIs feature flags (create, list, get, patch, delete)
- Data is stored in **PostgreSQL**
- The app is packaged as a **Docker image**
- **Docker Compose** is used for local development
- **Helm** and **Terraform** describe how it would run in Kubernetes
- **GitHub Actions** provides CI for build + test + k6 / Allure steps
- **Tekton** manifests mirror the CI pipeline in a Kubernetes-native way
- **Testcontainers**, **k6** and **Allure** cover integration, smoke and reporting

## Stack
- **Backend:** Spring Boot 3 / Java 21
- **Data:** PostgreSQL
- **Build:** Maven . Docker (multi-stage)
- **Runtime:** Docker Compose . Kubernetes (via Helm)
- **Infra as Code:** Terraform (Kubernetes namespace + config)
- **CI:** GitHub Actions [![CI](https://github.com/DMDwyer/devstream-portfolio/actions/workflows/ci.yml/badge.svg)](https://github.com/DMDwyer/devstream-portfolio/actions/workflows/ci.yml)
- **Kubernetes-native CI:** Tekton ![Tekton Pipeline](https://img.shields.io/badge/Tekton-Pipeline-blue?logo=tekton&logoColor=white)
- **Testing:** JUnit 5 . Testcontainers . K6 smoke tests 
- **Reporting:** Allure [![Allure Report](https://img.shields.io/badge/Allure-Report-blue?logo=qameta)](https://dmdwyer.github.io/devstream-portfolio/allure-latest/)
- **Observability:** Spring Boot Actuator . Micrometer Prometheus Registry

### High-Level Flow

````mermaid
flowchart LR
  API[Spring Boot API] --> DB[(PostgreSQL)]
  API --> Metrics[Actuator / Micrometer]
  Metrics --> Prom[Prometheus / Grafana]

  subgraph CI/CD
    GH[GitHub Actions] --> Build[Build & Test]
    Build --> Image[Docker Image]
    Helm --> K8s[(Kubernetes Namespace)]
    Tekton[Tekton Pipeline] --> K8s
  end

  k6[k6 Smoke Tests] --> API
  Testcontainers[Testcontainers] --> DB
  Allure[Allure Reports] --> GH
````

## Run

Build and run tests:
```bash
./mvnw test
```

Run the app in development mode: 

```bash
mvn spring-boot:run
```

Build a runnable artifact:

```bash
./mvnw -DskipTests package
java -jar target/devstream-portfolio-0.0.1-SNAPSHOT.jar
```

Run via Docker Compose (example infra under `infra/docker`):

```bash
docker compose -f infra/docker/docker-compose.yml up --build
```

## API
Base path: `/flags`

- `POST /flags` - Create a new feature flag. Input is validated against `FlagDto`.
- `GET /flags` - List flags (supports Spring`Pageable` query params: `page`, `size`, `sort`).
- `GET /flags/{key}` - Retrieve a flag by `flagKey`, returns `404` if not found.
- `PATCH /flags/{key}` - Partial update (fields set to `null` are ignored; `enabled` is a boxed `Boolean` so it can be omitted in patches).
- `DELETE /flags/{key}` - Delete a flag, returns `204 No Content` on success.
- `GET /flags/{key}/evaluate` - Evaluate the flag for a `userId` with optional attribute query params (e.g. `?userId=123&country=IE`).

Full request and response schemas are available in the project's Swagger spec. This can be found at runtime at the following path:

- Swagger UI: `/swagger-ui.html` or `/swagger-ui/index.html`

## Development notes
- MapStruct generated mappers: `target/generated-sources/annotations/...` after building. If you change DTOs, rebuild to regenerate mappers.
- The `enabled` field uses a boxed `Boolean` to allow null in partial updates; primitives will always be applied by MapStruct and cannot be ignored.

## Quality & Test Reporting

### Allure Test Reports

The project uses Allure Framework for rich test reporting with detailed test execution history, categorization, and analytics.

**Running tests and generating reports:**
```bash
# Run tests (generates allure-results/)
./mvnw test

# Generate and view HTML report
./mvnw allure:serve
```

Or use the convenience script:
```bash
chmod +x generate-allure-report.sh
./generate-allure-report.sh
```

**Features:**
- Tests organized by Epic → Feature → Story
- Severity levels (BLOCKER, CRITICAL, NORMAL)
- Detailed test descriptions and execution history
- Automatic report generation in CI/CD pipeline

**CI/CD Integration:**
- Test results uploaded as artifacts on every build
- Allure reports generated and published automatically
- Available in GitHub Actions artifacts tab

### Integration Test with Testcontainers

Some tests use [Testcontainers](https://testcontainers.org/) to run PostgreSQL in a disposable Docker container for realistic integration testing.

This allows repository/service layers to be tested against a real database locally and in CI without relying on shared infrastructure.

### k6 Smoke Tests

```bash
# Start the application
./mvnw -DskipTests spring-boot:run

# Run k6 tests (use --network host on Linux)
docker run --rm -i --network host \
  -v "$PWD/src/test/k6:/scripts" \
  grafana/k6 run /scripts/smoke.js
```

For Docker Desktop (Mac/Windows) or systems where `host.docker.internal` works:
```bash
docker run --rm -i \
  -e BASE_URL=http://host.docker.internal:8080 \
  -v "$PWD/src/test/k6:/scripts" \
  grafana/k6 run /scripts/smoke.js
```

## Observability

The service exposes production-grade health, readiness and metrics endpoints via Spring Boot Actuator and Micrometer.

### Endpoints
- `/actuator/health/liveness`
- `/actuator/health/readiness`
- `/actuator/prometheus` (Prometheus-formatted metrics)
- `/actuator/metrics`

### Custom Metrics
A custom `flag_evaluation_total` metric tracks the number of flag evaluations.

This is compatible with:
- Kubernetes probes
- Prometheus scraping
- Grafana dashboards
- k6 smoke/performance tests

## Terraform (Kubernetes namespace + config)
The `infra/terraform` folder contains Terraform code to provision:
- A Kubernetes namespace for the service
- A basic ConfigMap for feature flag defaults

```bash
cd infra/terraform
terraform init
terraform apply
```
## Tekton CI/CD Pipeline
The infra/tekton directory contains a lightweight Tekton Pipeline definition that mirrors the GitHub Actions CI workflow
This demonstrates how the service could be built and tested inside a Kubernetes-native CI/CD system, a common modern DevOps practice.

## Files Included
- `task-build.yaml` - a Tekton Task that executes a Maven build and runs testssts
- `pipeline.yaml` - a Pipeline composed of the build Task

## Running the Pipeline
```bash
kubectl apply -f infra/tekton/task-build.yaml
kubectl apply -f infra/tekton/pipeline.yaml
```