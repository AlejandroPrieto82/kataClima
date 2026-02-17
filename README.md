# 🌦️ Weather API — Kata Spring Boot + JWT

API REST para reportar y consultar el clima de ubicaciones, con seguridad implementada usando **Spring Security + JWT**.

---

## 📐 Arquitectura del proyecto

```
src/main/java/eci/edu/kata/
├── KataApplication.java          ← Punto de entrada
├── config/
│   ├── SecurityConfig.java       ← Reglas de seguridad y beans de autenticación
│   └── DataInitializer.java      ← Carga datos de prueba al iniciar
├── controller/
│   ├── AuthController.java       ← Endpoint de login
│   └── WeatherController.java    ← Endpoints GET y POST del clima
├── dto/
│   └── WeatherDtos.java          ← Objetos de transferencia de datos (request/response)
├── model/
│   ├── User.java                 ← Entidad de usuario (implementa UserDetails)
│   └── WeatherData.java          ← Entidad de clima en base de datos
├── repository/
│   ├── UserRepository.java       ← Acceso a datos de usuarios
│   └── WeatherRepository.java    ← Acceso a datos de clima
├── security/
│   ├── JwtService.java           ← Genera y valida tokens JWT
│   └── JwtFilter.java            ← Intercepta peticiones y valida el token
└── service/
    └── WeatherService.java       ← Lógica de negocio del clima
```

---

## 🧩 ¿Qué hace cada capa?

### `model/` — Entidades de base de datos

**`User.java`**
Representa un usuario del sistema. Implementa `UserDetails` de Spring Security, lo que permite que Spring lo use directamente para autenticación sin adaptadores extra. Usa Lombok (`@Data`, `@Builder`) para evitar escribir getters, setters y constructores manualmente.

**`WeatherData.java`**
Almacena el clima de una ubicación identificada por `locationId` (ej: `"bogota-col"`). Guarda ciudad, país, región, temperatura, presión, humedad y la fecha del último reporte.

---

### `dto/` — Objetos de transferencia (lo que entra y sale por HTTP)

**`WeatherDtos.java`**
Contiene todas las clases DTO en un solo archivo como clases estáticas internas:

| Clase | Uso |
|---|---|
| `WeatherResponse` | Lo que devuelve el GET |
| `LocationDto` | Parte de location dentro del response |
| `WeatherDto` | Parte de weather dentro del response |
| `WeatherRequest` | Lo que recibe el POST |
| `AuthRequest` | Lo que recibe el login (`username` + `password`) |
| `AuthResponse` | Lo que devuelve el login (`access_token`) |

---

### `repository/` — Acceso a base de datos

Interfaces que extienden `JpaRepository`. Spring genera automáticamente las consultas SQL a partir del nombre de los métodos:

- `findByUsername(String username)` → busca un usuario por nombre
- `findByLocationId(String locationId)` → busca datos de clima por ID de ubicación

---

### `security/` — El corazón de JWT

**`JwtService.java`**
Se encarga de tres cosas:
1. **Generar** un token firmado con el `jwt.secret` configurado en `application.properties`
2. **Extraer** el username de un token recibido
3. **Validar** que el token sea legítimo y no haya expirado

El token tiene una duración de 24 horas (`jwt.expiration=86400000` ms).

**`JwtFilter.java`**
Es un filtro que se ejecuta en **cada petición HTTP** antes de que llegue al controlador. Hace lo siguiente:

```
Petición entrante
      ↓
¿Tiene header "Authorization: Bearer <token>"?
      ↓ NO → Deja pasar (sin autenticación)
      ↓ SI
Extrae el token y obtiene el username
      ↓
Valida el token con JwtService
      ↓
Si es válido → registra al usuario en el contexto de seguridad de Spring
      ↓
Continúa con la petición
```

---

### `config/` — Configuración

**`SecurityConfig.java`**
Define las reglas de quién puede acceder a qué:

| Endpoint | Acceso |
|---|---|
| `POST /auth/login` | Público (sin token) |
| `GET /weather/{id}` | Público (sin token) |
| `POST /weather/{id}` | Requiere JWT válido |
| `/h2-console/**` | Público (solo para desarrollo) |

También configura:
- **`PasswordEncoder`**: usa BCrypt para hashear contraseñas
- **`UserDetailsService`**: le dice a Spring cómo cargar un usuario por nombre
- **`AuthenticationManager`**: gestiona el proceso de autenticación
- **Sesiones**: `STATELESS` (sin sesión en servidor, todo va en el JWT)

**`DataInitializer.java`**
Al arrancar la app crea automáticamente:
- Un usuario de prueba: `kata-user` / `password123`
- Un registro de clima para Bogotá con `locationId = bogota-col`

---

### `service/` — Lógica de negocio

**`WeatherService.java`**
- `getWeather(locationId)`: busca en BD y construye el `WeatherResponse`
- `reportWeather(locationId, request)`: actualiza los datos de clima en BD y retorna el nuevo estado

---

### `controller/` — Endpoints HTTP

**`AuthController.java`**
Expone `POST /auth/login`. Usa el `AuthenticationManager` para verificar usuario y contraseña, y si son correctos genera y devuelve un JWT.

**`WeatherController.java`**
- `GET /weather/{locationId}` → llama a `getWeather()`
- `POST /weather/{locationId}` → llama a `reportWeather()` (requiere JWT)

---

## 🚀 Cómo correr el proyecto

### Requisitos
- Java 17
- Maven (o usar el wrapper `./mvnw` incluido)

### Comando

```bash
./mvnw spring-boot:run
```

La app queda disponible en `http://localhost:8080`

### Consola H2 (base de datos en memoria)
Puedes ver los datos en: `http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:mem:weatherdb`
- **User:** `sa`
- **Password:** *(vacío)*

---

## 🔐 ¿Cómo funciona el flujo de seguridad?

```
1. Cliente hace POST /auth/login con usuario y contraseña
          ↓
2. Spring verifica las credenciales contra la base de datos
          ↓
3. Si son correctas, JwtService genera un token firmado
          ↓
4. El cliente recibe el token y lo guarda
          ↓
5. En futuras peticiones protegidas, el cliente envía:
   Header → Authorization: Bearer <token>
          ↓
6. JwtFilter intercepta la petición, valida el token
          ↓
7. Si es válido, Spring permite el acceso al endpoint
```

---

## 🧪 Cómo probar con Postman

### Paso 0 — Importar la colección

Puedes crear una colección nueva en Postman llamada **"Weather API Kata"** y agregar las siguientes requests.

---

### Request 1 — Consultar clima (sin token)

| Campo | Valor |
|---|---|
| **Método** | `GET` |
| **URL** | `http://localhost:8080/weather/bogota-col` |
| **Auth** | None |
| **Body** | ninguno |

**Respuesta esperada (200 OK):**
```json
{
    "location": {
        "city": "Bogotá",
        "country": "Colombia",
        "region": "Cundinamarca"
    },
    "weather": {
        "temp": 17.5,
        "pressure": 994.71,
        "humidity": 61
    }
}
```

---

### Request 2 — Login (obtener el token)

| Campo | Valor |
|---|---|
| **Método** | `POST` |
| **URL** | `http://localhost:8080/auth/login` |
| **Auth** | None |
| **Headers** | `Content-Type: application/json` |

**Body (raw → JSON):**
```json
{
    "username": "kata-user",
    "password": "password123"
}
```

**Respuesta esperada (200 OK):**
```json
{
    "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJrYXRhL...",
    "token_type": "Bearer"
}
```

> ⚠️ **Copia el valor de `access_token`**, lo necesitas en el siguiente paso.

---

### Request 3 — Reportar clima (con token)

| Campo | Valor |
|---|---|
| **Método** | `POST` |
| **URL** | `http://localhost:8080/weather/bogota-col` |
| **Headers** | `Content-Type: application/json` |

**Configurar el token en Postman:**
1. Ve a la pestaña **Authorization**
2. En **Type** selecciona **Bearer Token**
3. Pega el token del paso anterior en el campo **Token**

**Body (raw → JSON):**
```json
{
    "weather": {
        "temp": 20.0,
        "pressure": 1000.5,
        "humidity": 55
    }
}
```

**Respuesta esperada (200 OK):**
```json
{
    "location": {
        "city": "Bogotá",
        "country": "Colombia",
        "region": "Cundinamarca"
    },
    "weather": {
        "temp": 20.0,
        "pressure": 1000.5,
        "humidity": 55
    }
}
```

---

### Request 4 — Reportar clima SIN token (verificar seguridad)

Mismo que Request 3, pero **sin poner el Bearer Token**.

**Respuesta esperada (401 Unauthorized):**
```json
{
    "status": 401,
    "error": "Unauthorized"
}
```

> ✅ Esto confirma que la seguridad JWT funciona correctamente.

---

### Request 5 — Ubicación que no existe

| Campo | Valor |
|---|---|
| **Método** | `GET` |
| **URL** | `http://localhost:8080/weather/ciudad-inexistente` |

**Respuesta esperada (404 Not Found):**
```json
{
    "status": 404,
    "error": "Not Found",
    "message": "Ubicación no encontrada: ciudad-inexistente"
}
```

---

## 💡 Tip: Variable de entorno en Postman

Para no copiar el token manualmente cada vez, puedes automatizarlo:

1. En la **Request 2 (Login)**, ve a la pestaña **Tests** y agrega:
```javascript
const response = pm.response.json();
pm.environment.set("jwt_token", response.access_token);
```

2. En la **Request 3 (POST weather)**, en **Authorization → Bearer Token**, pon:
```
{{jwt_token}}
```

Así Postman guarda el token automáticamente después del login y lo usa en las siguientes peticiones.

---

## 🧱 Stack tecnológico

| Tecnología       | Versión  | Para qué                       |
|------------------|----------|--------------------------------|
| Spring Boot      | 3.2.0    | Framework principal            |
| Spring Security  | incluida | Seguridad y autenticación      |
| Spring Data JPA  | incluida | Acceso a base de datos         |
| JJWT             | 0.12.3   | Generar y validar tokens JWT   |
| H2 Database      | incluida | Base de datos en memoria (dev) |
| Lombok           | incluida | Reducir código boilerplate     |
| Java             | 17       | Lenguaje                       |