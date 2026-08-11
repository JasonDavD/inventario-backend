# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Spring Boot 3.2 / Java 17 backend for "Ferreteria Zamora" inventory management (CIBERTEC coursework). Exposes a REST API and a server-rendered Thymeleaf web portal against the same JPA entities.

## Commands

```bash
./mvnw spring-boot:run       # run locally, http://localhost:8080
./mvnw clean package         # build jar (target/inventario-0.0.1-SNAPSHOT.jar)
./mvnw test                  # run tests (no tests exist yet in the repo)
```

There is no linter configured. `mvnw.cmd` is the only wrapper script checked in (Windows); use `mvnw` if present on other platforms.

## Architecture

Three parallel entities — `Producto`, `Categoria`, `Proveedor` — each following the identical layered pattern in `src/main/java/com/inventario/`:

- **model/** — JPA `@Entity` classes, plain getters/setters, no Lombok. `Producto` has `@ManyToOne` relations to `Categoria` and `Proveedor` and sets `fechaRegistro` via `@PrePersist`.
- **repository/** — `JpaRepository<Entity, Long>` interfaces with no custom query methods.
- **controller/** — each entity has **two separate controllers**, not one:
  - `*RestController` — `@RestController` under `/api/{entidad}`, `@CrossOrigin(origins = "*")`, returns entities/DTOs directly as JSON (no service layer).
  - `*WebController` — `@Controller` under `/{entidad}`, returns Thymeleaf view names, handles form posts (`/guardar`, `/actualizar`, `/eliminar/{id}`) and redirects after mutations.

There is no service layer — controllers talk to repositories directly. When adding a new entity, replicate this same four-file shape (model, repository, REST controller, web controller) rather than introducing a service layer or DTOs, to stay consistent with the existing code.

`ProductoRestController`/`ProductoWebController` resolve `categoria`/`proveedor` relations by re-fetching the referenced entity from its repository by id before saving — the incoming `Producto` payload's nested objects are never trusted/persisted as-is.

Templates live in `src/main/resources/templates/` (Thymeleaf), one list + one create + one edit view per entity (e.g. `productos.html`, `nuevo_producto.html`, `editar_producto.html`).

## Configuration

`src/main/resources/application.properties` switches datasource by environment automatically:
- No `DATABASE_URL` env var → H2 in-memory (`jdbc:h2:mem:inventariodb`), H2 console enabled by default at `/h2-console`.
- `DATABASE_URL` set (Render deploy) → PostgreSQL, using `DATABASE_USER`/`DATABASE_PASSWORD`.

`spring.jpa.hibernate.ddl-auto=update` — schema is auto-migrated from entities, no Flyway/Liquibase.

## Deployment

Deployed to Render via `Dockerfile` (multi-stage Maven build → JRE runtime) or via `Procfile`/`system.properties` (Render's native buildpack path). Both entrypoints run the same built jar.
