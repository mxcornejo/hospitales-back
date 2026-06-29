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
docker compose up --build
```

El `docker-compose.yml` crea la red `hospital-net` y levanta todos los servicios
necesarios: Oracle, RabbitMQ, BFF, pacientes, signos vitales, alertas y auditoría
JSON.

El BFF permite llamadas del frontend mediante `CORS_ALLOWED_ORIGINS`. Si cambias
el dominio o puerto del frontend, agrega ese origin a esa variable en
`docker-compose.yml`.

Si vas a ejecutar el BFF manualmente con `docker run`, primero debe existir esa
red y deben estar levantados Oracle y los microservicios en la misma red:

```bash
docker network create hospital-net
```

Si después vuelves a usar `docker compose up --build` y aparece este error:

```text
network hospital-net was found but has incorrect label com.docker.compose.network
```

significa que `hospital-net` fue creada manualmente y Compose necesita
recrearla con sus propias etiquetas. Detén los contenedores que estén usando la
red, elimina la red y vuelve a levantar el stack:

```bash
docker compose down
docker network rm hospital-net
docker compose up --build
```

Para este proyecto las variables de base de datos esperadas por Spring son
`DB_URL`, `DB_USER` y `DB_PASS`:

```bash
docker run -d \
  --name hospital-bff \
  --network hospital-net \
  -p 8080:8080 \
  -e DB_URL='jdbc:oracle:thin:@oracle-db:1521/XEPDB1' \
  -e DB_USER='hospital_user' \
  -e DB_PASS='hospital123' \
  -e AZURE_TENANT_ID='common' \
  -e AZURE_CLIENT_ID='765a73b2-5568-41b3-a9a4-2d865745b67c' \
  -e MS_PACIENTES_URL='http://hospital-ms-pacientes:8081' \
  -e MS_SIGNOS_VITALES_URL='http://hospital-ms-signos-vitales:8082' \
  -e MS_ALERTAS_URL='http://hospital-ms-alertas:8083' \
  mxcornejo/hospital-bff:latest
```

## Servicios y puertos

| Servicio          | Puerto | Descripción                         |
| ----------------- | ------ | ----------------------------------- |
| BFF               | 8080   | Punto de entrada. Autenticación JWT |
| ms-pacientes      | 8081   | CRUD de pacientes                   |
| ms-signos-vitales | 8082   | CRUD de signos vitales              |
| ms-alertas        | 8083   | CRUD de alertas                     |
| ms-auditoria-json | 8084   | Consumidor MQ para archivos JSON    |
| Oracle DB         | 1521   | Base de datos Oracle XE 21          |
| RabbitMQ          | 5672   | Broker AMQP                         |
| RabbitMQ UI       | 15672  | Consola de administración           |

RabbitMQ Management queda disponible en `http://localhost:15672` con usuario
`hospital` y contraseña `hospital123`.

## Mensajería RabbitMQ

El flujo asíncrono usa exchanges `fanout` para distribuir cada mensaje a todos
sus consumidores:

| Exchange                    | Cola                          | Consumidor        | Acción                         |
| --------------------------- | ----------------------------- | ----------------- | ------------------------------ |
| hospital.alertas.exchange   | hospital.alertas.db.queue     | ms-alertas        | Guarda alerta en Oracle        |
| hospital.alertas.exchange   | hospital.alertas.json.queue   | ms-auditoria-json | Genera archivo JSON de alerta  |
| hospital.resumenes.exchange | hospital.resumenes.json.queue | ms-auditoria-json | Genera archivo JSON de resumen |

`ms-signos-vitales` publica alertas cuando un signo vital queda fuera de los
rangos configurados y publica un resumen periódico cada 5 minutos por defecto.

Variables útiles:

| Variable                | Valor por defecto |
| ----------------------- | ----------------- |
| RESUMENES_FIXED_RATE_MS | 300000            |
| THRESHOLD_FC_MIN        | 60                |
| THRESHOLD_FC_MAX        | 100               |
| THRESHOLD_PS_MIN        | 90                |
| THRESHOLD_PS_MAX        | 140               |
| THRESHOLD_SPO2_MIN      | 92                |
| THRESHOLD_TEMP_MAX      | 38                |

Los archivos JSON se guardan en `backend/hospital-files/alertas` y
`backend/hospital-files/resumenes` cuando el sistema se levanta con Docker.

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

### Docker Cloud / Docker Lab

Cuando el stack corre en Docker Cloud/Lab, Postman debe llamar a la IP pública
del BFF. No uses `localhost` ni `127.0.0.1` desde tu computador para probar
cloud, porque esas direcciones apuntan a tu propia máquina.

Para este despliegue:

```text
Base URL cloud: http://54.210.62.242:8080
```

Los microservicios siguen usando nombres internos dentro de Docker Compose:

```text
MS_PACIENTES_URL=http://ms-pacientes:8081
MS_SIGNOS_VITALES_URL=http://ms-signos-vitales:8082
MS_ALERTAS_URL=http://ms-alertas:8083
```

Primero valida conectividad pública del BFF:

```bash
curl -i http://54.210.62.242:8080/api/test/hello
```

Respuesta esperada:

```json
{"mensaje":"Hola desde el backend"}
```

Si Postman o curl muestran `Could not connect`, el problema todavía está en la
publicación del puerto `8080` o en el firewall/security group de la VM. En la VM
verifica:

```bash
docker compose ps
docker logs hospital-bff
curl -i http://localhost:8080/api/test/hello
```

`docker compose ps` debe mostrar el BFF con `0.0.0.0:8080->8080/tcp`.

### 1. Login local

```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Guardar el campo `token` de la respuesta.

### 1.b Login en Docker Cloud

```
POST http://54.210.62.242:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

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

En Docker Cloud usa la misma ruta contra la IP pública del BFF:

```
POST http://54.210.62.242:8080/api/signos-vitales
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
