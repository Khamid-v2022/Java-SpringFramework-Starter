# spring-starter

An **offline-ready WAR starter** built on **Spring Framework 7** — without Spring Boot.  
It uses the classic **Spring MVC + Hibernate + Oracle** stack and is designed for learning, extension, and USB-based offline development.

> For run instructions (online / offline / STS): **[how-to-run.md](how-to-run.md)**

---

## Purpose

| Goal | Description |
|------|-------------|
| **Learn Spring fundamentals** | Configure MVC, ORM, and transactions manually — no Spring Boot magic |
| **Offline development** | Copy to a USB drive and build/run without internet |
| **Real-world patterns** | Layered architecture, REST, WebSocket, document export samples |
| **STS-friendly** | Import into Spring Tool Suite (Eclipse) and develop locally |
| **Extension base** | Add new domains, screens, and APIs using the same patterns |

---

## Tech Stack

### Backend

| Layer | Technology | Version |
|-------|------------|---------|
| Framework | Spring Framework (MVC, ORM, TX, WebSocket) | **7.0.8** |
| ORM | Hibernate | **7.1.0** |
| Validation | Hibernate Validator + Jakarta Validation | 9.0.1 / 3.1 |
| View | Thymeleaf | 3.1.3 |
| JSON | Jackson | 2.18.3 |
| Connection pool | HikariCP | 6.2.1 |
| Database | **Oracle** (production) + **H2** (local/offline default) | ojdbc11 / H2 |
| Logging | SLF4J + Logback | 2.0.16 / 1.5.16 |
| Build | Maven (bundled in project) | 3.9.11 |
| Servlet | Jakarta Servlet | 6.1 |
| Server | Apache Tomcat | **11.x** |
| JDK | Java | **17+** (verified on 21) |

> Spring Framework 7 requires **Tomcat 11** and **JDK 17+**. Tomcat 9/10 will not work.

### Frontend (WebJars — cached locally for offline use)

| Library | Version |
|---------|---------|
| Bootstrap | 5.3.3 |
| jQuery | 3.7.1 |
| DataTables | 2.3.8 |
| Font Awesome | 6.5.1 |
| SockJS + STOMP | 1.5.1 / 2.3.4 |

### Documents & utilities (included for future features)

| Use case | Library |
|----------|---------|
| Excel / Word | Apache POI 5.4 |
| PDF | OpenPDF 2.0.3 |
| DTO mapping | ModelMapper |
| Common utilities | Commons Lang3, IO, Collections4, Text, CSV |
| HTML parsing | Jsoup |
| Image resize | Thumbnailator |
| Other | Guava |

---

## Sample Features

| Feature | URL | Description |
|---------|-----|-------------|
| **Product CRUD** | `/spring-starter/products` | Thymeleaf + Bootstrap + DataTables |
| **Excel export** | `/products/export/excel` | Apache POI (.xlsx) |
| **PDF export** | `/products/export/pdf` | OpenPDF |
| **Word export** | `/products/export/word` | Apache POI (.docx) |
| **REST API** | `/api/products` | JSON CRUD |
| **REST demo page** | `/api-demo` | jQuery calls to the API |
| **WebSocket chat** | `/websocket` | STOMP over SockJS |

The default database is **H2 in-memory**, so you can run the app without installing Oracle.

---

## Architecture

```
Browser (Bootstrap / jQuery / DataTables)
        │
        ▼
┌───────────────────────────────────────┐
│  Spring MVC  (@Controller / @RestController)
│  WebSocket   (STOMP)
└───────────────────────────────────────┘
        │
        ▼
┌───────────────────────────────────────┐
│  Service Layer  (@Service, @Transactional)
└───────────────────────────────────────┘
        │
        ▼
┌───────────────────────────────────────┐
│  DAO Layer  (Hibernate SessionFactory)
└───────────────────────────────────────┘
        │
        ▼
┌───────────────────────────────────────┐
│  HikariCP  →  H2 (default) / Oracle
└───────────────────────────────────────┘
```

### Package layout

```
com.example.starter
├── config/     WebAppInitializer, MVC, Hibernate, WebSocket, DataInitializer
├── domain/     JPA/Hibernate entities (@Entity)
├── dao/        SessionFactory-based DAOs (interface + impl)
├── service/    Business logic + @Transactional
├── dto/        Form / Response / API DTOs
├── web/        MVC controllers, REST controllers, WebSocket
└── export/     Excel / PDF / Word download endpoints
```

### Difference from Spring Boot

| Aspect | This project | Spring Boot |
|--------|--------------|-------------|
| Entry point | `WebAppInitializer` (Java Config) | `@SpringBootApplication` |
| Configuration | `@Configuration` classes | `application.yml` + auto-config |
| Server | External Tomcat 11 | Embedded Tomcat |
| Hibernate | `LocalSessionFactoryBean` (Spring ORM) | `spring-boot-starter-data-jpa` |
| Deployment | WAR → Tomcat `webapps/` | `java -jar` |

---

## Quick Start

### Online PC (development)

```bat
cd spring-starter
scripts\build-offline.bat
scripts\deploy-tomcat.bat
```

→ http://localhost:8080/spring-starter/products

### Offline PC (USB)

1. Copy the three folders listed below (**USB copy list**)
2. Run `scripts\offline-setup.bat` (once per PC)
3. Run `scripts\build-offline.bat`
4. Run `scripts\deploy-tomcat.bat`

Details: **[how-to-run.md](how-to-run.md)**

---

## USB Copy List

Copy everything in one step from an online PC:

```bat
scripts\copy-to-usb.bat F:\
```

| USB folder | Contents | Size (approx.) |
|------------|----------|----------------|
| `spring-starter\` | Project + bundled Maven (`.tools`) | 60 MB |
| `m2-repository\` | Full Maven dependency cache | 1.7 GB |
| `apache-tomcat-11.0.11\` | Tomcat 11 | 15 MB |

Also copy **JDK 17+** and **STS** installers if the offline PC does not have them.

---

## Using Oracle

1. Edit `src/main/resources/db-oracle.properties`
2. (Optional) Run `sql/oracle-schema.sql`
3. Start with JVM argument: `-Ddb.profile=oracle`

For Tomcat, add to `bin\setenv.bat`:

```bat
set "CATALINA_OPTS=%CATALINA_OPTS% -Ddb.profile=oracle"
```

---

## Extending the Project

When adding a new feature (e.g. **Customer**), follow this order:

### 1. Add an entity

`domain/Customer.java`

```java
@Entity
@Table(name = "CUSTOMERS")
public class Customer { /* id, name, ... */ }
```

### 2. Add a DAO

```java
// dao/CustomerDao.java        — interface
// dao/CustomerDaoImpl.java    — @Repository, uses SessionFactory
```

Copy and adapt `ProductDao` / `ProductDaoImpl` for the fastest path.

### 3. Add a service

```java
@Service
@Transactional(readOnly = true)
public class CustomerService { /* create, update, delete, find */ }
```

### 4. Add controllers

| Type | Location | Example |
|------|----------|---------|
| HTML pages | `web/CustomerController.java` | `@Controller`, `@RequestMapping` |
| REST API | `web/CustomerRestController.java` | `@RestController`, `/api/customers` |
| Export | `export/CustomerExportController.java` | POI / OpenPDF |

### 5. Add Thymeleaf templates

```
src/main/resources/templates/customers/
  list.html
  form.html
```

Use `products/list.html` and `layout.html` as references.

### 6. Add Maven dependencies (online PC only)

Add the dependency to `pom.xml` → run `scripts\prepare-offline.bat` → refresh `m2-repository` on the USB.

### 7. Component scan

New classes under `com.example.starter` are picked up automatically.  
If you use a different root package, update the scan settings in `RootConfig` / `WebMvcConfig`.

---

## Best Practices (used in this project)

### Layered architecture

- **Controller**: HTTP request/response only — no business logic
- **Service**: Transaction boundaries (`@Transactional`), domain rules
- **DAO**: Database access only (Hibernate Session)
- **DTO**: View/API objects — do not expose entities directly

### Transactions

- Default: `@Transactional(readOnly = true)` on service classes
- Write methods: `@Transactional` (readOnly = false)

### Validation

- Form/API input: `@Valid` + Bean Validation (`@NotBlank`, `@Min`, …)
- Always name path variables explicitly: `@PathVariable("id")` (works with or without `-parameters`)

### Configuration

- Switch DB via `-Ddb.profile=h2` / `-Ddb.profile=oracle` + `db-*.properties`
- **Java Config only** (`@Configuration`) — no Spring Boot
- Minimal `web.xml` — `WebAppInitializer` is the entry point

### Offline development

- Frontend libraries via WebJars (no CDN)
- Maven offline build verified with `-o`
- Copy `m2-repository` to `%USERPROFILE%\.m2\repository` (safe when USB drive letter changes)

### REST API

- Use proper HTTP status codes (201, 204, 400, 404)
- Separate REST and MVC exception handling with `@ControllerAdvice`

### Frontend

- Bootstrap 5 + shared `layout.html` fragments
- DataTables: use `<script th:inline="none">` so Thymeleaf does not parse `[[` as an expression

---

## Scripts

| Script | Purpose |
|--------|---------|
| `scripts/prepare-offline.bat` | Online PC: download all dependencies |
| `scripts/copy-to-usb.bat` | Copy project + repo + Tomcat to USB |
| `scripts/offline-setup.bat` | Offline PC: install m2-repository into `.m2` |
| `scripts/build-offline.bat` | Offline WAR build (`mvn -o`) |
| `scripts/deploy-tomcat.bat` | Deploy WAR to Tomcat and start |
| `scripts/stop-tomcat.bat` | Stop Tomcat |

---

## STS 5.3 Notes

- **Import**: Maven → Existing Maven Projects
- **Maven → Update Project** may fail with `InvalidOverlayConfigurationException` (known m2e-wtp issue)
- If that happens: edit code in STS, build and run with `scripts\build-offline.bat`

---

## License / Contribution

Free to modify and extend for learning and internal starter use.

---

## Related docs

- [how-to-run.md](how-to-run.md) — Offline setup, STS, Tomcat, troubleshooting
- [sql/oracle-schema.sql](sql/oracle-schema.sql) — Oracle DDL
