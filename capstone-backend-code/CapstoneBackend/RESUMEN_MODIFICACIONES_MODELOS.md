# 📋 Resumen de Modificaciones - Modelos y Repositorios

**Fecha**: 5 de Noviembre, 2025  
**Branch**: refactor/optimizacion-endpoints  
**Objetivo**: Alinear modelos JPA con el schema simplificado de `script.sql` e implementar nuevos modelos para juegos.

---

## ✅ MODELOS CREADOS (5 nuevos - ALCANCE DEL PROYECTO)

### 1. **Concept.kt** - Conceptos extraídos por Gemini AI
```kotlin
@Entity
@Table(name = "conceptos")
class Concept(
    var word: String,              // palabra_concepto
    var hint: String?,             // hint generado por IA
    var topic: Topic,              // id_tema
    var createdAt: LocalDateTime   // fecha_creacion
)
```
**Cambios respecto a schema anterior**:
- ❌ Eliminado: `dificultad` (innecesario - todo viene de IA)
- ❌ Eliminado: `extraido_por_ia` (redundante - siempre TRUE)

---

### 2. **Game.kt** - Partidas de juegos
```kotlin
@Entity
@Table(name = "juegos")
class Game(
    var user: User,
    var subject: Subject,
    var gameName: String?,          // nombre_juego (Hangman, CrissCross, etc.)
    var attemptsRemaining: Int?,    // intentos_restantes
    var gameStatus: String,         // estado_partida
    var startDate: LocalDateTime,   // fecha_inicio
    var endDate: LocalDateTime?,    // fecha_fin
    var score: BigDecimal?          // puntaje
)
```

---

### 3. **Metric.kt** - Métricas generales de preguntas
```kotlin
@Entity
@Table(name = "metricas")
class Metric(
    var user: User,
    var question: Question,
    var correctAnswer: Boolean,
    var responseTimeMs: Int?,
    var timestamp: LocalDateTime
)
```

---

### 4. **HangmanMetric.kt** - Métricas granulares del juego Hangman
```kotlin
@Entity
@Table(name = "metricas_juego_hangman")
class HangmanMetric(
    var game: Game,
    var user: User,
    var concept: Concept,
    var attemptedLetter: Char,      // letra_intentada
    var isCorrect: Boolean,         // es_correcta
    var letterPosition: Int?,       // posicion_letra (0-indexed)
    var responseTimeMs: Int?,
    var timestamp: LocalDateTime
)
```
**Propósito**: Rastrear cada intento de letra en el juego Hangman.

---

### 5. **HangmanResult.kt** - Resultados finales por palabra en Hangman
```kotlin
@Entity
@Table(name = "resultados_juego_hangman")
class HangmanResult(
    var game: Game,
    var concept: Concept,
    var guessed: Boolean,           // adivinado (completó la palabra?)
    var attemptsUsed: Int,          // intentos_usados
    var totalTimeMs: Int?,
    var scoreObtained: BigDecimal?,
    var livesRemaining: Int?,       // vidas_restantes
    var timestamp: LocalDateTime
)
```
**Constraint único**: `UNIQUE(id_juego, id_concepto)` - Solo un resultado por concepto por partida.

---

## ❌ MODELOS ELIMINADOS (Fuera del Alcance)

Se eliminaron los siguientes modelos porque **NO están en el alcance actual del proyecto**:

1. ❌ **Load.kt** - Auditoría de cargas masivas
2. ❌ **LoadState.kt** - Estados de carga masiva
3. ❌ **LoadType.kt** - Tipos de carga masiva
4. ❌ **ProcessedFile.kt** - Auditoría de PDFs procesados con IA

**Razón**: El proyecto actual se enfoca únicamente en la **gamificación** (juegos educativos), no en cargas masivas de archivos ni procesamiento batch de PDFs.

---

## 🔧 MODELOS MODIFICADOS (1)

### **Score.kt** - Puntajes por usuario/asignatura
**ANTES**:
```kotlin
class Score (
    val id: UUID = UUID.randomUUID(),
    val name: String = "",           // ❌ Campo incorrecto
    val user: User,
    val subject: Subject,            // ❌ Nombre de columna incorrecto
)
```

**DESPUÉS**:
```kotlin
class Score (
    var user: User,
    var subject: Subject,            // ✅ Ahora mapea a id_asignatura
    var score: BigDecimal,           // ✅ Campo puntaje agregado
    var assignmentDate: LocalDate    // ✅ fecha_asignacion
) {
    @Id
    @UuidGenerator
    var id: UUID? = null             // ✅ UUID generado por Hibernate
}
```

**Cambios**:
- ❌ Eliminado: campo `name` (no existe en schema)
- ✅ Agregado: campo `score: BigDecimal` (mapea a `puntaje`)
- ✅ Agregado: campo `assignmentDate: LocalDate` (mapea a `fecha_asignacion`)
- ✅ Corregido: `id_materia` → `id_asignatura`

---

## 🗂️ REPOSITORIOS CREADOS (5 nuevos - ALCANCE DEL PROYECTO)

### 1. **ConceptRepository.kt**
```kotlin
interface ConceptRepository : JpaRepository<Concept, UUID> {
    fun findByTopic(topic: Topic): List<Concept>
    fun findByTopicId(topicId: UUID): List<Concept>
    fun findByWordContainingIgnoreCase(word: String): List<Concept>
}
```

### 2. **GameRepository.kt**
```kotlin
interface GameRepository : JpaRepository<Game, UUID> {
    fun findByUser(user: User): List<Game>
    fun findByUserId(userId: UUID): List<Game>
    fun findBySubject(subject: Subject): List<Game>
    fun findByGameName(gameName: String): List<Game>
    fun findByGameStatus(gameStatus: String): List<Game>
    
    @Query("SELECT g FROM Game g WHERE g.user.id = :userId AND g.gameStatus = :status")
    fun findByUserIdAndStatus(userId: UUID, status: String): List<Game>
}
```

### 3. **MetricRepository.kt**
```kotlin
interface MetricRepository : JpaRepository<Metric, UUID> {
    fun findByUser(user: User): List<Metric>
    fun findByUserId(userId: UUID): List<Metric>
    fun findByQuestion(question: Question): List<Metric>
    fun findByCorrectAnswer(correctAnswer: Boolean): List<Metric>
}
```

### 4. **HangmanMetricRepository.kt**
```kotlin
interface HangmanMetricRepository : JpaRepository<HangmanMetric, UUID> {
    fun findByGame(game: Game): List<HangmanMetric>
    fun findByGameId(gameId: UUID): List<HangmanMetric>
    fun findByConcept(concept: Concept): List<HangmanMetric>
    fun findByGameAndConcept(game: Game, concept: Concept): List<HangmanMetric>
    fun findByIsCorrect(isCorrect: Boolean): List<HangmanMetric>
}
```

### 5. **HangmanResultRepository.kt**
```kotlin
interface HangmanResultRepository : JpaRepository<HangmanResult, UUID> {
    fun findByGame(game: Game): List<HangmanResult>
    fun findByGameId(gameId: UUID): List<HangmanResult>
    fun findByConcept(concept: Concept): List<HangmanResult>
    fun findByGuessed(guessed: Boolean): List<HangmanResult>
    fun existsByGameAndConcept(game: Game, concept: Concept): Boolean
}
```

---

## ❌ REPOSITORIOS ELIMINADOS (Fuera del Alcance)

Se eliminaron los siguientes repositorios:

1. ❌ **LoadRepository.kt**
2. ❌ **LoadStateRepository.kt**
3. ❌ **LoadTypeRepository.kt**
4. ❌ **ProcessedFileRepository.kt**

---

## 📦 DTOs CREADOS (3 archivos - ALCANCE DEL PROYECTO)

### 1. **ConceptDto.kt**
- `ConceptCreateDto` - Crear concepto
- `ConceptResponseDto` - Respuesta de concepto

### 2. **GameDto.kt**
- `GameStartDto` - Iniciar juego
- `GameResponseDto` - Estado del juego
- `GameEndDto` - Finalizar juego

### 3. **HangmanDto.kt**
- `HangmanGameStartDto` - Iniciar partida Hangman
- `HangmanGameStateDto` - Estado actual del juego
- `HangmanConceptDto` - Concepto en Hangman
- `HangmanAttemptDto` - Intentar una letra
- `HangmanAttemptResponseDto` - Respuesta del intento
- `HangmanGameResultDto` - Resultado final
- `HangmanConceptResultDto` - Resultado por concepto

---

## ❌ DTOs ELIMINADOS (Fuera del Alcance)

Se eliminó el siguiente archivo DTO:

1. ❌ **ProcessedFileDto.kt** - DTOs para procesamiento de PDFs con Gemini AI

**Razón**: No se implementará procesamiento de PDFs en esta fase del proyecto.

---

## 🏗️ PATRÓN ARQUITECTÓNICO IMPLEMENTADO

### **Patrón Table-Per-Game (Escalable)**

Para cada juego futuro, seguir este patrón:

1. **Tabla de métricas granulares**: `metricas_juego_{nombre}`
   - Ejemplo: `metricas_juego_hangman`
   - Ejemplo futuro: `metricas_juego_crisscross`

2. **Tabla de resultados finales**: `resultados_juego_{nombre}`
   - Ejemplo: `resultados_juego_hangman`
   - Ejemplo futuro: `resultados_juego_crisscross`

3. **Tablas compartidas**:
   - `conceptos` - Compartida por todos los juegos
   - `juegos` - Tabla genérica de partidas

---

## 📊 ESTADÍSTICAS FINALES

- **Modelos nuevos**: 5 (en alcance del proyecto)
- **Modelos eliminados**: 4 (fuera de alcance)
- **Modelos modificados**: 1
- **Repositorios nuevos**: 5 (en alcance)
- **Repositorios eliminados**: 4 (fuera de alcance)
- **DTOs nuevos**: 13 clases en 3 archivos (en alcance)
- **DTOs eliminados**: 1 archivo (fuera de alcance)
- **Total de archivos en alcance**: 13

---

## 🗑️ LIMPIEZA REALIZADA

### **Tablas eliminadas del script.sql**:
1. ❌ `estados_carga`
2. ❌ `tipos_carga`
3. ❌ `cargas`
4. ❌ `archivos_procesados`

### **Índices eliminados**:
- ❌ `idx_archivos_asignatura`
- ❌ `idx_archivos_estado`
- ❌ `idx_archivos_fecha`

### **Comentarios eliminados**:
- ❌ `COMMENT ON TABLE archivos_procesados`

### **Inserciones de datos eliminadas**:
- ❌ `ins_estado_carga`
- ❌ `ins_tipo_carga`
- ❌ `ins_carga`

---

### 1. **ConceptRepository.kt**
```kotlin
interface ConceptRepository : JpaRepository<Concept, UUID> {
    fun findByTopic(topic: Topic): List<Concept>
    fun findByTopicId(topicId: UUID): List<Concept>
    fun findByWordContainingIgnoreCase(word: String): List<Concept>
}
```

### 2. **GameRepository.kt**
```kotlin
interface GameRepository : JpaRepository<Game, UUID> {
    fun findByUser(user: User): List<Game>
    fun findByUserId(userId: UUID): List<Game>
    fun findBySubject(subject: Subject): List<Game>
    fun findByGameName(gameName: String): List<Game>
    fun findByGameStatus(gameStatus: String): List<Game>
    
    @Query("SELECT g FROM Game g WHERE g.user.id = :userId AND g.gameStatus = :status")
    fun findByUserIdAndStatus(userId: UUID, status: String): List<Game>
}
```

### 3. **MetricRepository.kt**
```kotlin
interface MetricRepository : JpaRepository<Metric, UUID> {
    fun findByUser(user: User): List<Metric>
    fun findByUserId(userId: UUID): List<Metric>
    fun findByQuestion(question: Question): List<Metric>
    fun findByCorrectAnswer(correctAnswer: Boolean): List<Metric>
}
```

### 4. **ProcessedFileRepository.kt**
```kotlin
interface ProcessedFileRepository : JpaRepository<ProcessedFile, UUID> {
    fun findByStatus(status: String): List<ProcessedFile>
    fun findBySubject(subject: Subject): List<ProcessedFile>
    fun findByUploadedBy(user: User): List<ProcessedFile>
    
    @Query("SELECT p FROM ProcessedFile p WHERE p.status = :status ORDER BY p.uploadDate ASC")
    fun findPendingFilesOrderByDate(status: String = ProcessedFile.STATUS_PENDING): List<ProcessedFile>
    
    fun existsByMongoDbPath(mongoDbPath: String): Boolean
}
```

### 5. **HangmanMetricRepository.kt**
```kotlin
interface HangmanMetricRepository : JpaRepository<HangmanMetric, UUID> {
    fun findByGame(game: Game): List<HangmanMetric>
    fun findByGameId(gameId: UUID): List<HangmanMetric>
    fun findByConcept(concept: Concept): List<HangmanMetric>
    fun findByGameAndConcept(game: Game, concept: Concept): List<HangmanMetric>
    fun findByIsCorrect(isCorrect: Boolean): List<HangmanMetric>
}
```

### 6. **HangmanResultRepository.kt**
```kotlin
interface HangmanResultRepository : JpaRepository<HangmanResult, UUID> {
    fun findByGame(game: Game): List<HangmanResult>
    fun findByGameId(gameId: UUID): List<HangmanResult>
    fun findByConcept(concept: Concept): List<HangmanResult>
    fun findByGuessed(guessed: Boolean): List<HangmanResult>
    fun existsByGameAndConcept(game: Game, concept: Concept): Boolean
}
```

### 7. **LoadStateRepository.kt**
```kotlin
interface LoadStateRepository : JpaRepository<LoadState, UUID> {
    fun findByStateName(stateName: String): LoadState?
}
```

### 8. **LoadTypeRepository.kt**
```kotlin
interface LoadTypeRepository : JpaRepository<LoadType, UUID> {
    fun findByTypeName(typeName: String): LoadType?
}
```

### 9. **LoadRepository.kt**
```kotlin
interface LoadRepository : JpaRepository<Load, UUID> {
    fun findByUploadedBy(user: User): List<Load>
    fun findByState(state: LoadState): List<Load>
    fun findByFileName(fileName: String): List<Load>
}
```

---

## 📦 DTOs CREADOS (4 archivos nuevos)

### 1. **ConceptDto.kt**
- `ConceptCreateDto` - Crear concepto
- `ConceptResponseDto` - Respuesta de concepto

### 2. **GameDto.kt**
- `GameStartDto` - Iniciar juego
- `GameResponseDto` - Estado del juego
- `GameEndDto` - Finalizar juego

### 3. **ProcessedFileDto.kt**
- `ProcessPdfRequestDto` - Solicitar procesamiento PDF
- `ProcessedFileResponseDto` - Respuesta de archivo procesado
- `GeminiExtractionResultDto` - Resultado de Gemini AI
- `ConceptoExtraidoDto` - Concepto extraído por IA
- `PreguntaGeneradaDto` - Pregunta generada por IA

### 4. **HangmanDto.kt**
- `HangmanGameStartDto` - Iniciar partida Hangman
- `HangmanGameStateDto` - Estado actual del juego
- `HangmanConceptDto` - Concepto en Hangman
- `HangmanAttemptDto` - Intentar una letra
- `HangmanAttemptResponseDto` - Respuesta del intento
- `HangmanGameResultDto` - Resultado final
- `HangmanConceptResultDto` - Resultado por concepto

---

## 🎯 PATRÓN ARQUITECTÓNICO IMPLEMENTADO

### **Patrón Table-Per-Game (Escalable)**

Para cada juego futuro, seguir este patrón:

1. **Tabla de métricas granulares**: `metricas_juego_{nombre}`
   - Ejemplo: `metricas_juego_hangman`
   - Ejemplo futuro: `metricas_juego_crisscross`

2. **Tabla de resultados finales**: `resultados_juego_{nombre}`
   - Ejemplo: `resultados_juego_hangman`
   - Ejemplo futuro: `resultados_juego_crisscross`

3. **Tablas compartidas**:
   - `conceptos` - Compartida por todos los juegos
   - `archivos_procesados` - Compartida por todos los juegos
   - `juegos` - Tabla genérica de partidas

---

## 📊 ESTADÍSTICAS

- **Modelos nuevos**: 9
- **Modelos modificados**: 1
- **Repositorios nuevos**: 9
- **DTOs nuevos**: 16 clases en 4 archivos
- **Total de archivos creados/modificados**: 23

---

## ✅ VALIDACIÓN

```bash
# Sin errores de compilación
✅ No errors found.
```

---

## 🚀 PRÓXIMOS PASOS

1. **Implementar Services**:
   - `ConceptService` - Gestión de conceptos
   - `GameService` - Gestión de partidas
   - `HangmanService` - Lógica del juego Hangman
   - `ProcessedFileService` - Procesamiento de PDFs con Gemini

2. **Implementar Controllers**:
   - `ConceptController` - Endpoints de conceptos
   - `GameController` - Endpoints de juegos
   - `HangmanController` - Endpoints específicos de Hangman
   - `ProcessedFileController` - Upload y procesamiento de PDFs

3. **Integración con Gemini AI**:
   - Modificar `GeminiAiService` para extraer conceptos
   - Implementar prompts específicos para extracción
   - Agregar validación de respuestas JSON

4. **Testing**:
   - Unit tests para repositorios
   - Integration tests para services
   - E2E tests para controllers

---

## 📝 NOTAS IMPORTANTES

### **Filosofía de Automatización**
> "extraido_por_ia siempre será así, ya que todo esto tiene que estar automatizado"

Todos los conceptos provienen de extracción automática con Gemini AI. Por eso se eliminaron:
- Campo `dificultad` (IA no categoriza dificultad)
- Campo `extraido_por_ia` (redundante - siempre TRUE)

### **Reglas del Juego Hangman**
- **3 vidas TOTALES** por partida (no por palabra)
- **10-12 conceptos** por partida
- **1 punto** por concepto completado
- Sin multiplicadores ni bonificaciones
- Sistema de puntuación simple

---

**Documento generado automáticamente** ✨
