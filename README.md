# 🚗 Vehicles Service

[![Build](https://github.com/albidcs/vehicles-service/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/albidcs/vehicles-service/actions/workflows/ci.yml)
[![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-6DB33F?logo=spring-boot&logoColor=white)](#)
[![OpenAPI](https://img.shields.io/badge/Swagger%20UI-/ui-85EA2D?logo=swagger&logoColor=white)](#)
[![PostgreSQL](https://img.shields.io/badge/DB-PostgreSQL-4169E1?logo=postgresql&logoColor=white)](#)
[![Flyway](https://img.shields.io/badge/DB-Flyway-red?logo=flyway&logoColor=white)](#)
[![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)](#)
[![JUnit5](https://img.shields.io/badge/Test-JUnit5-25A162?logo=junit5&logoColor=white)](#)
[![Mockito](https://img.shields.io/badge/Test-Mockito-pink?logo=mockito&logoColor=white)](#)
[![Docker](https://img.shields.io/badge/Container-Docker-2496ED?logo=docker&logoColor=white)](#)
[![License: MIT](https://img.shields.io/badge/License-MIT-black.svg)](LICENSE)

A **REST** API built with **Spring Boot** for managing vehicles.  
It demonstrates **Clean Architecture principles** (Hexagonal / Ports & Adapters) and **Domain-Driven Design (DDD)**, ensuring a **domain-centric, framework-agnostic core** with clear separation of concerns.

✨ Designed for:
- 🔹 Maintainability — business logic isolated from frameworks
- 🔹 Testability — pure domain can be tested without Spring/DB
- 🔹 Extensibility — adapters can evolve independently (swap JPA, add caching, new APIs)

## 🚀 API Endpoints (Ports)

Interactive documentation available at **[Swagger UI](http://localhost:8080/ui)** when app is running .

| Method   | Endpoint                                | Description                                                                 |
|----------|-----------------------------------------|-----------------------------------------------------------------------------|
| **POST** | [`/vehicles`](http://localhost:8080/ui#/default/createUsingPOST)      | Create a new vehicle                                                        |
| **GET**  | [`/vehicles/{id}`](http://localhost:8080/ui#/default/getByIdUsingGET) | Fetch a vehicle by its unique ID                                            |
| **GET**  | [`/vehicles`](http://localhost:8080/ui#/default/searchUsingGET)       | Search vehicles by filters (`make`, `model`, `modelYear`, `type`, `fuelType`, `vin`, `registrationNumber`) |
| **PUT**  | [`/vehicles/{id}`](http://localhost:8080/ui#/default/updateUsingPUT)  | Update an existing vehicle by ID                                            |
| **DELETE** | [`/vehicles/{id}`](http://localhost:8080/ui#/default/deleteUsingDELETE) | Delete a vehicle by ID                                                      |                                                   |


###  Examples
### 📌 Create a Vehicle
**Request**

```bash
curl -X POST http://localhost:8080/vehicles \
  -H "Content-Type: application/json" \
  -d '{
        "vin": "1HGCM82633A123456",
        "type": "CAR",
        "make": "Toyota",
        "model": "Corolla",
        "modelYear": 2020,
        "fuelType": "PETROL",
        "color": "Blue",
        "registrationNumber": "ABC123"
      }'
```
Response — 201 Created ✅
```bash
{
  "id": 1,
  "vin": "1HGCM82633A123456",
  "type": "CAR",
  "make": "Toyota",
  "model": "Corolla",
  "modelYear": 2020,
  "fuelType": "PETROL",
  "color": "Blue",
  "registrationNumber": "ABC123"
}
```


### 📌 Get Vehicle by ID
```bash
curl -X GET http://localhost:8080/vehicles/1 -H "Accept: application/json"
```
Response — 200 OK ✅
```bash
 {
  "id": 1,
  "vin": "1HGCM82633A123456",
  "type": "CAR",
  "make": "Toyota",
  "model": "Corolla",
  "modelYear": 2020,
  "fuelType": "PETROL",
  "color": "Blue",
  "registrationNumber": "ABC123"
}
```

### 📌 Search Vehicles
```bash
curl -X GET "http://localhost:8080/vehicles?make=Toyota&model=Corolla&modelYear=2020" -H "Accept: application/json"
```
Response — 200 OK ✅
```bash
 [
  {
    "id": 6,
    "vin": "1HGCM82633A123456",
    "type": "CAR",
    "make": "Toyota",
    "model": "Corolla",
    "modelYear": 2020,
    "fuelType": "PETROL",
    "color": "Blue",
    "registrationNumber": "ABC123"
  }
]
```


### 📌 Update Vehicle
```bash
curl -X PUT http://localhost:8080/vehicles/1 \
  -H "Content-Type: application/json" \
  -d '{
        "vin": "1HGCM82633A123456",
        "type": "CAR",
        "make": "Toyota",
        "model": "Corolla",
        "modelYear": 2021,
        "fuelType": "PETROL",
        "color": "Red",
        "registrationNumber": "ABC123"
      }'

```
Response — 200 OK ✅
```bash
 {
  "id": 1,
  "vin": "1HGCM82633A123456",
  "type": "CAR",
  "make": "Toyota",
  "model": "Corolla",
  "modelYear": 2021,
  "fuelType": "PETROL",
  "color": "Red",
  "registrationNumber": "ABC123"
}
```

### 📌 Delete Vehicle
```bash
curl -X DELETE http://localhost:8080/vehicles/1
```
Response — 204 No Content ✅
```bash
➡️ (empty body)
```



### ⚠️ Error Model

All errors follow a consistent JSON structure:

```bash
{
  "code": "VALIDATION_ERROR | INVALID_ARGUMENT | NOT_FOUND | CONFLICT | INTERNAL",
  "message": "Human-readable summary of the error",
  "fieldErrors": [
    {
      "field": "vin",
      "message": "must not be blank"
    }
  ]
}
```






## 🏗️ Architecture

This project follows **Hexagonal Architecture (Ports & Adapters)**, also aligned with **Google Clean Architecture** principles:

- ![Domain](https://img.shields.io/badge/Layer-Domain-blue)  
  Pure Java (entities, value objects, repository interfaces, domain exceptions). No framework dependencies.  

- ![Application](https://img.shields.io/badge/Layer-Application-green)  
  Services that orchestrate business logic via domain ports.  

- ![Adapters](https://img.shields.io/badge/Layer-Adapters-orange)  
  - **Web (inbound):** REST controllers, DTOs, validation, exception handling.  
  - **Persistence (outbound):** JPA entities, repositories, mappers, adapters.  

- ![Bootstrap](https://img.shields.io/badge/Layer-Bootstrap-yellow)  
  App startup, Spring configuration, and OpenAPI setup.  

- ![Infrastructure](https://img.shields.io/badge/Layer-Infrastructure-lightgrey)  
  Currently reserved for future use (security, messaging, external integrations).  

➡️ **Benefit:** Framework-agnostic core with testable business logic. Adapters can evolve independently (e.g., swap JPA, add caching, change web layer).

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

## ⚙️ Tech Stack

- ☕ **Java 21**  
- 🌱 **Spring Boot 3.5.4**  
- 🌍 **Spring Web (REST APIs)**  
- 🗄️ **Spring Data JPA** + 📦 **Hibernate ORM**  
- 🐘 **PostgreSQL** (with 🛫 **Flyway migrations**)  
- 📘 **OpenAPI / Swagger UI** (`/ui`)  
- ✅ **Spring Validation** (Jakarta Bean Validation with annotations like `@NotBlank`, `@Size`, `@Min`, `@Max`)  
- 🧪 **JUnit 5** + 🎭 **Mockito** + ✅ **AssertJ** (unit & integration testing)  
- 🔨 **Maven** (build & dependency management)  
- 🐳 **Docker** (DB provisioning & local dev)

# Getting Started

## Prerequisites
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



---

### Database Migrations

- Managed via [Flyway](https://flywaydb.org/).
- Migration files are under `src/main/resources/db/migration`.


## 🗄️ Database Schema

**vehicles** table:

| Column              | Type         | Constraints                       | Description                         |
|---------------------|--------------|-----------------------------------|-------------------------------------|
| id                  | BIGINT       | PK (auto-increment)               | Auto-generated primary key           |
| make                | VARCHAR(100) | NOT NULL                          | Manufacturer (e.g., Toyota)         |
| model               | VARCHAR(100) | NOT NULL                          | Model name                          |
| model_year          | INT          | NOT NULL                          | Model production year (>= 1886)     |
| vin                 | VARCHAR(17)  | NOT NULL, UNIQUE                  | Vehicle Identification Number (VIN) |
| type                | VARCHAR(20)  | NOT NULL                          | Vehicle type (e.g., CAR, TRUCK)     |
| fuel_type           | VARCHAR(20)  | NOT NULL                          | Fuel type (PETROL, DIESEL, ELECTRIC)|
| color               | VARCHAR(40)  | NULLABLE                          | Optional vehicle color              |
| registration_number | VARCHAR(20)  | UNIQUE, NULLABLE                  | License plate number                |

**Indexes**:  
- PK → `vehicles_pkey` (on `id`)  
- Unique → `uk_vehicles_vin` (on `vin`)  
- Unique → `uk_vehicles_registration` (on `registration_number`)  
- Additional indexes → on `make`, `model`, `model_year`

**Indexes**:  
- `idx_vehicles_make` on `make`  
- `idx_vehicles_model` on `model`  
- `idx_vehicles_year` on `model_year`  

### Testing

Run all unit tests:

```bash
./mvnw test
```

---

#### License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
