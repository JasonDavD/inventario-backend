# Inventario Backend - Ferreteria Zamora

Backend del Sistema de Gestion de Inventario desarrollado con Spring Boot.

## Tecnologias
- Java 17
- Spring Boot 3.2
- Spring Data JPA
- Spring Security + JWT (autenticacion y roles)
- PostgreSQL (produccion) / H2 (desarrollo)
- Thymeleaf (portal web)
- Maven

## Autenticacion

La API REST (`/api/**`) usa JWT stateless (pensado para ser consumida por la app iOS). El portal web usa sesion con login tradicional.

### Roles
| Rol | Permisos |
|-----|----------|
| `ADMIN` | CRUD completo de productos, categorias, proveedores y usuarios |
| `OPERADOR` | Lee todo; crea y actualiza productos. No puede eliminar productos ni tocar categorias/proveedores |
| `LECTOR` | Solo lectura (GET) |

### Login
```
POST /api/auth/login
Content-Type: application/json

{ "username": "admin", "password": "..." }
```
Devuelve un JWT que se usa en el header `Authorization: Bearer <token>` en el resto de los endpoints de `/api/**`.

El usuario admin inicial se crea solo al arrancar la aplicacion (usuario/password configurables por `ADMIN_USERNAME` / `ADMIN_INITIAL_PASSWORD`, ver Variables de entorno).

## Endpoints REST

| Metodo | Ruta | Descripcion | Rol minimo |
|--------|------|-------------|------------|
| POST | `/api/auth/login` | Login, devuelve JWT | Publico |
| GET | `/api/productos` | Listar productos | LECTOR |
| POST | `/api/productos` | Crear un producto | OPERADOR |
| PUT | `/api/productos/{id}` | Actualizar un producto | OPERADOR |
| DELETE | `/api/productos/{id}` | Eliminar un producto | ADMIN |
| GET | `/api/categorias` | Listar categorias | LECTOR |
| POST / PUT / DELETE | `/api/categorias`, `/api/categorias/{id}` | Gestionar categorias | ADMIN |
| GET | `/api/proveedores` | Listar proveedores | LECTOR |
| POST / PUT / DELETE | `/api/proveedores`, `/api/proveedores/{id}` | Gestionar proveedores | ADMIN |
| GET / POST / PUT / DELETE | `/api/usuarios`, `/api/usuarios/{id}` | Gestion de usuarios | ADMIN |

## Portal Web
Requiere login (`/login`). Rutas principales por recurso (`productos`, `categorias`, `proveedores`):
- `/productos` - Listado
- `/productos/nuevo` - Formulario de alta
- `/productos/editar/{id}` - Formulario de edicion

## Variables de entorno

| Variable | Uso |
|----------|-----|
| `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD` | Conexion a PostgreSQL (Render). Sin definir, usa H2 en memoria |
| `JWT_SECRET` | Secreto para firmar los JWT (minimo 32 caracteres) |
| `JWT_EXPIRATION_MINUTES` | Expiracion del token en minutos (default 120) |
| `ADMIN_USERNAME`, `ADMIN_INITIAL_PASSWORD` | Credenciales del usuario admin inicial |
| `H2_CONSOLE` | Habilita la consola H2 en `/h2-console` (default true; poner en false en produccion) |

## Ejecucion Local
```bash
./mvnw spring-boot:run
```
La aplicacion estara disponible en `http://localhost:8080`

## Curso
Desarrollo de Aplicaciones Moviles I - CIBERTEC (Ciclo V)
