# 🔧 Configuración del Entorno - Brain Boost Backend

**Proyecto**: Brain Boost - Plataforma de aprendizaje adaptativo con gamificación e IA  
**Repositorio**: [capstone_grupo_3](https://github.com/ignacio-leon-m/capstone_grupo_3)  
**Backend Path**: `capstone-backend-code/CapstoneBackend`  
**Última Actualización**: Noviembre 2025

---

## 📋 Tabla de Contenidos

1. [Frameworks y Tecnologías](#frameworks-y-tecnologías)
2. [Lenguajes y Versiones](#lenguajes-y-versiones)
3. [Bases de Datos](#bases-de-datos)
4. [Dependencias del Proyecto](#dependencias-del-proyecto)
5. [Herramientas de Desarrollo](#herramientas-de-desarrollo)
6. [APIs y Servicios Externos](#apis-y-servicios-externos)
7. [Configuración por Ambiente](#configuración-por-ambiente)

---

## 🚀 Frameworks y Tecnologías

### Framework Principal: Spring Boot
- **Versión**: 3.5.5
- **Propósito**: Framework principal para desarrollo de aplicaciones empresariales Java/Kotlin
- **Módulos Utilizados**:
  - **Spring Boot Starter Web**: API REST con arquitectura MVC
  - **Spring Boot Starter Data JPA**: Persistencia con Hibernate
  - **Spring Boot Starter Security**: Autenticación y autorización JWT
  - **Spring Boot Starter Validation**: Validación de datos con Bean Validation
  - **Spring Boot Starter Cache**: Caching con Caffeine
  - **Spring Boot Starter Actuator**: Monitoreo y métricas
  - **Spring Boot Starter Data MongoDB**: Gestión de contenido temporal (staging)

### ORM y Persistencia
- **Hibernate**: 6.6.26.Final
  - Mapeo objeto-relacional (ORM)
  - Optimización de consultas con Criteria API
  - Lazy loading y eager fetching configurables
  
- **Spring Data JPA**: 3.5.5
  - Repositorios declarativos
  - Query methods automáticos
  - Paginación y sorting integrados

### Seguridad
- **Spring Security**: 6.2.10
  - Configuración de endpoints protegidos
  - Filtros de autenticación personalizados
  - CORS configurado para permitir credenciales desde cualquier origen
  - Encriptación de contraseñas con BCrypt

- **JWT (JSON Web Tokens)**: 0.12.3
  - **Biblioteca**: `io.jsonwebtoken:jjwt-api`, `jjwt-impl`, `jjwt-jackson`
  - Access tokens: 1 hora en producción, 24 horas en desarrollo
  - Refresh tokens: 7 días
  - Firmado con HS256 (HMAC-SHA256)

### Migraciones de Base de Datos
- **Flyway**: 11.7.2
  - Versionado automático del esquema
  - Archivos SQL en `src/main/resources/db/migration/`
  - Baseline on migrate habilitado
  - Validación estricta en producción

---

## 💻 Lenguajes y Versiones

### Lenguaje Principal: Kotlin
- **Versión**: 1.9.25
- **Plugins**:
  - `kotlin("jvm")`: Compilación JVM
  - `kotlin("plugin.spring")`: Optimizaciones para Spring Boot (clases open por defecto)
  - `kotlin("plugin.jpa")`: Soporte para entidades JPA sin getters/setters
- **Características Utilizadas**:
  - Data classes para DTOs
  - Null safety
  - Extension functions
  - Coroutines (preparado para implementación futura)
  - Destructuring declarations

### Lenguaje Runtime: Java
- **Versión**: 21 (JDK 21.0.8+12-LTS-250)
- **Toolchain**: Especificado en `build.gradle.kts` para consistencia
- **Características Java 21**:
  - Virtual Threads (Project Loom) - preparado para uso
  - Pattern Matching mejorado
  - Record classes

### Frontend Estático
- **HTML5**: Estructura de páginas
- **CSS3**: Estilos con variables CSS personalizadas
- **JavaScript ES6+**: 
  - Fetch API para llamadas REST
  - Promises y async/await
  - Módulos ES6 (preparado)
  - Local Storage para tokens JWT

### Scripts de Build
- **PowerShell**: Scripts de automatización (Windows)
  - `test-environments.ps1`: Testing automatizado de ambientes
- **Bash**: Wrappers de Gradle (Linux/macOS)
  - `gradlew`: Gradle Wrapper

---

## 🗄️ Bases de Datos

### Base de Datos Principal: Neon PostgreSQL

**Información del Servicio**:
- **Proveedor**: Neon (https://neon.tech)
- **Tipo**: Serverless PostgreSQL
- **Versión PostgreSQL**: 17.5
- **Plan**: Free Tier con pooling automático
- **Región**: US West 2 (Oregon)
- **Endpoint**: `ep-fancy-tree-af5xp9ie-pooler.c-2.us-west-2.aws.neon.tech`
- **Base de datos**: `neondb`
- **SSL/TLS**: Requerido (`sslmode=require`, `channel_binding=require`)

**Características Técnicas**:
- **Connection Pooling**: HikariCP integrado en Spring Boot
  - Máximo: 10 conexiones (producción)
  - Mínimo idle: 5 conexiones
  - Connection timeout: 30 segundos
- **Extensiones PostgreSQL**:
  - `uuid-ossp`: Generación de UUIDs v4 para primary keys
- **Diseño del Esquema**:
  - UUIDs como primary keys (no autoincrement)
  - Estructura jerárquica: países → regiones → comunas → instituciones → carreras → asignaturas → temas → conceptos
  - CASCADE constraints para integridad referencial
  - Índices en foreign keys y columnas de búsqueda frecuente
  - TIMESTAMP con `CURRENT_TIMESTAMP` para auditoría

**Propósito**:
- Datos transaccionales críticos
- Usuarios, roles, autenticación
- Estructura académica (instituciones, carreras, asignaturas)
- Gamificación (juegos, puntajes, métricas)
- Contenido pedagógico (temas, conceptos, preguntas)
- Auditoría de cargas masivas

### Base de Datos Secundaria: MongoDB

**Información del Servicio**:
- **Versión**: Spring Data MongoDB 5.5.1
- **Propósito Diseñado**: Staging de contenido temporal antes de persistir en PostgreSQL
- **Problema Actual**: 

**Casos de Uso Planeados**:
- Almacenar temporalmente documentos procesados por IA
- Cache de respuestas Gemini antes de validación
- Buffer para carga masiva de contenido

**Resolución Pendiente**: Configurar MongoDB Atlas o eliminar dependencia en servicios

---

## 📦 Dependencias del Proyecto

### Inteligencia Artificial

#### Google Gemini SDK
```kotlin
implementation("com.google.genai:google-genai:1.23.0")
```
- **Propósito**: Integración con Google Gemini API para generación de contenido pedagógico
- **Modelo Utilizado**: `gemini-2.0-flash`
- **Funcionalidades**:
  - Extracción de conceptos desde PDFs
  - Generación de preguntas tipo quiz
  - Generación de pistas (hints) para Hangman
  - Análisis de errores de estudiantes para personalización
- **Configuración**: API Key en `application.properties` (`gemini.api-key`)

### Procesamiento de Archivos

#### Apache Tika
```kotlin
implementation("org.apache.tika:tika-core:3.2.3")
implementation("org.apache.tika:tika-parsers-standard-package:3.2.3")
```
- **Propósito**: Detección automática de tipos MIME y extracción de texto
- **Formatos Soportados**:
  - PDF (sin OCR)
  - Microsoft Word (DOC, DOCX)
  - Microsoft Excel (XLS, XLSX)
  - Texto plano (TXT)
  - HTML, XML
- **Integración**: `FileUploadService` usa Tika para procesamiento universal

#### Apache POI
```kotlin
implementation("org.apache.poi:poi:5.4.1")
implementation("org.apache.poi:poi-ooxml:5.4.1")
```
- **Propósito**: Carga masiva de usuarios desde archivos Excel
- **Funcionalidades**:
  - Lectura de archivos `.xlsx` con múltiples hojas
  - Validación de estructura de columnas
  - Normalización de datos (nombres, RUTs, correos)
  - Manejo de errores por fila con reporte detallado

### Caching

#### Caffeine
```kotlin
implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
```
- **Propósito**: Cache en memoria para respuestas de IA Gemini
- **Configuración**:
  - Tamaño máximo: 100 entradas
  - TTL (Time To Live): 1 hora
  - Eviction policy: LRU (Least Recently Used)
- **Integración**: Anotación `@Cacheable` en `GeminiAiService`

### Validación y Utilidades

#### Jakarta Validation
```kotlin
// Incluido en spring-boot-starter-validation
```
- **Anotaciones Utilizadas**:
  - `@NotNull`, `@NotBlank`, `@NotEmpty`
  - `@Email`, `@Pattern`, `@Size`
  - `@Min`, `@Max`
  - Custom validators para RUT chileno

#### Jackson
```kotlin
// Incluido en Spring Boot Web
```
- **Propósito**: Serialización/deserialización JSON
- **Configuración**:
  - Formato de fechas: ISO-8601
  - Ignorar propiedades desconocidas
  - Incluir solo propiedades no nulas

### Testing

#### JUnit 5 + Mockito
```kotlin
testImplementation("org.springframework.boot:spring-boot-starter-test")
```
- **JUnit Jupiter**: 5.10.x
- **Mockito**: Mocking de servicios
- **MockMvc**: Testing de controladores REST
- **TestContainers**: (preparado) Testing con PostgreSQL real

#### JaCoCo
```kotlin
plugins {
    jacoco
}
```
- **Propósito**: Cobertura de código
- **Configuración**: Reportes HTML y XML
- **Objetivo**: 80% de cobertura en servicios críticos

### Drivers de Base de Datos

#### PostgreSQL JDBC Driver
```kotlin
runtimeOnly("org.postgresql:postgresql")
```
- **Versión**: Gestionada por Spring Boot (42.7.x)
- **Propósito**: Conectividad JDBC con Neon PostgreSQL

---

## 🛠️ Herramientas de Desarrollo

### Sistema de Build: Gradle

**Versión**: 8.14.3 (Gradle Wrapper)

**Archivo de Configuración**: `build.gradle.kts` (Kotlin DSL)

**Plugins Aplicados**:
```kotlin
plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
}
```

**Tareas Principales**:
- `./gradlew clean`: Limpiar build anterior
- `./gradlew build`: Compilar y empaquetar JAR (127MB fat JAR)
- `./gradlew bootRun`: Ejecutar aplicación en desarrollo
- `./gradlew test`: Ejecutar tests unitarios
- `./gradlew bootJar`: Generar JAR ejecutable

**Configuración de Compilación**:
- Encoding: UTF-8
- Java toolchain: JDK 21
- Kotlin JVM target: 21
- Tests: JUnit Platform

### Contenedorización: Docker

**Dockerfile Multi-Stage**:
```dockerfile
# Stage 1: Build (gradle:8.5-jdk21)
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle gradle
COPY src src
RUN ./gradlew build --no-daemon -x test

# Stage 2: Runtime (eclipse-temurin:21-jre-jammy)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/CapstoneBackend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Características**:
- Compilación aislada (build stage)
- Imagen runtime ligera (JRE solamente)
- Health check integrado (`/actuator/health`)
- Optimizado para Render.com

**Docker Compose Local**:
```yaml
services:
  postgres:
    image: postgres:13
    ports: ["5432:5432"]
  backend:
    build: .
    depends_on: [postgres]
    ports: ["8080:8080"]
```

### Control de Versiones: Git

**Repositorio**: https://github.com/ignacio-leon-m/capstone_grupo_3

**Branch Actual**: `feature/app-web-close`

**Estructura de Branches**:
- `main`: Producción (auto-deploy a Render)
- `develop`: Desarrollo estable
- `feature/*`: Features en desarrollo

---

## 🌐 APIs y Servicios Externos

### Google Gemini API

**Documentación**: https://ai.google.dev/gemini-api/docs

**Configuración**:
```properties
gemini.api-key=${GEMINI_API_KEY:AIzaSyClZcoMsMzXYmSxazv6P4SNSdlOFW2FNo8}
```

**Modelo**: `gemini-2.0-flash`
- Velocidad: ~2 segundos por respuesta
- Contexto: 32k tokens
- Salida: Máximo 8k tokens

**Rate Limits**:
- Free tier: 15 requests/minute, 1500 requests/day
- Cache implementado para optimizar uso

**Integración**:
- Servicio: `GeminiAiService`
- Endpoints: `/api/ai/query`, `/api/files/upload-query-pdf`

### Neon PostgreSQL Database

**Dashboard**: https://console.neon.tech

**Características**:
- Serverless (sin servidor siempre activo)
- Auto-scaling automático
- Branching de base de datos (para testing)
- Connection pooling integrado
- SSL/TLS obligatorio

**Monitoreo**:
- Métricas de consultas
- Storage usado
- Conexiones activas
- Logs de queries lentos

---

## ⚙️ Configuración por Ambiente

### Estructura de Configuración

El proyecto utiliza **Spring Profiles** para separar ambientes:

```
src/main/resources/
├── application.properties           # Configuración base
├── application-dev.properties       # Desarrollo local
└── application-prod.properties      # Producción (Render)
```

### Activación de Perfiles

**Variable de Entorno**:
```bash
SPRING_PROFILES_ACTIVE=dev   # Desarrollo
SPRING_PROFILES_ACTIVE=prod  # Producción
```

**Valor por Defecto**: `dev` (si no se especifica)

---

### 🔹 Ambiente: Desarrollo (`dev`)

**Archivo**: `application-dev.properties`

**Características**:
- SQL logging habilitado
- Flyway validation deshabilitada (para cambios rápidos)
- Logging nivel DEBUG
- JWT tokens largos (24 horas para testing)
- Health check con detalles completos
- CORS permisivo
- MongoDB autoconfiguration deshabilitada

**Configuración Clave**:
```properties
# Base de datos
spring.datasource.url=jdbc:postgresql://ep-fancy-tree-af5xp9ie-pooler.c-2.us-west-2.aws.neon.tech/neondb?sslmode=require
spring.datasource.username=neondb_owner
spring.datasource.password=npg_CinWX0he6lUp

# JPA - Debugging
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG

# Flyway - Permisivo
spring.flyway.validate-on-migrate=false
spring.flyway.out-of-order=true

# JWT - Tokens largos para testing
jwt.access.expiration=86400000  # 24 horas

# Logging - Verbose
logging.level.org.duocuc.capstonebackend=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.security=DEBUG

# Actuator - Full details
management.endpoint.health.show-details=always

# MongoDB 
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
```

**Ejecución Local**:
```bash
# Windows (PowerShell)
$env:SPRING_PROFILES_ACTIVE="dev"
./gradlew bootRun

# Linux/macOS
export SPRING_PROFILES_ACTIVE=dev
./gradlew bootRun
```

**Puerto**: 8080

---

### 🔹 Ambiente: Producción (`prod`)

**Archivo**: `application-prod.properties`

**Características**:
- SQL logging deshabilitado (performance)
- Flyway validation habilitada
- Logging nivel INFO/WARN
- JWT tokens cortos (1 hora)
- Health check con autorización
- Error details ocultos (seguridad)
- Connection pool optimizado

**Configuración Clave**:
```properties
# Base de datos - Desde variables de entorno
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}

# Connection Pool - Optimizado
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000

# JPA - Sin SQL logging
spring.jpa.show-sql=false

# JWT - Tokens estándar
jwt.secret=${JWT_SECRET}
jwt.access.expiration=3600000  # 1 hora

# Logging - Solo INFO
logging.level.root=INFO
logging.level.org.springframework=WARN

# Actuator - Seguro
management.endpoint.health.show-details=when-authorized

# Seguridad - Ocultar detalles de errores
server.error.include-message=never
server.error.include-stacktrace=never
```

**Deployment**: Render.com

**Variables de Entorno en Render**:
```yaml
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://...
DATABASE_USERNAME=neondb_owner
DATABASE_PASSWORD=<secret>
JWT_SECRET=<secret>
GEMINI_API_KEY=<secret>
JAVA_TOOL_OPTIONS=-Xmx400m -Xms256m  # Free tier: 512MB RAM
```

**Puerto**: Dinámico (variable `$PORT` de Render)

---

## 📊 Resumen de Configuración

| Aspecto | Desarrollo | Producción |
|---------|-----------|------------|
| **Profile** | `dev` | `prod` |
| **Base de datos** | Neon PostgreSQL (misma) | Neon PostgreSQL |
| **SQL Logging** | ✅ Habilitado | ❌ Deshabilitado |
| **Flyway Validation** | ❌ Deshabilitada | ✅ Habilitada |
| **JWT Access Token** | 24 horas | 1 hora |
| **Logging Level** | DEBUG | INFO/WARN |
| **Health Check Details** | Siempre | Solo autorizado |
| **Error Stacktraces** | ✅ Visibles | ❌ Ocultos |
| **Connection Pool Max** | Default (10) | 10 |
| **Puerto** | 8080 | `$PORT` (variable) |

---

## 🔐 Variables de Entorno Requeridas

### Desarrollo Local

**Opcional** (tienen valores por defecto en `application-dev.properties`):
```bash
DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD
JWT_SECRET
GEMINI_API_KEY
```

### Producción (Render)

**Obligatorias** (configuradas en Render Dashboard):
```bash
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://ep-fancy-tree-af5xp9ie-pooler.c-2.us-west-2.aws.neon.tech/neondb?sslmode=require
DATABASE_USERNAME=neondb_owner
DATABASE_PASSWORD=<secret>
JWT_SECRET=<secret>
JWT_ACCESS_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000
GEMINI_API_KEY=<secret>
JAVA_TOOL_OPTIONS=-Xmx400m -Xms256m
PORT=8080  # Configurado automáticamente por Render
```

---

## 📚 Referencias

- **Spring Boot Docs**: https://docs.spring.io/spring-boot/index.html
- **Kotlin Docs**: https://kotlinlang.org/docs/home.html
- **Neon Docs**: https://neon.tech/docs/introduction
- **Google Gemini API**: https://ai.google.dev/gemini-api/docs
- **Flyway Docs**: https://documentation.red-gate.com/fd
- **Gradle Docs**: https://docs.gradle.org/current/userguide/userguide.html
- **Docker Docs**: https://docs.docker.com/

---

## 📝 Notas Adicionales

### Seguridad

- **Contraseñas**: Nunca en código fuente, solo variables de entorno
- **JWT Secret**: Mínimo 256 bits (generado aleatoriamente)
- **HTTPS**: Obligatorio en producción (Render lo proporciona automáticamente)
- **CORS**: Configurado para permitir credenciales desde cualquier origen (revisar para producción)

### Performance

- **Connection Pool**: Ajustado para free tier de Render (512MB RAM)
- **Cache Gemini**: Reduce llamadas API y mejora tiempos de respuesta
- **Lazy Loading**: Configurado en relaciones JPA para evitar N+1 queries

### Limitaciones Conocidas

- **MongoDB**: Configurado pero no operativo (ver sección Bases de Datos)
- **File Uploads**: Limitados a 10MB por archivo
- **Render Free Tier**: Se suspende tras 15 minutos de inactividad

---

**Última Revisión**: Noviembre 2025  
**Mantenido por**: Equipo Capstone Grupo 3 (Valencia, León, Bertero)
