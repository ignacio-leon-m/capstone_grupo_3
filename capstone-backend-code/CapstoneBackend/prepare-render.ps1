# ===================================
# 🚀 Script de Preparación para Render
# ===================================

Write-Host "=================================" -ForegroundColor Cyan
Write-Host "🚀 PREPARACIÓN PARA RENDER" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan
Write-Host ""

# Paso 1: Verificar build
Write-Host "📦 Paso 1: Verificando build..." -ForegroundColor Yellow
$buildResult = ./gradlew clean build -x test 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error en el build. Revisa los errores." -ForegroundColor Red
    exit 1
}
Write-Host "✅ Build exitoso" -ForegroundColor Green
Write-Host ""

# Paso 2: Verificar archivos necesarios
Write-Host "📋 Paso 2: Verificando archivos de configuración..." -ForegroundColor Yellow

$requiredFiles = @(
    "Dockerfile",
    "render.yaml",
    ".dockerignore",
    "src/main/resources/application.properties"
)

$allFilesExist = $true
foreach ($file in $requiredFiles) {
    if (Test-Path $file) {
        Write-Host "  ✅ $file" -ForegroundColor Green
    } else {
        Write-Host "  ❌ $file (faltante)" -ForegroundColor Red
        $allFilesExist = $false
    }
}

if (-not $allFilesExist) {
    Write-Host "❌ Faltan archivos necesarios" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Paso 3: Mostrar status de Git
Write-Host "📊 Paso 3: Estado del repositorio..." -ForegroundColor Yellow
git status --short
Write-Host ""

# Paso 4: Instrucciones
Write-Host "=================================" -ForegroundColor Cyan
Write-Host "📝 PRÓXIMOS PASOS:" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. Commit y push de los cambios:" -ForegroundColor White
Write-Host "   git add ." -ForegroundColor Gray
Write-Host "   git commit -m 'chore: configure Render deployment with Neon'" -ForegroundColor Gray
Write-Host "   git push origin main" -ForegroundColor Gray
Write-Host ""
Write-Host "2. Ve a Render Dashboard:" -ForegroundColor White
Write-Host "   https://dashboard.render.com/" -ForegroundColor Cyan
Write-Host ""
Write-Host "3. Crear nuevo Web Service:" -ForegroundColor White
Write-Host "   - New + → Web Service" -ForegroundColor Gray
Write-Host "   - Conecta tu repo: ignacio-leon-m/capstone_grupo_3" -ForegroundColor Gray
Write-Host "   - Branch: main" -ForegroundColor Gray
Write-Host "   - Root Directory: capstone-backend-code/CapstoneBackend" -ForegroundColor Gray
Write-Host "   - Runtime: Docker" -ForegroundColor Gray
Write-Host "   - Instance Type: Free" -ForegroundColor Gray
Write-Host ""
Write-Host "4. Añade estas variables de entorno:" -ForegroundColor White
Write-Host ""
Write-Host "   PORT=8080" -ForegroundColor Gray
Write-Host "   DATABASE_URL=jdbc:postgresql://ep-fancy-tree-af5xp9ie-pooler.c-2.us-west-2.aws.neon.tech/neondb?sslmode=require&channel_binding=require" -ForegroundColor Gray
Write-Host "   DATABASE_USERNAME=neondb_owner" -ForegroundColor Gray
Write-Host "   DATABASE_PASSWORD=npg_CinWX0he6lUp" -ForegroundColor Gray
Write-Host "   JWT_SECRET=f4e2a0b8d5c3e1a9f0b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6f5" -ForegroundColor Gray
Write-Host "   JWT_ACCESS_EXPIRATION=3600000" -ForegroundColor Gray
Write-Host "   JWT_REFRESH_EXPIRATION=604800000" -ForegroundColor Gray
Write-Host "   GEMINI_API_KEY=AIzaSyClZcoMsMzXYmSxazv6P4SNSdlOFW2FNo8" -ForegroundColor Gray
Write-Host "   JAVA_TOOL_OPTIONS=-Xmx512m -Xms256m" -ForegroundColor Gray
Write-Host "   SPRING_PROFILES_ACTIVE=production" -ForegroundColor Gray
Write-Host ""
Write-Host "5. Click en 'Create Web Service' y espera 5-10 minutos" -ForegroundColor White
Write-Host ""
Write-Host "=================================" -ForegroundColor Cyan
Write-Host "📖 Documentación completa: RENDER_DEPLOYMENT_GUIDE.md" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "✨ Tu backend estará disponible en:" -ForegroundColor Green
Write-Host "   https://brainboost-backend.onrender.com" -ForegroundColor Cyan
Write-Host ""
