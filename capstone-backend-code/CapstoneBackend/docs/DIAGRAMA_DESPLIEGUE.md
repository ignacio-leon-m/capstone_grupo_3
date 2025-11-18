# 🏗️ Diagrama de Despliegue e Infraestructura - Brain Boost Backend

**Proyecto**: Brain Boost - Plataforma de aprendizaje adaptativo  
**Arquitectura**: Monolito modular con despliegue cloud-native  
**Última Actualización**: Noviembre 2025

---

## 📋 Tabla de Contenidos

1. [Arquitectura General](#arquitectura-general)
2. [Diagrama de Despliegue](#diagrama-de-despliegue)
3. [Componentes de Infraestructura](#componentes-de-infraestructura)
4. [Flujo de Datos](#flujo-de-datos)
5. [Seguridad y Red](#seguridad-y-red)
6. [Escalabilidad y Alta Disponibilidad](#escalabilidad-y-alta-disponibilidad)
7. [Monitoreo y Observabilidad](#monitoreo-y-observabilidad)

---

## 🏛️ Arquitectura General

### Decisión Arquitectónica

El proyecto utiliza **arquitectura monolítica modular**, no microservicios.

**Justificación**:
- Equipo pequeño (3 desarrolladores)
- Proyecto académico con tiempo limitado
- Menor complejidad operacional
- Deployment unificado
- Transacciones ACID simplificadas

**Módulos Internos** (organizados en paquetes):
```
org.duocuc.capstonebackend/
├── auth/          # Autenticación y JWT
├── user/          # Gestión de usuarios
├── content/       # Contenido pedagógico
├── ai/            # Integración Gemini IA
├── scoring/       # Puntajes y rankings
├── game/          # Lógica de juegos (Hangman, etc.)
└── config/        # Configuración transversal
```

---

## 📐 Diagrama de Despliegue

### Vista de Alto Nivel

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          INTERNET / USUARIOS                             │
│                                                                          │
│   ┌──────────────┐         ┌──────────────┐         ┌──────────────┐   │
│   │  Estudiante  │         │   Profesor   │         │     Admin    │   │
│   │   (Mobile)   │         │    (Web)     │         │    (Web)     │   │
│   └──────┬───────┘         └──────┬───────┘         └──────┬───────┘   │
│          │                        │                        │           │
│          │ HTTPS                  │ HTTPS                  │ HTTPS     │
│          │                        │                        │           │
└──────────┼────────────────────────┼────────────────────────┼───────────┘
           │                        │                        │
           └────────────────────────┴────────────────────────┘
                                    │
                                    ▼
           ┌────────────────────────────────────────────────────┐
           │           RENDER.COM (Oregon, US West)             │
           │                                                    │
           │  ┌──────────────────────────────────────────────┐ │
           │  │     Docker Container Runtime                 │ │
           │  │  ┌────────────────────────────────────────┐  │ │
           │  │  │  Brain Boost Backend (JAR 127MB)       │  │ │
           │  │  │  ┌──────────────────────────────────┐  │  │ │
           │  │  │  │  Spring Boot 3.5.5 / Kotlin      │  │  │ │
           │  │  │  │  JRE 21 (eclipse-temurin)        │  │  │ │
           │  │  │  │  Port: $PORT (dinámico)          │  │  │ │
           │  │  │  │  RAM: 512MB (free tier)          │  │  │ │
           │  │  │  └──────────────────────────────────┘  │  │ │
           │  │  │                                        │  │ │
           │  │  │  Endpoints:                            │  │ │
           │  │  │  - /api/auth/**   (autenticación)      │  │ │
           │  │  │  - /api/users/**  (gestión usuarios)   │  │ │
           │  │  │  - /api/content/** (contenido)         │  │ │
           │  │  │  - /api/files/**  (uploads)            │  │ │
           │  │  │  - /api/ai/**     (Gemini IA)          │  │ │
           │  │  │  - /api/hangman/** (juegos)            │  │ │
           │  │  │  - /api/scoring/** (puntajes)          │  │ │
           │  │  │  - /actuator/**   (health, metrics)    │  │ │
           │  │  └────────────────────────────────────────┘  │ │
           │  │                                              │ │
           │  │  Auto-Deploy: GitHub main branch push       │ │
           │  │  Health Check: /actuator/health (30s)       │ │
           │  │  SSL/TLS: Automático (certificado Render)   │ │
           │  └──────────────────────────────────────────────┘ │
           │                                                    │
           │  Configuración:                                   │
           │  - Plan: Free Tier                                │
           │  - Region: Oregon (US West)                       │
           │  - Build: Docker multi-stage                      │
           │  - Runtime: Docker                                │
           │  - Auto-scale: ❌ (fixed 1 instance)              │
           │  - Suspensión: Sí (15 min inactividad)            │
           └────────────────────┬───────────────────────────────┘
                                │
                                │ JDBC over SSL/TLS
                                │ (connection pooling HikariCP)
                                │
                                ▼
           ┌────────────────────────────────────────────────────┐
           │         NEON.TECH (US West 2, AWS)                 │
           │                                                    │
           │  ┌──────────────────────────────────────────────┐ │
           │  │    PostgreSQL 17.5 (Serverless)              │ │
           │  │                                              │ │
           │  │  Database: neondb                            │ │
           │  │  Endpoint: ep-fancy-tree-af5xp9ie-pooler     │ │
           │  │  Connection Pooling: Integrado               │ │
           │  │  SSL Mode: require + channel_binding         │ │
           │  │  Storage: ~100MB usado (free tier: 512MB)    │ │
           │  │                                              │ │
           │  │  Tablas Principales:                         │ │
           │  │  - usuarios (auth + perfiles)                │ │
           │  │  - roles (admin, profesor, alumno)           │ │
           │  │  - instituciones, carreras, asignaturas      │ │
           │  │  - temas, conceptos (contenido pedagógico)   │ │
           │  │  - preguntas (quiz)                          │ │
           │  │  - juegos, puntajes, metricas (gamificación) │ │
           │  │                                              │ │
           │  │  Migraciones: Flyway (versionadas)           │ │
           │  │  Backups: Automáticos (Neon)                 │ │
           │  │  Branching: Disponible para testing          │ │
           │  └──────────────────────────────────────────────┘ │
           │                                                    │
           │  Configuración:                                   │
           │  - Plan: Free Tier                                │
           │  - Region: US West 2 (cerca de Render Oregon)    │
           │  - Auto-suspend: Sí (5 min inactividad)           │
           │  - Auto-resume: Automático en primer query        │
           └────────────────────────────────────────────────────┘
                                │
                                │ (Opcional - Configurado pero no usado)
                                ▼
           ┌────────────────────────────────────────────────────┐
           │            MONGODB (No operativo)                  │
           │  ⚠️ Configurado en dependencias pero bloqueado     │
           │     por falta de mongoTemplate bean                │
           └────────────────────────────────────────────────────┘


           ┌────────────────────────────────────────────────────┐
           │         GOOGLE GEMINI API (Global)                 │
           │                                                    │
           │  ┌──────────────────────────────────────────────┐ │
           │  │    Generative AI - gemini-2.0-flash          │ │
           │  │                                              │ │
           │  │  Endpoint: generativelanguage.googleapis.com │ │
           │  │  API Key: Autenticación                      │ │
           │  │  Rate Limit: 15 req/min (free tier)          │ │
           │  │  Cache: Caffeine (1 hora, 100 entries)       │ │
           │  │                                              │ │
           │  │  Funciones:                                  │ │
           │  │  - Extracción de conceptos desde PDFs        │ │
           │  │  - Generación de preguntas                   │ │
           │  │  - Generación de hints para Hangman          │ │
           │  │  - Análisis de errores de estudiantes        │ │
           │  └──────────────────────────────────────────────┘ │
           │                                                    │
           │  Backend llama via SDK: google-genai:1.23.0       │
           └────────────────────────────────────────────────────┘


           ┌────────────────────────────────────────────────────┐
           │           GITHUB (Control de Versiones)            │
           │                                                    │
           │  Repository: ignacio-leon-m/capstone_grupo_3      │
           │  Path: capstone-backend-code/CapstoneBackend      │
           │                                                    │
           │  Branch: main → Auto-deploy a Render              │
           │  Webhook: Push event triggers deployment          │
           └────────────────────────────────────────────────────┘
```

---

## 🧩 Componentes de Infraestructura

### 1. Frontend Layer (Cliente)

#### Web Browser (Profesores, Administradores)
- **Tecnología**: HTML5, CSS3, JavaScript ES6+
- **Ubicación**: Servido como recursos estáticos desde Spring Boot
- **Path**: `src/main/resources/static/`
- **Autenticación**: JWT en `localStorage`
- **Páginas**:
  - `/index.html` - Login
  - `/home.html` - Dashboard
  - `/user.html` - Gestión de usuarios
  - `/content-upload.html` - Carga de contenido (PDF)
  - `/professor-subject.html` - Asignación profesor-asignatura

#### Mobile App (Estudiantes)
- **Tecnología**: Android (Kotlin)
- **Repository**: Separado (`capstone-brainboost-app`)
- **API Base URL**: Configurada en `RetrofitClient`
- **Autenticación**: JWT en SharedPreferences

### 2. Application Layer (Backend)

#### Spring Boot Application
**Runtime Environment**:
- JRE 21 (OpenJDK eclipse-temurin)
- Kotlin 1.9.25
- Spring Boot 3.5.5

**Arquitectura Interna**:
```
┌─────────────────────────────────────────┐
│      Spring Boot Container              │
│  ┌───────────────────────────────────┐  │
│  │  Presentation Layer               │  │
│  │  - REST Controllers               │  │
│  │  - Exception Handlers             │  │
│  │  - Request/Response DTOs          │  │
│  └─────────────┬─────────────────────┘  │
│                │                        │
│  ┌─────────────▼─────────────────────┐  │
│  │  Security Layer                   │  │
│  │  - JwtAuthenticationFilter        │  │
│  │  - SecurityConfig                 │  │
│  │  - CORS Configuration             │  │
│  └─────────────┬─────────────────────┘  │
│                │                        │
│  ┌─────────────▼─────────────────────┐  │
│  │  Business Logic Layer             │  │
│  │  - Services (Auth, User, Content) │  │
│  │  - Game Engines (Hangman, etc.)   │  │
│  │  - AI Integration (Gemini)        │  │
│  │  - File Processing (Tika, POI)    │  │
│  └─────────────┬─────────────────────┘  │
│                │                        │
│  ┌─────────────▼─────────────────────┐  │
│  │  Data Access Layer                │  │
│  │  - Spring Data JPA Repositories   │  │
│  │  - Entities (JPA)                 │  │
│  │  - Query Methods                  │  │
│  └─────────────┬─────────────────────┘  │
│                │                        │
│  ┌─────────────▼─────────────────────┐  │
│  │  Cross-Cutting Concerns           │  │
│  │  - Logging (SLF4J)                │  │
│  │  - Caching (Caffeine)             │  │
│  │  - Actuator (Health, Metrics)     │  │
│  │  - Flyway Migrations              │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

**Endpoints Principales**:
- `POST /api/auth/login` - Autenticación JWT
- `POST /api/auth/register` - Registro de usuarios
- `GET /api/users` - Listar usuarios (paginado)
- `POST /api/files/upload-query-pdf` - Procesamiento de PDFs con IA
- `POST /api/hangman/start` - Iniciar juego Hangman
- `GET /api/scoring/ranking` - Obtener ranking de estudiantes
- `GET /actuator/health` - Health check

### 3. Database Layer

#### Neon PostgreSQL (Primary Database)
**Características**:
- **Serverless**: Sin servidor siempre activo
- **Auto-scaling**: Escala automáticamente con demanda
- **Connection Pooling**: Built-in pooler + HikariCP en backend
- **SSL/TLS**: Obligatorio (`sslmode=require`)
- **Branching**: Permite crear branches de BD para testing

**Configuración de Conexión**:
```properties
spring.datasource.url=jdbc:postgresql://ep-fancy-tree-af5xp9ie-pooler.c-2.us-west-2.aws.neon.tech/neondb?sslmode=require&channel_binding=require
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

**Schema Management**:
- Flyway migrations en `src/main/resources/db/migration/`
- Versionado: `V1__Initial_schema.sql`, `V2__Insert_initial_data.sql`
- Validación estricta en producción

#### MongoDB (Staging - No Operativo)
**Estado**: ⚠️ Configurado pero bloqueado
- Dependencia presente en `build.gradle.kts`
- Autoconfiguration deshabilitada en dev profile
- Servicios requieren bean `mongoTemplate` que no existe
- **Acción requerida**: Configurar MongoDB Atlas o eliminar dependencia

### 4. External Services Layer

#### Google Gemini API
**Propósito**: Generación de contenido pedagógico con IA

**Integración**:
- SDK: `com.google.genai:google-genai:1.23.0`
- Modelo: `gemini-2.0-flash`
- Autenticación: API Key en variable de entorno

**Rate Limiting**:
- 15 requests/minuto (free tier)
- Cache local con Caffeine (1 hora, 100 entradas)
- Reduce llamadas repetidas y mejora latencia

**Funcionalidades**:
- Extracción de conceptos clave desde documentos PDF
- Generación de preguntas tipo quiz
- Creación de pistas (hints) para juego Hangman
- Análisis de patrones de error de estudiantes

---

## 🔄 Flujo de Datos

### Flujo de Autenticación

```
┌─────────┐     POST /api/auth/login      ┌──────────┐
│ Cliente ├────────────────────────────────►  Spring  │
│  (Web)  │  { email, password }           │   Boot   │
└─────────┘                                └─────┬────┘
                                                 │
                                                 │ BCrypt.checkpw()
                                                 ▼
                                          ┌──────────────┐
                                          │  PostgreSQL  │
                                          │  (usuarios)  │
                                          └──────┬───────┘
                                                 │
                                                 │ Usuario encontrado
                                                 ▼
┌─────────┐                              ┌──────────┐
│ Cliente │◄─────────────────────────────┤  Spring  │
│  (Web)  │  { accessToken, refreshToken,│   Boot   │
└────┬────┘    user: { ... } }           └──────────┘
     │                                    JWT firmado con
     │                                    HS256 + secret
     │
     │ Almacena en localStorage
     │
     ▼
┌─────────┐     GET /api/users            ┌──────────┐
│ Cliente ├────────────────────────────────►  Spring  │
│  (Web)  │  Authorization: Bearer <token> │   Boot   │
└─────────┘                                └─────┬────┘
                                                 │
                                                 │ JwtAuthenticationFilter
                                                 │ valida y decodifica token
                                                 ▼
                                          ┌──────────────┐
                                          │ SecurityContext│
                                          │ (authenticated)│
                                          └──────┬───────┘
                                                 │
                                                 │ Autorización por rol
                                                 ▼
┌─────────┐                              ┌──────────┐
│ Cliente │◄─────────────────────────────┤  Spring  │
│  (Web)  │  [ { user1 }, { user2 }, ... ]│   Boot   │
└─────────┘                              └──────────┘
```

### Flujo de Carga de Contenido (Profesor)

```
┌───────────┐   POST /api/files/upload-query-pdf   ┌──────────┐
│ Profesor  ├─────────────────────────────────────►│  Spring  │
│   (Web)   │   multipart/form-data (PDF file)     │   Boot   │
└───────────┘                                      └────┬─────┘
                                                        │
                                                        │ FileUploadService
                                                        ▼
                                                   ┌────────────┐
                                                   │  Apache    │
                                                   │   Tika     │
                                                   │ (extract)  │
                                                   └─────┬──────┘
                                                         │
                                                         │ Texto extraído
                                                         ▼
                                                   ┌────────────┐
                                                   │   Gemini   │
                                                   │    API     │
                                                   │ (concepts) │
                                                   └─────┬──────┘
                                                         │
                                                         │ Conceptos extraídos
                                                         ▼
┌───────────┐                                     ┌─────────────┐
│ Profesor  │◄────────────────────────────────────┤ PostgreSQL  │
│   (Web)   │  { documentId, topicId, concepts }  │ (temas,     │
└───────────┘                                     │ conceptos)  │
                                                  └─────────────┘
```

### Flujo de Juego Hangman (Estudiante)

```
┌───────────┐   POST /api/hangman/start     ┌──────────┐
│Estudiante ├──────────────────────────────►│  Spring  │
│  (Mobile) │   { topicId, userId }          │   Boot   │
└───────────┘                                └────┬─────┘
                                                  │
                                                  │ HangmanService
                                                  ▼
                                             ┌─────────────┐
                                             │ PostgreSQL  │
                                             │ (conceptos) │
                                             └─────┬───────┘
                                                   │
                                                   │ Conceptos del tema
                                                   ▼
┌───────────┐                                ┌──────────┐
│Estudiante │◄───────────────────────────────┤  Spring  │
│  (Mobile) │  { gameId, word: "____",       │   Boot   │
└─────┬─────┘    attempts: 6, hint: "..." } └──────────┘
      │
      │ Envía letra
      ▼
┌───────────┐   POST /api/hangman/guess      ┌──────────┐
│Estudiante ├──────────────────────────────►│  Spring  │
│  (Mobile) │   { gameId, letter: "A" }      │   Boot   │
└───────────┘                                └────┬─────┘
                                                  │
                                                  │ Game logic (validación)
                                                  ▼
                                             ┌─────────────┐
                                             │ PostgreSQL  │
                                             │ (juegos,    │
                                             │  metricas)  │
                                             └─────┬───────┘
                                                   │
                                                   │ Estado actualizado
                                                   ▼
┌───────────┐                                ┌──────────┐
│Estudiante │◄───────────────────────────────┤  Spring  │
│  (Mobile) │  { word: "A___", attempts: 5,  │   Boot   │
└───────────┘    correct: true, won: false } └──────────┘
```

---

## 🔒 Seguridad y Red

### Capas de Seguridad

```
┌──────────────────────────────────────────────────────┐
│         Capa 1: Transporte (HTTPS/TLS 1.3)           │
│  ┌────────────────────────────────────────────────┐  │
│  │  - Certificado SSL automático (Render)         │  │
│  │  - Encriptación end-to-end                     │  │
│  │  - HSTS habilitado                             │  │
│  └────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────┘
                          │
                          ▼
┌──────────────────────────────────────────────────────┐
│    Capa 2: Autenticación (JWT con HS256)             │
│  ┌────────────────────────────────────────────────┐  │
│  │  - Access Token: 1 hora (prod), 24h (dev)      │  │
│  │  - Refresh Token: 7 días                       │  │
│  │  - Secret: 256 bits mínimo                     │  │
│  │  - Signature: HMAC-SHA256                      │  │
│  └────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────┘
                          │
                          ▼
┌──────────────────────────────────────────────────────┐
│     Capa 3: Autorización (Spring Security)           │
│  ┌────────────────────────────────────────────────┐  │
│  │  - Role-Based Access Control (RBAC)            │  │
│  │  - Roles: ADMIN, PROFESOR, ALUMNO              │  │
│  │  - Endpoints protegidos por @PreAuthorize      │  │
│  │  - SecurityFilterChain personalizado           │  │
│  └────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────┘
                          │
                          ▼
┌──────────────────────────────────────────────────────┐
│      Capa 4: Validación de Datos (Bean Validation)   │
│  ┌────────────────────────────────────────────────┐  │
│  │  - @NotNull, @Email, @Size en DTOs             │  │
│  │  - Custom validators (RUT chileno)             │  │
│  │  - SQL Injection prevention (JPA)              │  │
│  │  - XSS protection (escapado automático)        │  │
│  └────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────┘
                          │
                          ▼
┌──────────────────────────────────────────────────────┐
│       Capa 5: Base de Datos (PostgreSQL SSL)         │
│  ┌────────────────────────────────────────────────┐  │
│  │  - SSL/TLS obligatorio (channel_binding)       │  │
│  │  - Passwords: BCrypt (cost factor 10)          │  │
│  │  - Prepared Statements (JPA)                   │  │
│  │  - Connection pool limitado                    │  │
│  └────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────┘
```

### Configuración CORS

**Desarrollo**:
```kotlin
allowedOriginPatterns = listOf("*")
allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
allowCredentials = true
```

**Producción** (recomendado):
```kotlin
allowedOrigins = listOf(
    "https://brainboost-backend.onrender.com",
    "https://app.brainboost.cl"  // Si se implementa dominio personalizado
)
allowCredentials = true
```

### Endpoints Públicos vs Protegidos

**Públicos** (sin autenticación):
- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/hangman/**` (juegos públicos para testing)
- `GET /actuator/health`
- `GET /`, `/index.html`, archivos estáticos

**Protegidos** (requieren JWT):
- `GET /api/users` (ADMIN)
- `POST /api/files/upload-query-pdf` (PROFESOR)
- `POST /api/content/**` (PROFESOR)
- `GET /api/scoring/ranking` (PROFESOR, ADMIN)
- `POST /api/hangman/start` (ALUMNO)

---

## 📈 Escalabilidad y Alta Disponibilidad

### Limitaciones Actuales (Free Tier)

**Render.com Free Tier**:
- ❌ **No auto-scaling**: 1 instancia fija
- ❌ **Sleep mode**: Se suspende tras 15 minutos de inactividad
- ❌ **Cold start**: ~30-60 segundos para reactivar
- ✅ **Uptime**: 750 horas/mes gratis (suficiente para demo)

**Neon Free Tier**:
- ❌ **Auto-suspend**: BD se suspende tras 5 minutos de inactividad
- ✅ **Auto-resume**: Despierta automáticamente en primer query (~1s latencia)
- ✅ **Storage**: 512MB (actualmente usando ~100MB)
- ✅ **Compute**: Unlimited queries (con límites razonables)

### Estrategia de Escalamiento Futuro

#### Escalamiento Vertical (Render Paid Tiers)
```
Free Tier → Starter ($7/mes) → Standard ($25/mes)
  512MB       512MB              2GB RAM
  1 CPU       1 CPU              2 CPUs
  Sleep       Always-On          Always-On
  N/A         N/A                Auto-deploy
```

#### Escalamiento Horizontal (Preparación)
**Arquitectura sugerida para producción**:
```
                      Load Balancer
                           │
         ┌─────────────────┼─────────────────┐
         │                 │                 │
    Instance 1         Instance 2       Instance 3
    (Primary)         (Replica)        (Replica)
         │                 │                 │
         └─────────────────┼─────────────────┘
                           │
                    Neon PostgreSQL
                   (Read Replicas)
```

**Consideraciones**:
- Session management con Redis (reemplazar in-memory tokens)
- Sticky sessions en load balancer
- Database read replicas en Neon (plan Pro)
- CDN para recursos estáticos (Cloudflare)

### Caching Strategy

**Nivel 1: Application Cache (Caffeine)**
- Respuestas de Gemini API (1 hora)
- Reduce latencia de 2s a ~10ms
- Evita rate limits de Google

**Nivel 2: Database Query Cache (Hibernate)**
- Second-level cache habilitado
- Caché de entidades frecuentes (roles, instituciones)

**Nivel 3: HTTP Cache (futuro)**
- ETag headers para recursos estáticos
- Cache-Control para APIs idempotentes

---

## 📊 Monitoreo y Observabilidad

### Spring Boot Actuator

**Endpoints Habilitados**:
```
GET /actuator/health          # Health check (usado por Render)
GET /actuator/info            # Información de build
GET /actuator/metrics         # Métricas de JVM y app
GET /actuator/metrics/{name}  # Métrica específica
```

**Health Check Indicators**:
- Database connectivity (PostgreSQL)
- Disk space
- Application status

**Configuración**:
```properties
# Desarrollo
management.endpoint.health.show-details=always

# Producción
management.endpoint.health.show-details=when-authorized
```

### Logging

**Niveles por Ambiente**:

**Desarrollo**:
```properties
logging.level.org.duocuc.capstonebackend=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

**Producción**:
```properties
logging.level.root=INFO
logging.level.org.springframework=WARN
logging.level.org.hibernate=WARN
```

**Formato**:
- Console output (stdout)
- Render captura logs automáticamente
- Acceso via Render Dashboard → Logs tab

### Métricas Disponibles

**JVM**:
- `jvm.memory.used`, `jvm.memory.max`
- `jvm.threads.live`, `jvm.threads.daemon`
- `jvm.gc.pause` (garbage collection)

**HTTP**:
- `http.server.requests` (count, duration, status)
- `http.server.requests.active`

**Database**:
- `hikaricp.connections.active`
- `hikaricp.connections.idle`
- `hikaricp.connections.pending`

**Application**:
- Custom metrics con Micrometer (preparado)

### Alertas y Notificaciones (Futuro)

**Herramientas Sugeridas**:
- **UptimeRobot**: Monitoring de uptime (free tier: 50 monitors)
- **Sentry**: Error tracking y performance monitoring
- **New Relic**: APM completo (plan gratuito disponible)
- **Datadog**: Logs + metrics + traces (free tier limitado)

**Configuración de Alertas**:
- Downtime > 5 minutos → Email + Slack
- Error rate > 10% → Email
- Response time > 3s → Warning
- Database connections > 80% → Critical

---

## 🚀 Deployment Pipeline

### CI/CD Workflow

```
┌──────────────────────────────────────────────────────────────┐
│                  Developer Workflow                          │
└──────────────────────────┬───────────────────────────────────┘
                           │
                           │ git push origin main
                           ▼
┌──────────────────────────────────────────────────────────────┐
│                   GitHub Repository                          │
│  Branch: main                                                │
│  Path: capstone-backend-code/CapstoneBackend                 │
└──────────────────────────┬───────────────────────────────────┘
                           │
                           │ Webhook trigger
                           ▼
┌──────────────────────────────────────────────────────────────┐
│              Render.com Build Service                        │
│  ┌────────────────────────────────────────────────────────┐  │
│  │  Step 1: Clone repository                              │  │
│  │  Step 2: Read Dockerfile + render.yaml                 │  │
│  │  Step 3: Docker build (multi-stage)                    │  │
│  │         - Stage 1: gradle:8.5-jdk21                    │  │
│  │           ./gradlew build --no-daemon -x test          │  │
│  │         - Stage 2: eclipse-temurin:21-jre-jammy        │  │
│  │           COPY app.jar                                 │  │
│  │  Step 4: Push image to Render registry                 │  │
│  │  Duration: ~5-8 minutos                                │  │
│  └────────────────────────────────────────────────────────┘  │
└──────────────────────────┬───────────────────────────────────┘
                           │
                           │ Deploy new version
                           ▼
┌──────────────────────────────────────────────────────────────┐
│              Render.com Runtime (Oregon)                     │
│  ┌────────────────────────────────────────────────────────┐  │
│  │  Step 1: Stop old container (graceful shutdown)        │  │
│  │  Step 2: Start new container with env vars             │  │
│  │  Step 3: Wait for health check (/actuator/health)      │  │
│  │  Step 4: Route traffic to new container               │  │
│  │  Downtime: ~30-60 segundos (no zero-downtime en free)  │  │
│  └────────────────────────────────────────────────────────┘  │
└──────────────────────────┬───────────────────────────────────┘
                           │
                           │ Health check SUCCESS
                           ▼
┌──────────────────────────────────────────────────────────────┐
│                  Application Running                         │
│  URL: https://brainboost-backend.onrender.com                │
│  Status: Healthy                                             │
└──────────────────────────────────────────────────────────────┘
```

### Rollback Strategy

**Manual Rollback** (Render Dashboard):
1. Deploy → History
2. Seleccionar versión anterior
3. Click "Rollback"
4. Esperar health check

**Git-based Rollback**:
```bash
git revert HEAD
git push origin main
# Render auto-deploys la versión revertida
```

---

## 🔗 URLs y Accesos

### Producción (Render)
- **Backend API**: `https://brainboost-backend.onrender.com`
- **Health Check**: `https://brainboost-backend.onrender.com/actuator/health`
- **Dashboard Render**: https://dashboard.render.com

### Base de Datos (Neon)
- **Dashboard**: https://console.neon.tech
- **Connection String**: Ver variables de entorno en Render

### Repositorio
- **GitHub**: https://github.com/ignacio-leon-m/capstone_grupo_3

### Servicios Externos
- **Google Gemini**: https://ai.google.dev/gemini-api/docs
- **API Console**: https://console.cloud.google.com

---

## 📝 Diagrama de Red

```
                    INTERNET
                       │
                       │ HTTPS (443)
                       │
                       ▼
              ┌────────────────┐
              │  Render Edge   │
              │  CDN Network   │
              │  (Global)      │
              └────────┬───────┘
                       │
                       │ Internal Routing
                       │
                       ▼
           ┌───────────────────────┐
           │   Render Region       │
           │   (Oregon, US West)   │
           │                       │
           │  ┌─────────────────┐  │
           │  │ Docker Container│  │
           │  │ Spring Boot App │  │
           │  │ Port: 8080      │  │
           │  └────┬───────┬────┘  │
           │       │       │       │
           └───────┼───────┼───────┘
                   │       │
         ┌─────────┘       └──────────┐
         │                            │
         │ JDBC over SSL/TLS          │ HTTPS (443)
         ▼                            ▼
┌──────────────────┐        ┌──────────────────┐
│  Neon PostgreSQL │        │  Google Gemini   │
│  (US West 2)     │        │  API (Global)    │
│  Port: 5432      │        │                  │
└──────────────────┘        └──────────────────┘
```

---

## 🎨 Crear Diagramas con FossFLOW

### ¿Qué es FossFLOW?

**FossFLOW** es una herramienta open-source para crear diagramas isométricos de infraestructura de forma visual y profesional.

- **Repositorio**: https://github.com/stan-smith/FossFLOW
- **Demo Online**: https://stan-smith.github.io/FossFLOW/
- **Licencia**: MIT
- **Características**:
  - ✅ Diagramas isométricos 3D hermosos
  - ✅ PWA (funciona offline)
  - ✅ Auto-save cada 5 segundos
  - ✅ Importar/exportar JSON
  - ✅ Más de 100 iconos de infraestructura
  - ✅ Soporte para iconos personalizados
  - ✅ Multilenguaje (8 idiomas)

### Instalación y Uso

#### Opción 1: Uso Online (Más Rápido)

```bash
# Simplemente abre en tu navegador:
https://stan-smith.github.io/FossFLOW/
```

#### Opción 2: Docker (Recomendado para uso persistente)

```powershell
# Ejecutar con almacenamiento persistente
docker run -p 80:80 -v ${PWD}/fossflow-diagrams:/data/diagrams stnsmith/fossflow:latest

# Acceder en:
http://localhost
```

#### Opción 3: Desarrollo Local

```powershell
# Clonar repositorio
git clone https://github.com/stan-smith/FossFLOW
cd FossFLOW

# Instalar dependencias
npm install

# Compilar librería (primera vez)
npm run build:lib

# Ejecutar servidor de desarrollo
npm run dev

# Abrir en navegador:
http://localhost:3000
```

### Guía para Crear Diagrama de Brain Boost

#### 1. Componentes Disponibles en FossFLOW

FossFLOW incluye iconos isométricos para:
- **Servidores y Cloud**: AWS, Azure, GCP, servidores genéricos
- **Bases de Datos**: PostgreSQL, MongoDB, MySQL, Redis
- **Contenedores**: Docker, Kubernetes
- **Redes**: Load balancers, routers, firewalls
- **Servicios**: APIs, microservicios, aplicaciones web
- **Usuarios**: Desktop, mobile, navegadores

#### 2. Estructura Sugerida para Brain Boost

```
# Diagrama de Despliegue - Brain Boost Backend

Capa 1 (Superior):
┌─────────────────────────────────────────┐
│  USUARIOS / CLIENTES                     │
│  - Icono: Mobile (estudiante Android)   │
│  - Icono: Browser (profesor/admin web)  │
└─────────────────────────────────────────┘
         │ HTTPS
         ▼

Capa 2 (Media):
┌─────────────────────────────────────────┐
│  RENDER.COM                              │
│  - Icono: Cloud Service                  │
│  - Icono: Docker Container               │
│  - Label: "Spring Boot Backend"          │
│  - Label: "JRE 21 / Kotlin"             │
└─────────────────────────────────────────┘
         │ JDBC/SSL
         ▼

Capa 3 (Inferior):
┌──────────────────┐       ┌──────────────┐
│  NEON POSTGRESQL │       │ GOOGLE GEMINI│
│  - Icono: DB     │       │ - Icono: AI  │
│  - US West 2     │       │ - Global API │
└──────────────────┘       └──────────────┘
```

#### 3. Pasos para Crear el Diagrama

**Paso 1: Agregar Componentes**
1. Click en botón "+" (arriba derecha)
2. Buscar y arrastrar iconos:
   - `mobile` → Estudiante (Android)
   - `browser` → Profesor/Admin
   - `cloud-service` → Render.com
   - `docker` → Container
   - `postgresql` → Neon Database
   - `api` → Gemini API
   - `github` → Repositorio

**Paso 2: Conectar Componentes**
1. Seleccionar herramienta "Connector" (tecla 'C')
2. Modo Click (predeterminado):
   - Click en primer componente (ej: Mobile)
   - Click en segundo componente (ej: Cloud)
3. Añadir etiquetas: "HTTPS", "JDBC", "REST API"

**Paso 3: Personalizar**
1. Doble-click en componentes para editar texto
2. Agregar colores personalizados (Settings → Theme)
3. Ajustar tamaño de grid para mejor distribución

**Paso 4: Exportar**
1. Click en "Save" → "Export"
2. Guardar como: `brain-boost-deployment-diagram.json`
3. Opción: Exportar como imagen PNG (Settings → Export)

#### 4. Plantilla JSON para Brain Boost

Puedes importar esta plantilla base en FossFLOW:

```json
{
  "nodes": [
    {
      "id": "user-mobile",
      "type": "mobile",
      "label": "Estudiante (Android)",
      "position": {"x": 5, "y": 2}
    },
    {
      "id": "user-web",
      "type": "browser",
      "label": "Profesor/Admin (Web)",
      "position": {"x": 8, "y": 2}
    },
    {
      "id": "render-cloud",
      "type": "cloud-service",
      "label": "Render.com (Oregon)",
      "position": {"x": 6, "y": 5}
    },
    {
      "id": "spring-container",
      "type": "docker",
      "label": "Spring Boot Backend",
      "position": {"x": 6, "y": 6}
    },
    {
      "id": "neon-db",
      "type": "postgresql",
      "label": "Neon PostgreSQL (US West 2)",
      "position": {"x": 4, "y": 9}
    },
    {
      "id": "gemini-api",
      "type": "api",
      "label": "Google Gemini API",
      "position": {"x": 8, "y": 9}
    },
    {
      "id": "github-repo",
      "type": "github",
      "label": "GitHub Repository",
      "position": {"x": 10, "y": 5}
    }
  ],
  "connectors": [
    {"from": "user-mobile", "to": "render-cloud", "label": "HTTPS"},
    {"from": "user-web", "to": "render-cloud", "label": "HTTPS"},
    {"from": "render-cloud", "to": "spring-container"},
    {"from": "spring-container", "to": "neon-db", "label": "JDBC/SSL"},
    {"from": "spring-container", "to": "gemini-api", "label": "REST API"},
    {"from": "github-repo", "to": "render-cloud", "label": "Auto-deploy"}
  ]
}
```

#### 5. Iconos Personalizados (Opcional)

Para agregar logos específicos (Kotlin, Render, Neon):

1. Descargar iconos SVG desde:
   - **Iconify**: https://icon-sets.iconify.design/
   - **Flaticon**: https://www.flaticon.com/free-icons/isometric
   
2. En FossFLOW: Settings → Custom Icons → Upload
   
3. Arrastrar iconos personalizados al canvas

#### 6. Mejores Prácticas

- **Capas claras**: Separa usuarios, aplicación, base de datos
- **Etiquetas descriptivas**: Incluye tecnologías y regiones
- **Colores consistentes**: Usa paleta coherente (azul=usuarios, verde=servicios, naranja=datos)
- **Flujo de datos**: Usa flechas para mostrar dirección
- **Escala apropiada**: No sobrecargues el diagrama

#### 7. Exportar para Documentación

```powershell
# Después de crear el diagrama en FossFLOW:

# 1. Exportar como JSON (para edición futura)
# File → Export → brain-boost-deployment.json

# 2. Exportar como PNG (para documentos)
# Settings → Export → brain-boost-deployment.png

# 3. Guardar en repositorio
Move-Item brain-boost-deployment.json docs/diagrams/
Move-Item brain-boost-deployment.png docs/diagrams/

# 4. Referenciar en README.md
# ![Deployment Diagram](docs/diagrams/brain-boost-deployment.png)
```

### Recursos Adicionales FossFLOW

- **Documentación Completa**: https://github.com/stan-smith/FossFLOW/blob/master/FOSSFLOW_ENCYCLOPEDIA.md
- **Guía de Contribución**: https://github.com/stan-smith/FossFLOW/blob/master/CONTRIBUTING.md
- **Roadmap**: https://github.com/stan-smith/FossFLOW/blob/master/FOSSFLOW_TODO.md
- **Generación con LLM**: https://github.com/stan-smith/FossFLOW/blob/master/LLM-GENERATION-GUIDE.md

---

**Última Revisión**: Noviembre 2025  
**Mantenido por**: Equipo Capstone Grupo 3
