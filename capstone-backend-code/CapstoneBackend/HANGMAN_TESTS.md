# Pruebas de API Hangman

## Pre-requisitos
Asegúrate de tener datos de prueba en la base de datos:
- Al menos 1 usuario (user_id)
- Al menos 1 subject (subject_id) 
- Al menos 1 topic (topic_id)
- Al menos 10-12 concepts en ese topic

## 1. Iniciar un juego de Hangman

```bash
curl -X POST http://localhost:8080/api/hangman/start \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "subjectId": 1,
    "topicId": 1
  }'
```

**Respuesta esperada:**
```json
{
  "gameId": 1,
  "userId": 1,
  "subjectId": 1,
  "concepts": [
    {
      "conceptId": 1,
      "word": "PALABRA",
      "clue": "Una pista",
      "usedLetters": [],
      "correctGuesses": 0,
      "incorrectAttempts": 0
    },
    ...
  ],
  "currentConceptIndex": 0,
  "livesRemaining": 3,
  "gameStatus": "IN_PROGRESS",
  "score": 0
}
```

## 2. Obtener estado del juego

```bash
curl -X GET http://localhost:8080/api/hangman/games/1
```

## 3. Intentar adivinar una letra

### Intento válido (letra correcta)
```bash
curl -X POST http://localhost:8080/api/hangman/games/1/attempt \
  -H "Content-Type: application/json" \
  -d '{
    "conceptId": 1,
    "letter": "A",
    "responseTimeMs": 1500
  }'
```

**Respuesta esperada:**
```json
{
  "isCorrect": true,
  "positions": [1, 4],
  "livesRemaining": 3,
  "gameOver": false
}
```

### Intento válido (letra incorrecta)
```bash
curl -X POST http://localhost:8080/api/hangman/games/1/attempt \
  -H "Content-Type: application/json" \
  -d '{
    "conceptId": 1,
    "letter": "Z",
    "responseTimeMs": 2000
  }'
```

**Respuesta esperada:**
```json
{
  "isCorrect": false,
  "positions": [],
  "livesRemaining": 2,
  "gameOver": false
}
```

### Intento inválido (carácter no permitido)
```bash
curl -X POST http://localhost:8080/api/hangman/games/1/attempt \
  -H "Content-Type: application/json" \
  -d '{
    "conceptId": 1,
    "letter": "1",
    "responseTimeMs": 500
  }'
```

**Respuesta esperada:**
```json
{
  "isCorrect": false,
  "positions": [],
  "livesRemaining": 1,
  "gameOver": false,
  "message": "Carácter inválido. Solo letras son permitidas."
}
```

### Letra repetida (sin penalización)
```bash
curl -X POST http://localhost:8080/api/hangman/games/1/attempt \
  -H "Content-Type: application/json" \
  -d '{
    "conceptId": 1,
    "letter": "A",
    "responseTimeMs": 800
  }'
```

**Respuesta esperada:**
```json
{
  "isCorrect": true,
  "positions": [1, 4],
  "livesRemaining": 1,
  "gameOver": false,
  "message": "Letra ya utilizada anteriormente"
}
```

## 4. Enviar resultado de un concepto

Este endpoint permite enviar el resultado final de un concepto en el juego Hangman después de que el estudiante haya completado (o no) la palabra.

### Request

**Endpoint:** `POST /api/hangman/games/{gameId}/concepts/submit`

**Path Parameters:**
- `gameId` (UUID): ID del juego activo

**Request Body (HangmanConceptSubmitDto):**
```json
{
  "conceptId": "550e8400-e29b-41d4-a716-446655440000",
  "guessed": true,
  "timeMs": 45000
}
```

**Campos:**
- `conceptId` (UUID, requerido): ID del concepto completado
- `guessed` (boolean, requerido): `true` si adivinó la palabra completa, `false` si no
- `timeMs` (long, requerido): Tiempo total en milisegundos que tardó en este concepto

### Response

**Response Body (HangmanConceptResultDto):**
```json
{
  "conceptId": "550e8400-e29b-41d4-a716-446655440000",
  "word": "ALGORITMO",
  "guessed": true,
  "attemptsUsed": 4,
  "totalTimeMs": 45000,
  "scoreObtained": 1,
  "livesRemaining": 2
}
```

**Campos de respuesta:**
- `conceptId` (UUID): ID del concepto
- `word` (String): La palabra completa del concepto
- `guessed` (boolean): Si adivinó la palabra
- `attemptsUsed` (int): Cantidad de intentos incorrectos realizados
- `totalTimeMs` (long): Tiempo total en milisegundos
- `scoreObtained` (int): Puntaje obtenido (1 punto si adivinó, 0 si no)
- `livesRemaining` (int): Vidas restantes después de completar este concepto

### Ejemplos

#### Ejemplo 1: Concepto adivinado correctamente

**Request:**
```bash
curl -X POST http://localhost:8080/api/hangman/games/a1b2c3d4-e5f6-7890-abcd-ef1234567890/concepts/submit \
  -H "Content-Type: application/json" \
  -d '{
    "conceptId": "550e8400-e29b-41d4-a716-446655440000",
    "guessed": true,
    "timeMs": 45000
  }'
```

**Response (200 OK):**
```json
{
  "conceptId": "550e8400-e29b-41d4-a716-446655440000",
  "word": "ALGORITMO",
  "guessed": true,
  "attemptsUsed": 3,
  "totalTimeMs": 45000,
  "scoreObtained": 1,
  "livesRemaining": 2
}
```

#### Ejemplo 2: Concepto no adivinado (se quedó sin vidas)

**Request:**
```bash
curl -X POST http://localhost:8080/api/hangman/games/a1b2c3d4-e5f6-7890-abcd-ef1234567890/concepts/submit \
  -H "Content-Type: application/json" \
  -d '{
    "conceptId": "660f9511-f3a0-4826-b827-556766551111",
    "guessed": false,
    "timeMs": 62000
  }'
```

**Response (200 OK):**
```json
{
  "conceptId": "660f9511-f3a0-4826-b827-556766551111",
  "word": "RECURSIVIDAD",
  "guessed": false,
  "attemptsUsed": 5,
  "totalTimeMs": 62000,
  "scoreObtained": 0,
  "livesRemaining": 0
}
```

#### Ejemplo 3: Concepto rápido con pocas fallas

**Request:**
```bash
curl -X POST http://localhost:8080/api/hangman/games/a1b2c3d4-e5f6-7890-abcd-ef1234567890/concepts/submit \
  -H "Content-Type: application/json" \
  -d '{
    "conceptId": "770g0622-g4b1-5937-c938-667877662222",
    "guessed": true,
    "timeMs": 18500
  }'
```

**Response (200 OK):**
```json
{
  "conceptId": "770g0622-g4b1-5937-c938-667877662222",
  "word": "VARIABLE",
  "guessed": true,
  "attemptsUsed": 1,
  "totalTimeMs": 18500,
  "scoreObtained": 1,
  "livesRemaining": 3
}
```

### Validaciones y reglas de negocio

1. **Juego válido**: El juego debe existir y estar en estado "activo"
2. **Concepto único**: No puede enviar resultado para el mismo concepto más de una vez en el mismo juego
3. **Concepto existente**: El concepto debe existir en la base de datos
4. **Cálculo de intentos**: El sistema cuenta automáticamente los intentos usados basándose en las métricas registradas durante el juego
5. **Puntaje**: 
   - 1 punto si `guessed = true`
   - 0 puntos si `guessed = false`

### Errores posibles

**404 Not Found - Juego no encontrado:**
```json
{
  "error": "Juego no encontrado con ID: a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

**404 Not Found - Concepto no encontrado:**
```json
{
  "error": "Concepto no encontrado con ID: 550e8400-e29b-41d4-a716-446655440000"
}
```

**400 Bad Request - Resultado duplicado:**
```json
{
  "error": "Ya existe un resultado para este concepto en este juego"
}
```

**400 Bad Request - Juego no activo:**
```json
{
  "error": "El juego no está activo"
}
```

### Flujo de uso

1. El estudiante intenta letras usando `/games/{gameId}/attempt`
2. Cuando completa la palabra o se queda sin vidas para ese concepto:
   - La aplicación móvil calcula el tiempo total (`timeMs`)
   - Determina si adivinó la palabra (`guessed`)
   - Envía el resultado usando este endpoint
3. El backend registra el resultado en `resultados_juego_hangman`
4. Retorna el resumen del concepto con puntaje y estadísticas

## 5. Finalizar el juego

```bash
curl -X POST http://localhost:8080/api/hangman/games/1/end
```

**Respuesta esperada:**
```json
{
  "gameId": 1,
  "userId": 1,
  "subjectId": 1,
  "finalScore": 8.5,
  "conceptsCompleted": 8,
  "totalConcepts": 12,
  "timeSpent": "05:23",
  "completedAt": "2025-11-05 22:30:15",
  "metrics": {
    "avgTimePerConcept": 40250,
    "accuracy": 0.85,
    "conceptsWithErrors": 2
  }
}
```

## Flujo completo de prueba

```bash
# 1. Iniciar juego
GAME_ID=$(curl -s -X POST http://localhost:8080/api/hangman/start \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"subjectId":1,"topicId":1}' | jq -r '.gameId')

echo "Game ID: $GAME_ID"

# 2. Ver estado
curl -s http://localhost:8080/api/hangman/games/$GAME_ID | jq

# 3. Intentar letras
curl -s -X POST http://localhost:8080/api/hangman/games/$GAME_ID/attempt \
  -H "Content-Type: application/json" \
  -d '{"conceptId":1,"letter":"A","responseTimeMs":1500}' | jq

# 4. Enviar resultado del concepto
curl -s -X POST http://localhost:8080/api/hangman/games/$GAME_ID/concepts/submit \
  -H "Content-Type: application/json" \
  -d '{"conceptId":1,"responseTimeMs":15000,"attemptsRemaining":2}' | jq

# 5. Finalizar juego
curl -s -X POST http://localhost:8080/api/hangman/games/$GAME_ID/end | jq
```

## Casos de prueba de validación

### ✅ Validación: Solo letras permitidas
- Entrada: "A", "B", "Z" → ✅ Válido
- Entrada: "1", "@", " " → ❌ Inválido (penaliza vida)

### ✅ Validación: Letras repetidas sin penalización
- Primera "A" → procesa normalmente
- Segunda "A" → retorna mismo resultado, NO penaliza

### ✅ Validación: Total de 3 vidas
- 3 errores → livesRemaining = 0, gameOver = true

### ✅ Validación: 10-12 conceptos aleatorios
- Verificar que `concepts.length` esté entre 10 y 12

### ✅ Validación: Puntuación
- Concepto completado con 3 vidas → 1.0 punto
- Concepto completado con 2 vidas → ~0.8 puntos
- Concepto completado con 1 vida → ~0.6 puntos
