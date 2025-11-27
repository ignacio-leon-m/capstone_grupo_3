// GLOBAL HELPER (Outside DOMContentLoaded)
window.fetchAPI = async function(url, options = {}) {
    const token = localStorage.getItem('jwtToken');
    if (!token) throw new Error('No Auth Token');

    const headers = { 'Authorization': `Bearer ${token}`, ...options.headers };

    // Auto-detect JSON vs FormData
    if (!(options.body instanceof FormData)) {
        headers['Content-Type'] = 'application/json';
    }

    const res = await fetch(url, { ...options, headers });

    // Session expired or unauthorized
    if (res.status === 401 || res.status === 403) {
        console.warn('Sesión expirada o sin permisos (401/403)');
        localStorage.clear();
        window.location.href = '/index.html';
        throw new Error('Sesión expirada');
    }

    // Manejo de errores de negocio
    if (!res.ok) {
        const errorText = await res.text();
        try {
            const errJson = JSON.parse(errorText);
            throw new Error(errJson.message || `Error ${res.status}`);
        } catch (e) {
            if (res.status === 413) throw new Error('El archivo es demasiado grande (Máx 20MB).');
            throw new Error(errorText || `Error ${res.status}`);
        }
    }

    return await res.json();
};

// Immediate Auth & Role Check
// Reduce the risk of flashing unauthorized content
(function checkAuth() {
    const isPublicPage = window.location.pathname.endsWith('index.html') || window.location.pathname === '/';
    if (isPublicPage) return; // No validar en login

    const token = localStorage.getItem('jwtToken');
    const role = localStorage.getItem('userRole');

    // Base Auth Check
    if (!token || !role) {
        console.warn('Usuario no autenticado, redirigiendo...');
        localStorage.clear(); // Limpieza preventiva
        window.location.href = '/index.html';
        return;
    }

    // Route Specific Role Checks
    const path = window.location.pathname;

    // Admin Only Pages
    if (path.endsWith('create-professor.html') && role !== 'admin') {
        alert('Acceso denegado. Solo administradores.');
        window.location.href = '/home.html';
    }

    // Professor Only Pages
    if ((path.endsWith('user-upload.html') || path.endsWith('professor-subject.html')) && role === 'admin') {
        alert('Acceso denegado. Vista exclusiva para profesores.');
        window.location.href = '/home.html';
    }
    
    // Let the page load normally for authorized users
})();

// DOM Logic (Logout, UI elements)
document.addEventListener('DOMContentLoaded', () => {
    const role = localStorage.getItem('userRole');
    const path = window.location.pathname;
    
    console.log(`Auth OK. Rol: ${role} | Vista: ${path}`);

    // Logout Button Handler
    const logoutButton = document.querySelector('.logout-button');
    if (logoutButton) {
        logoutButton.addEventListener('click', (e) => {
            e.preventDefault();
            localStorage.clear();
            window.location.href = '/index.html';
        });
    }
});