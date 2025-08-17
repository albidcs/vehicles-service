# Vehicles Service

A backend service built with **Spring Boot 3 (Java 21)** following **Clean Architecture** principles.

## Features (in progress)
- Manage vehicles (create, search, find by ID)
- PostgreSQL database with **Flyway migrations**
- Domain-driven design: entities, repositories, and services isolated from frameworks
- Adapters for persistence (JPA) and web (REST, coming soon)

## Tech Stack
- Java 21
- Spring Boot 3
- PostgreSQL + Flyway
- JPA (Hibernate)
- JUnit 5 for testing

## Architecture

io.github.albi.vehicles
├── adapters           # web + persistence adapters
├── application        # use-case services
├── bootstrap          # Spring Boot entry point
├── domain             # core business model
└── infrastructure     # Spring config


## Getting Started
1. Start PostgreSQL (Docker example):
   ```bash
   docker run --name vehicles-db \
     -e POSTGRES_USER=vehicles \
     -e POSTGRES_PASSWORD=vehicles \
     -e POSTGRES_DB=vehicles \
     -p 5432:5432 -d postgres:16

2.Run the service 
   ```bash
   ./mvnw spring-boot:run
