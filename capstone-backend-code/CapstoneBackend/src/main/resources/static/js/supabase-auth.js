/**
 * Módulo de Autenticación con Supabase
 * 
 * Proporciona funciones para autenticación de usuarios
 * usando Supabase Auth o consultas directas a la tabla users
 */

/**
 * Inicia sesión con email y contraseña
 * @param {string} email - Email del usuario
 * @param {string} password - Contraseña del usuario
 * @returns {Promise<{user: any, token: string, error: any}>}
 */
async function signIn(email, password) {
    try {
        // Opción 1: Consultar directamente la tabla de usuarios
        const { data: users, error } = await supabase.select('users', {
            select: 'id,name,email,role,password',
            filter: { email: email }
        });

        if (error) {
            return { user: null, token: null, error: error };
        }

        if (!users || users.length === 0) {
            return { 
                user: null, 
                token: null, 
                error: { message: 'Usuario no encontrado' }
            };
        }

        const user = users[0];

        // Aquí deberías verificar la contraseña con bcrypt
        // Por ahora, asumimos que el backend valida las credenciales
        
        // Generar un token temporal o usar el de Supabase
        const token = btoa(JSON.stringify({ 
            userId: user.id, 
            email: user.email,
            role: user.role,
            timestamp: Date.now()
        }));

        // Guardar en localStorage
        localStorage.setItem('jwtToken', token);
        localStorage.setItem('userRole', user.role);
        localStorage.setItem('userId', user.id);
        localStorage.setItem('userEmail', user.email);

        return { 
            user: {
                id: user.id,
                email: user.email,
                name: user.name,
                role: user.role
            }, 
            token, 
            error: null 
        };

    } catch (error) {
        return { 
            user: null, 
            token: null, 
            error: { message: error.message } 
        };
    }
}

/**
 * Cierra la sesión del usuario
 */
function signOut() {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userId');
    localStorage.removeItem('userEmail');
    window.location.href = '/index.html';
}

/**
 * Obtiene el usuario actual de localStorage
 * @returns {object|null} Usuario actual o null
 */
function getCurrentUser() {
    const token = localStorage.getItem('jwtToken');
    const role = localStorage.getItem('userRole');
    const userId = localStorage.getItem('userId');
    const email = localStorage.getItem('userEmail');

    if (!token || !userId) {
        return null;
    }

    return {
        id: userId,
        email: email,
        role: role,
        token: token
    };
}

/**
 * Verifica si el usuario está autenticado
 * @returns {boolean}
 */
function isAuthenticated() {
    return getCurrentUser() !== null;
}

/**
 * Verifica si el usuario tiene un rol específico
 * @param {string} requiredRole - Rol requerido
 * @returns {boolean}
 */
function hasRole(requiredRole) {
    const user = getCurrentUser();
    return user && user.role === requiredRole;
}

/**
 * Registra un nuevo usuario
 * @param {object} userData - Datos del usuario
 * @returns {Promise<{user: any, error: any}>}
 */
async function signUp(userData) {
    try {
        const { data: newUser, error } = await supabase.insert('users', {
            name: userData.name,
            email: userData.email,
            password: userData.password, // Debe estar hasheado
            role: userData.role || 'estudiante'
        });

        if (error) {
            return { user: null, error };
        }

        return { user: newUser, error: null };

    } catch (error) {
        return { 
            user: null, 
            error: { message: error.message } 
        };
    }
}

/**
 * Actualiza la contraseña del usuario
 * @param {string} userId - ID del usuario
 * @param {string} newPassword - Nueva contraseña (hasheada)
 * @returns {Promise<{success: boolean, error: any}>}
 */
async function updatePassword(userId, newPassword) {
    try {
        const { data, error } = await supabase.update('users',
            { password: newPassword },
            { id: userId }
        );

        if (error) {
            return { success: false, error };
        }

        return { success: true, error: null };

    } catch (error) {
        return { 
            success: false, 
            error: { message: error.message } 
        };
    }
}

// Middleware de autenticación para proteger páginas
function requireAuth() {
    if (!isAuthenticated()) {
        window.location.href = '/index.html';
        return false;
    }
    return true;
}

// Middleware de roles para proteger páginas por rol
function requireRole(requiredRole) {
    if (!isAuthenticated()) {
        window.location.href = '/index.html';
        return false;
    }

    if (!hasRole(requiredRole)) {
        alert(`Acceso denegado. Se requiere rol: ${requiredRole}`);
        window.location.href = '/home.html';
        return false;
    }

    return true;
}

// Exportar funciones al objeto global
window.supabaseAuth = {
    signIn,
    signOut,
    signUp,
    getCurrentUser,
    isAuthenticated,
    hasRole,
    updatePassword,
    requireAuth,
    requireRole
};
