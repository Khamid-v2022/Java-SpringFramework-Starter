# How to Run — spring-starter (Offline)

Spring Framework 7.0.8 WAR project. **No Spring Boot.**
Default database is **H2 in-memory**, so Oracle is not required to test.

Verified working: CRUD, REST API, WebSocket, Excel/PDF/Word export,
Bootstrap 5.3 + jQuery + DataTables — all served from the local
Maven repository with **no internet access**.

---

## 1. Copy to the USB

On this (online) PC run:

```bat
cd /d E:\java-workspace\spring-starter
scripts\copy-to-usb.bat F:\
```

Replace `F:\` with your USB drive. It copies three things:

```text
F:\
  spring-starter\           project (~60 MB without target)
  m2-repository\            all dependencies (~1.7 GB, 27,000 files)
  apache-tomcat-11.0.11\    servlet container (~15 MB)
```

You need **~2 GB free** on the USB.

If the offline PC has no JDK or no STS, also copy:

- a **JDK 17+** installer (this project was verified on JDK 21)
- the **STS / Spring Tools for Eclipse** installer

> `spring-starter\.tools\apache-maven-3.9.11` is already inside the project.
> That is the Maven the offline PC uses, so Maven does not need to be installed
> there. Do not delete it.

---

## 2. First-time setup on the offline PC

Copy the three folders from the USB to the local disk, keeping them
**side by side**:

```text
D:\dev\spring-starter\
D:\dev\m2-repository\
D:\dev\apache-tomcat-11.0.11\
```

Install the JDK if needed, then open **cmd**:

```bat
cd /d D:\dev\spring-starter
scripts\offline-setup.bat
```

This copies `m2-repository` into `%USERPROFILE%\.m2\repository` and installs an
offline `settings.xml`. Run it **once** per PC.

> Why copy instead of pointing Maven at the USB path? A USB drive letter can
> change between PCs (`E:` → `F:`), which silently breaks the build. Using the
> default `%USERPROFILE%\.m2\repository` location means nothing to configure.

---

## 3. Build and run

```bat
cd /d D:\dev\spring-starter
scripts\build-offline.bat
scripts\deploy-tomcat.bat
```

Both scripts find the JDK and Tomcat automatically. Then open:

```text
http://localhost:8080/spring-starter/products
```

To stop Tomcat:

```bat
scripts\stop-tomcat.bat
```

After changing code, just repeat `build-offline.bat` and `deploy-tomcat.bat`.

If auto-detection fails, pass the Tomcat folder explicitly:

```bat
scripts\deploy-tomcat.bat "D:\dev\apache-tomcat-11.0.11"
```

---

## 4. Sample pages

| Feature | URL |
|---------|-----|
| CRUD + DataTables | `/spring-starter/products` |
| Export Excel | `/spring-starter/products/export/excel` |
| Export PDF | `/spring-starter/products/export/pdf` |
| Export Word | `/spring-starter/products/export/word` |
| REST API demo page | `/spring-starter/api-demo` |
| REST API (JSON) | `/spring-starter/api/products` |
| WebSocket chat | `/spring-starter/websocket` |

REST endpoints: `GET /api/products`, `GET /api/products/{id}`,
`POST /api/products`, `PUT /api/products/{id}`, `DELETE /api/products/{id}`.

---

## 5. Develop in STS

1. **File → Import → Maven → Existing Maven Projects** → select the project folder
2. **Window → Preferences → Java → Installed JREs** → add your JDK 17+, make it default
3. **Window → Preferences → Server → Runtime Environments → Add… → Apache Tomcat v11.0**
4. Right-click the project → **Maven → Update Project…** → OK

To run inside STS: right-click → **Run As → Run on Server** → Tomcat 11.

> **STS 5.3 note:** `Maven → Update Project` may fail with
> `InvalidOverlayConfigurationException`. That is a bug in the old m2e-wtp
> plugin, not in this project. Editing code in STS still works — just build and
> deploy with section 3 instead.

---

## 6. Switch to Oracle

1. Edit `src/main/resources/db-oracle.properties` (URL, user, password)
2. Optionally run `sql/oracle-schema.sql`
3. Start Tomcat with the JVM argument `-Ddb.profile=oracle`

Easiest way — create `<tomcat>\bin\setenv.bat`:

```bat
set "CATALINA_OPTS=%CATALINA_OPTS% -Ddb.profile=oracle"
```

In STS: **Run configuration → Arguments → VM arguments**.

The Oracle JDBC driver (`ojdbc11`) is already in the offline repository.

---

## 7. Troubleshooting

| Problem | Fix |
|---------|-----|
| `mvn` not recognized | Use `scripts\build-offline.bat` (bundled Maven) |
| Cannot resolve dependencies | Run `scripts\offline-setup.bat`; check `%USERPROFILE%\.m2\repository\org\springframework` exists |
| `Neither JAVA_HOME nor JRE_HOME is defined` | Install JDK 17+, or `set JAVA_HOME=C:\Program Files\Java\jdk-21` |
| `UnsupportedClassVersionError`, servlet errors | Must be JDK 17+ **and** Tomcat **11** (not 9 or 10) |
| Page returns 404 | Use the full path `/spring-starter/products` |
| Page returns 500 | See `<tomcat>\logs\localhost.<date>.log` |
| CSS/JS missing | Check `/spring-starter/webjars/bootstrap/5.3.3/css/bootstrap.min.css` returns 200 |
| Port 8080 already in use | Stop the other server, or edit `<tomcat>\conf\server.xml` |
| Maven tries to reach the internet | Confirm `%USERPROFILE%\.m2\settings.xml` contains `<offline>true</offline>` |

---

## 8. Adding libraries later

New dependencies can only be downloaded on an **online** PC:

1. Add the dependency to `pom.xml` while online
2. Run `scripts\prepare-offline.bat`
3. Run `scripts\copy-to-usb.bat F:\` again

---

## 9. Project layout

```text
src/main/java/com/example/starter/
  config/    WebAppInitializer, WebMvcConfig, PersistenceConfig, WebSocketConfig
  domain/    Hibernate entities (Product)
  dao/       SessionFactory-based DAOs
  service/   business logic + @Transactional
  web/       MVC controllers, REST controller, WebSocket controller
  export/    Excel / PDF / Word download endpoints
src/main/resources/
  templates/ Thymeleaf views (Bootstrap, DataTables)
  static/    application CSS
  db-h2.properties / db-oracle.properties
scripts/     offline setup, build, deploy, USB copy
sql/         Oracle schema
offline/     settings.xml template for offline Maven
```
