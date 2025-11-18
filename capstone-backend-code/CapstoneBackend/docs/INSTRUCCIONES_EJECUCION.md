# 🚀 Instrucciones de Ejecución y Pruebas - Brain Boost Backend

**Proyecto**: Brain Boost - Plataforma de aprendizaje adaptativo  
**Última Actualización**: Noviembre 2025

---

## 📋 Tabla de Contenidos

1. [Requisitos Previos](#requisitos-previos)
2. [Configuración Inicial](#configuración-inicial)
3. [Ejecución en Desarrollo](#ejecución-en-desarrollo)
4. [Ejecución en Producción](#ejecución-en-producción)
5. [Testing Manual](#testing-manual)
6. [Testing Automatizado](#testing-automatizado)
7. [Resolución de Problemas](#resolución-de-problemas)
8. [Comandos Útiles](#comandos-útiles)

---

## ✅ Requisitos Previos

### Software Requerido

#### Java Development Kit (JDK) 21
```powershell
# Verificar instalación
java -version
# Debe mostrar: openjdk version "21.0.8" o superior

# Descargar desde:
# https://adoptium.net/temurin/releases/?version=21
```

#### Gradle (incluido como Wrapper)
```powershell
# NO es necesario instalar Gradle globalmente
# El proyecto incluye Gradle Wrapper (gradlew)

# Verificar wrapper
./gradlew --version
# Debe mostrar: Gradle 8.14.3
```

#### PostgreSQL (opcional - ya está en Neon)
```powershell
# Solo si deseas ejecutar PostgreSQL localmente
# De lo contrario, usa Neon (recomendado)

# Verificar si PostgreSQL está instalado
psql --version

# Descargar desde:
# https://www.postgresql.org/download/
```

#### Git
```powershell
# Verificar instalación
git --version

# Descargar desde:
# https://git-scm.com/download/win
```

### Cuentas y Credenciales

#### Neon PostgreSQL (Producción)
- **Dashboard**: https://console.neon.tech
- **Credenciales**: Proporcionadas en variables de entorno

#### Google Gemini API (IA)
- **Dashboard**: https://console.cloud.google.com
- **API Key**: `AIzaSyClZcoMsMzXYmSxazv6P4SNSdlOFW2FNo8` (desarrollo)
- **Documentación**: https://ai.google.dev/gemini-api/docs

#### Render.com (Deployment)
- **Dashboard**: https://dashboard.render.com
- **Repositorio conectado**: ignacio-leon-m/capstone_grupo_3

---

## 🔧 Configuración Inicial

### 1. Clonar el Repositorio

```powershell
# Clonar desde GitHub
git clone https://github.com/ignacio-leon-m/capstone_grupo_3.git

# Navegar al directorio del backend
cd capstone_grupo_3\capstone-backend-code\CapstoneBackend

# Verificar branch actual
git branch
# Debe mostrar: * feature/app-web-close (o main)
```

### 2. Configurar Variables de Entorno (Opcional en Desarrollo)

El proyecto ya incluye valores por defecto en `application-dev.properties`, pero puedes sobrescribirlos:

**Opción A: Variables de Entorno (PowerShell)**
```powershell
# Configurar perfil de desarrollo
$env:SPRING_PROFILES_ACTIVE="dev"

# Base de datos (opcional - ya está en application-dev.properties)
$env:DATABASE_URL="jdbc:postgresql://ep-fancy-tree-af5xp9ie-pooler.c-2.us-west-2.aws.neon.tech/neondb?sslmode=require&channel_binding=require"
$env:DATABASE_USERNAME="neondb_owner"
$env:DATABASE_PASSWORD="npg_CinWX0he6lUp"

# JWT (opcional - ya está configurado)
$env:JWT_SECRET="f4e2a0b8d5c3e1a9f0b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6f5"

# Gemini API (opcional - ya está configurado)
$env:GEMINI_API_KEY="AIzaSyClZcoMsMzXYmSxazv6P4SNSdlOFW2FNo8"
```

**Opción B: Archivo `.env` (NO recomendado - usar solo localmente)**
```powershell
# Crear archivo .env en la raíz del proyecto
# ⚠️ NUNCA commitearlo a Git (ya está en .gitignore)

# Contenido de .env:
SPRING_PROFILES_ACTIVE=dev
DATABASE_URL=jdbc:postgresql://ep-fancy-tree-af5xp9ie-pooler.c-2.us-west-2.aws.neon.tech/neondb?sslmode=require
DATABASE_USERNAME=neondb_owner
DATABASE_PASSWORD=npg_CinWX0he6lUp
JWT_SECRET=f4e2a0b8d5c3e1a9f0b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6f5
GEMINI_API_KEY=AIzaSyClZcoMsMzXYmSxazv6P4SNSdlOFW2FNo8
```

### 3. Verificar Estructura del Proyecto

```powershell
# Listar archivos principales
ls

# Debe mostrar:
# build.gradle.kts          - Configuración de Gradle
# settings.gradle.kts       - Configuración de proyecto
# Dockerfile                - Configuración Docker
# render.yaml               - Configuración Render
# gradlew.bat               - Gradle Wrapper (Windows)
# src/                      - Código fuente
# build/                    - Archivos compilados
# .ai-docs/                 - Documentación del proyecto
```

---

## 🏃 Ejecución en Desarrollo

### Método 1: Gradle bootRun (Recomendado)

**Ventajas**: Recarga rápida, debugging integrado, sin generar JAR

```powershell
# Asegurar perfil de desarrollo
$env:SPRING_PROFILES_ACTIVE="dev"

# Ejecutar aplicación
./gradlew bootRun

# Salida esperada:
#   > Task :bootRun
#   
#   The following 1 profile is active: "dev"
#   ...
#   Started CapstoneBackendApplicationKt in 8.5 seconds
```

**Acceso**:
- URL Base: `http://localhost:8080`
- Health Check: `http://localhost:8080/actuator/health`
- Login: `http://localhost:8080/index.html`

**Detener**: `Ctrl + C`

### Método 2: Build JAR y Ejecutar

**Ventajas**: Más cercano a producción, testing de empaquetado

```powershell
# Limpiar build anterior
./gradlew clean

# Compilar y generar JAR (sin tests)
./gradlew build -x test

# Ubicación del JAR:
# build/libs/CapstoneBackend-0.0.1-SNAPSHOT.jar (127MB)

# Ejecutar JAR
$env:SPRING_PROFILES_ACTIVE="dev"
java -jar build\libs\CapstoneBackend-0.0.1-SNAPSHOT.jar

# Alternativa con memoria limitada (simular Render free tier)
java -Xmx400m -Xms256m -jar build\libs\CapstoneBackend-0.0.1-SNAPSHOT.jar
```

**Detener**: `Ctrl + C`

### Método 3: Docker Local

**Ventajas**: Entorno idéntico a producción, aislamiento completo

```powershell
# Construir imagen Docker
docker build -t brainboost-backend:dev .

# Ejecutar contenedor
docker run -p 8080:8080 `
  -e SPRING_PROFILES_ACTIVE=dev `
  -e DATABASE_URL="jdbc:postgresql://ep-fancy-tree-af5xp9ie-pooler.c-2.us-west-2.aws.neon.tech/neondb?sslmode=require" `
  -e DATABASE_USERNAME="neondb_owner" `
  -e DATABASE_PASSWORD="npg_CinWX0he6lUp" `
  brainboost-backend:dev

# Detener contenedor
docker ps  # Ver CONTAINER_ID
docker stop <CONTAINER_ID>
```

### Método 4: Docker Compose (PostgreSQL Local)

**Ventajas**: PostgreSQL local para desarrollo offline

```powershell
# Ejecutar PostgreSQL + Backend
docker-compose up

# Detener
docker-compose down

# Limpiar volúmenes (reset BD)
docker-compose down -v
```

---

## 🚀 Ejecución en Producción

### Deployment Automático (Render)

El proyecto está configurado para **auto-deploy** en Render cuando se hace push a `main`:

```powershell
# 1. Asegurar cambios committeados
git add .
git commit -m "feat: nueva funcionalidad"

# 2. Push a main (triggers auto-deploy)
git push origin main

# 3. Monitorear deployment en Render Dashboard
# URL: https://dashboard.render.com
# Duración: ~5-8 minutos (build + deploy)

# 4. Verificar deployment exitoso
curl https://brainboost-backend.onrender.com/actuator/health
# Debe devolver: {"status":"UP"}
```

### Deployment Manual (Render)

```powershell
# 1. Login a Render Dashboard
# https://dashboard.render.com

# 2. Seleccionar servicio: brainboost-backend

# 3. Click "Manual Deploy" → "Deploy latest commit"

# 4. Esperar build y deployment

# 5. Verificar logs en tiempo real:
# Dashboard → Logs tab
```

### Configuración de Variables de Entorno en Render

```powershell
# 1. Render Dashboard → brainboost-backend → Environment

# 2. Agregar/Editar variables:
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://ep-fancy-tree-af5xp9ie-pooler.c-2.us-west-2.aws.neon.tech/neondb?sslmode=require
DATABASE_USERNAME=neondb_owner
DATABASE_PASSWORD=<secret>  # Configurar en Render
JWT_SECRET=<secret>  # Generar nuevo secreto para producción
JWT_ACCESS_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000
GEMINI_API_KEY=<secret>
JAVA_TOOL_OPTIONS=-Xmx400m -Xms256m

# 3. Click "Save Changes"

# 4. Render re-deploya automáticamente
```

---

## 🧪 Testing Manual

### 1. Health Check

```powershell
# Verificar que la aplicación está corriendo
curl http://localhost:8080/actuator/health

# Respuesta esperada:
# {
#   "status": "UP",
#   "components": {
#     "db": { "status": "UP" },
#     "diskSpace": { "status": "UP" }
#   }
# }
```

### 2. Autenticación - Login

**Request**:
```powershell
# Crear usuario admin (primera vez)
curl -X POST http://localhost:8080/api/auth/register `
  -H "Content-Type: application/json" `
  -d '{
    "nombre": "Admin",
    "apellido": "Sistema",
    "correo": "admin@duoc.cl",
    "password": "Admin123!",
    "celular": "+56912345678",
    "rolNombre": "admin",
    "carreraId": "UUID-de-carrera"
  }'

# Login con credenciales
curl -X POST http://localhost:8080/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{
    "correo": "admin@duoc.cl",
    "password": "Admin123!"
  }'
```

**Respuesta Esperada**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "uuid",
    "nombre": "Admin",
    "apellido": "Sistema",
    "correo": "admin@duoc.cl",
    "rol": "admin"
  }
}
```

**Guardar token**:
```powershell
$TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 3. Listar Usuarios (Requiere Admin)

```powershell
# Listar usuarios con paginación
curl http://localhost:8080/api/users?page=0&size=10 `
  -H "Authorization: Bearer $TOKEN"

# Respuesta esperada:
# {
#   "content": [
#     { "id": "uuid", "nombre": "Admin", "correo": "admin@duoc.cl", ... }
#   ],
#   "totalElements": 1,
#   "totalPages": 1
# }
```

### 4. Upload PDF y Extracción de Conceptos (Requiere Profesor)

```powershell
# Subir PDF y extraer conceptos con IA
curl -X POST http://localhost:8080/api/files/upload-query-pdf `
  -H "Authorization: Bearer $TOKEN" `
  -F "file=@documento.pdf" `
  -F "query=Extrae los conceptos principales de este documento"

# Respuesta esperada:
# {
#   "fileName": "documento.pdf",
#   "aiResponse": "Conceptos principales:\n1. Variables\n2. Estructuras de control\n3. Funciones...",
#   "topicId": "uuid",
#   "conceptsExtracted": 15
# }
```

### 5. Iniciar Juego Hangman

```powershell
# Iniciar juego con tema extraído
curl -X POST http://localhost:8080/api/hangman/start `
  -H "Authorization: Bearer $TOKEN" `
  -H "Content-Type: application/json" `
  -d '{
    "topicId": "uuid-del-tema",
    "userId": "uuid-del-estudiante"
  }'

# Respuesta esperada:
# {
#   "gameId": "uuid",
#   "word": "_______",
#   "attempts": 6,
#   "hint": "Es una estructura de datos",
#   "guessedLetters": []
# }
```

### 6. Adivinar Letra (Hangman)

```powershell
# Enviar letra
curl -X POST http://localhost:8080/api/hangman/guess `
  -H "Authorization: Bearer $TOKEN" `
  -H "Content-Type: application/json" `
  -d '{
    "gameId": "uuid-del-juego",
    "letter": "A"
  }'

# Respuesta esperada:
# {
#   "word": "A___A__",
#   "attempts": 5,
#   "correct": true,
#   "won": false,
#   "gameOver": false
# }
```

---

## 🤖 Testing Automatizado

### Script PowerShell de Testing Automático

**Archivo**: `test-environments.ps1`

```powershell
# Ejecutar script de testing completo
./test-environments.ps1

# El script ejecuta:
# 1. Clean build
# 2. Verifica generación de JAR
# 3. Inicia servidor en background
# 4. Espera 45 segundos
# 5. Verifica /actuator/health
# 6. Confirma perfil activo
# 7. Muestra logs en caso de error
```

**Salida Esperada**:
```
===================================
TESTING BRAIN BOOST BACKEND
MULTI-ENVIRONMENT CONFIGURATION
===================================

[1/6] Limpiando build anterior...
✓ Build limpiado

[2/6] Compilando aplicación...
✓ Build exitoso (14.5s)

[3/6] Verificando JAR generado...
✓ JAR generado: 127.04 MB

[4/6] Iniciando servidor...
✓ Servidor iniciado (Job ID: 1)

[5/6] Verificando health check...
✓ Health check: Status = UP

[6/6] Verificando perfil activo...
✓ Perfil activo: dev

===================================
✅ TESTS EXITOSOS
===================================
```

### Tests Unitarios con JUnit

```powershell
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests de una clase específica
./gradlew test --tests org.duocuc.capstonebackend.service.AuthServiceTest

# Generar reporte de cobertura con JaCoCo
./gradlew test jacocoTestReport

# Ver reporte en:
# build/reports/jacoco/test/html/index.html
```

### Tests de Integración

```powershell
# Ejecutar tests de integración (requieren PostgreSQL)
./gradlew integrationTest

# Ejecutar con TestContainers (PostgreSQL en Docker)
./gradlew test --tests *IntegrationTest
```

---

## 🔧 Resolución de Problemas

### Problema 1: Aplicación No Inicia - MongoDB Error

**Error**:
```
Error creating bean with name 'persistingAiService'
Cannot resolve reference to bean 'mongoTemplate'
```

**Causa**: MongoDB configurado pero no disponible

**Solución**:
```powershell
# Opción A: Ya está deshabilitado en application-dev.properties
# Verificar que application-dev.properties contenga:
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration

# Opción B: Configurar MongoDB Atlas (si necesitas usarlo)
# 1. Crear cuenta en https://www.mongodb.com/cloud/atlas/register
# 2. Crear cluster gratuito
# 3. Agregar connection string a application.properties:
spring.data.mongodb.uri=mongodb+srv://user:password@cluster.mongodb.net/dbname
```

### Problema 2: Flyway Checksum Mismatch

**Error**:
```
FlywayException: Checksum mismatch for migration V1__Initial_schema.sql
```

**Causa**: Archivo de migración modificado después de ejecutarse

**Solución en Desarrollo**:
```properties
# application-dev.properties (ya configurado)
spring.flyway.validate-on-migrate=false
spring.flyway.out-of-order=true
```

**Solución en Producción**:
```powershell
# Opción A: Reparar Flyway (solo si sabes lo que haces)
./gradlew flywayRepair

# Opción B: Crear nueva migración en lugar de editar existente
# Crear: src/main/resources/db/migration/V3__Fix_schema.sql
```

### Problema 3: Puerto 8080 Ya en Uso

**Error**:
```
Port 8080 is already in use
```

**Solución**:
```powershell
# Ver procesos usando puerto 8080
Get-NetTCPConnection -LocalPort 8080

# Matar proceso por PID
Stop-Process -Id <PID> -Force

# O cambiar puerto en application.properties:
server.port=8081
```

### Problema 4: OutOfMemoryError en Render

**Error en logs de Render**:
```
java.lang.OutOfMemoryError: Java heap space
```

**Causa**: Render free tier tiene 512MB RAM, aplicación usa más

**Solución**:
```yaml
# render.yaml (ya configurado)
envVars:
  - key: JAVA_TOOL_OPTIONS
    value: -Xmx400m -Xms256m  # Limitar heap a 400MB
```

### Problema 5: Connection Timeout a Neon PostgreSQL

**Error**:
```
HikariPool-1 - Connection is not available, request timed out after 30000ms
```

**Causa**: Neon suspendió la BD por inactividad

**Solución**:
```powershell
# Esperar auto-resume (~1 segundo)
# Primera query despierta la BD automáticamente

# O configurar timeout más largo:
spring.datasource.hikari.connection-timeout=60000  # 60 segundos
```

### Problema 6: JWT Token Inválido

**Error**:
```
JWT signature does not match locally computed signature
```

**Causa**: JWT_SECRET diferente entre ambientes

**Solución**:
```powershell
# Asegurar mismo secret en desarrollo y producción
$env:JWT_SECRET="f4e2a0b8d5c3e1a9f0b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6f5"

# Verificar valor en application.properties
jwt.secret=${JWT_SECRET:f4e2a0b8d5c3e1a9f0b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6f5}
```

---

## 📦 Comandos Útiles

### Gradle

```powershell
# Limpiar build
./gradlew clean

# Compilar sin tests
./gradlew build -x test

# Compilar con tests
./gradlew build

# Ejecutar aplicación
./gradlew bootRun

# Ver dependencias
./gradlew dependencies

# Actualizar Gradle Wrapper
./gradlew wrapper --gradle-version 8.14.3

# Verificar sintaxis Kotlin
./gradlew check
```

### Docker

```powershell
# Build imagen
docker build -t brainboost-backend:latest .

# Ejecutar contenedor
docker run -p 8080:8080 brainboost-backend:latest

# Listar contenedores
docker ps

# Ver logs de contenedor
docker logs <CONTAINER_ID>

# Detener contenedor
docker stop <CONTAINER_ID>

# Eliminar contenedor
docker rm <CONTAINER_ID>

# Eliminar imagen
docker rmi brainboost-backend:latest

# Limpiar todo (⚠️ elimina todos los contenedores e imágenes)
docker system prune -a
```

### Git

```powershell
# Ver estado
git status

# Ver branches
git branch

# Cambiar de branch
git checkout main

# Crear nueva branch
git checkout -b feature/nueva-funcionalidad

# Ver commits recientes
git log --oneline -10

# Ver diferencias
git diff

# Stash cambios temporalmente
git stash
git stash pop  # Recuperar cambios

# Pull últimos cambios
git pull origin main

# Push a GitHub
git push origin feature/app-web-close
```

### PostgreSQL (Neon)

```powershell
# Conectar a Neon desde psql (requiere psql instalado)
$env:PGPASSWORD="npg_CinWX0he6lUp"
psql -h ep-fancy-tree-af5xp9ie-pooler.c-2.us-west-2.aws.neon.tech -U neondb_owner -d neondb

# Una vez conectado:
\dt          # Listar tablas
\d usuarios  # Describir tabla usuarios
SELECT * FROM usuarios LIMIT 5;
\q           # Salir
```

### Debugging

```powershell
# Ver logs de aplicación en tiempo real (Gradle)
./gradlew bootRun

# Ver logs con más detalle
./gradlew bootRun --info

# Ejecutar con debugger habilitado (puerto 5005)
./gradlew bootRun --debug-jvm

# Conectar IntelliJ IDEA:
# Run → Edit Configurations → + → Remote JVM Debug
# Host: localhost, Port: 5005
```

### Monitoreo

```powershell
# Ver métricas de JVM
curl http://localhost:8080/actuator/metrics

# Ver métrica específica (memoria)
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# Ver info de aplicación
curl http://localhost:8080/actuator/info

# Ver health con detalles (solo en dev)
curl http://localhost:8080/actuator/health
```

---

## 📊 Checklist de Verificación

### Pre-Deployment

- [ ] Tests pasan: `./gradlew test`
- [ ] Build exitoso: `./gradlew build`
- [ ] Health check funciona: `curl http://localhost:8080/actuator/health`
- [ ] Login funciona en `/index.html`
- [ ] Variables de entorno configuradas en Render
- [ ] No hay secretos en código (usar variables de entorno)
- [ ] Flyway migrations validadas: `./gradlew flywayValidate`

### Post-Deployment

- [ ] Render build exitoso (ver logs en dashboard)
- [ ] Health check producción: `curl https://brainboost-backend.onrender.com/actuator/health`
- [ ] Login funciona en producción
- [ ] API endpoints responden correctamente
- [ ] No hay errores en logs de Render
- [ ] Tiempo de respuesta < 3 segundos
- [ ] Base de datos conectada (verificar en logs)

---

## 🎓 Recursos Adicionales

### Documentación del Proyecto

- **Configuración**: `CONFIGURACION.md`
- **Diagrama de Despliegue**: `DIAGRAMA_DESPLIEGUE.md`
- **Guía de Ambientes**: `ENVIRONMENTS_GUIDE.md`
- **Documentación IA**: `.ai-docs/GUIA_DESARROLLO_CAPSTONE.md`

### Enlaces Útiles

- **Spring Boot Docs**: https://docs.spring.io/spring-boot/index.html
- **Kotlin Reference**: https://kotlinlang.org/docs/home.html
- **Neon Docs**: https://neon.tech/docs/introduction
- **Render Docs**: https://render.com/docs
- **Google Gemini API**: https://ai.google.dev/gemini-api/docs

### Soporte

- **GitHub Issues**: https://github.com/ignacio-leon-m/capstone_grupo_3/issues
- **Email**: [correos del equipo]

---

## 📝 Notas Finales

### Buenas Prácticas

1. **Siempre ejecuta en perfil `dev` localmente**
   ```powershell
   $env:SPRING_PROFILES_ACTIVE="dev"
   ```

2. **No commitees secretos al repositorio**
   - Usa variables de entorno
   - Verifica `.gitignore` antes de commit

3. **Ejecuta tests antes de push**
   ```powershell
   ./gradlew test
   ```

4. **Monitorea logs en producción**
   - Render Dashboard → Logs tab
   - Busca errores o warnings

5. **Documenta cambios importantes**
   - Actualiza README.md
   - Agrega comentarios en código complejo

### Performance Tips

- **Cache Gemini API**: Respuestas se cachean 1 hora
- **Connection Pool**: Limitado a 10 conexiones (Neon free tier)
- **File Uploads**: Máximo 10MB por archivo
- **Paginación**: Usa `?page=0&size=10` en endpoints de listado

### Seguridad

- **JWT Tokens**: Nunca los compartas públicamente
- **Passwords**: Siempre hasheadas con BCrypt
- **HTTPS**: Obligatorio en producción (Render lo proporciona)
- **CORS**: Configurado permisivo en dev, restringir en prod

---

**Última Revisión**: Noviembre 2025  
**Mantenido por**: Equipo Capstone Grupo 3 (Valencia, León, Bertero)

---

**¡Buena suerte con el proyecto! 🚀**
