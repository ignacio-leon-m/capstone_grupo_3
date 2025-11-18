# ===================================
# GUÍA DE AMBIENTES - BrainBoost Backend
# ===================================

## 📋 Resumen

El backend tiene 2 ambientes configurados:
- **Desarrollo (dev)**: Para desarrollo local
- **Producción (prod)**: Para Render + Neon

---

## 🛠️ DESARROLLO LOCAL

### Opción 1: Usar perfil dev (Recomendado)

```powershell
# Windows PowerShell
$env:SPRING_PROFILES_ACTIVE="dev"
./gradlew bootRun
```

### Opción 2: Sin perfil (usa defaults de application.properties)

```powershell
./gradlew bootRun
```

### Configuración (application-dev.properties)

- **Base de datos**: Neon (mismo que producción) o PostgreSQL local
- **Puerto**: 8080
- **SQL logging**: Activado (ver queries en consola)
- **JWT tokens**: Duran 24 horas (más tiempo para testing)
- **Actuator**: Más endpoints expuestos (/health, /info, /metrics, /env)
- **Log level**: DEBUG para debugging detallado

### Variables de entorno opcionales para desarrollo

```powershell
# Si quieres usar PostgreSQL local en lugar de Neon
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/brainboost_dev"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="postgres"

# Si quieres probar con otra API key de Gemini
$env:GEMINI_API_KEY="tu-key-de-desarrollo"
```

---

## 🚀 PRODUCCIÓN (RENDER)

### Configuración automática

Render automáticamente usa `application-prod.properties` cuando detecta:
```
SPRING_PROFILES_ACTIVE=prod
```

### Variables de entorno en Render Dashboard

**Obligatorias:**
- `SPRING_PROFILES_ACTIVE`: `prod`
- `DATABASE_URL`: (Neon connection string con SSL)
- `DATABASE_USERNAME`: (Neon user)
- `DATABASE_PASSWORD`: (Neon password)
- `JWT_SECRET`: (clave secreta fuerte de 64+ caracteres)
- `GEMINI_API_KEY`: (API key de Google Gemini)

**Opcionales:**
- `PORT`: `8080` (Render lo configura automáticamente)
- `JWT_ACCESS_EXPIRATION`: `3600000` (1 hora en ms)
- `JWT_REFRESH_EXPIRATION`: `604800000` (7 días en ms)

### Configuración (application-prod.properties)

- **Base de datos**: Neon PostgreSQL (SSL requerido)
- **Puerto**: 8080 (o el que Render asigne)
- **SQL logging**: Desactivado (mejor performance)
- **JWT tokens**: Duran 1 hora (más seguro)
- **Connection pool**: Optimizado (max 10, min 5)
- **Actuator**: Solo /health expuesto
- **Log level**: INFO (menos verbose, más limpio)
- **Error messages**: Ocultos (seguridad)

---

## 🔄 DIFERENCIAS CLAVE

| Característica | Desarrollo (dev) | Producción (prod) |
|----------------|------------------|-------------------|
| SQL Logging | ✅ Activado | ❌ Desactivado |
| Log Level | DEBUG | INFO |
| JWT Access Token | 24 horas | 1 hora |
| Actuator Endpoints | health, info, metrics, env | solo health |
| Error Stacktraces | Mostrados | Ocultos |
| Connection Pool | Default | Optimizado (10 max) |
| Health Details | Siempre visibles | Solo con auth |

---

## 🧪 TESTING

### Verificar que el perfil se cargó correctamente

```powershell
# Desarrollo - debería mostrar logs SQL y nivel DEBUG
./gradlew bootRun

# Buscar en los logs:
# "The following 1 profile is active: "dev""
# "spring.jpa.show-sql = true"
```

### Health check

**Desarrollo:**
```bash
curl http://localhost:8080/actuator/health
# Debería mostrar detalles completos del estado
```

**Producción:**
```bash
curl https://capstone-grupo-3.onrender.com/actuator/health
# Debería mostrar solo {"status":"UP"}
```

---

## 📝 NOTAS IMPORTANTES

1. **El perfil por defecto es `dev`**: Si no configuras nada, usa desarrollo
2. **Render usa `prod` automáticamente**: Configurado en render.yaml
3. **Ambos perfiles usan Neon**: Puedes usar la misma BD o crear dos instancias
4. **Los secrets NO están en el código**: Se leen de variables de entorno
5. **application.properties** es el fallback: Si no encuentra el perfil, usa estos valores

---

## 🔐 SEGURIDAD

### Desarrollo
- Puedes usar secrets de prueba hardcodeados
- JWT secret simple está OK
- Logging verbose ayuda al debugging

### Producción
- **NUNCA** commites secrets reales
- JWT secret debe ser 64+ caracteres aleatorios
- Logging mínimo para no exponer datos sensibles
- Stacktraces ocultos para evitar leaks de información

---

## 🚨 TROUBLESHOOTING

### Error: "No active profile set, falling back to default profiles"
**Solución**: Configurar `SPRING_PROFILES_ACTIVE`
```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
```

### Error: "Could not resolve placeholder 'DATABASE_URL'"
**Solución**: El perfil prod requiere variables de entorno. Usa dev o configura las variables.

### Los cambios de perfil no se aplican
**Solución**: Limpia y reconstruye
```powershell
./gradlew clean bootRun
```

### Render no usa el perfil correcto
**Solución**: Verifica en Render Dashboard que `SPRING_PROFILES_ACTIVE=prod`

---

## 📚 ARCHIVOS DE CONFIGURACIÓN

```
src/main/resources/
├── application.properties           # Base común + fallbacks
├── application-dev.properties       # Desarrollo local
└── application-prod.properties      # Producción Render
```

---

¿Preguntas? Revisa los comentarios en cada archivo .properties
