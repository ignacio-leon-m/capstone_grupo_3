# Guía Completa: Migración de PostgreSQL Local a Neon Database

## 📋 Índice

1. [Introducción](#introducción)
2. [Prerequisitos](#prerequisitos)
3. [Paso 1: Registro en Neon](#paso-1-registro-en-neon)
4. [Paso 2: Instalación de Neon CLI](#paso-2-instalación-de-neon-cli)
5. [Paso 3: Creación del Proyecto en Neon](#paso-3-creación-del-proyecto-en-neon)
6. [Paso 4: Preparación de las Migraciones de Flyway](#paso-4-preparación-de-las-migraciones-de-flyway)
7. [Paso 5: Configuración de la Aplicación](#paso-5-configuración-de-la-aplicación)
8. [Paso 6: Ejecución y Verificación](#paso-6-ejecución-y-verificación)
9. [Solución de Problemas](#solución-de-problemas)
10. [Mejores Prácticas](#mejores-prácticas)

---

## Introducción

Esta guía documenta el proceso completo de migración desde una base de datos PostgreSQL local (usando Docker) a **Neon Database**, una solución PostgreSQL serverless en la nube.

### ¿Por qué Neon?

- ✅ **Sin infraestructura**: No necesitas Docker ni PostgreSQL local
- ✅ **Escalamiento automático**: Se ajusta según el uso
- ✅ **Backups automáticos**: Protección de datos incluida
- ✅ **Branching**: Crea copias de la BD para testing sin costo adicional
- ✅ **SSL/TLS integrado**: Conexión segura por defecto
- ✅ **Plan gratuito**: Suficiente para desarrollo y proyectos pequeños

---

## Prerequisitos

Antes de comenzar, asegúrate de tener:

- [ ] Java 17 o superior instalado
- [ ] Gradle instalado (o usa el wrapper `./gradlew`)
- [ ] Node.js y npm instalados (para Neon CLI)
- [ ] Una aplicación Spring Boot con Flyway configurado
- [ ] Cuenta de correo electrónico para registro
- [ ] Conexión a Internet estable

---

## Paso 1: Registro en Neon

### 1.1 Acceder al sitio web

1. Visita [https://neon.tech](https://neon.tech)
2. Haz clic en **"Sign Up"** o **"Get Started"**

### 1.2 Crear cuenta

Puedes registrarte usando:

- **GitHub**: Recomendado para desarrolladores
- **Google**: Opción rápida con cuenta Google
- **Email**: Registro tradicional con correo electrónico

```
Ejemplo con GitHub:
1. Click en "Sign up with GitHub"
2. Autoriza la aplicación Neon
3. Completa el perfil si es necesario
```

### 1.3 Verificar cuenta

- Si usaste email, verifica tu bandeja de entrada
- Haz clic en el enlace de verificación
- Inicia sesión en la consola de Neon

### 1.4 Explorar el Dashboard

Una vez dentro, verás:
- Panel de control principal
- Botón para crear proyectos
- Menú de navegación lateral
- Opciones de configuración

---

## Paso 2: Instalación de Neon CLI

### 2.1 Instalar Neon CLI globalmente

Abre PowerShell o terminal y ejecuta:

```powershell
npm install -g neonctl
```

**Salida esperada:**
```
added 1 package in 2s
```

### 2.2 Verificar instalación

```powershell
neonctl --version
```

**Salida esperada:**
```
1.x.x
```

### 2.3 Inicializar Neon en tu proyecto

Navega a la carpeta de tu proyecto:

```powershell
cd C:\ruta\a\tu\proyecto\CapstoneBackend
```

Ejecuta:

```powershell
npx neonctl@latest init
```

**Proceso interactivo:**
```
? How would you like to authenticate? 
  > Web browser (recommended)
    API key

? Select a project or create a new one:
  > Create a new project
    existing-project-1
    existing-project-2

? Enter a name for your new project: capstone-brainboost

? Select a region:
  > US West (Oregon) - aws-us-west-2
    US East (N. Virginia) - aws-us-east-1
    Europe (Frankfurt) - aws-eu-central-1

✓ Project created successfully!
```

### 2.4 Autenticación

- El navegador se abrirá automáticamente
- Inicia sesión si no lo has hecho
- Autoriza Neon CLI
- Vuelve a la terminal

---

## Paso 3: Creación del Proyecto en Neon

### 3.1 Opciones de creación

Tienes dos opciones para crear el proyecto:

#### Opción A: Desde la Consola Web

1. Accede a [https://console.neon.tech](https://console.neon.tech)
2. Click en **"New Project"**
3. Configura:
   - **Project name**: `capstone-brainboost`
   - **Región**: `US West (Oregon)` - aws-us-west-2
   - **PostgreSQL version**: 17 (latest)
   - **Compute size**: Mantén el predeterminado (0.25 CU)
4. Click en **"Create Project"**

#### Opción B: Usando código (programáticamente)

Si estás usando un script o herramienta automatizada, puedes usar la API de Neon:

```kotlin
// Ejemplo conceptual - en la práctica usarás Neon CLI o la consola
val projectName = "capstone-brainboost"
val organizationId = "org-square-credit-27508595"
```

### 3.2 Obtener información del proyecto

Una vez creado, guarda esta información:

```yaml
Información del Proyecto:
  Nombre: capstone-brainboost
  ID: aged-water-40631549
  Rama: main (br-bold-silence-afgjmki1)
  Base de datos: neondb
  Usuario: neondb_owner
  Región: us-west-2
  
Cadena de Conexión:
  Host: ep-fancy-tree-af5xp9ie-pooler.c-2.us-west-2.aws.neon.tech
  Puerto: 5432
  Database: neondb
  Usuario: neondb_owner
  Password: npg_CinWX0he6lUp
  SSL Mode: require
```

⚠️ **IMPORTANTE**: Guarda la contraseña en un lugar seguro. Solo se muestra una vez.

---

## Paso 4: Preparación de las Migraciones de Flyway

### 4.1 Estructura de directorios

Crea la siguiente estructura si no existe:

```
src/
└── main/
    └── resources/
        └── db/
            └── migration/
                ├── V1__Initial_schema.sql
                └── V2__Insert_initial_data.sql
```

### 4.2 Crear archivo de esquema inicial

**Ruta**: `src/main/resources/db/migration/V1__Initial_schema.sql`

```sql
-- Extensión para generar UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--- Tablas de Ubicación e Institución
CREATE TABLE paises (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE regiones (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL UNIQUE,
    id_pais UUID NOT NULL,
    FOREIGN KEY (id_pais) REFERENCES paises(id)
);

CREATE TABLE comunas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL UNIQUE,
    id_region UUID NOT NULL,
    FOREIGN KEY (id_region) REFERENCES regiones(id)
);

CREATE TABLE instituciones (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(255) NOT NULL UNIQUE,
    id_comuna UUID,
    FOREIGN KEY (id_comuna) REFERENCES comunas(id)
);

-- Tablas de Roles y Contenido
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE carreras (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL,
    id_institucion UUID NOT NULL,
    FOREIGN KEY (id_institucion) REFERENCES instituciones(id),
    UNIQUE (nombre, id_institucion)
);

CREATE TABLE semestres (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE asignaturas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL,
    id_carrera UUID NOT NULL,
    FOREIGN KEY (id_carrera) REFERENCES carreras(id)
);

CREATE TABLE asignaturas_semestre (
    id_asignatura UUID NOT NULL,
    id_semestre UUID NOT NULL,
    PRIMARY KEY (id_asignatura, id_semestre),
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id),
    FOREIGN KEY (id_semestre) REFERENCES semestres(id)
);

-- Tablas de Usuarios
CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    rut VARCHAR (12) NOT NULL UNIQUE,
    id_rol UUID NOT NULL,
    id_carrera UUID NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    celular VARCHAR(20),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_ultimo_login TIMESTAMP,
    FOREIGN KEY (id_rol) REFERENCES roles(id),
    FOREIGN KEY (id_carrera) REFERENCES carreras(id)
);

-- Tabla de Relación Usuario-Asignatura
CREATE TABLE usuario_asignatura (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_usuario UUID NOT NULL,
    id_asignatura UUID NOT NULL,
    fecha_asignacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id) ON DELETE CASCADE,
    UNIQUE (id_usuario, id_asignatura)
);

-- Tablas de Gamificación
CREATE TABLE temas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL,
    id_asignatura UUID NOT NULL,
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id),
    UNIQUE (nombre, id_asignatura)
);

CREATE TABLE preguntas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    texto TEXT NOT NULL,
    respuesta_correcta TEXT NOT NULL,
    id_asignatura UUID NOT NULL,
    id_tema UUID NOT NULL,
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id),
    FOREIGN KEY (id_tema) REFERENCES temas(id)
);

CREATE TABLE conceptos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    palabra_concepto VARCHAR(255) NOT NULL,
    hint TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_tema UUID NOT NULL,
    FOREIGN KEY (id_tema) REFERENCES temas(id)
);

CREATE TABLE juegos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_usuario UUID NOT NULL,
    id_asignatura UUID NOT NULL,
    nombre_juego VARCHAR(50),
    intentos_restantes INT,
    estado_partida VARCHAR(50) NOT NULL DEFAULT 'En curso',
    fecha_inicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_fin TIMESTAMP,
    puntaje NUMERIC(10, 2),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id)
);

CREATE TABLE puntajes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_usuario UUID NOT NULL,
    id_asignatura UUID NOT NULL,
    puntaje NUMERIC(10, 2) NOT NULL,
    fecha_asignacion DATE NOT NULL DEFAULT CURRENT_DATE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id)
);

CREATE TABLE metricas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_usuario UUID NOT NULL,
    id_pregunta UUID NOT NULL,
    respuesta_correcta BOOLEAN NOT NULL,
    tiempo_respuesta_ms INT,
    fecha_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_pregunta) REFERENCES preguntas(id)
);

-- Tablas específicas por juego: HANGMAN
CREATE TABLE metricas_juego_hangman (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_usuario UUID NOT NULL,
    id_concepto UUID NOT NULL,
    letra_intentada CHAR(1) NOT NULL,
    es_correcta BOOLEAN NOT NULL,
    posicion_letra INT,
    tiempo_respuesta_ms INT,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id) ON DELETE CASCADE
);

CREATE TABLE resultados_juego_hangman (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_concepto UUID NOT NULL,
    adivinado BOOLEAN NOT NULL,
    intentos_usados INT NOT NULL,
    tiempo_total_ms INT,
    puntaje_obtenido NUMERIC(10, 2),
    vidas_restantes INT,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id) ON DELETE CASCADE,
    UNIQUE (id_juego, id_concepto)
);

-- Índices para mejorar performance
CREATE INDEX idx_usuario_asignatura_usuario ON usuario_asignatura(id_usuario);
CREATE INDEX idx_usuario_asignatura_asignatura ON usuario_asignatura(id_asignatura);
CREATE INDEX idx_usuario_asignatura_activo ON usuario_asignatura(activo);
CREATE INDEX idx_conceptos_tema ON conceptos(id_tema);
CREATE INDEX idx_conceptos_fecha ON conceptos(fecha_creacion DESC);
CREATE INDEX idx_metricas_hangman_juego ON metricas_juego_hangman(id_juego);
CREATE INDEX idx_metricas_hangman_usuario ON metricas_juego_hangman(id_usuario);
CREATE INDEX idx_metricas_hangman_concepto ON metricas_juego_hangman(id_concepto);
CREATE INDEX idx_metricas_hangman_fecha ON metricas_juego_hangman(fecha_hora DESC);
CREATE INDEX idx_resultados_hangman_juego ON resultados_juego_hangman(id_juego);
CREATE INDEX idx_resultados_hangman_adivinado ON resultados_juego_hangman(adivinado);
```

### 4.3 Crear archivo de datos iniciales

**Ruta**: `src/main/resources/db/migration/V2__Insert_initial_data.sql`

```sql
-- INSERCION DE DATOS INICIALES

WITH ins_pais AS (
    INSERT INTO paises (nombre) VALUES ('Chile') RETURNING id
),
ins_region AS (
    INSERT INTO regiones (nombre, id_pais)
    SELECT 'Metropolitana', id FROM ins_pais RETURNING id
),
ins_comuna AS (
    INSERT INTO comunas (nombre, id_region)
    SELECT 'Cerrillos', id FROM ins_region RETURNING id
),
ins_institucion AS (
    INSERT INTO instituciones (nombre, id_comuna)
    SELECT 'DUOC UC Sede Plaza Oeste', id FROM ins_comuna RETURNING id
),
ins_rol_1 AS (
    INSERT INTO roles (nombre) VALUES ('admin') RETURNING id
),
ins_rol_2 AS (
    INSERT INTO roles (nombre) VALUES ('profesor') RETURNING id
),
ins_rol_3 AS (
    INSERT INTO roles (nombre) VALUES ('alumno') RETURNING id
),
ins_semestre AS (
    INSERT INTO semestres (nombre) VALUES ('Primer Semestre 2026') RETURNING id
),
ins_carrera AS (
    INSERT INTO carreras (nombre, id_institucion)
    SELECT 'ing-informatica', id FROM ins_institucion RETURNING id
),
ins_asignatura AS (
    INSERT INTO asignaturas (nombre, id_carrera)
    SELECT 'Algoritmos y Programación', id FROM ins_carrera RETURNING id
),
ins_asignatura_semestre AS (
    INSERT INTO asignaturas_semestre (id_asignatura, id_semestre)
    SELECT ins_asignatura.id, ins_semestre.id 
    FROM ins_asignatura, ins_semestre
),
ins_usuario AS (
    INSERT INTO usuarios (nombre, apellido, rut, id_rol, id_carrera, password_hash, correo, celular)
    SELECT 'Cecilia', 'Arroyo', '12345678-9', ins_rol_1.id, ins_carrera.id, 
           '$2a$10$tCQpkcBLaBTmiMzOcdDamOkCxeGc4nIJXzFUkwcWAU8Cj5iRkLa/K',
           'cecilia.arroyo@duoc.cl', '1234567'
    FROM ins_rol_1, ins_carrera RETURNING id
),
ins_tema AS (
    INSERT INTO temas (nombre, id_asignatura)
    SELECT 'Conceptos Básicos', id FROM ins_asignatura RETURNING id
),
ins_pregunta AS (
    INSERT INTO preguntas (texto, respuesta_correcta, id_tema, id_asignatura)
    SELECT '¿Qué es una variable?', 
           'Un espacio en memoria para almacenar un valor.', 
           ins_tema.id, ins_asignatura.id
    FROM ins_asignatura, ins_tema RETURNING id
),
inst_concepto AS (
    INSERT INTO conceptos (palabra_concepto, hint, id_tema)
    SELECT 'VARIABLE', 
           'Espacio en memoria que almacena un valor', 
           ins_tema.id 
    FROM ins_tema RETURNING id
),
ins_juego AS (
    INSERT INTO juegos(id_usuario, id_asignatura, puntaje)
    SELECT ins_usuario.id, ins_asignatura.id, 150.00
    FROM ins_usuario, ins_asignatura
),
ins_puntajes AS (
    INSERT INTO puntajes (id_usuario, id_asignatura, puntaje)
    SELECT ins_usuario.id, ins_asignatura.id, 150.00
    FROM ins_usuario, ins_asignatura
)
INSERT INTO metricas (id_usuario, id_pregunta, respuesta_correcta, tiempo_respuesta_ms)
SELECT ins_usuario.id, ins_pregunta.id, TRUE, 3500
FROM ins_usuario, ins_pregunta;
```

### 4.4 Nomenclatura de archivos Flyway

Flyway sigue esta convención:

```
V{VERSION}__{DESCRIPCION}.sql

Ejemplos:
✅ V1__Initial_schema.sql
✅ V2__Insert_initial_data.sql
✅ V3__Add_user_preferences.sql
✅ V10__Add_indexes.sql

❌ v1_schema.sql (minúscula)
❌ V1_schema.sql (un solo guión bajo)
❌ schema.sql (sin versión)
```

---

## Paso 5: Configuración de la Aplicación

### 5.1 Actualizar application.properties

**Ruta**: `src/main/resources/application.properties`

**ANTES** (configuración local con Docker):
```properties
spring.application.name=CapstoneBackend
spring.flyway.baseline-on-migrate=true

# PostgreSQL Local
spring.datasource.url=jdbc:postgresql://localhost:5432/capstone-bbdd
spring.datasource.username=postgres
spring.datasource.password=vivigames77

# JPA/Hibernate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=none
```

**DESPUÉS** (configuración con Neon):
```properties
spring.application.name=CapstoneBackend
spring.flyway.baseline-on-migrate=true

# PostgreSQL - Neon Database
spring.datasource.url=jdbc:postgresql://ep-fancy-tree-af5xp9ie-pooler.c-2.us-west-2.aws.neon.tech/neondb?sslmode=require&channel_binding=require
spring.datasource.username=neondb_owner
spring.datasource.password=npg_CinWX0he6lUp

# JPA/Hibernate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=none

# JWT
jwt.secret=f4e2a0b8d5c3e1a9f0b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6f5
jwt.access.expiration=3600000
jwt.refresh.expiration=604800000

# GEMINI API
gemini.api-key=AIzaSyClZcoMsMzXYmSxazv6P4SNSdlOFW2FNo8
```

### 5.2 Formato de la URL de conexión

Estructura de la URL de Neon:

```
jdbc:postgresql://[HOST]/[DATABASE]?sslmode=require&channel_binding=require

Componentes:
- HOST: ep-fancy-tree-af5xp9ie-pooler.c-2.us-west-2.aws.neon.tech
- DATABASE: neondb
- PARÁMETROS:
  * sslmode=require (obligatorio para Neon)
  * channel_binding=require (seguridad adicional)
```

### 5.3 Variables de entorno (Opcional pero recomendado)

Para mayor seguridad en producción:

```properties
# application.properties
spring.datasource.url=${NEON_DATABASE_URL}
spring.datasource.username=${NEON_DATABASE_USER}
spring.datasource.password=${NEON_DATABASE_PASSWORD}
```

Luego configura las variables de entorno:

**Windows (PowerShell):**
```powershell
$env:NEON_DATABASE_URL="jdbc:postgresql://ep-fancy-tree-af5xp9ie-pooler.c-2.us-west-2.aws.neon.tech/neondb?sslmode=require"
$env:NEON_DATABASE_USER="neondb_owner"
$env:NEON_DATABASE_PASSWORD="npg_CinWX0he6lUp"
```

**Linux/Mac:**
```bash
export NEON_DATABASE_URL="jdbc:postgresql://ep-fancy-tree-af5xp9ie-pooler.c-2.us-west-2.aws.neon.tech/neondb?sslmode=require"
export NEON_DATABASE_USER="neondb_owner"
export NEON_DATABASE_PASSWORD="npg_CinWX0he6lUp"
```

### 5.4 Verificar configuración de Flyway

En `build.gradle.kts`, asegúrate de tener:

```kotlin
dependencies {
    // Flyway para migraciones
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    
    // PostgreSQL Driver
    runtimeOnly("org.postgresql:postgresql")
}
```

---

## Paso 6: Ejecución y Verificación

### 6.1 Compilar el proyecto

```powershell
./gradlew clean build
```

**Salida esperada:**
```
BUILD SUCCESSFUL in 15s
```

### 6.2 Ejecutar la aplicación

```powershell
./gradlew bootRun
```

**Logs esperados de Flyway:**

```
2025-11-21 19:14:16 INFO  org.flywaydb.core.FlywayExecutor
Database: jdbc:postgresql://ep-fancy-tree-af5xp9ie-pooler.c-2.us-west-2.aws.neon.tech/neondb (PostgreSQL 17.5)

2025-11-21 19:14:19 INFO  o.f.core.internal.command.DbValidate
Successfully validated 2 migrations (execution time 00:00.837s)

2025-11-21 19:14:22 INFO  o.f.core.internal.command.DbMigrate
Current version of schema "public": << Empty Schema >>

2025-11-21 19:14:22 INFO  o.f.core.internal.command.DbMigrate
Migrating schema "public" to version "1 - Initial schema"

2025-11-21 19:14:32 INFO  o.f.core.internal.command.DbMigrate
Migrating schema "public" to version "2 - Insert initial data"

2025-11-21 19:14:36 INFO  o.f.core.internal.command.DbMigrate
Successfully applied 2 migrations to schema "public", now at version v2 (execution time 00:08.341s)

2025-11-21 19:14:43 INFO  o.d.c.CapstoneBackendApplicationKt
Started CapstoneBackendApplicationKt in 33.766 seconds (process running for 34.43)
```

### 6.3 Verificar tablas creadas

**Opción A: Desde la Consola de Neon**

1. Accede a [https://console.neon.tech](https://console.neon.tech)
2. Selecciona tu proyecto `capstone-brainboost`
3. Ve a la pestaña **"SQL Editor"**
4. Ejecuta:

```sql
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_type = 'BASE TABLE' 
ORDER BY table_name;
```

**Resultado esperado:**
```
asignaturas
asignaturas_semestre
carreras
comunas
conceptos
flyway_schema_history
instituciones
juegos
metricas
metricas_juego_hangman
paises
preguntas
puntajes
regiones
resultados_juego_hangman
roles
semestres
temas
usuario_asignatura
usuarios
```

**Opción B: Con cliente PostgreSQL**

```bash
psql "postgresql://neondb_owner:npg_CinWX0he6lUp@ep-fancy-tree-af5xp9ie-pooler.c-2.us-west-2.aws.neon.tech/neondb?sslmode=require"
```

```sql
\dt
```

### 6.4 Verificar datos insertados

```sql
-- Verificar roles
SELECT nombre FROM roles ORDER BY nombre;
```

**Resultado esperado:**
```
admin
alumno
profesor
```

```sql
-- Verificar usuario admin
SELECT u.nombre, u.apellido, u.correo, r.nombre as rol 
FROM usuarios u 
JOIN roles r ON u.id_rol = r.id;
```

**Resultado esperado:**
```
nombre   | apellido | correo                   | rol
---------|----------|--------------------------|-------
Cecilia  | Arroyo   | cecilia.arroyo@duoc.cl  | admin
```

### 6.5 Probar la aplicación

1. Abre el navegador en `http://localhost:8080`
2. Deberías ver la página de login
3. Intenta iniciar sesión con:
   - **Email**: `cecilia.arroyo@duoc.cl`
   - **Password**: `duoc123`

---

## Solución de Problemas

### Problema 1: Error de conexión SSL

**Error:**
```
org.postgresql.util.PSQLException: The connection attempt failed.
FATAL: no pg_hba.conf entry for host
```

**Solución:**
Asegúrate de incluir `sslmode=require` en la URL:

```properties
spring.datasource.url=jdbc:postgresql://HOST/DATABASE?sslmode=require&channel_binding=require
```

### Problema 2: Flyway no encuentra las migraciones

**Error:**
```
WARN  o.f.core.internal.command.DbValidate : No migrations found. Are your locations set up correctly?
```

**Solución:**
1. Verifica que los archivos estén en `src/main/resources/db/migration/`
2. Verifica la nomenclatura: `V1__Description.sql`
3. Ejecuta:

```powershell
./gradlew clean build
```

### Problema 3: Error de autenticación

**Error:**
```
PSQLException: FATAL: password authentication failed for user "neondb_owner"
```

**Solución:**
1. Verifica que la contraseña sea correcta en `application.properties`
2. Si olvidaste la contraseña:
   - Ve a la consola de Neon
   - Selecciona tu proyecto
   - Ve a **Settings** → **Reset password**

### Problema 4: Migración ya aplicada

**Error:**
```
FlywayException: Validate failed: Migration version 1 is already applied
```

**Solución:**

**Opción A: Limpiar el esquema (⚠️ CUIDADO: Borra todos los datos)**

```sql
-- En el SQL Editor de Neon
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
```

**Opción B: Reparar historial de Flyway**

```sql
DELETE FROM flyway_schema_history WHERE version = '1';
```

### Problema 5: Timeout de conexión

**Error:**
```
Connection timeout: the driver has waited 60000 milliseconds
```

**Solución:**
1. Verifica tu conexión a Internet
2. Comprueba que el proyecto Neon esté activo (no en suspensión)
3. Aumenta el timeout en `application.properties`:

```properties
spring.datasource.hikari.connection-timeout=60000
spring.datasource.hikari.max-lifetime=1800000
```

---

## Mejores Prácticas

### 1. Seguridad

✅ **Hacer:**
- Usa variables de entorno para credenciales en producción
- Agrega `application.properties` a `.gitignore`
- Rota las contraseñas periódicamente
- Usa roles con permisos mínimos necesarios

❌ **No hacer:**
- Subir credenciales a repositorios públicos
- Compartir contraseñas en texto plano
- Usar la misma contraseña en todos los ambientes

### 2. Migraciones

✅ **Hacer:**
- Nombra las migraciones descriptivamente
- Versiona las migraciones secuencialmente
- Prueba las migraciones en desarrollo antes de producción
- Mantén las migraciones idempotentes cuando sea posible

❌ **No hacer:**
- Modificar migraciones ya aplicadas
- Usar fechas en lugar de versiones
- Mezclar DDL y DML en una migración (separa esquema y datos)

### 3. Neon Database

✅ **Hacer:**
- Usa branches para desarrollo/testing
- Monitorea el uso de recursos en el dashboard
- Configura alertas de límites de cuota
- Haz backups regulares (aunque Neon los hace automáticamente)

❌ **No hacer:**
- Usar el proyecto de producción para pruebas
- Ignorar las métricas de rendimiento
- Depender solo de backups automáticos para datos críticos

### 4. Desarrollo

✅ **Hacer:**
- Documenta cambios en el esquema
- Usa índices para consultas frecuentes
- Testea las consultas en el SQL Editor de Neon
- Mantén un archivo `.env.example` con la estructura

❌ **No hacer:**
- Hacer cambios directos en producción
- Ignorar las advertencias de Hibernate
- Usar `ddl-auto=create` o `update` en producción

---

## Recursos Adicionales

### Documentación Oficial

- **Neon**: [https://neon.tech/docs](https://neon.tech/docs)
- **Flyway**: [https://flywaydb.org/documentation](https://flywaydb.org/documentation)
- **Spring Boot**: [https://spring.io/guides](https://spring.io/guides)

### Consolas y Herramientas

- **Neon Console**: [https://console.neon.tech](https://console.neon.tech)
- **Neon CLI Docs**: [https://neon.tech/docs/reference/neon-cli](https://neon.tech/docs/reference/neon-cli)

### Comandos Útiles

```powershell
# Ver información del proyecto
neonctl projects list

# Ver branches
neonctl branches list --project-id aged-water-40631549

# Crear un nuevo branch
neonctl branches create --project-id aged-water-40631549 --name dev

# Ver métricas
neonctl operations list --project-id aged-water-40631549

# Obtener cadena de conexión
neonctl connection-string --project-id aged-water-40631549
```

---

## Resumen del Proceso

1. ✅ **Registro en Neon** → Crear cuenta en neon.tech
2. ✅ **Instalar Neon CLI** → `npm install -g neonctl`
3. ✅ **Crear Proyecto** → Desde consola web o CLI
4. ✅ **Preparar Migraciones** → Archivos SQL en `db/migration/`
5. ✅ **Configurar Aplicación** → Actualizar `application.properties`
6. ✅ **Ejecutar y Verificar** → `./gradlew bootRun`
7. ✅ **Probar Conexión** → Verificar tablas y datos

---

## Conclusión

Has migrado exitosamente tu aplicación de PostgreSQL local a Neon Database. Ahora disfrutas de:

- 🚀 **Infraestructura serverless** sin gestión de servidores
- 💰 **Costos optimizados** con plan gratuito generoso
- 🔒 **Seguridad mejorada** con SSL/TLS por defecto
- 📊 **Monitoreo integrado** desde el dashboard
- 🌿 **Branching de base de datos** para testing

**Próximos pasos sugeridos:**

1. Configurar alertas de uso en Neon
2. Crear un branch de desarrollo
3. Implementar CI/CD con Neon
4. Configurar backups programados adicionales
5. Optimizar queries usando el SQL Editor

---

**Fecha de creación**: Noviembre 21, 2025  
**Autor**: Documentado por GitHub Copilot  
**Proyecto**: BrainBoost - Capstone DUOC UC  
**Versión**: 1.0
