# 🚀 Guía de Configuración por Ambientes - BrainBoost Backend

## 📋 Estructura de Configuración

Este proyecto sigue las mejores prácticas de Spring Boot para manejar múltiples ambientes:

```
CapstoneBackend/
├── config/
│   ├── application-dev.properties          # Configuración de DESARROLLO
│   └── application-production.properties    # Configuración de PRODUCCIÓN
├── src/main/resources/
│   ├── application.properties              # Configuración BASE (común a todos)
│   └── db/migration/                       # Migraciones Flyway
├── .env.example                            # Plantilla de variables de entorno
├── .env                                    # Variables locales (NO commitear)
├── render.yaml                             # Configuración de Render (producción)
└── Dockerfile                              # Imagen Docker para producción
```

---

## 🔧 Ambientes

### 1️⃣ **DESARROLLO (Local)**

**Cuándo usar**: Desarrollo local en tu máquina

**Perfil**: `dev`

**Configuración**: `config/application-dev.properties`

**Características**:
- ✅ Logs detallados (SQL, Security, Debug)
- ✅ Tokens JWT más largos (1 día para acceso)
- ✅ Health checks con detalles completos
- ✅ CORS permisivo para localhost
- ✅ Hot reload con Spring DevTools

**Cómo activar**:

```bash
# Opción 1: Usando gradlew
./gradlew bootRun -Dspring.profiles.active=dev

# Opción 2: Variable de entorno
export SPRING_PROFILES_ACTIVE=dev
./gradlew bootRun

# Opción 3: En IntelliJ IDEA
Run Configuration → Environment Variables → SPRING_PROFILES_ACTIVE=dev
```

**Variables necesarias**: Ninguna, usa valores por defecto en `application-dev.properties`

---

### 2️⃣ **PRODUCCIÓN (Render)**

**Cuándo usar**: Deployment en Render.com

**Perfil**: `production`

**Configuración**: `config/application-production.properties`

**Características**:
- 🔒 Logs mínimos (solo WARN y ERROR)
- 🔒 Tokens JWT estándar (1 hora para acceso)
- 🔒 Health checks sin detalles sensibles
- 🔒 CORS configurado específicamente
- 🔒 Stacktraces ocultos

**Cómo activar**:

Render automáticamente usa el perfil `production` gracias a la variable de entorno en `render.yaml`:

```yaml
envVars:
  - key: SPRING_PROFILES_ACTIVE
    value: production
```

**Variables necesarias** (configuradas en Render Dashboard):

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DATABASE_URL` | Connection string de Neon | `jdbc:postgresql://ep-fancy-tree...` |
| `DATABASE_USERNAME` | Usuario de la BD | `neondb_owner` |
| `DATABASE_PASSWORD` | Password de la BD | `npg_CinWX0...` |
| `JWT_SECRET` | Secret para firmar tokens | Base64, 256-bit |
| `JWT_ACCESS_EXPIRATION` | Duración token acceso (ms) | `3600000` (1 hora) |
| `JWT_REFRESH_EXPIRATION` | Duración token refresh (ms) | `604800000` (7 días) |
| `GEMINI_API_KEY` | API key de Google Gemini | `AIzaSy...` |
| `PORT` | Puerto del servidor | `8080` |

---

## 🔐 Variables de Entorno

### Para Desarrollo Local

1. **Copia el archivo ejemplo**:
   ```bash
   cp .env.example .env
   ```

2. **Edita `.env`** con tus valores locales (el archivo está en `.gitignore`)

3. **Carga las variables** antes de ejecutar:
   ```bash
   # PowerShell
   Get-Content .env | ForEach-Object { 
       if ($_ -match '^([^=]+)=(.*)$') { 
           [Environment]::SetEnvironmentVariable($matches[1], $matches[2]) 
       } 
   }
   
   # Bash/Zsh
   export $(cat .env | xargs)
   ```

### Para Producción (Render)

Las variables se configuran en el dashboard de Render:

1. Ve a tu servicio en https://dashboard.render.com/
2. Settings → Environment
3. Añade cada variable de la tabla anterior
4. Click en "Save Changes"
5. Render re-desplegará automáticamente

---

## 🗃️ Base de Datos

### Desarrollo
- **Proveedor**: Neon PostgreSQL (pooler)
- **Región**: US West 2
- **Connection pooling**: Habilitado
- **SSL**: Requerido

### Producción
- **Misma BD** que desarrollo (Neon soporta múltiples conexiones)
- **Connection string**: Via variable `DATABASE_URL`
- **Migraciones**: Flyway las ejecuta automáticamente

### Migraciones (Flyway)

Ubicación: `src/main/resources/db/migration/`

```
V1__Initial_schema.sql          # Schema inicial
V2__Insert_initial_data.sql     # Datos de prueba
```

**Las migraciones se ejecutan automáticamente** en cada deploy.

---

## 🧪 Testing

### Ejecutar tests localmente:

```bash
# Todos los tests
./gradlew test

# Con perfil de desarrollo
./gradlew test -Dspring.profiles.active=dev

# Solo tests de integración
./gradlew integrationTest
```

---

## 📦 Build y Deployment

### Build local:

```bash
# Limpio
./gradlew clean build

# Sin tests (más rápido)
./gradlew clean build -x test

# El JAR se genera en: build/libs/CapstoneBackend-0.0.1-SNAPSHOT.jar
```

### Build en Render:

Render ejecuta automáticamente el Dockerfile que:
1. Usa `gradle:8.5-jdk21` para build
2. Copia dependencias y código fuente
3. Ejecuta `./gradlew build --no-daemon -x test`
4. Genera imagen final con `eclipse-temurin:21-jre-jammy`

---

## 🚀 Despliegue

### Desarrollo Local:

```bash
# Opción 1: Gradle (recomendado para desarrollo)
./gradlew bootRun -Dspring.profiles.active=dev

# Opción 2: JAR ejecutable
./gradlew build
java -jar build/libs/CapstoneBackend-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# Opción 3: Docker (pruebas locales)
docker build -t brainboost-backend .
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev brainboost-backend
```

### Producción (Render):

1. **Push a GitHub**:
   ```bash
   git add .
   git commit -m "feat: update configuration"
   git push origin feature/app-web-close
   ```

2. **Render auto-despliega** cuando detecta cambios en la rama configurada

3. **Verifica el deployment**:
   - Logs: https://dashboard.render.com/ → Tu servicio → Logs
   - Health: https://capstone-grupo-3.onrender.com/actuator/health

---

## 🔍 Debugging

### Ver configuración activa:

```bash
# Ejecutar con debug de configuración
./gradlew bootRun --debug | grep "property source"
```

### Ver perfil activo:

```bash
curl http://localhost:8080/actuator/info
```

### Problemas comunes:

| Problema | Solución |
|----------|----------|
| "Could not resolve placeholder 'DATABASE_URL'" | Variable de entorno no definida. Usa perfil `dev` o define la variable |
| "Access denied for user" | Credenciales incorrectas en `.env` o Render |
| "Unable to create initial connections" | Connection string mal formado o BD inaccesible |
| CORS errors | Verifica `WebConfig.kt` y el perfil activo |

---

## 📚 Archivos de Configuración

### `application.properties` (BASE)
- Configuración compartida entre todos los ambientes
- Define el perfil activo por defecto (`dev`)
- Configuración de Flyway y Actuator

### `application-dev.properties`
- Valores específicos para desarrollo
- Credenciales hardcodeadas (solo para dev)
- Logs verbose

### `application-production.properties`
- Valores específicos para producción
- Lee todas las credenciales de variables de entorno
- Logs mínimos, seguridad máxima

### `render.yaml`
- Infraestructura como código para Render
- Define perfil, región, health checks
- Lista de variables de entorno necesarias

### `.env.example`
- Plantilla de variables de entorno
- Se puede commitear (sin valores sensibles)
- Documentación de qué variables se necesitan

### `.env`
- Variables de entorno locales
- **NUNCA commitear** (está en `.gitignore`)
- Cada desarrollador tiene su propia copia

---

## ✅ Checklist de Configuración

### Para nuevo desarrollador:

- [ ] Clonar el repositorio
- [ ] Copiar `.env.example` a `.env`
- [ ] Ajustar valores en `.env` (DB credentials, API keys)
- [ ] Ejecutar `./gradlew bootRun -Dspring.profiles.active=dev`
- [ ] Verificar `http://localhost:8080/actuator/health`

### Para deployment en Render:

- [ ] Verificar `render.yaml` tiene todas las variables
- [ ] Configurar variables en Render Dashboard
- [ ] Push a la rama configurada
- [ ] Verificar logs en Render
- [ ] Probar health check: `https://tu-servicio.onrender.com/actuator/health`

---

## 🆘 Soporte

Si tienes problemas:

1. Verifica el perfil activo: `echo $SPRING_PROFILES_ACTIVE`
2. Revisa los logs: `./gradlew bootRun --info`
3. Verifica las variables: `env | grep DATABASE`
4. Consulta la documentación de Spring Boot: https://docs.spring.io/spring-boot/reference/

---

**Última actualización**: 21 de Noviembre, 2025
