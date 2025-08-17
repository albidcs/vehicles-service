# Vehicles Service

A clean architecture Spring Boot service for managing vehicles. Demonstrates domain-driven design and separation of concerns.

---

## Architecture

```
io.github.albi.vehicles
├── adapters           # web + persistence adapters
│   ├── web.vehicle    # REST controllers
│   └── persistence    # JPA repositories, DB adapters
├── application        # use-case services
├── bootstrap          # Spring Boot entry point
├── domain             # core business model (entities, repositories)
└── infrastructure     # Spring configuration
```

---

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.9+ (or use `./mvnw` wrapper)
- Docker (for PostgreSQL)

### Setup

1. Start PostgreSQL with Docker:
   ```bash
   docker run --name vehicles-db      -e POSTGRES_DB=vehicles      -e POSTGRES_USER=vehicles      -e POSTGRES_PASSWORD=vehicles      -p 5432:5432 -d postgres:16
   ```

2. Run database migrations (Flyway):
   ```bash
   ./mvnw clean flyway:migrate
   ```

3. Start the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The application expects the following default connection (from `application.yml`):
```
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/vehicles
    username: vehicles
    password: vehicles
```

---

## Database Migrations

- Managed via [Flyway](https://flywaydb.org/).
- Migration files are under `src/main/resources/db/migration`.

Example migration:

```sql
-- V1__create_vehicles.sql
create table if not exists vehicles (
   id    bigserial primary key,
   make  varchar(100) not null,
   model varchar(100) not null,
   year  int not null
   );
create index if not exists idx_vehicles_make  on vehicles(make);
create index if not exists idx_vehicles_model on vehicles(model);
create index if not exists idx_vehicles_year  on vehicles(year);
```

---

## Testing

Run all unit tests:

```bash
./mvnw test
```

---

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
