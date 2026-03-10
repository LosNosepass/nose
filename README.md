# 📱 Móviles API REST 1.0

API REST para gestión y consulta de móviles con autenticación JWT.
Basada en el proyecto base `SecurityStalessHttpBasic2026-9`.

---

## 🏗️ Arquitectura del proyecto

```
com.adorno
├── MovilesApiApplication.java
├── configuration/
│   ├── GlobalExceptionHandler.java
│   └── security/
│       ├── SecurityConfig.java
│       └── jwt/
│           ├── JWTUtils.java
│           └── filters/
│               └── JwtAuthorizationFilter.java
├── controller/
│   ├── AuthController.java
│   └── MovilController.java
├── model/
│   ├── ERole.java
│   ├── RoleEntity.java
│   ├── UserEntity.java
│   ├── entities/
│   │   └── Movil.java
│   └── dtos/
│       ├── MovilCreateDTO.java
│       ├── MovilResumenDTO.java (record)
│       ├── MovilDetalleDTO.java (record)
│       ├── LoginRequestDTO.java (record)
│       ├── JwtResponseDTO.java  (record)
│       └── UserCreateDTO.java  (record)
├── repositories/
│   ├── MovilRepository.java
│   ├── MovilSpecification.java
│   ├── RoleRepository.java
│   └── UserRepository.java
├── services/
│   ├── MovilService.java
│   ├── UserService.java
│   └── UserDetailsServiceImpl.java
├── mappers/
│   └── MovilMapper.java (MapStruct)
└── populaters/
    └── DataInitializer.java
```

---

## 🚀 Puesta en marcha

### 1. Requisitos
- Java 17+
- Maven 3.x
- MySQL 8.x

### 2. Base de datos
```sql
CREATE DATABASE moviles_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configuración (`application.properties`)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/moviles_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
```

### 4. Compilar y ejecutar
```bash
mvn clean install
mvn spring-boot:run
```

Al arrancar, se crean automáticamente los **roles** (ROLE_ADMIN, ROLE_USER, ROLE_GUEST)
y el **usuario admin** por defecto:
- usuario: `admin`
- contraseña: `admin123`

---

## 🔐 Seguridad y roles

| Rol          | Acceso                                             |
|--------------|----------------------------------------------------|
| Guest        | Tendencias, búsqueda, detalle, comparar             |
| ROLE_USER    | Igual que Guest (autenticado)                       |
| ROLE_ADMIN   | CRUD completo sobre móviles + listado completo      |

---

## 📡 Endpoints de la API

### Autenticación

#### `POST /api/auth/login`
Autentica y devuelve un token JWT.
```json
// Request
{ "username": "admin", "password": "admin123" }

// Response 200
{
  "token": "eyJhbGciOiJIUzI1...",
  "type": "Bearer",
  "username": "admin",
  "roles": ["ROLE_ADMIN"]
}
```

#### `POST /api/auth/register`
Registra un nuevo usuario (ROLE_USER por defecto).
```json
// Request
{
  "username": "usuario1",
  "email": "u1@email.com",
  "password": "pass123",
  "roles": ["user"]   // opcional: "admin", "guest"
}
```

---

### Endpoints PÚBLICOS (sin autenticación)

#### `GET /api/moviles/tendencias`
Los **5 móviles más consultados** (página principal). Devuelve info resumida.
```json
[
  {
    "id": 3,
    "marca": "Samsung",
    "modelo": "Galaxy S24",
    "procesadorNucleos": 8,
    "ramGb": 12,
    "almacenamientoGb": 256,
    "precio": 799.99
  },
  ...
]
```

#### `GET /api/moviles/marcas`
Lista de marcas disponibles en la base de datos.
```json
["Apple", "OnePlus", "Samsung", "Xiaomi"]
```

#### `GET /api/moviles/tecnologias-pantalla`
Tecnologías de pantalla disponibles.
```json
["AMOLED", "IPS", "LCD", "OLED"]
```

#### `GET /api/moviles/{id}`
Detalle completo de un móvil. Incrementa contador de consultas.
```json
{
  "id": 1,
  "marca": "Samsung",
  "modelo": "Galaxy S24",
  "procesadorTipo": "Snapdragon 8 Gen 3",
  "procesadorNucleos": 8,
  "procesadorVelocidadGhz": 3.3,
  "almacenamientoGb": 256,
  "pantallaTamanoPulgadas": 6.2,
  "pantallaTecnologia": "AMOLED",
  "ramGb": 8,
  "dimAltoCm": 14.7,
  "dimAnchoCm": 7.06,
  "dimGrosorCm": 0.76,
  "pesoGr": 167,
  "camaraMpx": 50.0,
  "bateriaMah": 4000,
  "nfc": true,
  "precio": 799.99,
  "fechaLanzamiento": "2024-01-17"
}
```

#### `GET /api/moviles/buscar` — Búsqueda con criterios
El **precio es obligatorio** en todas las búsquedas. El resto son opcionales.

| Parámetro           | Tipo       | ¿Obligatorio? | Descripción                        |
|---------------------|------------|---------------|------------------------------------|
| `precioMin`         | BigDecimal | ✅ Sí          | Precio mínimo                      |
| `precioMax`         | BigDecimal | ✅ Sí          | Precio máximo                      |
| `marca`             | String     | No            | Filtrar por marca exacta           |
| `ramMin`            | Integer    | No            | RAM mínima en GB                   |
| `ramMax`            | Integer    | No            | RAM máxima en GB                   |
| `nfc`               | Boolean    | No            | true / false                       |
| `pantallaTecnologia`| String     | No            | AMOLED, OLED, IPS, LCD...          |

**Ejemplos:**
```
# Solo por marca y precio:
GET /api/moviles/buscar?marca=Samsung&precioMin=200&precioMax=800

# Precio + RAM + NFC:
GET /api/moviles/buscar?precioMin=100&precioMax=500&ramMin=8&nfc=true

# Solo precio (muestra todos en ese rango):
GET /api/moviles/buscar?precioMin=0&precioMax=9999

# Varios criterios:
GET /api/moviles/buscar?precioMin=300&precioMax=700&ramMin=6&nfc=true&pantallaTecnologia=AMOLED
```

#### `GET /api/moviles/comparar?id1=X&id2=Y`
Devuelve los datos **completos de dos móviles en un array** para comparación columna a columna.
```json
[
  { "id": 1, "marca": "Samsung", "modelo": "Galaxy S24", ... },
  { "id": 2, "marca": "Apple",   "modelo": "iPhone 15",  ... }
]
```

---

### Endpoints de ADMINISTRADOR 🔒

Requieren header: `Authorization: Bearer <token>`

#### `GET /api/moviles`
Lista todos los móviles (info resumida).

#### `POST /api/moviles`
Crea un nuevo móvil.
```json
{
  "marca": "Google",
  "modelo": "Pixel 9",
  "procesadorTipo": "Google Tensor G4",
  "procesadorNucleos": 9,
  "procesadorVelocidadGhz": 3.1,
  "almacenamientoGb": 128,
  "pantallaTamanoPulgadas": 6.3,
  "pantallaTecnologia": "OLED",
  "ramGb": 12,
  "dimAltoCm": 15.23,
  "dimAnchoCm": 7.2,
  "dimGrosorCm": 0.85,
  "pesoGr": 198,
  "camaraMpx": 50.0,
  "bateriaMah": 4700,
  "nfc": true,
  "precio": 799.00,
  "fechaLanzamiento": "2024-08-13"
}
```

#### `PUT /api/moviles/{id}`
Actualiza completamente un móvil existente (mismo body que POST).

#### `DELETE /api/moviles/{id}`
Elimina un móvil. Devuelve `204 No Content`.

---

## 📋 Modelo de datos — Entidad Movil

| Campo                    | Tipo         | Descripción                          |
|--------------------------|--------------|--------------------------------------|
| `marca`                  | String       | Marca del fabricante                 |
| `modelo`                 | String       | Nombre del modelo                    |
| `procesadorTipo`         | String       | Nombre del procesador                |
| `procesadorNucleos`      | Integer      | Número de núcleos                    |
| `procesadorVelocidadGhz` | Double       | Velocidad máxima en GHz              |
| `almacenamientoGb`       | Integer      | Almacenamiento interno en GB         |
| `pantallaTamanoPulgadas` | Double       | Tamaño de pantalla en pulgadas       |
| `pantallaTecnologia`     | String       | Tecnología: AMOLED, OLED, IPS, LCD  |
| `ramGb`                  | Integer      | Memoria RAM en GB                    |
| `dimAltoCm`              | Double       | Alto en centímetros                  |
| `dimAnchoCm`             | Double       | Ancho en centímetros                 |
| `dimGrosorCm`            | Double       | Grosor en centímetros                |
| `pesoGr`                 | Integer      | Peso en gramos                       |
| `camaraMpx`              | Double       | Megapíxeles de la cámara             |
| `bateriaMah`             | Integer      | Capacidad de batería en mAh          |
| `nfc`                    | Boolean      | Tiene NFC: true / false              |
| `precio`                 | BigDecimal   | Precio actual en euros               |
| `fechaLanzamiento`       | LocalDate    | Fecha de lanzamiento (YYYY-MM-DD)    |
| `numConsultas`           | Long         | Contador interno para tendencias     |

---

## 🔄 Flujo de uso típico

```
1. [Guest] GET /api/moviles/tendencias        → Ver los 5 más populares
2. [Guest] GET /api/moviles/{id}              → Click en uno → detalle completo
3. [Guest] GET /api/moviles/marcas            → Obtener marcas disponibles
4. [Guest] GET /api/moviles/buscar?...        → Buscar con criterios + precio
5. [Guest] GET /api/moviles/comparar?id1=&id2= → Comparar dos móviles

6. POST /api/auth/login                       → Obtener token JWT (admin)
7. [Admin] POST /api/moviles                  → Añadir nuevo móvil
8. [Admin] PUT  /api/moviles/{id}             → Editar móvil
9. [Admin] DELETE /api/moviles/{id}           → Borrar móvil
```

---

## 🛠️ Tecnologías utilizadas

- Spring Boot 3.3
- Spring Security + JWT (jjwt 0.12.3)
- Spring Data JPA + Specifications (búsqueda dinámica)
- MapStruct (mappings DTO ↔ Entity)
- Lombok
- MySQL 8
- Bean Validation (jakarta.validation)
