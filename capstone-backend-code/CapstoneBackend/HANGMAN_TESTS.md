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

```bash
curl -X POST http://localhost:8080/api/hangman/games/1/concepts/submit \
  -H "Content-Type: application/json" \
  -d '{
    "conceptId": 1,
    "responseTimeMs": 15000,
    "attemptsRemaining": 1
  }'
```

**Respuesta esperada:**
```json
{
  "conceptId": 1,
  "scoreObtained": 1.0,
  "message": "Concepto completado exitosamente"
}
```

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
