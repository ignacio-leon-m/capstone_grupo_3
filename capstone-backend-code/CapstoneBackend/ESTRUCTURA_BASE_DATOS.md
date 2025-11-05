# 🗄️ Estructura de Base de Datos - Brain Boost Capstone

## 📋 Índice
1. [Visión General](#visión-general)
2. [Arquitectura de Datos](#arquitectura-de-datos)
3. [Tablas por Categoría](#tablas-por-categoría)
4. [Relaciones Clave](#relaciones-clave)
5. [Flujos de Datos Críticos](#flujos-de-datos-críticos)
6. [Vinculación con Microservicios](#vinculación-con-microservicios)

---

## 🎯 Visión General

La base de datos PostgreSQL de Brain Boost está diseñada siguiendo un modelo relacional normalizado que soporta:

- **Gamificación**: Sistema de puntajes, rankings y métricas de rendimiento
- **Aprendizaje Adaptativo**: Análisis de errores para personalización con IA
- **Gestión Académica**: Estructura jerárquica de ubicación, institución, carreras y asignaturas
- **Multi-rol**: Estudiante, Profesor y Administrador
- **Auditoría**: Trazabilidad de cargas masivas y acciones del sistema

### 🔑 Características Técnicas
- **UUIDs**: Todos los IDs son UUID v4 (generados con `uuid-ossp`)
- **Timestamps**: Auditoría automática con `DEFAULT CURRENT_TIMESTAMP`
- **Integridad Referencial**: `CASCADE` para mantener consistencia
- **Normalización**: Estructuras jerárquicas para evitar redundancia

---

## 🏗️ Arquitectura de Datos

### Diagrama de Dependencias

```
paises
  └─ regiones
      └─ comunas
          └─ instituciones
              └─ carreras
                  ├─ asignaturas ←─ asignaturas_semestre ─→ semestres
                  │     ├─ temas
                  │     │   ├─ preguntas
                  │     │   └─ conceptos
                  │     ├─ juegos
                  │     └─ puntajes
                  └─ usuarios (+ roles)
                        ├─ metricas ─→ preguntas
                        ├─ juegos
                        ├─ puntajes
                        └─ cargas ←─ estados_carga, tipos_carga
```

---

## 📊 Tablas por Categoría

### 1️⃣ **Ubicación e Institución** (Contexto Geográfico)

#### **`paises`** - Catálogo de Países
```sql
CREATE TABLE paises (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL UNIQUE
);
```
**Propósito**: Base de la jerarquía geográfica para ubicación global.

**Vinculación con Proyecto**: 
- Permite escalabilidad internacional del sistema
- Actualmente poblado con "Chile"

---

#### **`regiones`** - Divisiones Administrativas
```sql
CREATE TABLE regiones (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL UNIQUE,
    id_pais UUID NOT NULL,
    FOREIGN KEY (id_pais) REFERENCES paises(id)
);
```
**Propósito**: Organización territorial dentro de cada país.

**Vinculación con Proyecto**:
- Facilita filtros por ubicación geográfica
- Ejemplo: "Metropolitana" para Duoc UC sede Plaza Oeste

---

#### **`comunas`** - Nivel Específico de Ubicación
```sql
CREATE TABLE comunas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL UNIQUE,
    id_region UUID NOT NULL,
    FOREIGN KEY (id_region) REFERENCES regiones(id)
);
```
**Propósito**: Nivel más granular de ubicación.

**Vinculación con Proyecto**:
- Permite identificar sedes específicas de Duoc UC
- Ejemplo: "Cerrillos" (Plaza Oeste)

---

#### **`instituciones`** - Centros Educativos
```sql
CREATE TABLE instituciones (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(255) NOT NULL UNIQUE,
    id_comuna UUID,
    FOREIGN KEY (id_comuna) REFERENCES comunas(id)
);
```
**Propósito**: Almacena las instituciones educativas y su ubicación.

**Vinculación con Proyecto**:
- **Multi-sede**: Soporta múltiples sedes de Duoc UC
- **Escalabilidad**: Puede incluir otras instituciones en el futuro
- Ejemplo: "DUOC UC Sede Plaza Oeste"

---

### 2️⃣ **Roles y Estructura Académica**

#### **`roles`** - Perfiles de Usuario ⭐
```sql
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(50) NOT NULL UNIQUE
);
```
**Propósito**: Define los 3 roles del sistema.

**Vinculación con Proyecto**:
- **`admin`**: Gestión completa del sistema
- **`profesor`**: Carga contenido, ve estadísticas de sus cursos
- **`alumno`**: Juega, visualiza progreso, recibe aprendizaje adaptativo

**Flujo de Autorización**:
```
Auth Service → Genera JWT con rol
↓
BFF → Valida rol en cada request
↓
Microservicios → Filtran datos según rol
```

---

#### **`carreras`** - Programas Académicos
```sql
CREATE TABLE carreras (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL,
    id_institucion UUID NOT NULL,
    FOREIGN KEY (id_institucion) REFERENCES instituciones(id),
    UNIQUE (nombre, id_institucion)
);
```
**Propósito**: Almacena las carreras ofrecidas por cada institución.

**Vinculación con Proyecto**:
- **Segmentación de Contenido**: Cada carrera tiene sus propias asignaturas
- Ejemplo: "ing-informatica" (Ingeniería Informática)
- **UNIQUE constraint**: Evita duplicados por institución

---

#### **`semestres`** - Períodos Académicos
```sql
CREATE TABLE semestres (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(50) NOT NULL UNIQUE
);
```
**Propósito**: Define períodos académicos.

**Vinculación con Proyecto**:
- Organización temporal del contenido
- Ejemplo: "Primer Semestre 2026"
- Permite historización de asignaturas por período

---

#### **`asignaturas`** - Materias de Estudio
```sql
CREATE TABLE asignaturas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL,
    id_carrera UUID NOT NULL,
    FOREIGN KEY (id_carrera) REFERENCES carreras(id)
);
```
**Propósito**: Materias específicas de cada carrera.

**Vinculación con Proyecto**:
- **Núcleo del contenido**: Las preguntas y temas pertenecen a asignaturas
- **Profesor**: Solo gestiona asignaturas asignadas
- **Estudiante**: Selecciona asignaturas para jugar
- Ejemplo: "Algoritmos y Programación"

**Relación con Content Service**:
```
Content Service 
  → CRUD de asignaturas
  → Asignación de profesores a asignaturas (lógica de negocio)
  → Filtrado por carrera del usuario
```

---

#### **`asignaturas_semestre`** - Tabla Intermedia (N:M)
```sql
CREATE TABLE asignaturas_semestre (
    id_asignatura UUID NOT NULL,
    id_semestre UUID NOT NULL,
    PRIMARY KEY (id_asignatura, id_semestre),
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id),
    FOREIGN KEY (id_semestre) REFERENCES semestres(id)
);
```
**Propósito**: Resuelve la relación muchos a muchos entre asignaturas y semestres.

**Vinculación con Proyecto**:
- Una asignatura puede dictarse en múltiples semestres
- Un semestre contiene múltiples asignaturas
- **Flexibilidad**: Permite reutilización de contenido entre períodos

---

### 3️⃣ **Usuarios y Seguridad** ⭐

#### **`usuarios`** - Tabla Central de Usuarios
```sql
CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    rut VARCHAR(12) NOT NULL UNIQUE,
    id_rol UUID NOT NULL,
    id_carrera UUID NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    celular VARCHAR(20),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_ultimo_login TIMESTAMP,
    FOREIGN KEY (id_rol) REFERENCES roles(id),
    FOREIGN KEY (id_carrera) REFERENCES carreras(id)
);
```

**Propósito**: Almacena toda la información de usuarios del sistema.

**Vinculación con Proyecto**:

**Auth Service**:
- `password_hash`: Almacenado con bcrypt (`$2a$10$...`)
- `correo`: Usado como username para login
- `rut`: Identificador único chileno
- `estado`: Permite activar/desactivar cuentas

**User Service**:
- `id_rol`: Define permisos (alumno/profesor/admin)
- `id_carrera`: Filtra contenido disponible
- `fecha_ultimo_login`: Tracking de actividad

**Campos de Auditoría**:
- `fecha_creacion`: Timestamp de registro
- `fecha_ultimo_login`: Última actividad

**Ejemplo de Hash (script.sql)**:
```
password_hash: '$2a$10$tCQpkcBLaBTmiMzOcdDamOkCxeGc4nIJXzFUkwcWAU8Cj5iRkLa/K'
→ Contraseña encriptada con bcrypt (10 rondas)
```

---

### 4️⃣ **Contenido y Conocimiento** ⭐⭐⭐

#### **`temas`** - Categorización de Contenido
```sql
CREATE TABLE temas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL,
    id_asignatura UUID NOT NULL,
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id),
    UNIQUE (nombre, id_asignatura)
);
```

**Propósito**: **Capa de organización conceptual** dentro de cada asignatura.

**Vinculación con Proyecto**:
- **Granularidad**: Permite organizar preguntas por temas específicos
- **Análisis de IA**: El IA Service analiza errores POR TEMA
- Ejemplo: Dentro de "Algoritmos y Programación":
  - Tema: "Conceptos Básicos"
  - Tema: "Estructuras de Control"
  - Tema: "Funciones y Procedimientos"

**Impacto en Aprendizaje Adaptativo**:
```
IA Service → Consulta metricas
↓
Agrupa errores por id_tema
↓
Identifica temas con mayor tasa de error
↓
Genera preguntas de refuerzo en esos temas
```

---

#### **`preguntas`** - Banco de Contenido Teórico ⭐⭐⭐
```sql
CREATE TABLE preguntas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    texto TEXT NOT NULL,
    respuesta_correcta TEXT NOT NULL,
    id_asignatura UUID NOT NULL,
    id_tema UUID NOT NULL,
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id),
    FOREIGN KEY (id_tema) REFERENCES temas(id)
);
```

**Propósito**: **Corazón del contenido educativo** del sistema.

**Vinculación con Proyecto**:

**Content Service**:
- **CRUD Manual**: Profesores crean/editan/eliminan preguntas
- **Filtrado**: Solo preguntas de asignaturas asignadas al profesor
- **Consulta**: Estudiantes reciben preguntas de sus asignaturas

**IA Service**:
- **Input para Gemini API**: Contexto de preguntas existentes
- **Fallback**: Si Gemini falla, usa preguntas estáticas de esta tabla
- **Caché**: Preguntas generadas por IA eventualmente se guardan aquí

**Estructura de Datos**:
- `texto`: El enunciado de la pregunta
- `respuesta_correcta`: Respuesta válida
- `id_tema`: **CRÍTICO** para análisis adaptativo
- `id_asignatura`: Contexto de la materia

**Ejemplo (script.sql)**:
```sql
texto: '¿Qué es una variable?'
respuesta_correcta: 'Un espacio en memoria para almacenar un valor.'
id_tema: [UUID del tema "Conceptos Básicos"]
id_asignatura: [UUID de "Algoritmos y Programación"]
```

---

#### **`conceptos`** - Palabras Clave por Tema
```sql
CREATE TABLE conceptos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    palabra_concepto VARCHAR(255) NOT NULL,
    id_tema UUID NOT NULL,
    FOREIGN KEY (id_tema) REFERENCES temas(id)
);
```

**Propósito**: **Vocabulario técnico** asociado a cada tema.

**Vinculación con Proyecto**:
- **IA Service**: Contexto adicional para generar preguntas coherentes
- **Búsqueda**: Permite búsqueda semántica de contenido
- **Gamificación**: Puede usarse para juegos de términos/definiciones

**Ejemplo (script.sql)**:
```sql
palabra_concepto: 'variable'
id_tema: [UUID de "Conceptos Básicos"]
```

**Uso en Generación de Preguntas con IA**:
```
Content Service → Envía conceptos del tema a IA Service
↓
IA Service → Contexto para Gemini API:
  "Genera preguntas sobre: variable, función, parámetro..."
↓
Gemini → Preguntas coherentes con vocabulario del tema
```

---

### 5️⃣ **Gamificación y Métricas** ⭐⭐⭐

#### **`juegos`** - Historial de Sesiones de Juego
```sql
CREATE TABLE juegos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_usuario UUID NOT NULL,
    id_asignatura UUID NOT NULL,
    nombre_juego VARCHAR(50),
    intentos_restantes INT,
    estado_partida VARCHAR(50) NOT NULL DEFAULT 'En curso',
    fecha_inicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_fin TIMESTAMP,
    puntaje NUMERIC(10, 2),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id)
);
```

**Propósito**: **Registro de cada sesión de juego** del estudiante.

**Vinculación con Proyecto**:

**Scoring Service**:
- Crea nueva sesión al iniciar juego
- Actualiza `estado_partida`: "En curso" → "Completado" / "Abandonado"
- Calcula y guarda `puntaje` final

**Campos Críticos**:
- `nombre_juego`: Tipo de juego (ej: "Ahorcado", "Quiz Rápido")
- `intentos_restantes`: Mecánica específica del juego
- `fecha_inicio` / `fecha_fin`: Duración de la sesión
- `puntaje`: Resultado final

**Flujo de Juego**:
```
1. Estudiante inicia juego
   ↓
2. BFF → Scoring Service: CREATE juego (estado: 'En curso')
   ↓
3. Estudiante responde preguntas
   ↓
4. BFF → Scoring Service: UPDATE puntaje, intentos_restantes
   ↓
5. Fin de juego
   ↓
6. BFF → Scoring Service: UPDATE fecha_fin, estado_partida: 'Completado'
```

---

#### **`metricas`** - Análisis Granular de Rendimiento ⭐⭐⭐⭐⭐
```sql
CREATE TABLE metricas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_usuario UUID NOT NULL,
    id_pregunta UUID NOT NULL,
    respuesta_correcta BOOLEAN NOT NULL,
    tiempo_respuesta_ms INT,
    fecha_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_pregunta) REFERENCES preguntas(id)
);
```

**Propósito**: **LA TABLA MÁS CRÍTICA PARA EL APRENDIZAJE ADAPTATIVO**

**Vinculación con Proyecto**:

### 🧠 **CORAZÓN DEL SISTEMA DE IA**

Esta tabla es el **fundamento del aprendizaje adaptativo**. Cada vez que un estudiante responde una pregunta, se registra:

**Campos Críticos**:
- `respuesta_correcta`: TRUE/FALSE (análisis de errores)
- `tiempo_respuesta_ms`: Velocidad de respuesta
- `id_pregunta`: **Vincula con tema** (pregunta → tema)
- `fecha_hora`: Temporal del aprendizaje

**Flujo de Análisis Adaptativo**:
```sql
-- 1. IA Service analiza errores por tema
SELECT 
    t.nombre AS tema,
    COUNT(*) AS total_respuestas,
    SUM(CASE WHEN m.respuesta_correcta = FALSE THEN 1 ELSE 0 END) AS total_errores,
    CAST(SUM(CASE WHEN m.respuesta_correcta = FALSE THEN 1 ELSE 0 END) AS FLOAT) / COUNT(*) AS tasa_error
FROM metricas m
JOIN preguntas p ON m.id_pregunta = p.id
JOIN temas t ON p.id_tema = t.id
WHERE m.id_usuario = [UUID_ESTUDIANTE]
GROUP BY t.id, t.nombre
ORDER BY tasa_error DESC;

-- Resultado:
-- tema                         | total_respuestas | total_errores | tasa_error
-- "Punteros y Referencias"     | 20               | 15            | 0.75
-- "Recursividad"               | 10               | 6             | 0.60
-- "Conceptos Básicos"          | 30               | 5             | 0.17
```

**2. IA Service envía contexto a Gemini API**:
```json
{
  "usuario_id": "uuid-estudiante",
  "temas_debiles": [
    {
      "tema": "Punteros y Referencias",
      "tasa_error": 0.75,
      "total_errores": 15
    },
    {
      "tema": "Recursividad",
      "tasa_error": 0.60,
      "total_errores": 6
    }
  ],
  "solicitud": "Generar 5 preguntas de nivel intermedio sobre Punteros y Referencias"
}
```

**3. Gemini genera preguntas personalizadas** enfocadas en debilidades

**Relación con Microservicios**:
- **Scoring Service**: INSERT de cada respuesta
- **IA Service**: SELECT para análisis
- **Content Service**: JOIN con preguntas y temas

---

#### **`puntajes`** - Puntaje Acumulado por Asignatura
```sql
CREATE TABLE puntajes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_usuario UUID NOT NULL,
    id_asignatura UUID NOT NULL,
    puntaje NUMERIC(10, 2) NOT NULL,
    fecha_asignacion DATE NOT NULL DEFAULT CURRENT_DATE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id)
);
```

**Propósito**: **Puntaje total** del usuario en cada asignatura.

**Vinculación con Proyecto**:

**Scoring Service**:
- Actualiza después de cada juego
- Suma puntos de la sesión al total acumulado

**Uso en Rankings**:
- Base para generar tabla `ranking` (si existiera)
- Ordenamiento de estudiantes

**Diferencia con `juegos.puntaje`**:
- `juegos.puntaje`: Puntaje de UNA sesión
- `puntajes.puntaje`: Puntaje TOTAL acumulado

**Ejemplo de Actualización**:
```sql
-- Usuario completa juego con 50 puntos
-- Puntaje previo en asignatura: 150

UPDATE puntajes 
SET puntaje = puntaje + 50  -- 150 + 50 = 200
WHERE id_usuario = [UUID] AND id_asignatura = [UUID];
```

---

### 6️⃣ **Auditoría y Carga Masiva**

#### **`estados_carga`** - Estados de Procesos
```sql
CREATE TABLE estados_carga (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre_estado VARCHAR(50) NOT NULL UNIQUE
);
```

**Propósito**: Catálogo de estados posibles para cargas masivas.

**Valores Típicos**:
- "Completado"
- "Pendiente"
- "Fallido"
- "En Proceso"

---

#### **`tipos_carga`** - Tipos de Carga Masiva
```sql
CREATE TABLE tipos_carga (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre_tipo VARCHAR(100) NOT NULL UNIQUE
);
```

**Propósito**: Catálogo de tipos de carga.

**Vinculación con Proyecto**:

**Ejemplos de Tipos**:
- "Carga Masiva Apuntes" (MongoDB)
- "Carga Masiva Usuarios" (Profesores)
- "Carga Masiva Alumnos" (Asignación a asignaturas)
- "Carga Masiva Preguntas" (Banco de contenido)

---

#### **`cargas`** - Historial de Cargas Masivas
```sql
CREATE TABLE cargas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_usuario_carga UUID NOT NULL,
    fecha_hora_carga TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    nombre_archivo VARCHAR(255) NOT NULL,
    id_estado UUID NOT NULL,
    detalle_error TEXT,
    id_tipo_carga UUID NOT NULL,
    FOREIGN KEY (id_usuario_carga) REFERENCES usuarios(id),
    FOREIGN KEY (id_estado) REFERENCES estados_carga(id),
    FOREIGN KEY (id_tipo_carga) REFERENCES tipos_carga(id)
);
```

**Propósito**: **Auditoría completa** de todas las cargas masivas del sistema.

**Vinculación con Proyecto**:

**Profesor**:
1. Carga archivo PDF de apuntes → MongoDB
2. Se registra en `cargas`:
   - `nombre_archivo`: "apuntes_semana_1.pdf"
   - `id_tipo_carga`: "Carga Masiva Apuntes"
   - `id_estado`: "Completado"

3. IA Service procesa archivo
4. Si hay error → `detalle_error` contiene el stack trace

**Administrador**:
- Visualiza historial completo de cargas
- Detecta errores recurrentes
- Audita acciones de profesores

**Ejemplo (script.sql)**:
```sql
nombre_archivo: 'apuntes_semana_1.pdf'
id_usuario_carga: [UUID de Cecilia Arroyo]
id_estado: [UUID de "Completado"]
id_tipo_carga: [UUID de "Carga Masiva Apuntes"]
```

---

## 🔗 Relaciones Clave del Sistema

### 1. **Usuario → Carrera → Asignaturas**
```
usuarios.id_carrera
  → carreras.id
      → asignaturas.id_carrera
```
**Impacto**: Estudiante solo ve asignaturas de SU carrera

---

### 2. **Asignatura → Temas → Preguntas**
```
asignaturas.id
  → temas.id_asignatura
      → preguntas.id_tema
```
**Impacto**: Organización jerárquica del conocimiento

---

### 3. **Usuario → Métricas → Preguntas → Temas** ⭐⭐⭐
```
usuarios.id
  → metricas.id_usuario
      → metricas.id_pregunta
          → preguntas.id_tema
```
**Impacto**: **ANÁLISIS ADAPTATIVO DE IA**

---

### 4. **Usuario → Juegos → Asignatura**
```
usuarios.id
  → juegos.id_usuario
      → juegos.id_asignatura
```
**Impacto**: Historial de sesiones por materia

---

## 🔄 Flujos de Datos Críticos

### Flujo 1: Inicio de Sesión
```
1. Usuario ingresa correo + contraseña
   ↓
2. Auth Service → SELECT FROM usuarios WHERE correo = ?
   ↓
3. Valida password_hash con bcrypt
   ↓
4. UPDATE usuarios SET fecha_ultimo_login = NOW()
   ↓
5. Genera JWT con: id, rol, carrera
   ↓
6. Cliente almacena token
```

---

### Flujo 2: Profesor Carga Contenido Teórico
```
1. Profesor selecciona asignatura asignada
   ↓
2. Sube archivo PDF
   ↓
3. Content Service → MongoDB (contenido bruto)
   ↓
4. Content Service → INSERT INTO cargas (estado: 'Pendiente')
   ↓
5. IA Service procesa archivo con Gemini
   ↓
6. IA Service → INSERT INTO preguntas (preguntas generadas)
   ↓
7. Content Service → UPDATE cargas (estado: 'Completado')
```

---

### Flujo 3: Estudiante Juega (Aprendizaje Adaptativo) ⭐⭐⭐
```
1. Estudiante selecciona asignatura
   ↓
2. BFF → Scoring Service: INSERT INTO juegos (estado: 'En curso')
   ↓
3. BFF → IA Service: Solicita cuestionario adaptativo
   ↓
4. IA Service:
   a. SELECT FROM metricas WHERE id_usuario = ? 
      JOIN preguntas JOIN temas
   b. Agrupa por tema, calcula tasa_error
   c. Identifica temas débiles
   d. Consulta caché
   e. Si no hay caché → Gemini API con contexto
   f. Genera preguntas enfocadas en debilidades
   ↓
5. Cliente recibe preguntas
   ↓
6. Por cada respuesta:
   a. BFF → Scoring Service: INSERT INTO metricas
   b. BFF → Scoring Service: UPDATE juegos (puntaje, intentos)
   ↓
7. Fin de juego:
   a. UPDATE juegos (fecha_fin, estado: 'Completado')
   b. UPDATE puntajes (puntaje += puntaje_sesión)
```

---

### Flujo 4: Profesor Visualiza Estadísticas
```
1. Profesor accede a dashboard
   ↓
2. BFF → Content Service: 
   SELECT asignaturas WHERE profesor_asignado = ?
   ↓
3. BFF → Scoring Service:
   SELECT u.nombre, p.puntaje
   FROM puntajes p
   JOIN usuarios u ON p.id_usuario = u.id
   WHERE p.id_asignatura IN (asignaturas_del_profesor)
   ORDER BY p.puntaje DESC
   ↓
4. Cliente muestra ranking
```

---

## 🔗 Vinculación con Microservicios

### **Auth Service** ↔️ `usuarios`
```
- Autenticación: SELECT WHERE correo = ?
- Validación: Compara password_hash
- Actualización: UPDATE fecha_ultimo_login
- JWT: Incluye id_rol, id_carrera
```

---

### **User Service** ↔️ `usuarios`, `roles`, `carreras`
```
- Gestión de perfiles: CRUD en usuarios
- Asignación de roles: UPDATE id_rol
- Filtrado por carrera: JOIN con carreras
- Estado de cuenta: UPDATE estado
```

---

### **Content Service** ↔️ `asignaturas`, `temas`, `preguntas`, `conceptos`, `cargas`
```
- CRUD de preguntas: INSERT, UPDATE, DELETE en preguntas
- Gestión de temas: INSERT, SELECT en temas
- Carga de contenido: INSERT en cargas
- Consulta por asignatura: WHERE id_asignatura = ?
- Palabras clave: SELECT FROM conceptos WHERE id_tema = ?
```

---

### **IA Service** ↔️ `metricas`, `preguntas`, `temas`
```sql
-- Análisis de debilidades (CRÍTICO)
SELECT 
    t.id AS tema_id,
    t.nombre AS tema,
    COUNT(*) AS total,
    SUM(CASE WHEN m.respuesta_correcta = FALSE THEN 1 ELSE 0 END) AS errores
FROM metricas m
JOIN preguntas p ON m.id_pregunta = p.id
JOIN temas t ON p.id_tema = t.id
WHERE m.id_usuario = ?
GROUP BY t.id, t.nombre
HAVING COUNT(*) >= 5  -- Al menos 5 respuestas
ORDER BY CAST(SUM(...) AS FLOAT) / COUNT(*) DESC;
```

---

### **Scoring Service** ↔️ `juegos`, `puntajes`, `metricas`
```
- Crear sesión: INSERT INTO juegos
- Registrar respuesta: INSERT INTO metricas
- Actualizar puntaje sesión: UPDATE juegos
- Actualizar puntaje total: UPDATE puntajes
- Finalizar juego: UPDATE juegos (fecha_fin, estado)
```

---

## 📊 Queries Críticos del Sistema

### Query 1: Temas Débiles del Estudiante (IA Service)
```sql
WITH analisis_temas AS (
    SELECT 
        t.id,
        t.nombre,
        COUNT(*) AS total_respuestas,
        SUM(CASE WHEN m.respuesta_correcta = FALSE THEN 1 ELSE 0 END) AS total_errores,
        AVG(m.tiempo_respuesta_ms) AS tiempo_promedio
    FROM metricas m
    JOIN preguntas p ON m.id_pregunta = p.id
    JOIN temas t ON p.id_tema = t.id
    WHERE m.id_usuario = :usuario_id
      AND p.id_asignatura = :asignatura_id
    GROUP BY t.id, t.nombre
    HAVING COUNT(*) >= 3  -- Al menos 3 respuestas
)
SELECT 
    id,
    nombre,
    total_respuestas,
    total_errores,
    ROUND(CAST(total_errores AS NUMERIC) / total_respuestas * 100, 2) AS porcentaje_error,
    CASE 
        WHEN CAST(total_errores AS NUMERIC) / total_respuestas >= 0.7 THEN 'CRITICO'
        WHEN CAST(total_errores AS NUMERIC) / total_respuestas >= 0.5 THEN 'ALTO'
        WHEN CAST(total_errores AS NUMERIC) / total_respuestas >= 0.3 THEN 'MEDIO'
        ELSE 'BAJO'
    END AS nivel_prioridad
FROM analisis_temas
ORDER BY CAST(total_errores AS NUMERIC) / total_respuestas DESC;
```

---

### Query 2: Ranking por Asignatura (Scoring Service)
```sql
SELECT 
    ROW_NUMBER() OVER (ORDER BY p.puntaje DESC) AS posicion,
    u.nombre || ' ' || u.apellido AS estudiante,
    p.puntaje,
    u.correo
FROM puntajes p
JOIN usuarios u ON p.id_usuario = u.id
WHERE p.id_asignatura = :asignatura_id
  AND u.id_rol = (SELECT id FROM roles WHERE nombre = 'alumno')
ORDER BY p.puntaje DESC
LIMIT 10;
```

---

### Query 3: Historial de Juegos del Estudiante (Scoring Service)
```sql
SELECT 
    j.id,
    a.nombre AS asignatura,
    j.nombre_juego,
    j.fecha_inicio,
    j.fecha_fin,
    j.puntaje,
    j.estado_partida,
    EXTRACT(EPOCH FROM (j.fecha_fin - j.fecha_inicio)) / 60 AS duracion_minutos
FROM juegos j
JOIN asignaturas a ON j.id_asignatura = a.id
WHERE j.id_usuario = :usuario_id
ORDER BY j.fecha_inicio DESC
LIMIT 20;
```

---

### Query 4: Progreso del Estudiante por Tema (Content Service)
```sql
SELECT 
    t.nombre AS tema,
    COUNT(DISTINCT m.id_pregunta) AS preguntas_respondidas,
    SUM(CASE WHEN m.respuesta_correcta = TRUE THEN 1 ELSE 0 END) AS aciertos,
    SUM(CASE WHEN m.respuesta_correcta = FALSE THEN 1 ELSE 0 END) AS errores,
    ROUND(
        CAST(SUM(CASE WHEN m.respuesta_correcta = TRUE THEN 1 ELSE 0 END) AS NUMERIC) / 
        COUNT(*) * 100, 2
    ) AS porcentaje_acierto
FROM metricas m
JOIN preguntas p ON m.id_pregunta = p.id
JOIN temas t ON p.id_tema = t.id
WHERE m.id_usuario = :usuario_id
  AND p.id_asignatura = :asignatura_id
GROUP BY t.id, t.nombre
ORDER BY porcentaje_acierto ASC;
```

---

### Query 5: Asignaturas del Profesor (Content Service)
```sql
-- Nota: Requiere tabla de asignación profesor_asignatura (no existe en script.sql)
-- Este es un ejemplo de lógica de negocio que debe implementarse

-- Opción 1: Tabla intermedia (recomendado)
CREATE TABLE profesor_asignatura (
    id_profesor UUID NOT NULL,
    id_asignatura UUID NOT NULL,
    fecha_asignacion DATE DEFAULT CURRENT_DATE,
    PRIMARY KEY (id_profesor, id_asignatura),
    FOREIGN KEY (id_profesor) REFERENCES usuarios(id),
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id)
);

-- Query con tabla intermedia
SELECT a.id, a.nombre
FROM asignaturas a
JOIN profesor_asignatura pa ON a.id = pa.id_asignatura
WHERE pa.id_profesor = :profesor_id;
```

---

## 🎯 Datos de Ejemplo (script.sql)

El script inserta datos iniciales con una **transacción CTE compleja**:

```sql
WITH ins_pais AS (
    INSERT INTO paises (nombre) VALUES ('Chile') RETURNING id
),
ins_region AS (
    INSERT INTO regiones (nombre, id_pais)
        SELECT 'Metropolitana', id FROM ins_pais RETURNING id
),
-- ... más CTEs encadenados
```

### Datos Insertados:
```
País: Chile
  └─ Región: Metropolitana
      └─ Comuna: Cerrillos
          └─ Institución: DUOC UC Sede Plaza Oeste
              └─ Carrera: ing-informatica
                  └─ Asignatura: Algoritmos y Programación
                      └─ Tema: Conceptos Básicos
                          ├─ Pregunta: "¿Qué es una variable?"
                          └─ Concepto: "variable"

Semestre: Primer Semestre 2026

Roles: admin, profesor, alumno

Usuario: Cecilia Arroyo (admin)
  - RUT: 12345678-9
  - Correo: cecilia.arroyo@duoc.cl
  - Password: [hash bcrypt]

Estado Carga: Completado
Tipo Carga: Carga Masiva Apuntes
Carga: apuntes_semana_1.pdf (Completado)

Juego: 150.00 puntos
Puntaje Acumulado: 150.00
Métrica: Respuesta correcta en 3500ms
```

---

## ⚠️ Consideraciones Importantes

### 1. **Tabla Faltante: `profesor_asignatura`**
El script actual NO incluye la relación entre profesores y asignaturas. **Debe implementarse**:

```sql
CREATE TABLE profesor_asignatura (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_profesor UUID NOT NULL,
    id_asignatura UUID NOT NULL,
    fecha_asignacion DATE DEFAULT CURRENT_DATE,
    UNIQUE (id_profesor, id_asignatura),
    FOREIGN KEY (id_profesor) REFERENCES usuarios(id),
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id)
);
```

---

### 2. **Tabla Faltante: `ranking`**
El informe menciona una tabla `ranking` optimizada, pero no está en el script:

```sql
CREATE TABLE ranking (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_usuario UUID NOT NULL,
    id_asignatura UUID NOT NULL,
    puntaje_total NUMERIC(10, 2) NOT NULL,
    posicion INT NOT NULL,
    fecha_calculo DATE DEFAULT CURRENT_DATE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id),
    UNIQUE (id_usuario, id_asignatura)
);

-- Índice para optimizar consultas
CREATE INDEX idx_ranking_asignatura_posicion 
ON ranking(id_asignatura, posicion);
```

---

### 3. **Índices para Performance**
El script NO incluye índices. **Recomendados**:

```sql
-- Consultas de IA Service
CREATE INDEX idx_metricas_usuario_fecha 
ON metricas(id_usuario, fecha_hora DESC);

CREATE INDEX idx_metricas_pregunta 
ON metricas(id_pregunta);

-- Consultas de Content Service
CREATE INDEX idx_preguntas_tema 
ON preguntas(id_tema);

CREATE INDEX idx_preguntas_asignatura 
ON preguntas(id_asignatura);

-- Consultas de Scoring Service
CREATE INDEX idx_juegos_usuario_fecha 
ON juegos(id_usuario, fecha_inicio DESC);

CREATE INDEX idx_puntajes_asignatura 
ON puntajes(id_asignatura, puntaje DESC);
```

---

### 4. **Seguridad: Row-Level Security (RLS)**
Para PostgreSQL en producción, considerar **políticas RLS**:

```sql
-- Profesores solo ven sus asignaturas
ALTER TABLE asignaturas ENABLE ROW LEVEL SECURITY;

CREATE POLICY profesor_asignaturas ON asignaturas
FOR SELECT
USING (
    id IN (
        SELECT id_asignatura 
        FROM profesor_asignatura 
        WHERE id_profesor = current_setting('app.current_user_id')::UUID
    )
);
```

---

## 🚀 Próximos Pasos de Implementación

### Sprint 3 (Actual) - IA Service
1. **Implementar Query de Análisis de Temas** (Query 1)
2. **Integrar con Gemini API** usando contexto de temas débiles
3. **Sistema de Caché** en Redis para preguntas generadas
4. **Pruebas Unitarias** de análisis de métricas

### Sprint 4 - Content Service
1. **Crear tabla `profesor_asignatura`**
2. **Implementar CRUD de preguntas** con validación de permisos
3. **Endpoint de carga masiva** que inserta en `cargas`
4. **Integración con MongoDB** para contenido bruto

### Sprint 5 - Scoring Service
1. **Implementar registro de métricas** (INSERT en cada respuesta)
2. **Cálculo de puntajes** (UPDATE en `juegos` y `puntajes`)
3. **Generación de rankings** (Query 2)
4. **Crear tabla `ranking`** con job nocturno de actualización

---

## 📚 Conclusión

La estructura de la base de datos de Brain Boost está **bien diseñada** para soportar:
- ✅ Gamificación con historial completo
- ✅ Aprendizaje adaptativo vía tabla `metricas`
- ✅ Organización jerárquica del conocimiento (país → institución → carrera → asignatura → tema → pregunta)
- ✅ Multi-rol con seguridad
- ✅ Auditoría de cargas masivas

**Áreas de Mejora**:
- ⚠️ Agregar tabla `profesor_asignatura`
- ⚠️ Crear tabla `ranking` optimizada
- ⚠️ Implementar índices para performance
- ⚠️ Considerar RLS para seguridad en producción

**La tabla `metricas` es el CORAZÓN del sistema adaptativo** y debe ser la prioridad en la implementación del IA Service.

---

*Documento generado analizando script.sql y vinculado con GUIA_DESARROLLO_CAPSTONE.md*
*Última actualización: Noviembre 2025*
