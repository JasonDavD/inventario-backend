# Inventario Backend - Ferreteria Zamora

Backend del Sistema de Gestion de Inventario desarrollado con Spring Boot.

## Tecnologias
- Java 17
- Spring Boot 3.2
- Spring Data JPA
- PostgreSQL (produccion) / H2 (desarrollo)
- Thymeleaf (portal web)
- Maven

## Endpoints REST

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/api/productos` | Listar todos los productos |
| POST | `/api/productos` | Crear un producto |
| PUT | `/api/productos/{id}` | Actualizar un producto |
| DELETE | `/api/productos/{id}` | Eliminar un producto |

## Portal Web
- `/productos` - Listado de productos
- `/productos/nuevo` - Formulario para nuevo producto
- `/productos/editar/{id}` - Formulario para editar producto

## Ejecucion Local
```bash
./mvnw spring-boot:run
```
La aplicacion estara disponible en `http://localhost:8080`

## Curso
Desarrollo de Aplicaciones Moviles I - CIBERTEC (Ciclo V)
