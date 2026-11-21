# Origin API

API REST de gestión de tareas hecha con **Java 17 y Spring Boot 3**. Permite crear, consultar, actualizar y eliminar tareas, con filtros, paginación, validación de datos y documentación interactiva.

Fue mi primer proyecto backend propio. Esta versión lo lleva de una API en memoria a una API preparada para producción: base de datos real, tests y Docker.

## Tecnologías

| Área | Herramienta |
|---|---|
| Lenguaje y framework | Java 17, Spring Boot 3.5 |
| Persistencia | Spring Data JPA (Hibernate), PostgreSQL 16, H2 en memoria para desarrollo y tests |
| Validación | Bean Validation (`@NotBlank`, `@Size`, `@FutureOrPresent`…) |
| Documentación | OpenAPI / Swagger UI (springdoc) |
| Tests | JUnit 5, Mockito, MockMvc, `@DataJpaTest` |
| Entorno | Docker y Docker Compose |
| Otros | Lombok, Maven |

## Arrancar el proyecto

### Opción 1: rápido, sin instalar base de datos (H2 en memoria)

Requisito: Java 17 o superior.

```bash
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

Los datos se pierden al parar la aplicación. Es lo más cómodo para probar.

### Opción 2: con PostgreSQL usando Docker Compose

Requisito: Docker.

```bash
cp .env.example .env          # y cambia POSTGRES_PASSWORD
docker compose up --build
```

Para pararlo y borrar también los datos: `docker compose down -v`.

En los dos casos:

- API: http://localhost:8080/tasks
- Swagger UI: http://localhost:8080/swagger-ui.html

## Endpoints

| Método | Ruta | Descripción | Respuesta correcta |
|---|---|---|---|
| `GET` | `/tasks` | Lista tareas (paginado, con filtros opcionales) | `200` |
| `GET` | `/tasks/{id}` | Obtiene una tarea | `200` |
| `POST` | `/tasks` | Crea una tarea | `201` + cabecera `Location` |
| `PUT` | `/tasks/{id}` | Actualiza una tarea completa | `200` |
| `DELETE` | `/tasks/{id}` | Elimina una tarea | `204` |

### Filtros, paginación y ordenación en `GET /tasks`

| Parámetro | Ejemplo | Descripción |
|---|---|---|
| `completed` | `true` | Filtra por estado |
| `priority` | `ALTA` | Filtra por prioridad: `BAJA`, `MEDIA` o `ALTA` |
| `page` | `0` | Número de página (empieza en 0) |
| `size` | `10` | Tamaño de página (por defecto 10, máximo 50) |
| `sort` | `dueDate,asc` | Campo y dirección. Por defecto `createdAt,desc` |

Ejemplo: `GET /tasks?completed=false&priority=ALTA&sort=dueDate,asc`

### Ejemplo: crear una tarea

```http
POST /tasks
Content-Type: application/json

{
  "title": "Preparar entrevista técnica",
  "description": "Repasar Spring Boot, JPA y SQL",
  "priority": "ALTA",
  "dueDate": "2026-12-15"
}
```

Respuesta `201 Created`:

```json
{
  "id": 1,
  "title": "Preparar entrevista técnica",
  "description": "Repasar Spring Boot, JPA y SQL",
  "completed": false,
  "priority": "ALTA",
  "dueDate": "2026-12-15",
  "createdAt": "2026-11-21T12:00:00Z",
  "updatedAt": "2026-11-21T12:00:00Z"
}
```

### Reglas de los datos

| Campo | Regla |
|---|---|
| `title` | Obligatorio, máximo 100 caracteres |
| `description` | Opcional, máximo 500 caracteres |
| `priority` | `BAJA`, `MEDIA` o `ALTA`. Al crear, si no se indica, es `MEDIA` |
| `dueDate` | Opcional, formato `AAAA-MM-DD`. Al crear no puede ser anterior a hoy |
| `completed` | Al crear siempre es `false`. En `PUT` es obligatorio |

### Errores

Todas las respuestas de error tienen el mismo formato. En los errores de validación se indica qué campo falla:

```json
{
  "timestamp": "2026-11-21T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Hay campos con valores no válidos",
  "path": "/tasks",
  "fieldErrors": { "title": "El título es obligatorio" }
}
```

| Código | Cuándo |
|---|---|
| `400` | Datos no válidos, JSON mal formado, parámetro con tipo incorrecto u ordenación por un campo inexistente |
| `404` | La tarea o la ruta no existen |
| `500` | Error inesperado. Se registra en el log y no se muestran detalles internos al cliente |

## Tests

```bash
./mvnw test
```

21 tests en cuatro niveles:

- **Servicio** (`TaskServiceTest`): lógica de negocio con Mockito, sin base de datos.
- **Controlador** (`TaskControllerTest`): endpoints, códigos HTTP y validación con MockMvc.
- **Repositorio** (`TaskRepositoryTest`): filtros, paginación y fechas de auditoría sobre H2.
- **Extremo a extremo** (`OriginApiApplicationTests`): ciclo completo de una tarea con la aplicación entera.

También hay una colección de Postman en [`postman/`](postman/OriginAPI.postman_collection.json).

## Estructura

```
src/main/java/io/github/andyan07/originapi
├── config/       Configuración de Swagger
├── controller/   Endpoints REST
├── dto/          Datos de entrada y salida (records)
├── exception/    Excepciones y manejador global de errores
├── model/        Entidad JPA y enum de prioridad
├── repository/   Acceso a datos y filtros (Specifications)
└── service/      Lógica de negocio
```

## Decisiones técnicas

- **Entidad separada de la API.** La entidad `Task` no se expone directamente: la API recibe y devuelve records (`TaskCreateRequest`, `TaskUpdateRequest`, `TaskResponse`). Así se puede cambiar la base de datos sin romper el contrato con los clientes.
- **Lombok sin `@Data` en la entidad.** Solo se usan `@Getter`, `@Setter` y `@NoArgsConstructor`. `@Data` genera `equals`, `hashCode` y `toString` con todos los campos, lo que en entidades JPA puede dar problemas con relaciones y carga perezosa.
- **Filtros con Specifications.** Los filtros opcionales se combinan en una sola consulta, sin escribir un método distinto para cada combinación.
- **Página propia (`PageResponse`).** Se devuelve un JSON estable en vez del objeto `Page` de Spring, cuyo formato no está pensado como contrato público.
- **H2 para desarrollar, PostgreSQL para desplegar.** Arrancar y testear no requiere instalar nada; la base de datos real se levanta con Docker Compose y las credenciales llegan por variables de entorno.
- **Contenedor seguro.** La imagen se construye en dos etapas (solo queda el JRE y el `.jar`), se ejecuta sin usuario root y el puerto solo se publica en `127.0.0.1`.

### Error corregido de la versión anterior

La primera versión comparaba identificadores con `task.getId() == id`, siendo ambos `Integer`. En Java, `==` compara referencias de objetos, no valores. Funcionaba por casualidad hasta el id 127, porque Java reutiliza los objetos `Integer` entre -128 y 127; a partir del 128 la tarea "no se encontraba". La solución correcta es `equals()`. En esta versión la búsqueda la hace la base de datos con `findById`, así que el problema desaparece.

## Próximos pasos

- Migraciones de base de datos con Flyway (ahora el esquema lo genera Hibernate).
- Autenticación con Spring Security y JWT.
- Despliegue en la nube.

---

Origin API es el punto donde dejé el volante para tomar el teclado. Después de miles de kilómetros en carretera, empecé un viaje distinto: el del desarrollo de software.
