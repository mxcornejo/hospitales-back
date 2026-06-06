# Sistema de Alertas Médicas en Tiempo Real - Backend

Sistema de microservicios para la gestión de señales vitales y alertas de pacientes críticos en un hospital.

## Arquitectura

```
┌─────────────────────────────────────────────────────┐
│                    POSTMAN / Frontend                │
└──────────────────────────┬──────────────────────────┘
                           │ HTTP
┌──────────────────────────▼──────────────────────────┐
│           BFF (puerto 8080)                         │
│     Spring Boot + Spring Security + JWT             │
│  /api/auth  /api/pacientes  /api/signos-vitales     │
│  /api/alertas  /api/usuarios                        │
└────────┬──────────────┬──────────────┬──────────────┘
         │              │              │
    ┌────▼────┐   ┌─────▼─────┐  ┌────▼─────┐
    │ms-paci- │   │ms-signos- │  │ms-alerta │
    │entes    │   │vitales    │  │s         │
    │:8081    │   │:8082      │  │:8083     │
    └────┬────┘   └─────┬─────┘  └────┬─────┘
         │              │              │
    ┌────▼──────────────▼──────────────▼─────┐
    │         Oracle DB (XEPDB1)             │
    │  PACIENTES | SIGNOS_VITALES | ALERTAS  │
    │  USUARIOS                              │
    └────────────────────────────────────────┘
```

## Requisitos

- Docker Desktop (con Docker Compose)
- Java 17+ (solo para desarrollo local)
- Maven 3.9+ (solo para desarrollo local)

## Levantar con Docker

```bash
docker-compose up --build
```

## Servicios y puertos

| Servicio          | Puerto | Descripción                         |
| ----------------- | ------ | ----------------------------------- |
| BFF               | 8080   | Punto de entrada. Autenticación JWT |
| ms-pacientes      | 8081   | CRUD de pacientes                   |
| ms-signos-vitales | 8082   | CRUD de signos vitales              |
| ms-alertas        | 8083   | CRUD de alertas                     |
| Oracle DB         | 1521   | Base de datos Oracle XE 21          |

## Usuarios por defecto

| Usuario   | Contraseña   | Rol       |
| --------- | ------------ | --------- |
| admin     | admin123     | ADMIN     |
| medico    | medico123    | MEDICO    |
| enfermera | enfermera123 | ENFERMERA |

## Endpoints BFF (requieren token JWT excepto /api/auth/login)

### Autenticación

| Método | Endpoint        | Descripción       |
| ------ | --------------- | ----------------- |
| POST   | /api/auth/login | Obtener token JWT |

### Pacientes

| Método | Endpoint                       | Descripción         |
| ------ | ------------------------------ | ------------------- |
| GET    | /api/pacientes                 | Listar todos        |
| GET    | /api/pacientes/{id}            | Obtener por ID      |
| GET    | /api/pacientes/estado/{estado} | Filtrar por estado  |
| POST   | /api/pacientes                 | Crear paciente      |
| PUT    | /api/pacientes/{id}            | Actualizar paciente |
| DELETE | /api/pacientes/{id}            | Eliminar paciente   |

### Signos Vitales

| Método | Endpoint                                          | Descripción           |
| ------ | ------------------------------------------------- | --------------------- |
| GET    | /api/signos-vitales                               | Listar todos          |
| GET    | /api/signos-vitales/{id}                          | Obtener por ID        |
| GET    | /api/signos-vitales/paciente/{pacienteId}         | Por paciente          |
| GET    | /api/signos-vitales/paciente/{pacienteId}/ultimos | Últimos 10 registros  |
| POST   | /api/signos-vitales                               | Registrar signo vital |
| PUT    | /api/signos-vitales/{id}                          | Actualizar            |
| DELETE | /api/signos-vitales/{id}                          | Eliminar              |

### Alertas

| Método | Endpoint                           | Descripción       |
| ------ | ---------------------------------- | ----------------- |
| GET    | /api/alertas                       | Listar todas      |
| GET    | /api/alertas/{id}                  | Obtener por ID    |
| GET    | /api/alertas/paciente/{pacienteId} | Por paciente      |
| GET    | /api/alertas/estado/{estado}       | Por estado        |
| POST   | /api/alertas                       | Crear alerta      |
| PUT    | /api/alertas/{id}                  | Actualizar alerta |
| DELETE | /api/alertas/{id}                  | Eliminar alerta   |

### Usuarios (solo ADMIN)

| Método | Endpoint           | Descripción        |
| ------ | ------------------ | ------------------ |
| GET    | /api/usuarios      | Listar usuarios    |
| GET    | /api/usuarios/{id} | Obtener por ID     |
| POST   | /api/usuarios      | Crear usuario      |
| PUT    | /api/usuarios/{id} | Actualizar usuario |
| DELETE | /api/usuarios/{id} | Eliminar usuario   |

### Prueba (sin autenticación)

| Método | Endpoint        | Descripción                   |
| ------ | --------------- | ----------------------------- |
| GET    | /api/test/hello | Mensaje de prueba del backend |

## Pruebas con Postman

### 1. Login

```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Guardar el campo `token` de la respuesta.

### 2. Usar el token en las demás peticiones

En Postman: pestaña **Authorization** → **Bearer Token** → pegar el token.

### 3. Crear paciente

```
POST http://localhost:8080/api/pacientes
Authorization: Bearer <token>
Content-Type: application/json

{
  "nombre": "Pedro",
  "apellido": "Soto",
  "rut": "15432678-3",
  "edad": 55,
  "habitacion": "UCI-03",
  "diagnostico": "Arritmia cardíaca",
  "estado": "CRITICO"
}
```

### 4. Registrar signo vital

```
POST http://localhost:8080/api/signos-vitales
Authorization: Bearer <token>
Content-Type: application/json

{
  "pacienteId": 1,
  "frecuenciaCardiaca": 115,
  "presionSistolica": 150,
  "presionDiastolica": 90,
  "saturacionOxigeno": 93.5,
  "temperatura": 38.5
}
```

### 5. Crear alerta

```
POST http://localhost:8080/api/alertas
Authorization: Bearer <token>
Content-Type: application/json

{
  "pacienteId": 1,
  "tipo": "TAQUICARDIA",
  "severidad": "ALTA",
  "descripcion": "Frecuencia cardíaca superior a 100 lpm",
  "estado": "ACTIVA"
}
```

## Valores de referencia

### Estado del paciente

- `CRITICO`, `ESTABLE`, `ALTA`

### Tipo de alerta

- `TAQUICARDIA`, `BRADICARDIA`, `HIPOXIA`, `HIPERTENSION`, `HIPOTENSION`, `FIEBRE`, `OTRO`

### Severidad de alerta

- `ALTA`, `MEDIA`, `BAJA`

### Estado de alerta

- `ACTIVA`, `RESUELTA`
