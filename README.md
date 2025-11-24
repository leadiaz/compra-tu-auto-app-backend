# Backend para proyecto Practicas de Desarrollo de Software | Universidad Nacional de Quilmes 2025

Este repositorio contiene el backend del proyecto. A continuación se listan los endpoints disponibles en los controllers, cómo usarlos y qué respuesta generan.


## Formato general
- Base URL de ejemplo en ejecución local: `http://localhost:8080/api/1/compra-tu-auto`.
- Los JSON de ejemplo muestran los campos presentes en los DTOs usados por los endpoints.

## Endpoints

### CompraController
- POST /compras

Descripción: crea una compra a partir de una oferta y un comprador.

Request body (CrearCompraRequest):
```json
{
  "ofertaId": 123,
  "compradorId": 45,
  "precioCerrado": 250000.00
}
```

Responses:
- 200 OK
    - Body: `CompraResponse`
      ```json
      {
        "id": 1,
        "ofertaId": 123,
        "compradorId": 45,
        "precioCerrado": 250000.00,
        "fechaCompra": "2025-10-16T12:34:56Z"
      }
      ```
- 404 Not Found: si la creación falla por argumentos inválidos o estado inconsistente.


### OfertaController
- GET /ofertas?concesionariaId={concesionariaId}

Descripción: lista las ofertas de una concesionaria.

Query params:
- `concesionariaId` (Long) - obligatorio.

Responses:
- 200 OK
    - Body: array de `OfertaResponse`:
      ```json
      [
        {
          "id": 10,
          "autoId": 5,
          "concesionariaId": 1,
          "titulo": "Toyota Corolla 2020",
          "descripcion": "Excelente estado",
          "precio": "250000",
          "estado": "DISPONIBLE"
        }
      ]
      ```
- 404 Not Found: si no hay ofertas para la concesionaria.


- GET /ofertas/autos/{autoId}

Descripción: lista las ofertas asociadas a un auto especificado por `autoId`.

Path params:
- `autoId` (Long)

Responses:
- 200 OK: array de `OfertaResponse` (misma estructura que arriba).
- 404 Not Found: si no hay ofertas para el auto.


### ResenaController
- POST /resenas

Descripción: crea una reseña para un auto por parte de un usuario.

Request body (CrearResenaRequest):
```json
{
  "autoId": 5,
  "usuarioId": 12,
  "rating": 5,
  "comentario": "Muy buen auto"
}
```

Responses:
- 200 OK
    - Body: `ResenaResponse`:
      ```json
      {
        "id": 1,
        "autoId": 5,
        "usuarioId": 12,
        "rating": 5,
        "comentario": "Muy buen auto",
        "createdAt": "2025-10-16T12:34:56Z"
      }
      ```
- 404 Not Found: si hay datos inválidos o conflicto de estado.


- GET /resenas/autos/{autoId}
  Descripción: obtiene todas las reseñas de un auto.

Path params:
- `autoId` (Long)

Responses:
- 200 OK: array de `ResenaResponse`.
- 404 Not Found: si no hay reseñas.


### UsuarioController
Base: `/usuarios`

Listado de endpoints:

- GET /usuarios/{usuarioId}/favorito

Descripción: obtiene el favorito del usuario (si existe).
Path params:
- `usuarioId` (Long)

Responses:
- 200 OK: `FavoritoResponse`:
  ```json
  {
    "id": 1,
    "usuarioId": 12,
    "ofertaId": 34
  }
  ```
- 204 No Content: el usuario no tiene favorito definido.
- 404 Not Found: si `usuarioId` inválido.


- PUT /usuarios/{usuarioId}/favorito/{ofertaId}

Descripción: define la oferta indicada como favorito del usuario.
Path params:
- `usuarioId` (Long)
- `ofertaId` (Long)

Responses:
- 200 OK: `FavoritoResponse` (favorito creado o actualizado).
- 404 Not Found: si usuario u oferta no existe.
- 422 Unprocessable Entity: si la operación no puede completarse por negocio (por ejemplo, oferta no disponible).


- GET /usuarios/{usuarioId}/compras

Descripción: lista las compras realizadas por un usuario.
Path params:
- `usuarioId` (Long)

Responses:
- 200 OK: array de `CompraResponse`.
- 404 Not Found: si no hay compras para el usuario.


- POST /usuarios
- (compatibilidad) POST /api/usuarios

Descripción: crea un nuevo usuario.
Request body (CrearUsuarioRequest):
```json
{
  "email": "test@test.com",
  "password": "pwd",
  "nombre": "Test",
  "apellido": "User",
  "tipoUsuario": "COMPRADOR"
}
```

Valores posibles para `tipoUsuario`: ADMIN | CONCESIONARIA | COMPRADOR

Responses:
- 201 Created: `UsuarioResponse` (id, email, nombre, apellido, createdAt, activo, tipo)
- 400 Bad Request: en caso de parámetros inválidos o errores de validación.


- GET /usuarios

Descripción: obtiene todos los usuarios.
Responses:
- 200 OK: array de `UsuarioResponse`.


- GET /usuarios/{id}
  Descripción: obtiene un usuario por su id.
  Path params:
- `id` (Long)

Responses:
- 200 OK: `UsuarioResponse`.
- 404 Not Found: si no existe.


- GET /usuarios/por-tipo/{tipoUsuario}

Descripción: obtiene usuarios filtrando por tipo (p.ej. `COMPRADOR`, `CONCESIONARIA`, `ADMIN`).
Path params:
- `tipoUsuario` (String)

Responses:
- 200 OK: array de `UsuarioResponse`.
- 400 Bad Request: si el tipo es inválido.


### AuthController

#### POST /auth/login

Descripción: autentica un usuario usando email (campo `usuario` en el body) y `password`. Devuelve un token JWT y un `UsuarioResponse` con los datos públicos del usuario si las credenciales son correctas.

**Este endpoint es público** y no requiere autenticación.

Request body (LoginRequest):
```json
{
  "usuario": "test@test.com",
  "password": "pwd"
}
```

Responses:
- 200 OK
  - Body: `LoginResponse`:
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "usuario": {
        "id": 1,
        "email": "test@test.com",
        "nombre": "Test",
        "apellido": "User",
        "fechaAlta": "2025-10-16T12:34:56",
        "activo": true,
        "tipoUsuario": "COMPRADOR"
      }
    }
    ```
- 401 Unauthorized: credenciales inválidas.

**Uso del token**: El token JWT devuelto debe incluirse en todas las requests subsiguientes en el header:
```
Authorization: Bearer <token>
```


## Autenticación y Autorización por Roles

### Implementación

El sistema implementa **autenticación JWT** y **autorización por roles** para garantizar que cada usuario solo pueda acceder a los recursos y operaciones permitidas según su rol.

#### Componentes principales

1. **Enum `Rol`**: Define los tres roles del sistema:
   - `COMPRADOR`: Usuario comprador habitual
   - `CONCESIONARIA`: Concesionaria que gestiona ofertas
   - `ADMIN`: Administrador del sistema

2. **Entidad `Usuario`**: 
   - Contiene método `getRol()` que determina el rol basado en la instancia (UsuarioAdmin, UsuarioConcesionaria, UsuarioComprador)
   - Password encriptado con BCrypt
   - Email único

3. **JWT Service (`JwtService`)**:
   - Genera tokens JWT que incluyen: `userId`, `email` (subject) y `rol`
   - Valida tokens y extrae información del usuario
   - Tokens expiran después de 24 horas por defecto (configurable)

4. **UsuarioDetailsService**:
   - Implementa `UserDetailsService` de Spring Security
   - Carga usuarios y asigna roles como autoridades (`ROLE_COMPRADOR`, `ROLE_CONCESIONARIA`, `ROLE_ADMIN`)

5. **JwtAuthenticationFilter**:
   - Filtro que intercepta cada request
   - Extrae el token JWT del header `Authorization: Bearer <token>`
   - Valida el token y establece el contexto de seguridad de Spring

6. **SecurityConfig**:
   - Configuración de seguridad de Spring Security
   - Habilita `@PreAuthorize` para autorización a nivel de método
   - Define rutas públicas (login, registro, Swagger)

### Uso de tokens JWT

Para acceder a endpoints protegidos, incluye el token JWT en el header de la request:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Autorización por roles

Los endpoints están protegidos con `@PreAuthorize` según el rol requerido:

#### Roles y permisos

**COMPRADOR** puede:
- ✅ Crear compras (`POST /compras`)
- ✅ Crear reseñas (`POST /resenas`)
- ✅ Ver reseñas (`GET /resenas/autos/{autoId}`)
- ✅ Ver ofertas (`GET /ofertas`, `GET /ofertas/autos/{autoId}`)
- ✅ Gestionar favoritos (`GET /usuarios/{id}/favorito`, `PUT /usuarios/{id}/favorito/{ofertaId}`)
- ✅ Ver sus compras (`GET /usuarios/{id}/compras`)
- ✅ Ver su propio perfil (`GET /usuarios/{id}` - solo su propio ID)

**CONCESIONARIA** puede:
- ✅ Ver ofertas (`GET /ofertas`, `GET /ofertas/autos/{autoId}`)
- ✅ Ver reseñas (`GET /resenas/autos/{autoId}`)
- ✅ Ver su propio perfil (`GET /usuarios/{id}` - solo su propio ID)
- ❌ No puede crear compras, reseñas ni gestionar favoritos

**ADMIN** puede:
- ✅ Acceder a **todos** los endpoints
- ✅ Ver todos los usuarios (`GET /usuarios`, `GET /usuarios/por-tipo/{tipo}`)
- ✅ Ver cualquier usuario (`GET /usuarios/{id}`)
- ✅ Ver compras, favoritos y reseñas de cualquier usuario

### Tabla de permisos por endpoint

| Endpoint | COMPRADOR | CONCESIONARIA | ADMIN |
|----------|-----------|---------------|-------|
| `POST /compras` | ✅ | ❌ | ✅ |
| `POST /resenas` | ✅ | ❌ | ✅ |
| `GET /resenas/autos/{autoId}` | ✅ | ✅ | ✅ |
| `GET /ofertas` | ✅ | ✅ | ✅ |
| `GET /ofertas/autos/{autoId}` | ✅ | ✅ | ✅ |
| `GET /usuarios/{id}/favorito` | ✅ | ❌ | ✅ |
| `PUT /usuarios/{id}/favorito/{ofertaId}` | ✅ | ❌ | ✅ |
| `GET /usuarios/{id}/compras` | ✅ | ❌ | ✅ |
| `GET /usuarios` | ❌ | ❌ | ✅ |
| `GET /usuarios/por-tipo/{tipo}` | ❌ | ❌ | ✅ |
| `GET /usuarios/{id}` | Solo propio | Solo propio | Cualquiera |
| `POST /usuarios` | ✅ (público) | ✅ (público) | ✅ (público) |
| `POST /auth/login` | ✅ (público) | ✅ (público) | ✅ (público) |

### Seguridad implementada

1. **Autenticación**:
   - Passwords encriptados con BCrypt
   - Tokens JWT firmados con clave secreta
   - Validación de token en cada request

2. **Autorización**:
   - Validación de roles en el backend (independiente del frontend)
   - `@PreAuthorize` en cada endpoint protegido
   - Validación de acceso a recursos propios (ej: usuarios solo pueden ver su propio perfil)

3. **Rutas públicas**:
   - `/auth/login` - Autenticación
   - `POST /usuarios` - Registro de nuevos usuarios
   - `/swagger-ui/**`, `/v3/api-docs/**` - Documentación API

### Configuración

El token JWT se configura mediante propiedades en `application.properties`:

```properties
jwt.secret=MiClaveSecretaMuyLargaParaJWTQueDebeSerAlMenos256BitsParaHS256
jwt.expiration=86400000  # 24 horas en milisegundos
```

**⚠️ Importante**: En producción, la clave secreta (`jwt.secret`) debe ser:
- Al menos 256 bits de longitud
- Generada de forma segura y aleatoria
- Almacenada de forma segura (variables de entorno, secretos, etc.)
- Nunca commitada en el repositorio


## Manejo de Excepciones

La API utiliza excepciones personalizadas para manejar diferentes tipos de errores:

- **EntidadNoEncontradaException**: Se lanza cuando se intenta acceder a una entidad que no existe (usuario, oferta, auto, etc.). Retorna **404 Not Found**.
- **CredencialesInvalidasException**: Se lanza cuando las credenciales de autenticación son incorrectas. Retorna **401 Unauthorized**.
- **IllegalStateException**: Se lanza cuando se violan reglas de negocio. Puede retornar **404 Not Found** (estados inconsistentes) o **422 Unprocessable Entity** (reglas de negocio).
- **IllegalArgumentException**: Se lanza cuando los argumentos proporcionados son inválidos. Retorna **404 Not Found** para entidades no encontradas o **400 Bad Request** para otros casos.

Todas las excepciones son manejadas por el `GlobalExceptionHandler` y retornan un `ErrorResponse` con el formato:
```json
{
  "timestamp": "2025-10-16T12:34:56",
  "status": 404,
  "error": "Not Found",
  "message": "Usuario no encontrado",
  "path": "/usuarios/999"
}
```

## DTOs principales (campos relevantes)
- CrearCompraRequest: `ofertaId`, `compradorId`, `precioCerrado` (BigDecimal)
- CompraResponse: `id`, `ofertaId`, `compradorId`, `precioCerrado`, `fechaCompra` (OffsetDateTime)
- OfertaResponse: `id`, `autoId`, `concesionariaId`, `titulo`, `descripcion`, `precio`, `estado`
- CrearResenaRequest: `autoId`, `usuarioId`, `rating`, `comentario`
- ResenaResponse: `id`, `autoId`, `usuarioId`, `rating`, `comentario`, `createdAt`
- CrearUsuarioRequest: `email`, `password`, `nombre`, `apellido`, `tipoUsuario`
- UsuarioResponse: `id`, `email`, `nombre`, `apellido`, `createdAt`, `activo`, `tipoUsuario`
- LoginRequest: `usuario` (email), `password`
- LoginResponse: `token` (JWT), `usuario` (UsuarioResponse)
- FavoritoResponse: `id`, `usuarioId`, `ofertaId`


## Cómo ejecutar tests
Se usa Maven Wrapper incluido en el repo (`./mvnw`). Los tests están configurados para usar una base de datos en memoria H2 durante la ejecución de pruebas (archivo `src/test/resources/application.properties`), por lo que no es necesario tener PostgreSQL levantado para ejecutar los tests.

Comando para ejecutar los tests:

```bash
./mvnw test
```

Si quieres ejecutar en modo más verboso o con la salida estándar de Maven:

```bash
./mvnw -DskipTests=false test
```


## Entornos / Perfiles

La configuración actual del proyecto mantiene el archivo raíz `application.properties` apuntando a PostgreSQL por defecto (comportamiento original). Además existe un perfil `dev` pensado para desarrollo local con H2 en memoria.

- `application.properties` (root): apunta por defecto a PostgreSQL (producción-like). Si arrancas la aplicación sin especificar perfil, se usará esta configuración.

- `dev` (desarrollo): usa H2 en memoria y la consola H2 está habilitada en `http://localhost:8080/h2-console`.
  - Configuración: `src/main/resources/application-dev.properties`
  - En `dev` Flyway está deshabilitado y Hibernate crea el esquema (`spring.jpa.hibernate.ddl-auto=create-drop`).
  - Ejecutar local con perfil `dev`:

```bash
# con mvnw
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
# o estableciendo la variable de entorno
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

Notas sobre producción/local con Postgres:

- Si quieres usar Postgres localmente o en despliegues, `application.properties` ya apunta a Postgres. Puedes pasar credenciales mediante variables de entorno y/o modificar `application.properties`.
- Los archivos `src/main/resources/application-prod.properties` y `src/main/resources/application-staging.properties` se conservan como referencia (comentados). Si prefieres eliminarlos, es seguro hacerlo porque la configuración por defecto está en `application.properties`.

Ejemplo de ejecución usando la configuración por defecto (Postgres) y variables de entorno:

```bash
DB_HOST=127.0.0.1 DB_PORT=5432 DB_NAME=compra_auto DB_USERNAME=compra_user DB_PASSWORD=secret \
  java -jar target/pdss22025-0.0.1-SNAPSHOT.jar
```

## Notas sobre migraciones y H2

Las migraciones en `src/main/resources/db/migration` están escritas pensando en PostgreSQL; por eso en `dev` usamos H2 con Hibernate creando el esquema automáticamente. Si deseas probar las migraciones contra H2, revisa las SQL que usan funciones/constructos específicos de Postgres (por ejemplo `plpgsql`, `pg_constraint`, `DO $$`) y adáptalas o crea versiones compatibles con H2.

## Notas
- En `src/main/resources/application.properties` la configuración por defecto apunta a PostgreSQL (entorno de producción). Para pruebas, `src/test/resources/application.properties` fuerza H2 en memoria y desactiva Flyway.


## Documentación API (Swagger / OpenAPI)

Si ejecutas la aplicación con la dependencia de `springdoc-openapi`, la documentación OpenAPI y la UI de Swagger quedan disponibles por defecto en:

- Swagger UI (interfaz web): http://localhost:8080/swagger-ui.html
- Alternativa: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

Ejecuta la app (dev o configuración por defecto) y abre la URL en tu navegador para explorar y probar los endpoints desde la UI.
