# Devstream Portfolio
A compact showcase of a Spring Boot application and accompanying infrastructure patterns (Docker, Helm, Terraform) used to demonstrate modern Java + DevOps practices.

## Features
- Manage, feature flags (create, list, get, patch, delete)
- Evaluate flags for users with attribute-based rules
- Pageable list endpoints and validation on DTOs

## Stack
- **Backend:** Spring Boot 3 / Java 21
- **Infra:** Docker . Helm . Terraform
- **CI/CD:** GitHub Actions / Tekton
![CI](https://github.com/DMDwyer/devstream-portfolio/actions/workflows/ci.yml/badge.svg)
- **Testing:** K6 . Allure

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

- `POST /flags` - Create a new featrue flag. Input is valiated against `FlagDto`.
- `GET /flags` - List flags (supports Spring`Pageable` query params: `page`, `size`, `sort`).
- `GET /flags/{key}` - Retrieve a flag by `flagKey`, returns `404` if not found.
- `PATCH /flags/{key}` - Partial update (fields set to `null` are ignored; `enabled` is a boxed `Boolean` so it can be omitted in patchesl).
- `DELETE /flags/{key}` - Delete a flag, returns `204 No Content` on success.
- `GET /flags/{key}/evaluate` - Evaluate the flag for a `userId` with optional attribute query params (e.g. `?userId=123&courntry=IE`).

Full request and response schemas are available in the project's Swagger spec. This can be found at runtime at the following path:

- Swagger UI: `/swagger-ui.html` or `/swagger-ui/index.html`

## Development notes
- MapStruct generated mappers: `target/generated-sources/annotations/...` after building. If you change DTOs, rebuild to regenerate mappers.
- The `enabled` field uses a boxed `Boolean` to allow null in partial updates; primitives will always be applied by MapStruct and connot be ignored.


## Terraform (Kubernetes namespace + config)
The 'infra/terraform' folder contains Terraform code to provisioon:
- A Kubernetes namespace for the service
- A basic ConfigMap for feature flag defaults

```bash
cd infra/terraform
terraform init
terraform apply
```


