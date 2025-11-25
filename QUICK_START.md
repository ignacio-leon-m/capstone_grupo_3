# 🚀 BrainBoost - Inicio Rápido con Docker

## 📋 Pre-requisitos

1. **Docker Desktop** instalado y corriendo
2. **Git** (para clonar el repositorio)
3. **Gemini API Key** (obtener en https://aistudio.google.com/app/apikey)

## ⚡ Inicio Rápido (5 minutos)

### 1️⃣ Configurar API Key

```powershell
# Copiar el archivo de ejemplo
Copy-Item .env.example .env

# Editar .env y agregar tu Gemini API Key
notepad .env
```

### 2️⃣ Levantar los servicios

```powershell
# Usando el script de gestión (recomendado)
.\docker-manage.ps1 start

# O directamente con docker-compose
docker-compose up -d
```

### 3️⃣ Verificar que todo está corriendo

```powershell
.\docker-manage.ps1 status
```

Espera a que todos los servicios estén **healthy** (puede tomar 1-2 minutos).

### 4️⃣ Acceder a la aplicación

Abre tu navegador en: **http://localhost:8080**

## 👤 Usuarios de Prueba

### Administrador
- **Email**: `cecilia.arroyo@duoc.cl`
- **Password**: `duoc123`

### Profesor
- **Email**: `cespinoza@duoc.cl`
- **Password**: `duoc123`

## 🛠️ Comandos Útiles

```powershell
# Ver logs en tiempo real
.\docker-manage.ps1 logs

# Reiniciar servicios
.\docker-manage.ps1 restart

# Detener todo
.\docker-manage.ps1 stop

# Limpiar y empezar de cero
.\docker-manage.ps1 clean
.\docker-manage.ps1 rebuild
```

## 📚 Documentación Completa

- **Docker**: Ver `DOCKER_README.md`
- **Backend**: Ver `capstone-backend-code/CapstoneBackend/README.md`
- **Deployment**: Ver `capstone-backend-code/CapstoneBackend/docs/`

## 🐛 Problemas Comunes

### Puerto 8080 ya está en uso
```powershell
# Detener el proceso que usa el puerto
Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process

# O cambiar el puerto en docker-compose.yml
```

### Docker Desktop no inicia
- Reinicia tu computadora
- Verifica que la virtualización esté habilitada en BIOS

### Servicios no son healthy
```powershell
# Ver logs del servicio problemático
docker-compose logs postgres
docker-compose logs mongodb
docker-compose logs backend
```

## 📞 Soporte

Si encuentras problemas, revisa:
1. Los logs: `.\docker-manage.ps1 logs`
2. El estado: `.\docker-manage.ps1 status`
3. La documentación en `DOCKER_README.md`
