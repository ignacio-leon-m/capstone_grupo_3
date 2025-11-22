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

    if (role === 'profesor' && currentPath === '/create-professor.html') {
        alert('Acceso denegado. Solo los administradores pueden crear profesores.');
        window.location.href = '/home.html';
        return;
    }

    if (role !== 'profesor' && (currentPath === '/user-upload.html' || currentPath === '/user.html')) {
        alert('Acceso denegado. Solo los profesores pueden gestionar la carga de alumnos.');
        window.location.href = '/home.html';
        return;
    }

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
