/**
 * Cliente de Supabase para BrainBoost
 * 
 * Este módulo proporciona acceso a la base de datos Supabase
 * desde el frontend de manera segura usando la API REST.
 */

// Configuración de Supabase
const SUPABASE_URL = 'https://dynehineagvxftqchjwz.supabase.co';
const SUPABASE_ANON_KEY = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImR5bmVoaW5lYWd2eGZ0cWNoanciLCJyb2xlIjoiYW5vbiIsImlhdCI6MTczMjE0NDkyNCwiZXhwIjoyMDQ3NzIwOTI0fQ.vbZN9-qd5hQYLJKZWGEKvFmGf3gHO6cRpL-6kLuXqrU';

/**
 * Cliente de Supabase usando fetch API nativo
 * Compatible con navegadores modernos, no requiere instalación
 */
class SupabaseClient {
    constructor(url, anonKey) {
        this.url = url;
        this.anonKey = anonKey;
        this.headers = {
            'apikey': anonKey,
            'Authorization': `Bearer ${anonKey}`,
            'Content-Type': 'application/json',
            'Prefer': 'return=representation'
        };
    }

    /**
     * Obtiene el token JWT del usuario autenticado
     */
    getAuthToken() {
        return localStorage.getItem('jwtToken') || this.anonKey;
    }

    /**
     * Actualiza los headers con el token de autenticación
     */
    getHeaders() {
        return {
            ...this.headers,
            'Authorization': `Bearer ${this.getAuthToken()}`
        };
    }

    /**
     * SELECT - Consulta datos de una tabla
     * @param {string} table - Nombre de la tabla
     * @param {object} options - Opciones de consulta
     * @returns {Promise<{data: any[], error: any}>}
     */
    async select(table, options = {}) {
        try {
            let url = `${this.url}/rest/v1/${table}`;
            const params = new URLSearchParams();

            // Selección de columnas
            if (options.select) {
                params.append('select', options.select);
            }

            // Filtros
            if (options.filter) {
                Object.entries(options.filter).forEach(([key, value]) => {
                    params.append(key, `eq.${value}`);
                });
            }

            // Ordenamiento
            if (options.order) {
                params.append('order', options.order);
            }

            // Límite
            if (options.limit) {
                params.append('limit', options.limit);
            }

            if (params.toString()) {
                url += `?${params.toString()}`;
            }

            const response = await fetch(url, {
                method: 'GET',
                headers: this.getHeaders()
            });

            const data = await response.json();

            if (!response.ok) {
                return { data: null, error: data };
            }

            return { data, error: null };
        } catch (error) {
            return { data: null, error: error.message };
        }
    }

    /**
     * INSERT - Inserta datos en una tabla
     * @param {string} table - Nombre de la tabla
     * @param {object|array} data - Datos a insertar
     * @returns {Promise<{data: any, error: any}>}
     */
    async insert(table, data) {
        try {
            const response = await fetch(`${this.url}/rest/v1/${table}`, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify(data)
            });

            const result = await response.json();

            if (!response.ok) {
                return { data: null, error: result };
            }

            return { data: result, error: null };
        } catch (error) {
            return { data: null, error: error.message };
        }
    }

    /**
     * UPDATE - Actualiza datos en una tabla
     * @param {string} table - Nombre de la tabla
     * @param {object} data - Datos a actualizar
     * @param {object} filter - Filtros para identificar registros
     * @returns {Promise<{data: any, error: any}>}
     */
    async update(table, data, filter) {
        try {
            const params = new URLSearchParams();
            Object.entries(filter).forEach(([key, value]) => {
                params.append(key, `eq.${value}`);
            });

            const response = await fetch(`${this.url}/rest/v1/${table}?${params.toString()}`, {
                method: 'PATCH',
                headers: this.getHeaders(),
                body: JSON.stringify(data)
            });

            const result = await response.json();

            if (!response.ok) {
                return { data: null, error: result };
            }

            return { data: result, error: null };
        } catch (error) {
            return { data: null, error: error.message };
        }
    }

    /**
     * DELETE - Elimina datos de una tabla
     * @param {string} table - Nombre de la tabla
     * @param {object} filter - Filtros para identificar registros
     * @returns {Promise<{data: any, error: any}>}
     */
    async delete(table, filter) {
        try {
            const params = new URLSearchParams();
            Object.entries(filter).forEach(([key, value]) => {
                params.append(key, `eq.${value}`);
            });

            const response = await fetch(`${this.url}/rest/v1/${table}?${params.toString()}`, {
                method: 'DELETE',
                headers: this.getHeaders()
            });

            if (!response.ok) {
                const error = await response.json();
                return { data: null, error };
            }

            return { data: true, error: null };
        } catch (error) {
            return { data: null, error: error.message };
        }
    }

    /**
     * RPC - Llama a una función de PostgreSQL
     * @param {string} functionName - Nombre de la función
     * @param {object} params - Parámetros de la función
     * @returns {Promise<{data: any, error: any}>}
     */
    async rpc(functionName, params = {}) {
        try {
            const response = await fetch(`${this.url}/rest/v1/rpc/${functionName}`, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify(params)
            });

            const data = await response.json();

            if (!response.ok) {
                return { data: null, error: data };
            }

            return { data, error: null };
        } catch (error) {
            return { data: null, error: error.message };
        }
    }
}

// Crear instancia global del cliente
const supabase = new SupabaseClient(SUPABASE_URL, SUPABASE_ANON_KEY);

// Exportar para uso en otros módulos
window.supabase = supabase;

// Ejemplo de uso:
/*
// SELECT
const { data: users, error } = await supabase.select('users', {
    select: 'id,name,email',
    filter: { role: 'profesor' },
    order: 'created_at.desc',
    limit: 10
});

// INSERT
const { data: newUser, error } = await supabase.insert('users', {
    name: 'Juan Pérez',
    email: 'juan@ejemplo.com',
    role: 'estudiante'
});

// UPDATE
const { data: updated, error } = await supabase.update('users', 
    { name: 'Juan García' },
    { id: 123 }
);

// DELETE
const { data: deleted, error } = await supabase.delete('users', { id: 123 });

// RPC (función personalizada)
const { data: result, error } = await supabase.rpc('get_user_statistics', { 
    user_id: 123 
});
*/
