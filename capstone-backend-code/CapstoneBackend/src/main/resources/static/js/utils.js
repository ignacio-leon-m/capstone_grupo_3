// utils.js - Validación de autenticación y permisos para vistas administrativas

document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('jwtToken');
    const role = localStorage.getItem('userRole');

    // Redireccionar si no hay token
    if (!token) {
        window.location.href = '/index.html';
        return;
    }

    // Verificar que existe el rol
    if (!role) {
        alert('Error: No se pudo determinar tu rol de usuario.');
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('userRole');
        window.location.href = '/index.html';
        return;
    }

    const currentPath = window.location.pathname;

    // Validación de permisos por página
    // Solo ADMIN puede crear profesores
    if (role !== 'admin' && currentPath === '/create-professor.html') {
        alert('Acceso denegado. Solo los administradores pueden crear profesores.');
        window.location.href = '/home.html';
        return;
    }

    // Solo PROFESOR puede gestionar carga masiva de alumnos (Excel)
    if (role === 'admin' && currentPath === '/user-upload.html') {
        alert('Acceso denegado. Solo los profesores pueden realizar la carga masiva de alumnos.');
        window.location.href = '/home.html';
        return;
    }

    // Solo PROFESOR puede ver sus materias asignadas
    if (role === 'admin' && currentPath === '/professor-subject.html') {
        alert('Acceso denegado. Esta vista es solo para profesores.');
        window.location.href = '/home.html';
        return;
    }

    // ADMIN y PROFESOR pueden acceder a:
    // - content.html (ver contenidos)
    // - content-upload.html (cargar contenidos)
    // - user.html (modal de usuarios - ADMIN puede ver, PROFESOR gestiona)

    const logoutButton = document.querySelector('.logout-button');
    if (logoutButton) {
        logoutButton.addEventListener('click', (e) => {
            e.preventDefault();
            localStorage.removeItem('jwtToken');
            localStorage.removeItem('userRole');
            window.location.href = '/index.html';
        });
    }

    console.log('Usuario autenticado:', role, 'en vista:', currentPath);
});
