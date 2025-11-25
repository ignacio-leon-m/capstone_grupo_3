# 📚 Análisis de Contenido y Flujos - Brain Boost Backend

**Proyecto**: Brain Boost - Plataforma de aprendizaje adaptativo  
**Fecha de Análisis**: Noviembre 24, 2025  
**Autor**: GitHub Copilot  
**Versión**: 1.0.0

---

## 📋 Tabla de Contenidos

1. [Resumen Ejecutivo](#resumen-ejecutivo)
2. [Arquitectura del Sistema](#arquitectura-del-sistema)
3. [Modelo de Datos de Contenido](#modelo-de-datos-de-contenido)
4. [Flujos de Carga de Contenido](#flujos-de-carga-de-contenido)
5. [Reglas de Negocio Identificadas](#reglas-de-negocio-identificadas)
6. [Estrategia de Recursos Estáticos](#estrategia-de-recursos-estáticos)
7. [Propuesta de Implementación](#propuesta-de-implementación)

---

## 📊 Resumen Ejecutivo

### Descripción del Proyecto

**Brain Boost** es una plataforma educativa adaptativa con gamificación e IA que:

- **Gestiona contenido pedagógico** estructurado jerárquicamente (Asignaturas → Temas → Conceptos/Preguntas)
- **Utiliza IA Generativa** (Google Gemini 2.0 Flash) para extraer conceptos de PDFs y generar contenido dinámico
- **Implementa gamificación** mediante juegos educativos (Hangman/Ahorcado) basados en conceptos
- **Personaliza el aprendizaje** mediante métricas de rendimiento y scoring adaptativo
- **Soporta roles diferenciados**: Administrador, Profesor, Alumno

### Tecnologías Core

- **Backend**: Spring Boot 3.5.5 + Kotlin 1.9.25
- **Base de Datos**: PostgreSQL 17.5 (Neon Serverless)
- **IA**: Google Gemini SDK 1.23.0
- **Procesamiento**: Apache Tika 3.2.3, Apache POI 5.4.1
- **Cache**: Caffeine 3.1.8
- **Deployment**: Render.com + Docker

---

## 🏗️ Arquitectura del Sistema

### Arquitectura General

```
┌─────────────────────────────────────────────────────────────────┐
│                    BRAIN BOOST BACKEND                           │
│                  (Monolito Modular - Kotlin)                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐                │
│  │   Auth     │  │   User     │  │  Content   │                │
│  │  Service   │  │  Service   │  │  Service   │                │
│  └────────────┘  └────────────┘  └────────────┘                │
│                                                                  │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐                │
│  │    AI      │  │   Game     │  │  Scoring   │                │
│  │  Service   │  │  Service   │  │  Service   │                │
│  └────────────┘  └────────────┘  └────────────┘                │
│                                                                  │
├─────────────────────────────────────────────────────────────────┤
│                    PostgreSQL 17.5                               │
│           (Neon Serverless - US West 2)                          │
└─────────────────────────────────────────────────────────────────┘
          │                              │
          │                              │
          ▼                              ▼
    Google Gemini API            Static Resources
    (IA Generativa)              (Frontend + Assets)
```

### Módulos Relevantes para Contenido

```kotlin
org.duocuc.capstonebackend/
├── controller/
│   ├── FileUploadController.kt      // Carga PDFs y Excel
│   ├── SubjectController.kt         // Gestión de asignaturas
│   └── HangmanController.kt         // Juego Ahorcado
├── service/
│   ├── FileUploadService.kt         // Procesa archivos
│   ├── GeminiAiService.kt           // Interacción con Gemini
│   ├── ConceptService.kt            // CRUD de conceptos
│   └── HangmanService.kt            // Lógica del juego
├── model/
│   ├── Subject.kt                   // Entidad Asignatura
│   ├── Topic.kt                     // Entidad Tema
│   ├── Concept.kt                   // Entidad Concepto
│   └── Question.kt                  // Entidad Pregunta
└── repository/
    ├── SubjectRepository.kt
    ├── TopicRepository.kt
    ├── ConceptRepository.kt
    └── QuestionRepository.kt
```

---

## 🗄️ Modelo de Datos de Contenido

### Jerarquía de Contenido Pedagógico

```
Institución (DUOC UC)
    ├── Carrera (Ing. Informática)
    │   ├── Asignatura (Algoritmos y Programación)
    │   │   ├── Tema 1 (Conceptos Básicos)
    │   │   │   ├── Concepto 1 (VARIABLE)
    │   │   │   │   └── Hint: "Espacio en memoria..."
    │   │   │   ├── Concepto 2 (FUNCION)
    │   │   │   └── Concepto 3 (LOOP)
    │   │   │   
    │   │   ├── Tema 2 (Estructuras de Datos)
    │   │   │   ├── Concepto 4 (ARRAY)
    │   │   │   └── Concepto 5 (LISTA)
    │   │   │
    │   │   └── Preguntas (Quiz)
    │   │       ├── Pregunta 1 → Tema 1
    │   │       └── Pregunta 2 → Tema 2
```

### Entidades Core

#### 1. **Subject**

```kotlin
@Entity
@Table(name = "asignaturas")
class Subject(
    @Column(name = "nombre", nullable = false, length = 100)
    var name: String,
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_carrera", nullable = false)
    var degree: Degree
) {
    @Id
    @UuidGenerator
    @Column(name = "id")
    var id: UUID? = null
}
```

**Reglas**:
- Una asignatura pertenece a **una carrera**
- Una asignatura tiene **múltiples temas**
- Una asignatura tiene **múltiples preguntas**

#### 2. **Topic (Tema)**

```kotlin
@Entity
@Table(
    name = "temas",
    uniqueConstraints = [UniqueConstraint(
        name = "uk_tema_nombre_asignatura",
        columnNames = ["nombre","id_asignatura"]
    )]
)
class Topic(
    @Column(name = "nombre", nullable = false, length = 100)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_asignatura", nullable = false)
    var subject: Subject
) {
    @Id
    @UuidGenerator
    @Column(name = "id")
    var id: UUID? = null
}
```

**Reglas**:
- Un tema pertenece a **una asignatura**
- El nombre del tema es **único por asignatura**
- Un tema tiene **múltiples conceptos**
- Un tema tiene **múltiples preguntas**

#### 3. **Concept (Concepto)**

```kotlin
@Entity
@Table(name = "conceptos")
class Concept(
    @Column(name = "palabra_concepto", nullable = false, length = 255)
    var word: String,

    @Column(name = "hint", columnDefinition = "text")
    var hint: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tema", nullable = false)
    var topic: Topic,

    @Column(name = "fecha_creacion")
    var createdAt: LocalDateTime = LocalDateTime.now()
) {
    @Id
    @UuidGenerator
    @Column(name = "id")
    var id: UUID? = null
}
```

**Reglas**:
- Un concepto pertenece a **un tema**
- `word`: Palabra clave educativa (ej: "VARIABLE", "FUNCIÓN")
- `hint`: Pista opcional generada por IA Gemini
- Usado en el **juego Hangman** para adivinar palabras
- `createdAt`: Auditoría de creación

#### 4. **Question (Pregunta)**

```kotlin
@Entity
@Table(name = "preguntas")
class Question(
    @Column(name = "texto", nullable = false, columnDefinition = "text")
    var text: String,

    @Column(name = "respuesta_correcta", nullable = false, columnDefinition = "text")
    var correctAnswer: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_asignatura", nullable = false)
    var subject: Subject,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tema", nullable = false)
    var topic: Topic
) {
    @Id
    @UuidGenerator
    @Column(name = "id")
    var id: UUID? = null
}
```

**Reglas**:
- Una pregunta pertenece a **una asignatura**
- Una pregunta pertenece a **un tema**
- Usado para **quiz/evaluaciones**
- Puede ser generada por IA o cargada manualmente

---

## 🔄 Flujos de Carga de Contenido

### Flujo 1: Carga de Contenido desde PDF con IA

```
┌──────────────┐
│   Profesor   │
│  (Web UI)    │
└──────┬───────┘
       │
       │ 1. Sube PDF educativo
       │ POST /api/files/upload-query-pdf
       │ + prompt personalizado
       ▼
┌───────────────────────────────┐
│  FileUploadController         │
│  - processPdfFileAndQuery()   │
└──────────┬────────────────────┘
           │
           │ 2. Guarda PDF en disco
           │    (uploads/UUID.pdf)
           ▼
┌───────────────────────────────┐
│  FileUploadService            │
│  - savePdf()                  │
└──────────┬────────────────────┘
           │
           │ 3. Extrae texto con Tika
           ▼
┌───────────────────────────────┐
│  PdfTextExtractor             │
│  - safeExtract()              │
└──────────┬────────────────────┘
           │
           │ 4. Envía texto + prompt a IA
           ▼
┌───────────────────────────────┐
│  GeminiAiService              │
│  - query(text, prompt)        │
│  Modelo: gemini-2.0-flash     │
└──────────┬────────────────────┘
           │
           │ 5. IA extrae conceptos/preguntas
           ▼
┌───────────────────────────────┐
│  Response JSON                │
│  {                            │
│    "concepts": [              │
│      "VARIABLE",              │
│      "FUNCIÓN",               │
│      "LOOP"                   │
│    ],                         │
│    "questions": [...]         │
│  }                            │
└──────────┬────────────────────┘
           │
           │ 6. Profesor valida y guarda
           │ POST /api/concepts
           │ POST /api/questions
           ▼
┌───────────────────────────────┐
│  PostgreSQL                   │
│  - temas                      │
│  - conceptos                  │
│  - preguntas                  │
└───────────────────────────────┘
```

### Flujo 2: Carga Masiva de Usuarios (Excel)

```
┌──────────────┐
│   Profesor   │
└──────┬───────┘
       │
       │ 1. Sube Excel (.xlsx)
       │ POST /api/files/upload-excel
       ▼
┌───────────────────────────────┐
│  FileUploadController         │
│  - uploadExcelFile()          │
└──────────┬────────────────────┘
           │
           │ 2. Procesa con Apache POI
           ▼
┌───────────────────────────────┐
│  FileUploadService            │
│  - processExcelFile()         │
│  Lee fila por fila (drop 11)  │
│  Normaliza nombres (titleCase)│
└──────────┬────────────────────┘
           │
           │ 3. Crea DTOs de registro
           ▼
┌───────────────────────────────┐
│  List<RegisterRequestDto>     │
│  - name, lastName, rut        │
│  - email, password, role      │
└──────────┬────────────────────┘
           │
           │ 4. Registra en BD
           │ AuthService.registerStudentFromExcel()
           ▼
┌───────────────────────────────┐
│  PostgreSQL - usuarios        │
│  + usuario_asignatura         │
└───────────────────────────────┘
```

### Flujo 3: Juego Hangman (Uso de Conceptos)

```
┌──────────────┐
│   Alumno     │
└──────┬───────┘
       │
       │ 1. Inicia juego
       │ POST /api/hangman/start
       │ { topicId: UUID }
       ▼
┌───────────────────────────────┐
│  HangmanService               │
│  - startGame()                │
└──────────┬────────────────────┘
           │
           │ 2. Obtiene conceptos del tema
           │ conceptRepository.findByTopicId()
           ▼
┌───────────────────────────────┐
│  PostgreSQL - conceptos       │
│  WHERE id_tema = ?            │
└──────────┬────────────────────┘
           │
           │ 3. Selecciona concepto aleatorio
           ▼
┌───────────────────────────────┐
│  Concept                      │
│  word: "VARIABLE"             │
│  hint: "Espacio en memoria..."│
└──────────┬────────────────────┘
           │
           │ 4. Retorna palabra oculta
           ▼
┌───────────────────────────────┐
│  Response                     │
│  {                            │
│    "word": "________",        │
│    "hint": "Espacio...",      │
│    "attempts": 6              │
│  }                            │
└───────────────────────────────┘
```

---

## 📜 Reglas de Negocio Identificadas

### RN-01: Jerarquía de Contenido

1. **Institución → Carrera → Asignatura → Tema → Concepto/Pregunta**        
2. No se puede crear un **Tema** sin una **Asignatura** válida
3. No se puede crear un **Concepto** sin un **Tema** válido
4. Las **Preguntas** deben estar asociadas tanto a **Asignatura** como a **Tema**

### RN-02: Unicidad de Nombres

1. Un **Tema** debe tener nombre único **por Asignatura** (constraint UK)
2. No hay restricción de unicidad en **Conceptos** (pueden repetirse entre temas)
3. Los **RUT** de usuarios son únicos a nivel sistema

### RN-03: Generación de Contenido con IA

1. El **prompt** para Gemini debe ser específico para cada tipo de contenido:
   - Extracción de conceptos
   - Generación de preguntas
   - Generación de hints
2. El contenido generado por IA debe ser **validado por el profesor** antes de persistir
3. Se debe implementar **cache** (Caffeine) para evitar llamadas repetidas a la API

### RN-04: Gamificación (Hangman)

1. Un juego de Hangman se basa en **conceptos** de un **tema específico**
2. Cada concepto tiene:
   - `word`: La palabra a adivinar (mayúsculas)
   - `hint`: Pista opcional
3. Métricas del juego se guardan en:
   - `metricas_juego_hangman`: Intentos por letra
   - `resultados_juego_hangman`: Resultado por concepto

### RN-05: Auditoría y Trazabilidad

1. Los **Conceptos** tienen `fecha_creacion` para auditoría
2. Los **Usuarios** tienen `fecha_creacion` y `fecha_ultimo_login`
3. Las **Métricas** tienen `fecha_hora` para análisis temporal

### RN-06: Roles y Permisos

| Rol | Permisos de Contenido |
|-----|----------------------|
| **Administrador** | CRUD completo sobre todo contenido |
| **Profesor** | Subir PDFs, crear Temas/Conceptos/Preguntas para sus asignaturas |
| **Alumno** | Solo lectura: ver temas, jugar con conceptos |

### RN-07: Fallback de IA

1. Si Gemini API falla o excede rate limit (15 req/min):
   - El sistema debe retornar contenido **estático pre-cargado**
   - Debe informar al usuario del fallback
2. El cache Caffeine debe tener:
   - Tamaño máximo: 100 entradas
   - TTL: 1 hora

---

## 💾 Estrategia de Contenido Educativo en Base de Datos

### Enfoque Implementado

El contenido educativo (temas, conceptos, preguntas) **se almacena directamente en PostgreSQL**, no en archivos JSON estáticos. Esto proporciona:

1. **Integridad referencial** entre asignaturas, temas, conceptos y preguntas
2. **Consultas eficientes** con índices y JOINs nativos
3. **Transaccionalidad** en operaciones CRUD
4. **Fallback natural** cuando la IA de Gemini falla
5. **Escalabilidad** sin límites de tamaño de archivos

### Migración Flyway V3

**Archivo**: `src/main/resources/db/migration/V3__Insert_educational_content.sql`

Este script inserta contenido educativo inicial:

```sql
-- Estructura de inserción
DO $$
DECLARE
    v_asignatura_id UUID;
    v_tema_basicos_id UUID;
    v_tema_estructuras_id UUID;
    v_tema_ordenamiento_id UUID;
BEGIN
    -- Buscar asignatura existente
    SELECT id INTO v_asignatura_id 
    FROM asignaturas 
    WHERE nombre = 'Algoritmos y Programación';

    -- Insertar 3 temas
    INSERT INTO temas (nombre, id_asignatura) VALUES
    ('Conceptos Básicos de Programación', v_asignatura_id),
    ('Estructuras de Datos', v_asignatura_id),
    ('Algoritmos de Ordenamiento', v_asignatura_id);

    -- Insertar 45 conceptos (15 por tema)
    INSERT INTO conceptos (palabra_concepto, id_tema) VALUES
    ('VARIABLE', v_tema_basicos_id),
    ('FUNCION', v_tema_basicos_id),
    -- ... más conceptos

    -- Insertar 23 preguntas
    INSERT INTO preguntas (texto, respuesta_correcta, id_asignatura, id_tema) VALUES
    ('¿Qué es una variable?', 'Un espacio en memoria...', v_asignatura_id, v_tema_basicos_id);
    -- ... más preguntas
END $$;
```

### Contenido Insertado

| Entidad | Cantidad | Distribución |
|---------|----------|--------------|
| **Temas** | 3 | Conceptos Básicos, Estructuras de Datos, Algoritmos de Ordenamiento |
| **Conceptos** | 45 | 15 por tema (para juego Hangman) |
| **Preguntas** | 23 | 8 + 8 + 7 por tema (para quiz) |

#### Tema 1: Conceptos Básicos de Programación (15 conceptos)
```
VARIABLE, FUNCION, BUCLE, CONDICIONAL, ALGORITMO, CONSTANTE, 
OPERADOR, EXPRESION, ASIGNACION, SINTAXIS, COMENTARIO, 
COMPILADOR, DEPURACION, PARAMETRO, RETORNO
```

#### Tema 2: Estructuras de Datos (15 conceptos)
```
ARRAY, LISTA, PILA, COLA, DICCIONARIO, INDICE, NODO, 
MATRIZ, CONJUNTO, TUPLA, ITERADOR, ARBOL, GRAFO, 
ENLAZADA, ORDENADA
```

#### Tema 3: Algoritmos de Ordenamiento (15 conceptos)
```
ORDENAMIENTO, BURBUJA, SELECCION, INSERCION, QUICKSORT, 
MERGESORT, PIVOTE, COMPLEJIDAD, RECURSION, INTERCAMBIO, 
COMPARACION, ESTABLE, PARTICION, FUSION, ITERACION
```

---

## 🚀 Implementación Realizada

### ✅ Fase 1: Migración Flyway con Contenido Base (COMPLETADO)

**Archivo creado**: `V3__Insert_educational_content.sql`

**Contenido insertado**:
- ✅ 3 temas para "Algoritmos y Programación"
- ✅ 45 conceptos (15 por tema) para juego Hangman
- ✅ 23 preguntas (distribuidas por tema) para quiz

**Ventajas del enfoque SQL**:
1. **Sin código Kotlin adicional**: La migración se ejecuta automáticamente con Flyway
2. **Transaccional**: Todo o nada, garantiza consistencia
3. **Versionado**: Flyway trackea qué migraciones se ejecutaron
4. **Idempotente**: `ON CONFLICT DO NOTHING` previene duplicados
5. **Auditable**: El script SQL es el "source of truth"

### 🔄 Fase 2: Fallback Automático en Servicios (Siguiente Paso)

El `HangmanService` ya utiliza el contenido de la BD como fuente:

```kotlin
// src/main/kotlin/.../service/HangmanService.kt
fun startGame(request: StartGameRequest): StartGameResponse {
    // Busca conceptos directamente desde PostgreSQL
    val availableConcepts = conceptRepository.findByTopicId(request.topicId)
    
    if (availableConcepts.isEmpty()) {
        throw IllegalStateException("No hay conceptos disponibles para este tema")
    }
    
    // Selecciona concepto aleatorio
    val selectedConcept = availableConcepts.random()
    // ...
}
```

**No se necesita fallback artificial**: El contenido YA ESTÁ en la BD gracias a V3.

### 📊 Fase 3: Endpoints Existentes para Gestión de Contenido

Los servicios actuales ya permiten CRUD completo:

```kotlin
// ConceptService - CRUD de conceptos
fun createConcepts(topicId: UUID, concepts: List<ConceptCreateDto>)
fun getConceptsByTopic(topicId: UUID): List<ConceptResponseDto>
fun searchConcepts(query: String): List<ConceptResponseDto>

// Si se necesita, se puede crear un endpoint de carga masiva:
@PostMapping("/api/concepts/batch")
fun batchCreateConcepts(@RequestBody request: BatchConceptRequest)
```

---

## 📊 Métricas de Éxito

| Métrica | Objetivo |
|---------|----------|
| **Contenido Base** | 3 asignaturas, 9 temas, 135 conceptos, 90 preguntas |
| **Cobertura de Fallback** | 100% de temas con contenido estático |
| **Tiempo de Carga** | < 2 segundos para seed completo de 1 asignatura |
| **Validación** | 0 errores en formato JSON |

---

## 🔗 Referencias

- **Informe del Proyecto**: `Informe-Valencia-Leon-Bertero-ultima-version.pdf`
- **Configuración**: `docs/CONFIGURACION.md`
- **Arquitectura**: `docs/DIAGRAMA_DESPLIEGUE.md`
- **Migraciones BD**: `src/main/resources/db/migration/`
- **Repositorio**: [capstone_grupo_3](https://github.com/ignacio-leon-m/capstone_grupo_3)

---

**Nota**: Este documento es un análisis de la arquitectura actual y una propuesta de mejora. La implementación del flujo de carga de recursos estáticos está pendiente y debe ser desarrollada en coordinación con el equipo.
