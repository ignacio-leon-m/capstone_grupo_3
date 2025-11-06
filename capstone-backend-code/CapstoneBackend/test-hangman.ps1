# Script de prueba para endpoints de Hangman
# Asegúrate de que el servidor esté corriendo en http://localhost:8080

Write-Host "=== PRUEBA DE ENDPOINTS HANGMAN ===" -ForegroundColor Cyan
Write-Host ""

# 1. Verificar que el servidor esté activo
Write-Host "1. Verificando servidor..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/subjects" -Method Get
    Write-Host "✓ Servidor activo" -ForegroundColor Green
    Write-Host "Subjects encontrados: $($response.Count)" -ForegroundColor Gray
} catch {
    Write-Host "✗ Servidor no responde" -ForegroundColor Red
    Write-Host "Error: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 2. Iniciar un juego de Hangman
Write-Host "2. Iniciando juego de Hangman..." -ForegroundColor Yellow
$startGameBody = @{
    userId = 1
    subjectId = 1
    topicId = 1
} | ConvertTo-Json

try {
    $game = Invoke-RestMethod -Uri "http://localhost:8080/api/hangman/start" `
        -Method Post `
        -ContentType "application/json" `
        -Body $startGameBody
    
    Write-Host "✓ Juego iniciado con ID: $($game.gameId)" -ForegroundColor Green
    Write-Host "  - Conceptos: $($game.concepts.Count)" -ForegroundColor Gray
    Write-Host "  - Vidas: $($game.livesRemaining)" -ForegroundColor Gray
    Write-Host "  - Estado: $($game.gameStatus)" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Primer concepto:" -ForegroundColor Gray
    Write-Host "  Palabra: $($game.concepts[0].word)" -ForegroundColor Gray
    Write-Host "  Pista: $($game.concepts[0].clue)" -ForegroundColor Gray
    
    $gameId = $game.gameId
    $firstConceptId = $game.concepts[0].conceptId
    $firstWord = $game.concepts[0].word
} catch {
    Write-Host "✗ Error al iniciar juego" -ForegroundColor Red
    Write-Host "Error: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 3. Intentar una letra correcta
Write-Host "3. Intentando letra A..." -ForegroundColor Yellow
$attemptBody = @{
    conceptId = $firstConceptId
    letter = "A"
    responseTimeMs = 1500
} | ConvertTo-Json

try {
    $attemptResult = Invoke-RestMethod -Uri "http://localhost:8080/api/hangman/games/$gameId/attempt" `
        -Method Post `
        -ContentType "application/json" `
        -Body $attemptBody
    
    if ($attemptResult.isCorrect) {
        Write-Host "✓ Letra correcta!" -ForegroundColor Green
        Write-Host "  - Posiciones: $($attemptResult.positions -join ', ')" -ForegroundColor Gray
    } else {
        Write-Host "✗ Letra incorrecta" -ForegroundColor Red
    }
    Write-Host "  - Vidas restantes: $($attemptResult.livesRemaining)" -ForegroundColor Gray
    Write-Host "  - Juego terminado: $($attemptResult.gameOver)" -ForegroundColor Gray
} catch {
    Write-Host "✗ Error al intentar letra" -ForegroundColor Red
    Write-Host "Error: $_" -ForegroundColor Red
}

Write-Host ""

# 4. Intentar un carácter inválido (número)
Write-Host "4. Intentando carácter inválido (1)..." -ForegroundColor Yellow
$invalidAttemptBody = @{
    conceptId = $firstConceptId
    letter = "1"
    responseTimeMs = 500
} | ConvertTo-Json

try {
    $invalidResult = Invoke-RestMethod -Uri "http://localhost:8080/api/hangman/games/$gameId/attempt" `
        -Method Post `
        -ContentType "application/json" `
        -Body $invalidAttemptBody
    
    Write-Host "  - Es correcto: $($invalidResult.isCorrect)" -ForegroundColor Gray
    Write-Host "  - Vidas restantes: $($invalidResult.livesRemaining)" -ForegroundColor Gray
    if ($invalidResult.message) {
        Write-Host "  - Mensaje: $($invalidResult.message)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "✗ Error: $_" -ForegroundColor Red
}

Write-Host ""

# 5. Obtener estado del juego
Write-Host "5. Obteniendo estado del juego..." -ForegroundColor Yellow
try {
    $gameState = Invoke-RestMethod -Uri "http://localhost:8080/api/hangman/games/$gameId" -Method Get
    
    Write-Host "✓ Estado obtenido" -ForegroundColor Green
    Write-Host "  - Game ID: $($gameState.gameId)" -ForegroundColor Gray
    Write-Host "  - Vidas: $($gameState.livesRemaining)" -ForegroundColor Gray
    Write-Host "  - Score: $($gameState.score)" -ForegroundColor Gray
    Write-Host "  - Concepto actual: $($gameState.currentConceptIndex + 1)/$($gameState.concepts.Count)" -ForegroundColor Gray
} catch {
    Write-Host "✗ Error al obtener estado" -ForegroundColor Red
    Write-Host "Error: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== FIN DE PRUEBAS ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "Para más pruebas, revisa el archivo HANGMAN_TESTS.md" -ForegroundColor Gray
