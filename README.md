# Vehicles Service

A clean architecture Spring Boot service for managing vehicles. Demonstrates domain-driven design and separation of concerns.

---

## Architecture

```
vehicles-service
├── src
│   ├── main
│   │   ├── java/io/github/albi/vehicles
│   │   │   ├── adapters                # Adapters layer (connects domain <-> external systems)
│   │   │   │   ├── persistence/vehicle
│   │   │   │   │   ├── VehicleEntity.java              # JPA entity mapped to `vehicles` table
│   │   │   │   │   ├── VehicleJpaRepository.java       # Spring Data JPA repository
│   │   │   │   │   ├── VehicleMapper.java              # Converts between domain Vehicle <-> VehicleEntity
│   │   │   │   │   ├── VehicleRepositoryJpaAdapter.java# Adapter that implements domain `VehicleRepository` using JPA
│   │   │   │   │   └── package-info.java               # Package-level documentation
│   │   │   │   └── web/vehicle
│   │   │   │       ├── dto
│   │   │   │       │   ├── VehicleRequest.java         # Incoming request DTO (used in POST/PUT APIs)
│   │   │   │       │   ├── VehicleResponse.java        # Outgoing response DTO
│   │   │   │       ├── VehicleController.java          # REST controller exposing `/vehicles` endpoints
│   │   │   │       ├── GlobalExceptionHandler.java     # Handles validation & runtime errors → JSON response
│   │   │   │       └── package-info.java
│   │   │   │
│   │   │   ├── application/vehicle
│   │   │   │   ├── VehicleService.java                 # Application service with business use cases (CRUD, search)
│   │   │   │   └── package-info.java
│   │   │   │
│   │   │   ├── bootstrap
│   │   │   │   ├── VehiclesApplication.java            # Spring Boot entrypoint (`main` class)
│   │   │   │   ├── ApplicationServiceConfig.java       # Wires domain → adapter (manual Spring beans if needed)
│   │   │   │   ├── OpenApiConfig.java                  # Swagger/OpenAPI configuration
│   │   │   │   └── package-info.java
│   │   │   │
│   │   │   ├── domain/vehicle                          # Pure domain layer (business rules & models)
│   │   │   │   ├── Vehicle.java                        # Aggregate root (domain model for a Vehicle)
│   │   │   │   ├── VehicleId.java                      # Value object wrapper for ID
│   │   │   │   ├── Vin.java                            # Value object wrapper for VIN (validation inside)
│   │   │   │   ├── VehicleType.java                    # Enum: CAR, TRUCK, etc.
│   │   │   │   ├── FuelType.java                       # Enum: PETROL, DIESEL, ELECTRIC, etc.
│   │   │   │   ├── VehicleRepository.java              # Domain port (repository interface)
│   │   │   │   ├── VehicleNotFoundException.java       # Domain-specific exception
│   │   │   │   └── package-info.java
│   │   │   │
│   │   │   └── infrastructure/config
│   │   │       └── package-info.java                   # Reserved for infra config
│   │   │
│   │   └── resources
│   │       ├── application.yml                         # Main Spring Boot config (DB, server, etc.)
│   │       └── db/migration                            # Flyway migration scripts
│   │           ├── V1__create_vehicles.sql             # Initial table creation
│   │           ├── V2__rename_year_to_model_year.sql   # Migration: column rename
│   │           └── V3__vehicle_richer_fields.sql       # Migration: add VIN, type, fuelType, etc.
│   │
│   ├── test
│   │   ├── java/io/github/albi/vehicles
│   │   │   ├── adapters/persistence/vehicle
│   │   │   │   ├── VehicleMapperTest.java              # Unit tests for mapping Entity <-> Domain
│   │   │   │   └── VehicleRepositoryJpaAdapterTest.java# Tests for persistence adapter with in-memory DB
│   │   │   │
│   │   │   ├── web/vehicle
│   │   │   │   └── VehicleControllerTest.java          # MockMvc/Web tests for REST API endpoints
│   │   │   │
│   │   │   ├── domain/vehicle
│   │   │   │   ├── VehicleIdTest.java                  # Unit test for VehicleId validation
│   │   │   │   ├── VehicleServiceTest.java             # Unit test for service logic (with fake repo)
│   │   │   │   └── VehicleTest.java                    # Unit test for Vehicle domain validation rules
│   │   │
│   │   └── resources
│   │       └── application-test.yml                    # Test-specific Spring config (H2 DB, etc.)
│   │
│   └── target                                          # Build artifacts (compiled classes, JAR, etc.)
│
├── .mvn                                                # Maven wrapper
├── .idea                                               # IntelliJ project files
```

⚙️ Technologies Used
•	☕ Java 21
•	🌱 Spring Boot 3.5.4
•	🌍 Spring Web (REST APIs)
•	🗄️ Spring Data JPA
•	📦 Hibernate ORM
•	🐘 PostgreSQL
•	🛫 Flyway (DB migrations)
•	📘 OpenAPI / Swagger UI
•	🧪 JUnit 5
•	🎭 Mockito
•	✅ AssertJ
•	🔨 Maven
•	🐳 Docker

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
