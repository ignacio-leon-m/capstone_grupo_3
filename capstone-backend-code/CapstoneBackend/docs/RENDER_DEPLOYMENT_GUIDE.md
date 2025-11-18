# ===================================
# 🚀 Guía de Despliegue en Render
# ===================================

## 📋 Pre-requisitos

- ✅ Cuenta en Render.com (gratuita)
- ✅ Base de datos Neon configurada y corriendo
- ✅ Repositorio Git con tu código

## 🎯 Paso 1: Preparar tu Repositorio

1. Asegúrate de que los siguientes archivos estén en tu repo:
   - ✅ `Dockerfile` (ya existe)
   - ✅ `render.yaml` (creado)
   - ✅ `.dockerignore` (creado)
   - ✅ `application.properties` actualizado con variables de entorno

2. Commit y push de los cambios:
```bash
git add .
git commit -m "chore: configure Render deployment with Neon database"
git push origin main
```

## 🚀 Paso 2: Crear Servicio Web en Render

### Opción A: Usando Render Dashboard (Recomendado)

1. **Ve a Render.com y haz login**
   - URL: https://dashboard.render.com/

2. **Crear nuevo Web Service**
   - Click en "New +" → "Web Service"
   - Conecta tu repositorio de GitHub: `ignacio-leon-m/capstone_grupo_3`
   - Selecciona la rama: `main`

3. **Configuración del Servicio**
   - **Name:** `brainboost-backend`
   - **Region:** Oregon (US West) - Más cercano a Neon US West
   - **Branch:** `main`
   - **Root Directory:** `capstone-backend-code/CapstoneBackend`
   - **Runtime:** Docker
   - **Instance Type:** Free

4. **Variables de Entorno** (IMPORTANTE)

   Añade estas variables en la sección "Environment Variables":

   ```
   PORT=8080
   
   DATABASE_URL=jdbc:postgresql://ep-fancy-tree-af5xp9ie-pooler.c-2.us-west-2.aws.neon.tech/neondb?sslmode=require&channel_binding=require
   
   DATABASE_USERNAME=neondb_owner
   
   DATABASE_PASSWORD=npg_CinWX0he6lUp
   
   JWT_SECRET=f4e2a0b8d5c3e1a9f0b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6f5
   
   JWT_ACCESS_EXPIRATION=3600000
   
   JWT_REFRESH_EXPIRATION=604800000
   
   GEMINI_API_KEY=AIzaSyClZcoMsMzXYmSxazv6P4SNSdlOFW2FNo8
   
   JAVA_TOOL_OPTIONS=-Xmx512m -Xms256m
   
   SPRING_PROFILES_ACTIVE=production
   ```

5. **Deploy**
   - Click en "Create Web Service"
   - Render comenzará a construir y desplegar tu aplicación
   - Espera 5-10 minutos para el primer despliegue

### Opción B: Usando render.yaml (Blueprint)

1. Ve a Render Dashboard → "Blueprints"
2. Click en "New Blueprint Instance"
3. Conecta tu repositorio
4. Render detectará automáticamente el `render.yaml`
5. Añade las variables de entorno manualmente (las marcadas con `sync: false`)
6. Click en "Apply"

## 📊 Paso 3: Verificar el Despliegue

### Verificar Logs

1. En el Dashboard de Render, selecciona tu servicio
2. Ve a la pestaña "Logs"
3. Busca el mensaje:
   ```
   Started CapstoneBackendApplication in X seconds
   ```

### Probar la API

Tu backend estará disponible en:
```
https://brainboost-backend.onrender.com
```

**Endpoints de prueba:**

1. **Health Check**
   ```bash
   curl https://brainboost-backend.onrender.com/actuator/health
   ```
   Respuesta esperada: `{"status":"UP"}`

2. **Login Test**
   ```bash
   curl -X POST https://brainboost-backend.onrender.com/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"admin@example.com","password":"password123"}'
   ```

## 🔄 Paso 4: Configurar Auto-Deploy

Render desplegará automáticamente cada vez que hagas push a la rama `main`.

Para desactivar auto-deploy:
1. Settings → "Auto-Deploy" → OFF

## 🌐 Paso 5: Actualizar Frontend

Si tienes un frontend en Netlify/Vercel, actualiza la URL del backend:

```javascript
// Cambiar de:
const API_URL = 'http://localhost:8080';

// A:
const API_URL = 'https://brainboost-backend.onrender.com';
```

**Ejemplo para fetch:**
```javascript
const res = await fetch('https://brainboost-backend.onrender.com/api/auth/login', {
  method: 'POST',
  headers: {'Content-Type': 'application/json'},
  body: JSON.stringify({ email, password })
});
```

## ⚙️ Configuración Avanzada

### Custom Domain

1. En Render Dashboard → tu servicio → "Settings"
2. Scroll a "Custom Domains"
3. Añade tu dominio personalizado
4. Configura DNS según las instrucciones

### Health Check Endpoint

Render verificará automáticamente:
```
GET /actuator/health
```

Si no existe, añade Spring Boot Actuator:

```kotlin
// build.gradle.kts
implementation("org.springframework.boot:spring-boot-starter-actuator")
```

### Escalado (Plan Paid)

- **Starter Plan ($7/mes):** 512MB RAM
- **Standard Plan ($25/mes):** 2GB RAM, mejor performance

## 🐛 Troubleshooting

### Error: "Application failed to start"

**Revisa logs:**
1. Dashboard → Logs
2. Busca excepciones relacionadas con:
   - Conexión a base de datos
   - Variables de entorno faltantes
   - Errores de compilación

**Soluciones comunes:**
```bash
# 1. Verificar variables de entorno
# Dashboard → Environment → Verificar todas las variables

# 2. Verificar conexión a Neon
# Logs deben mostrar: "HikariPool-1 - Start completed"

# 3. Verificar Java version
# Dockerfile usa: eclipse-temurin:21-jre
```

### Error: "Database connection timeout"

**Causa:** Neon en modo sleep (plan gratuito)

**Solución:**
1. Ve a Neon Console
2. Activa la base de datos manualmente
3. Re-deploy en Render

### Error: "Out of Memory"

**Causa:** Free tier tiene solo 512MB RAM

**Solución:**
```bash
# Ajustar JAVA_TOOL_OPTIONS en Render:
JAVA_TOOL_OPTIONS=-Xmx400m -Xms200m
```

### Despliegue Lento

**Causa:** Free tier tiene CPU limitada

**Primera construcción:** 10-15 minutos (normal)
**Siguientes:** 3-5 minutos (cache de Docker)

## 📊 Monitoreo

### Métricas Disponibles

En Render Dashboard:
- CPU usage
- Memory usage
- Request count
- Response times

### Logs en Tiempo Real

```bash
# Ver logs en terminal (opcional)
# Instalar Render CLI:
npm install -g @render-cli/render

# Login
render login

# Ver logs
render logs -f brainboost-backend
```

## 🔒 Seguridad

### Variables Sensibles

✅ **Nunca commitees en Git:**
- `DATABASE_PASSWORD`
- `JWT_SECRET`
- `GEMINI_API_KEY`

✅ **Usa siempre variables de entorno en Render**

### CORS

Si tienes problemas de CORS, añade en tu backend:

```kotlin
@Configuration
class CorsConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("https://tu-frontend.netlify.app")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowCredentials(true)
    }
}
```

## 💰 Costos

### Free Tier
- ✅ 750 horas/mes (suficiente para 1 servicio 24/7)
- ✅ Sleep después de 15 minutos de inactividad
- ✅ Cold start: ~30 segundos

### Starter Tier ($7/mes)
- ✅ Sin sleep
- ✅ 512MB RAM garantizada
- ✅ Mejor para producción

## 🎉 ¡Listo!

Tu backend Spring Boot está desplegado en:
```
🌐 https://brainboost-backend.onrender.com
```

Conectado a Neon Database:
```
🗄️ ep-fancy-tree-af5xp9ie-pooler.c-2.us-west-2.aws.neon.tech
```

## 📞 Soporte

- Render Docs: https://render.com/docs
- Neon Docs: https://neon.tech/docs
- Spring Boot Docs: https://spring.io/projects/spring-boot

---

**Creado:** Noviembre 2025  
**Stack:** Spring Boot + Neon PostgreSQL + Render
