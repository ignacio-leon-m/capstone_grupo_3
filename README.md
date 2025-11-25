# 🧠 BrainBoost - Plataforma Educativa Gamificada

Sistema de aprendizaje interactivo con gamificación e inteligencia artificial para educación superior.

## 📁 Estructura del Proyecto

```
capstone_grupo_3/
├── capstone-backend-code/          # Backend Spring Boot + Kotlin
│   └── CapstoneBackend/
│       ├── src/                    # Código fuente
│       ├── docs/                   # Documentación técnica
│       ├── Dockerfile              # Imagen Docker del backend
│       └── docker-compose.yml      # Compose local (deprecated)
│
├── capstone-brainboost-app/        # App móvil Android (Kotlin)
├── capstone-kotlin-code/           # Prototipos Kotlin
├── Fase 1/                         # Entregables Fase 1
├── Fase 2/                         # Entregables Fase 2
├── postman/                        # Colecciones API
│
├── docker-compose.yml              # 🐳 Docker Compose (USAR ESTE)
├── docker-manage.ps1               # Script de gestión Docker
├── QUICK_START.md                  # Guía de inicio rápido
└── DOCKER_README.md                # Documentación Docker completa

```

## 🚀 Inicio Rápido

### Opción 1: Docker (Recomendado)

```powershell
# 1. Configurar variables de entorno
Copy-Item .env.example .env
# Editar .env y agregar tu GEMINI_API_KEY

# 2. Iniciar servicios
.\docker-manage.ps1 start

# 3. Verificar estado
.\docker-manage.ps1 status

# 4. Acceder a la aplicación
# http://localhost:8080
```

Ver [QUICK_START.md](QUICK_START.md) para más detalles.

### Opción 2: Desarrollo Local

Requiere:
- JDK 21
- PostgreSQL 17.5
- MongoDB 7.0
- Gradle 8.5

```powershell
cd capstone-backend-code/CapstoneBackend
.\gradlew bootRun
```

Ver [capstone-backend-code/CapstoneBackend/README.md](capstone-backend-code/CapstoneBackend/README.md) para configuración detallada.

## 🏗️ Arquitectura

### Backend (Spring Boot + Kotlin)
- **Framework**: Spring Boot 3.5.5
- **Lenguaje**: Kotlin 1.9.25
- **BD Principal**: PostgreSQL 17.5
- **BD Documentos**: MongoDB 7.0
- **IA**: Google Gemini 2.0 Flash
- **Seguridad**: JWT + Spring Security
- **ORM**: Hibernate + JPA
- **Migraciones**: Flyway

### Funcionalidades Principales
- 🎮 **Juego Ahorcado**: Aprendizaje de conceptos
- 📝 **Quiz Interactivo**: Evaluación de conocimientos
- 📄 **Carga de Contenido IA**: Generación automática desde PDFs
- 👥 **Gestión de Usuarios**: Roles (Admin, Profesor, Alumno)
- 📊 **Métricas y Analytics**: Seguimiento de progreso
- 🏆 **Sistema de Puntajes**: Gamificación

## 👥 Usuarios de Prueba

### Administrador
- Email: `cecilia.arroyo@duoc.cl`
- Password: `duoc123`
- Rol: `admin`

### Profesor
- Email: `cespinoza@duoc.cl`
- Password: `duoc123`
- Rol: `profesor`

## 📡 API Endpoints

### Autenticación
- `POST /api/auth/register` - Registro de usuario
- `POST /api/auth/login` - Inicio de sesión

### Usuarios
- `GET /api/users/me` - Usuario actual
- `GET /api/users` - Listar usuarios (admin)

### Asignaturas
- `GET /api/subjects/professor/{id}` - Asignaturas del profesor
- `GET /api/subjects/student/{id}` - Asignaturas del estudiante

### Contenido
- `POST /api/ai/pdf/concepts` - Generar conceptos desde PDF
- `GET /api/subjects/{id}/topics` - Temas de asignatura

### Juegos
- `GET /api/hangman/word/{subjectId}` - Obtener palabra (Ahorcado)
- `POST /api/hangman/save-score` - Guardar puntaje

Ver documentación completa en: `/docs/API_DOCUMENTATION.md`

## 🔧 Tecnologías

### Backend
- Kotlin 1.9.25
- Spring Boot 3.5.5
- PostgreSQL 17.5
- MongoDB 7.0
- Google Gemini AI
- Apache Tika (extracción PDF)
- Apache POI (Excel)
- Flyway (migraciones)
- JWT (autenticación)

### DevOps
- Docker & Docker Compose
- Render (deployment)
- GitHub Actions (CI/CD)

### Frontend Web
- HTML5, CSS3, JavaScript
- Bootstrap 5.3
- Font Awesome
- Responsive Design

## 📚 Documentación

- [QUICK_START.md](QUICK_START.md) - Inicio rápido
- [DOCKER_README.md](DOCKER_README.md) - Docker completo
- [Backend README](capstone-backend-code/CapstoneBackend/README.md) - Backend detallado
- [Deployment Guide](capstone-backend-code/CapstoneBackend/docs/RENDER_DEPLOYMENT_GUIDE.md) - Despliegue
- [Configuration Guide](capstone-backend-code/CapstoneBackend/docs/CONFIG_GUIDE.md) - Configuración
- [Análisis de Contenido](capstone-backend-code/CapstoneBackend/docs/ANALISIS_CONTENIDO_Y_FLUJOS.md) - Flujos de negocio

## 🐛 Troubleshooting

### Docker
```powershell
# Ver logs
.\docker-manage.ps1 logs

# Estado de servicios
.\docker-manage.ps1 status

# Reconstruir todo
.\docker-manage.ps1 rebuild
```

### Base de Datos
```powershell
# Conectar a PostgreSQL
docker exec -it brainboost-postgres psql -U postgres -d capstone-bbdd

# Conectar a MongoDB
docker exec -it brainboost-mongodb mongosh -u admin -p adminpass123
```

### Backend
```powershell
# Shell en contenedor
docker exec -it brainboost-backend /bin/bash

# Logs específicos
docker logs brainboost-backend --tail 100 -f
```

## 🤝 Contribución

### Branching Strategy
- `main` - Producción
- `develop` - Desarrollo
- `feature/*` - Nuevas características
- `bugfix/*` - Correcciones

### Commits
Formato: `tipo(scope): descripción`

Tipos:
- `feat`: Nueva característica
- `fix`: Corrección de bug
- `docs`: Documentación
- `refactor`: Refactorización
- `test`: Tests
- `chore`: Tareas de mantenimiento

## 📝 License

Este proyecto es parte del Capstone de DUOC UC - Ingeniería Informática.

## 👨‍💻 Equipo

- Desarrollo Backend: Spring Boot + Kotlin
- Desarrollo Frontend: Web y Android
- Base de Datos: PostgreSQL + MongoDB
- IA: Google Gemini Integration
- DevOps: Docker + Render

---

**DUOC UC** - Ingeniería Informática - Capstone 2025
