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
  "tipoUsuario": "COMPRADOR" // ADMIN | CONCESIONARIA | COMPRADOR
}
```

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


## DTOs principales (campos relevantes)
- CrearCompraRequest: `ofertaId`, `compradorId`, `precioCerrado` (BigDecimal)
- CompraResponse: `id`, `ofertaId`, `compradorId`, `precioCerrado`, `fechaCompra` (OffsetDateTime)
- OfertaResponse: `id`, `autoId`, `concesionariaId`, `titulo`, `descripcion`, `precio`, `estado`
- CrearResenaRequest: `autoId`, `usuarioId`, `rating`, `comentario`
- ResenaResponse: `id`, `autoId`, `usuarioId`, `rating`, `comentario`, `createdAt`
- CrearUsuarioRequest: `email`, `password`, `nombre`, `apellido`, `tipoUsuario`
- UsuarioResponse: `id`, `email`, `nombre`, `apellido`, `createdAt`, `activo`, `tipo` (construido en controller)
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


## Notas
- En `src/main/resources/application.properties` la configuración por defecto apunta a PostgreSQL (entorno de producción). Para pruebas, `src/test/resources/application.properties` fuerza H2 en memoria y desactiva Flyway.


