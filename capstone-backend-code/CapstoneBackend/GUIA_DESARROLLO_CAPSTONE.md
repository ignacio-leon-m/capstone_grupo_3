# 🚀 Guía de Desarrollo - Brain Boost Capstone

## 📋 Resumen Ejecutivo del Proyecto

**Brain Boost** es una aplicación de aprendizaje adaptativo basada en gamificación e inteligencia artificial para estudiantes del Duoc UC, diseñada para mejorar la retención de conocimiento en materias teóricas.

### Factor Diferenciador Clave
- **Personalización Inteligente**: Sistema que analiza errores y aciertos del estudiante usando Google Gemini API
- **Aprendizaje Adaptativo**: Genera preguntas dinámicas enfocadas en áreas de debilidad
- **Gamificación con Propósito**: Convierte deficiencias en oportunidades de mejora

---

## 🎯 Objetivos del Proyecto

### Objetivo General
Desarrollar una aplicación de aprendizaje adaptativo basada en gamificación e IA para estudiantes del Duoc UC.

### Objetivos Específicos
1. **Gestión de Usuarios**: Sistema con 3 roles (Estudiante, Profesor, Administrador)
2. **Integración IA**: Microservicio que se integra con Google Gemini para preguntas dinámicas
3. **Gamificación**: Módulo de juego con puntajes, rankings y desafíos
4. **Arquitectura Escalable**: Microservicios en GCP con PostgreSQL y NoSQL

---

## 🏗️ Arquitectura del Sistema

### Patrón Arquitectónico
**Microservicios + Backend for Frontend (BFF)**

### Microservicios Principales

#### 1. **Auth Service** (Autenticación)
- Registro e inicio de sesión
- Generación de JWT
- Validación de credenciales

#### 2. **User Service** (Gestión de Usuarios)
- Gestión de perfiles
- Asignación de roles (Estudiante, Profesor, Administrador)
- Mantenimiento de estado de perfiles

#### 3. **Content Service** (Contenido Académico)
- CRUD del banco de preguntas
- Gestión de carreras, asignaturas y materias
- Comunicación con MongoDB (contenido bruto)
- Comunicación con IA Service (contenido adaptativo)

#### 4. **IA Service** (Inteligencia Artificial) ⭐
**Motor de personalización clave del sistema**
- Proxy seguro para Google Gemini API
- **Análisis del historial de errores del usuario**
- Generación de preguntas dinámicas y personalizadas
- Sistema de caché para optimizar costos y latencia
- **Lógica de preguntas de refuerzo**: Prioriza temas donde el usuario comete más errores

#### 5. **Scoring Service** (Puntuación y Ranking)
- Cálculo de puntajes
- Gestión de rankings
- Actualización de progreso del estudiante

#### 6. **BFF (Backend for Frontend)**
**Punto único de entrada crítico**
- Orquesta y consolida información de múltiples microservicios
- Protege claves privadas (API keys) del cliente
- Optimiza respuestas para aplicación móvil y web

---

## 💾 Persistencia de Datos

### PostgreSQL (Base de Datos Transaccional Principal)
**Almacena datos críticos con alta integridad referencial:**
- Usuarios y roles
- Estructura del contenido académico (carreras, asignaturas, preguntas CRUD)
- Lógica de gamificación (puntajes, rankings, métricas)

### MongoDB (Base de Datos No Relacional)
**Solo para ingesta y staging de datos voluminosos:**
- Contenido teórico bruto cargado por profesores
- Archivos y texto para procesamiento por IA Service
- Se libera después del procesamiento

---

## 📊 Modelo de Base de Datos - Tablas Clave

### Tablas de Ubicación e Institución
- `paises`, `regiones`, `comunas`, `instituciones`

### Tablas de Contenido Académico
- `roles`: Estudiante, Profesor, Administrador
- `carreras`, `semestres`, `asignaturas`
- `asignaturas_semestre`: Relación N:M

### Tablas de Usuarios y Gamificación ⭐
- **`usuarios`**: Información de usuarios + relación con carreras
- **`preguntas`**: Banco de contenido teórico (id, texto, respuesta_correcta, tema, id_asignatura)
- **`juegos`**: Historial de sesiones de juego
- **`metricas`**: Cada respuesta individual para análisis de rendimiento ⚡
  - `respuesta_correcta` (TRUE/FALSE)
  - `tiempo_respuesta_ms`
  - `fecha_hora`
- **`puntajes`**: Puntaje acumulado por usuario/asignatura
- **`ranking`**: Tabla optimizada para visualización

### Tablas de Carga y Auditoría
- `estados_carga`, `tipos_carga`, `cargas`

---

## 🔑 Funcionalidades por Rol

### 👨‍🎓 Estudiante
- Acceso exclusivo al módulo de juego
- Selección de materias según carrera
- Visualización de progreso y áreas de mejora
- Edición de perfil (foto/ícono)
- **Recibe cuestionarios adaptativos basados en su historial de errores**

### 👨‍🏫 Profesor
- Visualización de rankings y progreso de estudiantes
- **Carga de contenido teórico** (documentos, PDFs, texto) por materia
- CRUD manual de preguntas en banco de contenido
- **Carga masiva de alumnos** a asignaturas asignadas
- Acceso solo a datos de sus asignaturas asignadas

### 👨‍💼 Administrador
- Privilegios máximos
- Gestión completa de usuarios, contenido y configuraciones
- Asignación de profesores a asignaturas

---

## 🎮 Sistema de Gamificación y Aprendizaje Adaptativo

### Mecánicas de Juego
- Niveles progresivos
- Puntajes acumulativos
- Rankings globales y por curso
- Sistema de logros
- Desafíos personalizados

### 🧠 Lógica de Aprendizaje Adaptativo (CRÍTICO)

**Flujo de personalización:**

1. **Registro de Rendimiento**:
   - Cada respuesta se guarda en tabla `metricas` (correcta/incorrecta, tiempo, tema)
   
2. **Análisis del Historial**:
   - IA Service analiza tabla `metricas` del usuario
   - Identifica temas con más errores
   - Calcula áreas de debilidad

3. **Generación de Preguntas de Refuerzo**:
   - Solicitud a Google Gemini API con contexto del historial
   - Prioriza temas donde el usuario falla más
   - Genera preguntas dinámicas enfocadas en debilidades

4. **Sistema de Caché**:
   - Cache de preguntas generadas para reducir llamadas a API
   - Fallback a banco estático si se exceden cuotas de Gemini

---

## 🛠️ Stack Tecnológico

### Backend
- **Lenguaje**: Java + Kotlin
- **Framework**: Spring Boot
- **Arquitectura**: Microservicios

### Frontend
- **Móvil**: Android nativo (Kotlin)
- **Web**: Gestión y administración (solo Profesor/Admin)

### Bases de Datos
- **PostgreSQL**: Datos transaccionales
- **MongoDB**: Staging de contenido masivo

### IA y APIs
- **Google Gemini API**: Generación de contenido dinámico

### Cloud y Despliegue
- **Plataforma**: Google Cloud Platform (GCP)
- **Compute**: Google Compute Engine (server-capstone-g3)
- **Proxy Inverso**: Nginx
- **Contenedores**: Docker

---

## 🔒 Seguridad

### Medidas Implementadas
1. **Acceso a PostgreSQL Restringido**:
   - Puerto 5432 limitado a rango de IP específico
   - No se permiten conexiones remotas públicas

2. **Autenticación Reforzada**:
   - SSH solo por claves (sin contraseñas)
   - Usuario DB con contraseña fuerte
   - Método `scram-sha-256` para autenticación

3. **Protección de API Keys**:
   - BFF protege claves de Gemini API
   - No se exponen secretos al cliente

4. **JWT para Autorización**:
   - Tokens generados por Auth Service
   - Validación en cada microservicio

### Recomendaciones Futuras
- Migrar a **Google Cloud Run** para producción
- Comunicación local entre backend y DB
- Evitar exponer claves de API

---

## 📅 Plan de Trabajo (Metodología Scrum)

### ✅ Sprint 1: Pre-desarrollo (16-30 agosto) - COMPLETADO
- Definición de alcances
- Diseño de arquitectura
- Diagrama de base de datos
- Preparación de ambientes

### ✅ Sprint 2: Base Tecnológica (31 agosto - 14 sept) - COMPLETADO
- Configuración GCP
- Desarrollo Auth Service
- Instalación PostgreSQL
- Pruebas unitarias e integración

### 🔄 Sprint 3: Integración con IA (15-29 sept) - EN DESARROLLO
**Actividades clave:**
- Desarrollo IA Service
- Integración con Google Gemini API
- **Implementación de lógica de preguntas de refuerzo** ⭐
- Sistema de caché
- Pruebas unitarias e integración

**Obstáculos identificados:**
- Riesgo de exceder cuotas gratuitas de Gemini API
- Latencia inconsistente

**Mitigaciones:**
- Fallback a banco estático (código 429)
- Caché de preguntas generadas

### 📋 Sprint 4: Lógica de Gamificación (30 sept - 13 oct)
- Desarrollo Content Service
- Lógica de juegos (puntuación, progreso)
- Gestión de rankings y logros

### 📋 Sprint 5: Frontend y Conexión (14-27 oct)
- Desarrollo pantallas principales móvil
- Conexión con Auth & User Service
- Pruebas de conectividad

### 📋 Sprint 6: Desarrollo de Experiencia (28 oct - 10 nov)
- Pantallas de juego
- Comunicación con IA Service
- Integración de gamificación en UI

### 📋 Sprint 7: Pruebas y Despliegue (11-24 nov)
- UAT (User Acceptance Testing)
- Pruebas de estrés y rendimiento
- Preparación de producción

### 📋 Sprint 8: Cierre (25 nov - 15 dic)
- Documentación final
- Presentación y demo
- Bloqueo de código

---

## 🚨 Riesgos y Mitigaciones

### Riesgo 1: Cuotas de Google Gemini API
**Mitigación**: Sistema de fallback + caché de preguntas

### Riesgo 2: Inconsistencia en API del BFF
**Mitigación**: Contratos de datos inmutables + Swagger/OpenAPI

### Riesgo 3: Problemas de rendimiento móvil
**Mitigación**: Optimización de JSON + datos mínimos necesarios

### Riesgo 4: Errores en lógica de ranking/progreso
**Mitigación**: Pruebas unitarias exhaustivas + revisiones de código

### Riesgo 5: Vulnerabilidades de seguridad
**Mitigación**: Pruebas de penetración + validación estricta de archivos

---

## 🎯 Próximos Pasos Inmediatos (Sprint Actual)

### Para IA Service (Prioridad ALTA)
1. **Implementar análisis de historial de errores**:
   ```kotlin
   // Consultar tabla metricas del usuario
   // Agrupar por tema
   // Identificar temas con más errores
   // Generar contexto para Gemini API
   ```

2. **Desarrollar lógica de preguntas de refuerzo**:
   - Crear algoritmo que priorice temas débiles
   - Integrar con Gemini API con contexto personalizado
   - Implementar ponderación de dificultad

3. **Sistema de caché**:
   - Implementar Redis o caché en memoria
   - Definir política de expiración
   - Fallback a banco estático

4. **Pruebas unitarias**:
   - Análisis de historial
   - Generación de preguntas
   - Manejo de errores de API

### Para Content Service
1. Finalizar CRUD de preguntas
2. Integración con MongoDB para contenido bruto
3. Endpoint para solicitar preguntas adaptativas a IA Service

### Para Scoring Service
1. Diseñar tabla `metricas` (ya está en diagrama)
2. Implementar registro de cada respuesta
3. Calcular estadísticas de rendimiento por tema

---

## 📝 Notas de Implementación

### Flujo de Generación de Cuestionario Adaptativo

```
1. Usuario solicita nuevo cuestionario
   ↓
2. BFF recibe solicitud → Content Service
   ↓
3. Content Service → IA Service
   ↓
4. IA Service:
   a. Consulta tabla metricas del usuario
   b. Analiza historial de errores
   c. Identifica temas débiles
   d. Consulta caché
   e. Si no hay caché → Google Gemini API
   f. Genera preguntas de refuerzo
   ↓
5. IA Service → Content Service → BFF → Cliente
```

### Estructura de Datos para Análisis de Errores

```json
{
  "usuario_id": 123,
  "analisis_temas": [
    {
      "tema": "JOINs en SQL",
      "total_preguntas": 20,
      "respuestas_incorrectas": 15,
      "tasa_error": 0.75,
      "prioridad": "ALTA"
    },
    {
      "tema": "Normalización de Bases de Datos",
      "total_preguntas": 10,
      "respuestas_incorrectas": 3,
      "tasa_error": 0.30,
      "prioridad": "MEDIA"
    }
  ],
  "recomendacion": "Generar 5 preguntas de JOINs, 3 de Normalización, 2 mixtas"
}
```

---

## 🎓 Alineación con Perfil de Egreso

Este proyecto demuestra:
- Capacidad de crear soluciones informáticas integrales
- Aplicación de metodología Scrum
- Construcción de arquitectura escalable de microservicios
- Programación con buenas prácticas
- Manejo de bases de datos relacionales y no relacionales
- Integración de IA y APIs externas
- Seguridad y manejo de vulnerabilidades
- Desarrollo en la nube (GCP)

---

## 📚 Referencias y Recursos

### Documentación Técnica
- Spring Boot: https://spring.io/projects/spring-boot
- Google Gemini API: https://ai.google.dev/
- PostgreSQL: https://www.postgresql.org/docs/
- MongoDB: https://docs.mongodb.com/

### Herramientas de Desarrollo
- Docker
- Nginx
- Git
- Swagger/OpenAPI

---

## ✅ Checklist de Implementación

### IA Service
- [ ] Consulta a tabla metricas
- [ ] Algoritmo de análisis de errores
- [ ] Integración con Gemini API
- [ ] Sistema de caché
- [ ] Fallback a banco estático
- [ ] Pruebas unitarias
- [ ] Pruebas de integración

### Content Service
- [ ] CRUD de preguntas
- [ ] Integración con MongoDB
- [ ] Endpoint para IA Service
- [ ] Pruebas unitarias

### Scoring Service
- [ ] Tabla metricas implementada
- [ ] Registro de respuestas
- [ ] Cálculo de puntajes
- [ ] Actualización de rankings
- [ ] Pruebas unitarias

### BFF
- [ ] Orquestación de servicios
- [ ] Protección de API keys
- [ ] Optimización de respuestas
- [ ] Pruebas de integración

---

## 🎯 Objetivo Final

Entregar una aplicación móvil Android nativa que transforme el estudio tradicional en una experiencia gamificada e inteligente, donde cada error se convierte en una oportunidad de aprendizaje personalizado gracias al análisis de IA.

**La clave del éxito está en la implementación efectiva del IA Service y su capacidad de generar contenido adaptativo basado en el historial real del estudiante.**

---

*Documento generado a partir del Informe Valencia-Leon-Bertero.pdf usando markitdown*
*Última actualización: Noviembre 2025*
