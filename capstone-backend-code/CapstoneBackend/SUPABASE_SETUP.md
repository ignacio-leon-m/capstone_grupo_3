# 🚀 Guía de Configuración Supabase para BrainBoost

## 📋 Resumen

Tu aplicación BrainBoost ya está configurada para conectarse a Supabase desde Netlify. Esta guía explica cómo usar la conexión.

## 🔗 Información de Conexión

- **URL del Proyecto**: https://dynehineagvxftqchjwz.supabase.co
- **Sitio en Netlify**: https://brainboost-static.netlify.app/
- **Región**: us-east-2

## 📁 Archivos Creados

### 1. `supabase-client.js`
Cliente JavaScript para interactuar con la API REST de Supabase.

**Ubicación**: `/js/supabase-client.js`

**Uso básico**:
```javascript
// SELECT
const { data, error } = await supabase.select('users', {
    select: 'id,name,email',
    filter: { role: 'profesor' },
    limit: 10
});

// INSERT
const { data, error } = await supabase.insert('users', {
    name: 'Juan Pérez',
    email: 'juan@ejemplo.com'
});

// UPDATE
const { data, error } = await supabase.update('users',
    { name: 'Nuevo Nombre' },
    { id: 123 }
);

// DELETE
const { data, error } = await supabase.delete('users', { id: 123 });
```

### 2. `supabase-auth.js`
Módulo de autenticación con funciones helper.

**Ubicación**: `/js/supabase-auth.js`

**Funciones disponibles**:
```javascript
// Iniciar sesión
const { user, token, error } = await supabaseAuth.signIn(email, password);

// Cerrar sesión
supabaseAuth.signOut();

// Obtener usuario actual
const user = supabaseAuth.getCurrentUser();

// Verificar autenticación
if (supabaseAuth.isAuthenticated()) {
    // Usuario autenticado
}

// Verificar rol
if (supabaseAuth.hasRole('profesor')) {
    // Usuario es profesor
}
```

### 3. `supabase-example.html`
Página de demostración con ejemplos de todas las operaciones.

**URL**: `/supabase-example.html`

Incluye ejemplos interactivos de:
- Autenticación
- SELECT (consultas)
- INSERT (insertar)
- UPDATE (actualizar)
- DELETE (eliminar)

### 4. `netlify.toml`
Configuración de Netlify con:
- Variables de entorno
- Proxy para la API
- Headers de seguridad
- Redirecciones

## 🌐 Despliegue en Netlify

### Opción 1: Desde GitHub

1. Conecta tu repositorio a Netlify
2. Configura el build:
   - **Base directory**: `.`
   - **Publish directory**: `src/main/resources/static`
   - **Build command**: (vacío, archivos estáticos)

3. Las variables de entorno ya están en `netlify.toml`

### Opción 2: Netlify CLI

```bash
# Instalar CLI
npm install -g netlify-cli

# Login
netlify login

# Desplegar
cd src/main/resources/static
netlify deploy --prod
```

### Opción 3: Drag & Drop

1. Ve a https://app.netlify.com/drop
2. Arrastra la carpeta `src/main/resources/static`

## 🔒 Configuración de Supabase

### 1. Crear Tablas

En el Dashboard de Supabase (https://supabase.com/dashboard/project/dynehineagvxftqchjwz):

```sql
-- Ejemplo: Tabla de usuarios
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role TEXT DEFAULT 'estudiante',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Habilitar Row Level Security (RLS)
ALTER TABLE users ENABLE ROW LEVEL SECURITY;

-- Política: Los usuarios pueden ver todos los registros
CREATE POLICY "Allow read access to all users"
ON users FOR SELECT
USING (true);

-- Política: Solo usuarios autenticados pueden insertar
CREATE POLICY "Allow insert for authenticated users"
ON users FOR INSERT
WITH CHECK (auth.role() = 'authenticated');
```

### 2. Configurar Autenticación

En Dashboard → Authentication:

1. **Email Auth**: Habilitar
2. **Providers**: Configurar Google, GitHub, etc. (opcional)
3. **Email Templates**: Personalizar (opcional)

### 3. Configurar API

En Dashboard → Settings → API:

1. **URL**: Ya configurada
2. **anon key**: Ya en el código
3. **service_role key**: Solo para backend (¡no expongas!)

## 🔄 Migrar desde tu Backend Actual

### Paso 1: Actualizar `index.html`

```html
<!-- Agregar antes del cierre de </body> -->
<script src="/js/supabase-client.js"></script>
<script src="/js/supabase-auth.js"></script>

<script>
    // Reemplazar el login actual
    document.getElementById('loginForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        const { user, token, error } = await supabaseAuth.signIn(email, password);

        if (error) {
            alert('Error: ' + error.message);
        } else {
            window.location.href = '/home.html';
        }
    });
</script>
```

### Paso 2: Actualizar Otras Páginas

Reemplaza las llamadas fetch a tu backend:

**Antes**:
```javascript
const response = await fetch('/api/users', {
    headers: {
        'Authorization': `Bearer ${token}`
    }
});
```

**Después**:
```javascript
const { data, error } = await supabase.select('users');
```

## 🧪 Probar la Conexión

### 1. Abrir la Página de Demo

Visita: `https://brainboost-static.netlify.app/supabase-example.html`

### 2. Probar en Consola del Navegador

```javascript
// Verificar que el cliente está cargado
console.log(window.supabase);

// Hacer una consulta de prueba
const { data, error } = await supabase.select('users', { limit: 1 });
console.log(data, error);
```

## 🔐 Seguridad

### Variables de Entorno en Netlify

Para mayor seguridad, usa variables de entorno en Netlify:

1. Dashboard de Netlify → Site Settings → Environment Variables
2. Agrega:
   - `SUPABASE_URL`
   - `SUPABASE_ANON_KEY`

3. Actualiza `supabase-client.js`:
```javascript
const SUPABASE_URL = window.ENV?.SUPABASE_URL || 'https://dynehineagvxftqchjwz.supabase.co';
const SUPABASE_ANON_KEY = window.ENV?.SUPABASE_ANON_KEY || 'tu-key';
```

### Row Level Security (RLS)

Siempre habilita RLS en todas las tablas:

```sql
ALTER TABLE nombre_tabla ENABLE ROW LEVEL SECURITY;
```

Define políticas específicas por tabla.

## 📊 Monitoreo

### Dashboard de Supabase

Monitorea:
- API Usage
- Database Performance
- Auth Logs
- Storage Usage

### Netlify Analytics

Monitorea:
- Tráfico web
- Errores 404
- Tiempo de carga

## 🆘 Solución de Problemas

### Error: "CORS policy"

- Verifica que las políticas CORS estén configuradas en `netlify.toml`
- Comprueba las RLS policies en Supabase

### Error: "Tenant or user not found"

- Este error ocurre con conexión PostgreSQL directa
- La solución es usar la API REST (ya implementada)

### Error: "Invalid API key"

- Verifica que el `SUPABASE_ANON_KEY` esté correcto
- Copia la key desde Dashboard → Settings → API

## 📚 Recursos

- [Documentación Supabase](https://supabase.com/docs)
- [API Reference](https://supabase.com/docs/reference/javascript)
- [Netlify Docs](https://docs.netlify.com)

## ✅ Próximos Pasos

1. ✅ **Conexión configurada** - Los archivos JS están listos
2. 📝 **Crear tablas en Supabase** - Usa el SQL Editor
3. 🔄 **Migrar autenticación** - Actualiza `index.html`
4. 🚀 **Desplegar en Netlify** - Push a GitHub o drag & drop
5. 🧪 **Probar en producción** - Usa `supabase-example.html`

---

¿Necesitas ayuda? Revisa la página de ejemplo o consulta la documentación de Supabase.
