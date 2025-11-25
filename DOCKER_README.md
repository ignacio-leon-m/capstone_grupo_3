# 🚀 BrainBoost - Docker Setup

## Requisitos Previos

- Docker Desktop instalado y corriendo
- Docker Compose (incluido en Docker Desktop)
- Al menos 4GB de RAM disponible para Docker

## Configuración Inicial

### 1. Configurar Variables de Entorno

Copia el archivo de ejemplo y configura tu API key de Gemini:

```powershell
Copy-Item .env.example .env
```

Luego edita el archivo `.env` y agrega tu **Gemini API Key**:
```
GEMINI_API_KEY=tu-api-key-aquí
```

Puedes obtener tu API key en: https://aistudio.google.com/app/apikey

### 2. Construir y Levantar los Servicios

Desde la raíz del proyecto, ejecuta:

```powershell
docker-compose up --build
```

Esto levantará 3 servicios:
- **PostgreSQL** (puerto 5432): Base de datos principal
- **MongoDB** (puerto 27017): Base de datos de documentos
- **Backend** (puerto 8080): Aplicación Spring Boot

### 3. Verificar que todo está corriendo

```powershell
docker-compose ps
```

Deberías ver 3 contenedores corriendo:
- `brainboost-postgres` (healthy)
- `brainboost-mongodb` (healthy)
- `brainboost-backend` (healthy)

### 4. Acceder a la Aplicación

Una vez que todos los servicios estén saludables (healthy):

- **Frontend**: http://localhost:8080
- **API Health Check**: http://localhost:8080/actuator/health
- **API Docs**: http://localhost:8080/swagger-ui.html (si está configurado)

## Comandos Útiles

### Ver logs en tiempo real
```powershell
docker-compose logs -f backend
```

### Detener todos los servicios
```powershell
docker-compose down
```

### Detener y eliminar volúmenes (⚠️ borra los datos)
```powershell
docker-compose down -v
```

### Reconstruir solo el backend
```powershell
docker-compose up --build backend
```

### Acceder a la shell del contenedor backend
```powershell
docker exec -it brainboost-backend /bin/bash
```

### Ver logs de un servicio específico
```powershell
# PostgreSQL
docker-compose logs postgres

# MongoDB
docker-compose logs mongodb

# Backend
docker-compose logs backend
```

## Usuarios por Defecto

Después de la primera ejecución (Flyway migrará la base de datos):

### Administrador
- **Email**: cecilia.arroyo@duoc.cl
- **Password**: duoc123
- **Rol**: admin

### Profesor
- **Email**: cespinoza@duoc.cl
- **Password**: duoc123
- **Rol**: profesor

## Troubleshooting

### El backend no inicia
1. Verifica que PostgreSQL y MongoDB estén healthy:
   ```powershell
   docker-compose ps
   ```

2. Revisa los logs del backend:
   ```powershell
   docker-compose logs backend
   ```

### Error de conexión a la base de datos
- Asegúrate de que los puertos 5432 y 27017 no estén siendo usados por otras aplicaciones
- Verifica que Docker tenga suficiente memoria asignada (mínimo 4GB)

### El backend se reinicia constantemente
- Revisa el healthcheck: `docker-compose logs backend`
- Puede ser que Flyway esté fallando en las migraciones
- Verifica que el `GEMINI_API_KEY` esté configurado correctamente

### Limpiar todo y empezar de nuevo
```powershell
# Detener y eliminar contenedores, redes y volúmenes
docker-compose down -v

# Eliminar imágenes build
docker image prune -a

# Reconstruir desde cero
docker-compose up --build
```

## Estructura de Volúmenes

Los datos persisten en volúmenes de Docker:
- `postgres_data`: Datos de PostgreSQL
- `mongodb_data`: Datos de MongoDB
- `mongodb_config`: Configuración de MongoDB
- `backend_uploads`: Archivos subidos (PDFs, Excel, etc.)

## Red de Docker

Todos los servicios están en la red `brainboost-network`, lo que permite la comunicación entre contenedores usando nombres de servicio (ej: `postgres`, `mongodb`).

## Producción

Para despliegue en producción:

1. Cambia las contraseñas en `docker-compose.yml`
2. Usa secrets de Docker en lugar de variables de entorno
3. Configura un proxy reverso (Nginx) para HTTPS
4. Ajusta los límites de recursos en `docker-compose.yml`

Consulta `docs/RENDER_DEPLOYMENT_GUIDE.md` para despliegue en Render.
