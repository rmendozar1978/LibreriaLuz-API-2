# Librería Luz — API REST

**Evidencias:** GA7-220501096-AA5-EV03 (Diseño y desarrollo de servicios web — proyecto) y GA7-220501096-AA5-EV04 (API del proyecto)
**Aprendiz:** Juan Sebastián Ávila Patiño
**Ficha:** ADSO 3235898
**Programa:** Tecnólogo en Análisis y Desarrollo de Software — SENA

## 1. Descripción

API REST del proyecto formativo **Librería Luz**. Cubre lo que necesita el software de la librería:

| Módulo | Qué resuelve |
|---|---|
| Autenticación | Registro de usuarios e inicio de sesión con contraseña cifrada (BCrypt). |
| Usuarios | Consulta de los usuarios registrados (sin exponer contraseñas). |
| Categorías | Clasificación del catálogo (novela, poesía, programación…). |
| Libros | Catálogo: crear, consultar, buscar por título/autor/categoría, actualizar y eliminar. |
| Ventas | Registro de ventas con cálculo del total y descuento automático del stock. |

Tecnologías: Java 11, Spring Boot 2.7.18, Spring Data JPA, Bean Validation, H2 (por defecto) o MySQL, springdoc-openapi (Swagger UI), JUnit 5 + MockMvc, Postman.

## 2. Diseño

### Arquitectura por capas

```text
Cliente (Postman / web)
      │  JSON sobre HTTP
      ▼
controller/   → recibe la petición, valida el DTO (@Valid) y devuelve el código HTTP
      ▼
service/      → reglas de negocio (correo/ISBN únicos, stock, borrado seguro)
      ▼
repository/   → acceso a datos con Spring Data JPA
      ▼
Base de datos (H2 en archivo ./data o MySQL libreria_luz_db)
```

`exception/GlobalExceptionHandler` convierte todos los errores en respuestas `{"mensaje": "..."}` con el código HTTP correcto, sin exponer trazas internas.

### Modelo de datos

```text
usuarios (id, nombre, correo UNIQUE, contrasena_hash)
categorias (id, nombre UNIQUE, descripcion)
libros (id, titulo, autor, isbn UNIQUE, precio, stock, categoria_id → categorias)
ventas (id, fecha, usuario_id → usuarios, total)
detalles_venta (id, venta_id → ventas, libro_id → libros, cantidad, precio_unitario)
```

### Códigos de respuesta usados

| Código | Cuándo |
|---|---|
| 200 OK | Consulta, actualización o eliminación correcta; login válido. |
| 201 Created | Registro creado (usuario, categoría, libro, venta). |
| 400 Bad Request | Datos faltantes o inválidos, JSON mal formado, id no numérico. |
| 401 Unauthorized | Credenciales incorrectas en el login. |
| 404 Not Found | El recurso (o la categoría/usuario/libro referenciado) no existe. |
| 409 Conflict | Correo, ISBN o nombre de categoría repetido; stock insuficiente; borrar algo que tiene registros relacionados. |

## 3. Ejecutar el proyecto

Requisitos: JDK 11 o superior y Maven 3.6+ (o el wrapper `mvnw` incluido).

```bash
mvn clean test          # 10 pruebas automatizadas
mvn spring-boot:run     # inicia la API
```

La API queda en **http://localhost:9090**.

- Se usa el puerto 9090 porque en muchos equipos Windows el rango 8003–8102 (incluido 8080) lo reserva Hyper-V/Docker. Para otro puerto: `set PORT=8080` (Windows) o `PORT=8080 mvn spring-boot:run`.
- Por defecto usa H2 en archivo (`./data`), así que no hay que instalar ningún motor de base de datos.
- Para usar MySQL (base `libreria_luz_db`, se crea sola):
  `mvn spring-boot:run -Dspring-boot.run.profiles=mysql` (usuario `root`; se cambia con `DB_USER` y `DB_PASSWORD`).

**Documentación interactiva:** http://localhost:9090/swagger-ui.html (OpenAPI JSON en `/v3/api-docs`).

## 4. Documentación de los servicios

Todas las peticiones con cuerpo usan `Content-Type: application/json`. Los errores devuelven `{"mensaje": "texto del error"}`.

### 4.1 Autenticación

#### POST `/api/auth/registro` — Registrar usuario
Cuerpo:
```json
{ "nombre": "Juan Sebastián Ávila", "correo": "juan@librerialuz.com", "contrasena": "ClaveSegura123!" }
```
Reglas: nombre de 3 a 100 caracteres, correo válido y único (no distingue mayúsculas), contraseña de 8 a 100 caracteres. La contraseña se guarda como hash BCrypt.
Respuesta `201`:
```json
{ "id": 1, "nombre": "Juan Sebastián Ávila", "correo": "juan@librerialuz.com" }
```
Errores: `400` datos inválidos · `409` "El correo ya está registrado."

#### POST `/api/auth/login` — Iniciar sesión
Cuerpo:
```json
{ "correo": "juan@librerialuz.com", "contrasena": "ClaveSegura123!" }
```
Respuesta `200`:
```json
{ "mensaje": "Autenticación satisfactoria.", "usuario": { "id": 1, "nombre": "Juan Sebastián Ávila", "correo": "juan@librerialuz.com" } }
```
Errores: `400` campos vacíos · `401` "Error en la autenticación." (no se revela si falló el correo o la contraseña).

### 4.2 Usuarios

| Método y ruta | Descripción | Respuestas |
|---|---|---|
| GET `/api/usuarios` | Lista los usuarios (id, nombre, correo). | 200 |
| GET `/api/usuarios/{id}` | Consulta un usuario. | 200 · 404 |
| DELETE `/api/usuarios/{id}` | Elimina un usuario sin ventas. | 200 · 404 · 409 si tiene ventas |

### 4.3 Categorías

| Método y ruta | Descripción | Respuestas |
|---|---|---|
| GET `/api/categorias` | Lista las categorías ordenadas por nombre. | 200 |
| GET `/api/categorias/{id}` | Consulta una categoría. | 200 · 404 |
| POST `/api/categorias` | Crea una categoría. | 201 · 400 · 409 nombre repetido |
| PUT `/api/categorias/{id}` | Actualiza nombre y descripción. | 200 · 400 · 404 · 409 |
| DELETE `/api/categorias/{id}` | Elimina una categoría sin libros. | 200 · 404 · 409 si tiene libros |

Cuerpo de POST/PUT:
```json
{ "nombre": "Novela", "descripcion": "Obras narrativas de ficción" }
```

### 4.4 Libros

| Método y ruta | Descripción | Respuestas |
|---|---|---|
| GET `/api/libros` | Lista el catálogo. Filtros opcionales: `?buscar=texto` (título o autor) y `?categoriaId=1`. | 200 |
| GET `/api/libros/{id}` | Consulta un libro con su categoría. | 200 · 404 |
| POST `/api/libros` | Crea un libro. | 201 · 400 · 404 categoría inexistente · 409 ISBN repetido |
| PUT `/api/libros/{id}` | Actualiza todos los datos del libro. | 200 · 400 · 404 · 409 |
| DELETE `/api/libros/{id}` | Elimina un libro que no tenga ventas. | 200 · 404 · 409 si ya se vendió |

Cuerpo de POST/PUT (`categoriaId` es opcional):
```json
{ "titulo": "Cien años de soledad", "autor": "Gabriel García Márquez", "isbn": "978-0307474728",
  "precio": 45000, "stock": 10, "categoriaId": 1 }
```
Reglas: precio mayor que cero (máx. 2 decimales), stock mayor o igual a cero, ISBN de 4 a 20 caracteres (dígitos, guiones o X).

Respuesta `201`:
```json
{ "id": 1, "titulo": "Cien años de soledad", "autor": "Gabriel García Márquez", "isbn": "978-0307474728",
  "precio": 45000, "stock": 10, "categoria": { "id": 1, "nombre": "Novela", "descripcion": "Obras narrativas de ficción" } }
```

### 4.5 Ventas

| Método y ruta | Descripción | Respuestas |
|---|---|---|
| POST `/api/ventas` | Registra una venta, calcula el total y descuenta el stock. | 201 · 400 · 404 usuario o libro inexistente · 409 stock insuficiente |
| GET `/api/ventas` | Lista las ventas (más recientes primero). Filtro opcional `?usuarioId=1`. | 200 · 404 |
| GET `/api/ventas/{id}` | Consulta una venta con su detalle. | 200 · 404 |

Cuerpo de POST:
```json
{ "usuarioId": 1, "items": [ { "libroId": 1, "cantidad": 2 }, { "libroId": 2, "cantidad": 1 } ] }
```
Si algún libro no tiene stock suficiente no se guarda nada (transacción completa). Respuesta `201`:
```json
{ "id": 1, "fecha": "2026-10-29T21:32:35", "usuario": { "id": 1, "nombre": "Juan Sebastián Ávila", "correo": "juan@librerialuz.com" },
  "total": 128000.50,
  "detalles": [ { "libroId": 1, "titulo": "Cien años de soledad", "cantidad": 2, "precioUnitario": 48000, "subtotal": 96000 },
                { "libroId": 2, "titulo": "El amor en los tiempos del cólera", "cantidad": 1, "precioUnitario": 32000.50, "subtotal": 32000.50 } ] }
```

## 5. Pruebas

- **Automatizadas (JUnit + MockMvc):** `mvn test` — 10 pruebas de integración que cubren todos los servicios y los casos de error.
- **Postman:** importar `postman/LibreriaLuz-API.postman_collection.json` y `postman/LibreriaLuz-Local.postman_environment.json`, iniciar la API y usar *Run collection*. Son 32 peticiones con 60 verificaciones. Cada petición guarda en variables los ids que usan las siguientes, y un sufijo único permite repetir la ejecución sin chocar con datos anteriores.
- Desde consola (opcional): `npx newman run postman/LibreriaLuz-API.postman_collection.json -e postman/LibreriaLuz-Local.postman_environment.json`

## 6. Seguridad y alcance

- Las contraseñas se guardan con BCrypt y ninguna respuesta incluye la contraseña ni su hash.
- El login no revela si el correo existe.
- Proyecto académico: no incluye tokens JWT, roles ni bloqueo por intentos fallidos; al desplegar en un servidor se debe usar HTTPS.

## 7. Estructura

```text
LibreriaLuz-API/
├── pom.xml, mvnw, mvnw.cmd, README.md, ENDPOINTS.txt, Repositorio_GitHub.txt
├── postman/                 → colección, entorno y reporte de la ejecución
└── src/
    ├── main/java/com/librerialuz/
    │   ├── LibreriaLuzApplication.java
    │   ├── config/          → SecurityConfig (BCrypt), OpenApiConfig (Swagger)
    │   ├── controller/      → Auth, Usuario, Categoria, Libro, Venta
    │   ├── dto/             → objetos de entrada y salida JSON
    │   ├── exception/       → manejo global de errores
    │   ├── model/           → entidades JPA
    │   ├── repository/      → Spring Data JPA
    │   └── service/         → reglas de negocio
    ├── main/resources/      → application.properties, application-mysql.properties
    └── test/                → pruebas de integración
```
