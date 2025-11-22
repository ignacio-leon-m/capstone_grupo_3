# 🎨 BrainBoost Design System - Guía de Estilos

**Versión:** 1.0.0  
**Fecha:** Noviembre 2025  
**Framework Base:** Bootstrap 5.3.3 + Sistema Custom

---

## 📋 Tabla de Contenidos

1. [Introducción](#introducción)
2. [Colores](#colores)
3. [Tipografía](#tipografía)
4. [Espaciado](#espaciado)
5. [Componentes](#componentes)
6. [Utilidades](#utilidades)
7. [Ejemplos de Uso](#ejemplos-de-uso)

---

## 🎯 Introducción

El sistema de diseño de BrainBoost proporciona un conjunto coherente de estilos, componentes y utilidades reutilizables. Este sistema está construido sobre Bootstrap 5.3.3 y extiende sus capacidades con variables CSS personalizadas y componentes específicos de BrainBoost.

### Estructura de Archivos

```
src/main/resources/static/css/
├── theme.css              # Sistema de diseño base (OBLIGATORIO)
├── login.css              # Estilos específicos de login
├── home.css               # Estilos específicos de home
├── forms.css              # Estilos de formularios y páginas relacionadas
├── content.css            # Estilos de páginas de contenido
├── content-upload.css     # Estilos de carga de contenido
├── user.css               # Estilos de gestión de usuarios
└── user-upload.css        # Estilos de carga de usuarios
```

### Importación Correcta

**IMPORTANTE:** Siempre importar en este orden:

```html
<!-- 1. Fuentes -->
<link href="https://fonts.googleapis.com/css2?family=Nunito:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">

<!-- 2. Bootstrap (para layout y utilities) -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">

<!-- 3. Sistema de Diseño BrainBoost -->
<link rel="stylesheet" href="/css/theme.css">

<!-- 4. CSS específico de la página -->
<link rel="stylesheet" href="/css/tu-pagina.css">
```

---

## 🎨 Colores

### Paleta Institucional BrainBoost

#### Colores Primarios

```css
--color-primary: #007bff;        /* Azul Principal */
--color-primary-dark: #0056b3;   /* Azul Oscuro */
--color-primary-light: #66b3ff;  /* Azul Claro */
```

**Uso:** Botones primarios, enlaces, encabezados importantes.

**Ejemplo:**
```html
<button class="btn btn-primary">Acción Principal</button>
<h1 class="text-primary">Título Importante</h1>
```

#### Colores de Marca

```css
--color-magenta: #ff007f;        /* Magenta BrainBoost */
--color-orange: #ff8c00;         /* Naranja BrainBoost */
--color-yellow: #ffd700;         /* Amarillo BrainBoost */
--color-cyan: #00ffff;           /* Cyan BrainBoost */
```

**Uso:** Acentos, gradientes, elementos decorativos.

**Ejemplo:**
```html
<a href="#" class="text-magenta">Enlace Especial</a>
```

#### Colores de Estado

| Color | Variable CSS | Hex | Uso |
|-------|--------------|-----|-----|
| ✅ Success | `--color-success` | #28a745 | Mensajes de éxito, estados completados |
| ⚠️ Warning | `--color-warning` | #ffc107 | Advertencias, procesos en curso |
| ❌ Error | `--color-error` | #dc3545 | Errores, acciones destructivas |
| ℹ️ Info | `--color-info` | #17a2b8 | Información general |

**Ejemplo:**
```html
<button class="btn btn-success">Guardar</button>
<button class="btn btn-danger">Eliminar</button>
<div class="alert alert-warning">Advertencia</div>
```

#### Escala de Grises

```css
--color-dark: #1a1a1a           /* Texto principal */
--color-grey-900: #212529       /* Texto secundario */
--color-grey-600: #6c757d       /* Texto muted */
--color-grey-300: #dee2e6       /* Bordes */
--color-grey-100: #f8f9fa       /* Fondos claros */
--color-white: #ffffff          /* Blanco */
```

### Clases de Utilidad

```html
<!-- Texto -->
<p class="text-primary">Texto azul</p>
<p class="text-success">Texto verde</p>
<p class="text-muted">Texto gris</p>

<!-- Fondo -->
<div class="bg-primary text-white">Fondo azul</div>
<div class="bg-light">Fondo gris claro</div>
```

---

## 📝 Tipografía

### Fuente Principal

**Familia:** Nunito (Google Fonts)  
**Pesos disponibles:** 300, 400, 500, 600, 700, 800

```css
--font-primary: 'Nunito', sans-serif;
```

### Escala de Tamaños

| Clase | Variable | Tamaño | Uso |
|-------|----------|--------|-----|
| `.text-xs` | `--text-xs` | 12px | Texto muy pequeño, labels |
| `.text-sm` | `--text-sm` | 14px | Texto secundario, descripciones |
| `.text-base` | `--text-base` | 16px | Texto base del body |
| `.text-lg` | `--text-lg` | 18px | Botones grandes, destacados |
| `.text-xl` | `--text-xl` | 20px | Subtítulos |
| `.text-2xl` | `--text-2xl` | 24px | Títulos de sección |
| `.text-3xl` | `--text-3xl` | 30px | Títulos principales |
| `.text-4xl` | `--text-4xl` | 36px | Títulos hero |

### Pesos de Fuente

```html
<p class="font-light">Light (300)</p>
<p class="font-normal">Normal (400)</p>
<p class="font-medium">Medium (500)</p>
<p class="font-semibold">Semibold (600)</p>
<p class="font-bold">Bold (700)</p>
<p class="font-extrabold">Extrabold (800)</p>
```

### Ejemplo de Jerarquía

```html
<h1 class="text-4xl font-bold text-primary">Título Principal</h1>
<h2 class="text-3xl font-bold">Título de Sección</h2>
<h3 class="text-2xl font-semibold">Subtítulo</h3>
<p class="text-base font-normal">Párrafo normal de texto.</p>
<small class="text-sm text-muted">Nota secundaria</small>
```

---

## 📏 Espaciado

### Sistema de Espaciado

Basado en una escala consistente de 4px:

| Variable | Valor | Uso |
|----------|-------|-----|
| `--spacing-xs` | 4px | Separaciones mínimas |
| `--spacing-sm` | 8px | Padding interno pequeño |
| `--spacing-md` | 16px | Espaciado estándar |
| `--spacing-lg` | 24px | Separación de secciones |
| `--spacing-xl` | 32px | Espaciado grande |
| `--spacing-2xl` | 48px | Espaciado muy grande |

### Clases de Utilidad

```html
<!-- Margin -->
<div class="m-0">Sin margin</div>
<div class="m-3">Margin medio (16px)</div>
<div class="mt-4">Margin top grande (24px)</div>
<div class="mb-5">Margin bottom extra (32px)</div>

<!-- Padding -->
<div class="p-3">Padding medio (16px)</div>
<div class="px-4">Padding horizontal grande</div>
<div class="py-2">Padding vertical pequeño</div>
```

---

## 🧩 Componentes

### 1. Botones

#### Variantes Sólidas

```html
<!-- Primario (con gradiente) -->
<button class="btn btn-primary">Botón Primario</button>

<!-- Secundario -->
<button class="btn btn-secondary">Botón Secundario</button>

<!-- Estados -->
<button class="btn btn-success">Éxito</button>
<button class="btn btn-warning">Advertencia</button>
<button class="btn btn-danger">Peligro</button>
<button class="btn btn-info">Información</button>
```

#### Variantes Outline

```html
<button class="btn btn-outline-primary">Primario Outline</button>
<button class="btn btn-outline-success">Éxito Outline</button>
<button class="btn btn-outline-danger">Peligro Outline</button>
```

#### Tamaños

```html
<button class="btn btn-primary btn-sm">Pequeño</button>
<button class="btn btn-primary btn-md">Mediano</button>
<button class="btn btn-primary btn-lg">Grande</button>
<button class="btn btn-primary btn-xl">Extra Grande</button>
```

#### Botón 3D (Efecto BrainBoost)

```html
<button class="btn btn-primary btn-3d">Botón con Efecto 3D</button>
<button class="btn btn-success btn-3d">Guardar 3D</button>
```

**Características:**
- Sombra de profundidad
- Animación al hacer clic (presiona el botón)
- Hover con elevación

#### Botón Ancho Completo

```html
<button class="btn btn-primary btn-block">Botón Completo</button>
<button class="btn btn-primary btn-lg btn-block btn-3d">Login</button>
```

#### Estados

```html
<!-- Deshabilitado -->
<button class="btn btn-primary" disabled>Deshabilitado</button>

<!-- Con ícono -->
<button class="btn btn-primary">
  <i class="fas fa-upload me-2"></i>Subir Archivo
</button>
```

---

### 2. Formularios

#### Input Básico

```html
<div class="form-group">
  <label for="nombre" class="form-label">Nombre</label>
  <input type="text" id="nombre" class="form-control" placeholder="Ingresa tu nombre">
</div>
```

#### Tamaños

```html
<input type="text" class="form-control form-control-sm" placeholder="Pequeño">
<input type="text" class="form-control" placeholder="Normal">
<input type="text" class="form-control form-control-lg" placeholder="Grande">
```

#### Estados de Validación

```html
<!-- Válido -->
<div class="form-group">
  <input type="email" class="form-control is-valid">
  <div class="valid-feedback">¡Correcto!</div>
</div>

<!-- Inválido -->
<div class="form-group">
  <input type="email" class="form-control is-invalid">
  <div class="invalid-feedback">Email inválido</div>
</div>
```

#### Select Múltiple

```html
<select class="form-control" multiple size="6">
  <option>Opción 1</option>
  <option>Opción 2</option>
  <option>Opción 3</option>
</select>
```

#### Formulario Completo

```html
<form class="form-container">
  <div class="form-group">
    <label for="email" class="form-label">Email</label>
    <input type="email" id="email" class="form-control" required>
  </div>
  
  <div class="form-group">
    <label for="password" class="form-label">Contraseña</label>
    <input type="password" id="password" class="form-control" required>
  </div>
  
  <button type="submit" class="btn btn-primary btn-lg btn-block btn-3d">
    Enviar
  </button>
</form>
```

---

### 3. Cards

#### Card Básica

```html
<div class="card">
  <div class="card-header">
    Encabezado de Card
  </div>
  <div class="card-body">
    <h5 class="card-title">Título</h5>
    <p class="card-text">Contenido de la card.</p>
    <button class="btn btn-primary">Acción</button>
  </div>
  <div class="card-footer">
    Pie de Card
  </div>
</div>
```

#### Card Elevada (con hover)

```html
<div class="card card-elevated">
  <div class="card-body">
    <h5 class="card-title">Card Elevada</h5>
    <p class="card-text">Se eleva al hacer hover.</p>
  </div>
</div>
```

#### Card con Borde Gradiente (estilo BrainBoost)

```html
<div class="card card-gradient-border">
  <div class="card-body">
    <h5 class="card-title">Card BrainBoost</h5>
    <p class="card-text">Borde con gradiente colorido.</p>
  </div>
</div>
```

---

### 4. Tablas

#### Tabla Básica

```html
<table class="table">
  <thead>
    <tr>
      <th>Nombre</th>
      <th>Email</th>
      <th>Rol</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Juan Pérez</td>
      <td>juan@example.com</td>
      <td>Admin</td>
    </tr>
  </tbody>
</table>
```

#### Tabla con Hover

```html
<table class="table table-hover">
  <!-- ... -->
</table>
```

#### Tabla Striped

```html
<table class="table table-striped">
  <!-- ... -->
</table>
```

#### Tabla Completa con Card

```html
<div class="card card-elevated">
  <div class="card-header">
    <h5 class="mb-0">Lista de Usuarios</h5>
  </div>
  <div class="card-body">
    <div class="table-responsive">
      <table class="table table-hover">
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Email</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Usuario 1</td>
            <td>user1@example.com</td>
            <td>
              <button class="btn btn-sm btn-primary">Editar</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>
```

---

### 5. Indicadores de Estado

```html
<!-- Completado -->
<span class="status-circle status-completed" title="Completado"></span>

<!-- Procesando -->
<span class="status-circle status-processing" title="Procesando"></span>

<!-- Error -->
<span class="status-circle status-error" title="Error"></span>

<!-- Pendiente -->
<span class="status-circle status-pending" title="Pendiente"></span>
```

**Con texto:**

```html
<div class="d-flex align-items-center gap-2">
  <span class="status-circle status-completed"></span>
  <span>Completado</span>
</div>
```

---

### 6. Botón de Logout Flotante

```html
<div class="logout-fixed-container">
  <button class="logout-icon-button" title="Cerrar Sesión">
    <span class="logout-tooltip">Cerrar Sesión</span>
    <i class="fas fa-sign-out-alt"></i>
  </button>
</div>
```

**Características:**
- Fijo en la esquina inferior derecha
- Tooltip al hacer hover
- Animación de elevación
- Cambia a rojo al hover

---

### 7. Container de Login

```html
<div class="login-container">
  <div class="logo-area">
    <img src="/images/bb-logo.png" alt="Logo">
  </div>
  
  <form class="login-form">
    <h2>Iniciar Sesión</h2>
    
    <div class="form-group">
      <input type="email" class="form-control" placeholder="Email">
    </div>
    
    <div class="form-group">
      <input type="password" class="form-control" placeholder="Contraseña">
    </div>
    
    <button type="submit" class="btn btn-primary btn-lg btn-block btn-3d">
      Entrar
    </button>
    
    <div class="links">
      <a href="#">¿Olvidaste tu contraseña?</a>
    </div>
  </form>
</div>
```

**Características:**
- Borde con gradiente animado
- Centrado vertical y horizontal
- Fondo translúcido
- Responsive

---

## 🛠️ Utilidades

### Sombras

```html
<div class="shadow-sm">Sombra pequeña</div>
<div class="shadow">Sombra media</div>
<div class="shadow-lg">Sombra grande</div>
<div class="shadow-xl">Sombra extra grande</div>
```

### Bordes Redondeados

```html
<div class="rounded-none">Sin bordes redondeados</div>
<div class="rounded-sm">Bordes pequeños</div>
<div class="rounded">Bordes medianos</div>
<div class="rounded-lg">Bordes grandes</div>
<div class="rounded-full">Completamente redondo</div>
```

### Display & Layout

```html
<!-- Flexbox (Bootstrap) -->
<div class="d-flex justify-content-center align-items-center">
  Contenido centrado
</div>

<!-- Grid (Bootstrap) -->
<div class="container">
  <div class="row">
    <div class="col-md-6">Columna 1</div>
    <div class="col-md-6">Columna 2</div>
  </div>
</div>
```

---

## 📱 Responsive

El sistema usa los breakpoints de Bootstrap:

| Breakpoint | Tamaño | Clase |
|------------|--------|-------|
| xs | < 576px | Sin prefijo |
| sm | ≥ 576px | `-sm-` |
| md | ≥ 768px | `-md-` |
| lg | ≥ 992px | `-lg-` |
| xl | ≥ 1200px | `-xl-` |
| xxl | ≥ 1400px | `-xxl-` |

**Ejemplos:**

```html
<!-- Columna completa en móvil, mitad en desktop -->
<div class="col-12 col-md-6">Contenido</div>

<!-- Ocultar en móvil -->
<div class="d-none d-md-block">Solo desktop</div>

<!-- Tamaño de texto responsive -->
<h1 class="text-2xl text-md-3xl text-lg-4xl">Título Responsive</h1>
```

---

## 💡 Ejemplos de Uso Completos

### Página de Login

```html
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Login - BrainBoost</title>
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="/css/theme.css">
  <link rel="stylesheet" href="/css/login.css">
</head>
<body>
  <div class="login-container">
    <div class="logo-area">
      <img src="/images/bb-logo.png" alt="BrainBoost Logo">
    </div>
    
    <form class="login-form">
      <h2>Iniciar Sesión</h2>
      
      <div class="form-group">
        <input type="email" class="form-control" placeholder="Correo electrónico" required>
      </div>
      
      <div class="form-group">
        <input type="password" class="form-control" placeholder="Contraseña" required>
      </div>
      
      <button type="submit" class="btn btn-primary btn-lg btn-block btn-3d">
        Entrar
      </button>
      
      <div class="links">
        <a href="#">¿Olvidaste tu contraseña?</a>
      </div>
    </form>
  </div>
</body>
</html>
```

### Dashboard con Cards

```html
<div class="container my-5">
  <div class="row">
    <div class="col-12 col-lg-8 mx-auto">
      <div class="card card-elevated shadow-lg">
        <div class="card-body text-center">
          <h1 class="card-title mb-4 font-bold">Bienvenido</h1>
          <p class="card-text text-muted mb-4">Selecciona una opción:</p>
          
          <div class="d-grid gap-3">
            <button class="btn btn-primary btn-lg btn-3d">
              <i class="fas fa-upload me-2"></i>Carga de Contenido
            </button>
            
            <button class="btn btn-primary btn-lg btn-3d">
              <i class="fas fa-users me-2"></i>Carga de Usuarios
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<div class="logout-fixed-container">
  <button class="logout-icon-button">
    <span class="logout-tooltip">Cerrar Sesión</span>
    <i class="fas fa-sign-out-alt"></i>
  </button>
</div>
```

### Tabla de Datos

```html
<div class="container my-4">
  <h1 class="mb-4 text-primary font-bold">Contenido</h1>
  
  <div class="card card-elevated shadow-lg">
    <div class="card-header bg-white border-0 py-3">
      <h2 class="h5 mb-0">Estado Archivos Procesados</h2>
    </div>
    
    <div class="card-body">
      <div class="alert alert-light mb-3">
        <p class="mb-2 font-semibold">Leyenda de estados:</p>
        <span class="badge bg-success me-2">
          <i class="fas fa-check-circle"></i> Completado
        </span>
        <span class="badge bg-warning text-dark me-2">
          <i class="fas fa-spinner"></i> Procesando
        </span>
        <span class="badge bg-danger">
          <i class="fas fa-exclamation-circle"></i> Errores
        </span>
      </div>
      
      <div class="table-responsive">
        <table class="table table-hover">
          <thead>
            <tr>
              <th>Sección</th>
              <th>Asignatura</th>
              <th>Estado</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>A</td>
              <td>Matemáticas</td>
              <td><span class="status-circle status-completed"></span></td>
            </tr>
            <tr>
              <td>B</td>
              <td>Lenguaje</td>
              <td><span class="status-circle status-processing"></span></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>
```

---

## ✅ Checklist de Implementación

Antes de considerar completa una página, verifica:

- [ ] `theme.css` está importado **antes** del CSS específico
- [ ] Todas las fuentes Nunito (300-800) están cargadas
- [ ] No hay estilos inline en el HTML
- [ ] Los botones usan clases del sistema (`btn-primary`, `btn-3d`, etc.)
- [ ] Los formularios usan `form-group`, `form-label`, `form-control`
- [ ] Las cards usan `card`, `card-body`, `card-header`
- [ ] Los colores se aplican mediante variables CSS
- [ ] El espaciado usa clases utilitarias o variables CSS
- [ ] La página es responsive (probada en móvil y desktop)
- [ ] Los estados interactivos (hover, focus) funcionan correctamente

---

## 🔄 Migración desde Sistema Antiguo

### Reemplazos Comunes

| Antiguo | Nuevo |
|---------|-------|
| `.input-group` (formularios) | `.form-group` |
| `.submit-button` | `.btn .btn-primary .btn-lg .btn-3d` |
| Estilos inline | Clases del sistema |
| Colors hardcoded | Variables CSS |
| `.card .shadow-sm` | `.card .card-elevated .shadow-lg` |

### Proceso de Migración

1. **Importar theme.css** en el `<head>`
2. **Reemplazar clases** de formularios y botones
3. **Eliminar estilos inline**
4. **Simplificar CSS específico** (delegar al sistema)
5. **Probar en diferentes resoluciones**

---

## 📞 Soporte

Para dudas sobre el sistema de diseño o implementación:
- Revisar esta guía completa
- Inspeccionar `theme.css` para variables disponibles
- Consultar documentación de Bootstrap 5.3

---

**Última actualización:** Noviembre 2025  
**Mantenido por:** Equipo BrainBoost
