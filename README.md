# SIGOMEI

**Sistema de Gestión de Órdenes de Mantenimiento de Equipos Industriales**

Aplicación distribuida cliente-servidor desarrollada en Java con comunicación por Sockets TCP.

---

## Prerrequisitos

| Herramienta | Versión mínima |
|-------------|---------------|
| Java JDK    | 17            |
| Apache Maven| 3.8           |
| MySQL       | 8.0           |

---

## Configuración de la base de datos

1. Inicia MySQL y ejecuta el script de creación:

```bash
mysql -u root -p < sigomei.sql
```

Esto crea la base de datos `sigomei`, las tablas y carga datos de prueba.

2. Configura las credenciales JDBC en `src/main/resources/db.properties`:

```
db.url=jdbc:mysql://127.0.0.1:3306/sigomei?serverTimezone=UTC
db.user=root
db.password=TU_CONTRASEÑA
```

---

## Compilar el proyecto

Desde la raíz del proyecto (donde está `pom.xml`):

```bash
mvn compile
```

Para compilar y empaquetar en un JAR:

```bash
mvn package -DskipTests
```

---

## Ejecutar el servidor

El servidor debe iniciarse **antes** que el cliente.

```bash
mvn exec:java -Dexec.mainClass="com.sigomei.server.ServerMain"
```

O si ya tienes el JAR generado:

```bash
java -cp target/sigomei-1.0-SNAPSHOT.jar com.sigomei.server.ServerMain
```

El servidor escucha en el puerto **5000** por defecto.

---

## Ejecutar el cliente (GUI)

Con el servidor en ejecución, abre otra terminal y ejecuta:

```bash
mvn exec:java -Dexec.mainClass="com.sigomei.client.ClientMain"
```

O con el JAR:

```bash
java -cp target/sigomei-1.0-SNAPSHOT.jar com.sigomei.client.ClientMain
```

Al iniciar, el cliente intentará conectarse automáticamente a `localhost:5000`.  
Si el servidor no está disponible aparecerá un aviso; puedes reconectar desde el menú **Conexión → Conectar al servidor**.

---

## Estructura del proyecto

```
SIGOMEI/
├── pom.xml
├── sigomei.sql                  ← Script BD (CREATE TABLE + datos de prueba)
├── README.md
└── src/
    ├── main/java/com/sigomei/
    │   ├── model/
    │   │   ├── Equipo.java
    │   │   ├── Tecnico.java
    │   │   └── Orden.java
    │   ├── exception/
    │   │   └── BusinessException.java
    │   ├── service/
    │   │   ├── EquipoService.java
    │   │   ├── TecnicoService.java
    │   │   ├── OrdenService.java
    │   │   └── impl/
    │   │       ├── EquipoServiceImpl.java
    │   │       ├── TecnicoServiceImpl.java
    │   │       └── OrdenServiceImpl.java
    │   ├── dao/
    │   │   ├── EquipoDAO.java
    │   │   ├── TecnicoDAO.java
    │   │   └── OrdenDAO.java
    │   ├── protocol/
    │   │   ├── Request.java     ← Mensaje cliente → servidor
    │   │   └── Response.java    ← Mensaje servidor → cliente
    │   ├── server/
    │   │   ├── ServerMain.java  ← Punto de entrada del servidor
    │   │   └── ClientHandler.java
    │   └── client/
    │       ├── ClientMain.java  ← Punto de entrada del cliente
    │       ├── ServerConnection.java
    │       ├── MainFrame.java
    │       ├── EquipoPanel.java
    │       ├── TecnicoPanel.java
    │       └── OrdenPanel.java
    └── test/java/com/sigomei/
        ├── RN01Test.java
        ├── ...
        └── RN08Test.java
```

---

## Protocolo de comunicación

El cliente y el servidor se comunican mediante **serialización de objetos Java** sobre TCP.

- El cliente envía un objeto `Request` con:
  - `command` — acción a realizar (p.ej. `LIST_EQUIPOS`, `ADD_ORDEN`)
  - `payload` — objeto asociado (p.ej. un `Equipo`, un `Integer` con el ID)

- El servidor responde con un objeto `Response` con:
  - `success` — `true` si la operación fue exitosa
  - `data` — resultado (p.ej. `List<Equipo>`, `null`)
  - `message` — descripción del resultado o del error

---

## Ejecutar las pruebas unitarias

```bash
mvn test
```

Las 22 pruebas unitarias (RN01Test – RN08Test) validan las reglas de negocio de los servicios.

---

## Autores

| Persona | Responsabilidad |
|---------|-----------------|
| Persona 1 | Backend: servicios, DAO, MySQL, servidor TCP |
| Persona 2 | Cliente GUI Swing, Script SQL, README |
| Persona 3 | Pruebas unitarias TDD, integración final |

Universidad Veracruzana
