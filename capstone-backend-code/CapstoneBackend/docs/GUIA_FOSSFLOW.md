# Guía: Crear Diagrama de Despliegue en FossFLOW

## ✅ Ya tienes FossFLOW abierto

Ya ejecutaste: `start https://stan-smith.github.io/FossFLOW/`

## 📝 Pasos para Crear el Diagrama Manualmente

### 1. Agregar Componentes (Click en "+" arriba a la derecha)

Arrastra estos iconos al canvas:

| Icono | Ubicación | Etiqueta |
|-------|-----------|----------|
| 📱 **mobile** | Arriba izquierda | "Estudiante (Android)" |
| 💻 **browser** | Arriba derecha | "Profesor/Admin (Web)" |
| ☁️ **cloud** | Centro arriba | "Internet HTTPS" |
| 🖥️ **server** | Centro | "Render.com\nOregon US West" |
| 📦 **container** | Centro abajo | "Docker Container\nSpring Boot 3.5.5" |
| 🗄️ **database** | Abajo izquierda | "Neon PostgreSQL 17.5\nUS West 2" |
| 🤖 **api** | Abajo derecha | "Google Gemini API\ngemini-2.0-flash" |
| 📂 **storage** | Derecha | "GitHub Repository" |

### 2. Conectar Componentes (Tecla 'C' o click en icono Connector)

**Modo Click** (predeterminado):
1. Click en primer componente
2. Click en segundo componente
3. Doble-click en línea para agregar etiqueta

**Conexiones a crear:**

```
Mobile → Internet (etiqueta: "HTTPS")
Browser → Internet (etiqueta: "HTTPS")
Internet → Render.com (etiqueta: "TLS 1.3")
Render.com → Docker (etiqueta: "Port 8080")
Docker → Neon PostgreSQL (etiqueta: "JDBC/SSL")
Docker → Gemini API (etiqueta: "REST API")
GitHub → Render.com (etiqueta: "Auto-deploy")
```

### 3. Personalizar (Opcional)

- **Colores**: Click derecho en componente → "Change Color"
  - Azul (#4A90E2): Usuarios
  - Verde (#6DB33F): Backend
  - Morado (#336791): Base de datos
  - Rojo (#DB4437): APIs externas

- **Textos**: Doble-click en componente para editar

- **Notas**: Click en icono "Text" para agregar:
  - "FREE TIER - 512MB RAM" (cerca de Render)
  - "Serverless - Auto-suspend" (cerca de Neon)
  - "Rate Limit: 15 req/min" (cerca de Gemini)

### 4. Guardar

**Opción A: Exportar JSON**
- Menu → Export → Download JSON
- Guardar como: `brain-boost-deployment.json`

**Opción B: Exportar Imagen**
- Menu → Export → Download PNG
- Guardar como: `brain-boost-deployment.png`

**Opción C: Guardar en sesión**
- Click "Save" (guarda en localStorage del navegador)

## 🎯 Estructura Recomendada (Vista de Capas)

```
┌─────────────────────────────────────────┐
│ CAPA 1: CLIENTES                         │
│ [Mobile]              [Browser]          │
│  Android              Web                │
└─────────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│ CAPA 2: INTERNET                         │
│        [Cloud/Internet]                  │
│        HTTPS / TLS 1.3                   │
└─────────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│ CAPA 3: APLICACIÓN                       │
│ [Render.com]                             │
│   └─ [Docker Container]                  │
│       └─ [Spring Boot Backend]           │
└─────────────────────────────────────────┘
          │                │
          │                └──────────────┐
          ▼                               ▼
┌─────────────────────┐    ┌─────────────────────┐
│ CAPA 4: DATOS       │    │ CAPA 5: SERVICIOS   │
│ [Neon PostgreSQL]   │    │ [Google Gemini API] │
│  Serverless DB      │    │  IA Generativa      │
└─────────────────────┘    └─────────────────────┘

           [GitHub] ──(auto-deploy)──► [Render.com]
```

## 💡 Tips

1. **Usa el grid**: Ayuda a alinear componentes
2. **Zoom**: Rueda del mouse o botones +/- 
3. **Pan**: Click y arrastra en área vacía
4. **Undo/Redo**: Ctrl+Z / Ctrl+Y
5. **Selección múltiple**: Ctrl+Click
6. **Duplicar**: Ctrl+D en componente seleccionado

## 📁 Guardar en el Proyecto

Después de exportar desde FossFLOW:

```powershell
# Mover archivos al proyecto
Move-Item -Path "$env:USERPROFILE\Downloads\brain-boost-deployment.json" `
          -Destination "docs\diagrams\" -Force

Move-Item -Path "$env:USERPROFILE\Downloads\brain-boost-deployment.png" `
          -Destination "docs\diagrams\" -Force

# Verificar
Get-ChildItem "docs\diagrams\"
```

## 📚 Referencias

- **FossFLOW Online**: https://stan-smith.github.io/FossFLOW/
- **Documentación**: https://github.com/stan-smith/FossFLOW
- **Iconos gratuitos**: https://icon-sets.iconify.design/

---

**Nota**: FossFLOW no tiene importación automática de JSON en formato libre. Debes crear el diagrama manualmente usando la interfaz visual. El archivo JSON que exportes será en el formato propietario de FossFLOW para poder reimportarlo después.
