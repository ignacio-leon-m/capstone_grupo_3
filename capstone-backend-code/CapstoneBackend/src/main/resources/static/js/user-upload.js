document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('jwtToken');
    const role = localStorage.getItem('userRole');

    // Redirecciona si no hay token
    if (!token) {
        window.location.href = '/index.html';
        return;
    }
    // Redirecciona si el rol no es profesor
    if (role !== 'profesor') {
        alert('Acceso denegado. Solo los profesores pueden acceder a esta página.');
        window.location.href = '/home.html';
        return;
    }

    const fileInput = document.getElementById('fileInput');
    const fileNameSpan = document.getElementById('fileName');
    const uploadButton = document.getElementById('uploadButton');
    const uploadForm = document.getElementById('uploadForm');

    fileInput.addEventListener('change', () => {
        if (fileInput.files.length > 0) {
            fileNameSpan.textContent = fileInput.files[0].name;
            uploadButton.disabled = false;
        } else {
            fileNameSpan.textContent = 'Ningún archivo seleccionado';
            uploadButton.disabled = true;
        }
    });

    uploadForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        if (fileInput.files.length === 0) {
            alert('Por favor, selecciona un archivo .xlsx para subir.');
            return;
        }

        const file = fileInput.files[0];
        if (!file.name.toLowerCase().endsWith('.xlsx') && !file.name.toLowerCase().endsWith('.xls')) {
            alert('El archivo debe ser de tipo Excel (.xlsx o .xls).');
            return;
        }

        const formData = new FormData();
        formData.append('file', file); // El nombre 'file' debe coincidir con @RequestParam("file")

        uploadButton.disabled = true;
        uploadButton.textContent = 'Subiendo...';

        try {
            const res = await fetch('/api/files/upload-excel', {
                method: 'POST',
                headers: {
                    // NO incluye 'Content-Type' porque el navegador lo gestiona con FormData
                    'Authorization': `Bearer ${token}` // 3. Autenticación con Token
                },
                body: formData
            });

            const responseText = await res.text();

            if (res.ok) {
                alert('Éxito: ' + responseText);
                fileInput.value = ''; // Limpiar el input
                fileNameSpan.textContent = 'Ningún archivo seleccionado';
            } else {
                alert('Error: ' + responseText);
            }
        } catch (err) {
            console.error('Error de red al subir el archivo:', err);
            alert('Error de red. No se pudo conectar con el servidor.');
        } finally {
            uploadButton.disabled = false;
            uploadButton.textContent = 'Cargar';
        }
    });

    // Lógica del botón de logout
    const logoutButton = document.querySelector('.logout-button');
    if(logoutButton) {
        logoutButton.addEventListener('click', (e) => {
            e.preventDefault();
            localStorage.removeItem('jwtToken');
            localStorage.removeItem('userRole');
            window.location.href = '/index.html';
        });
    }

    const returnButton = document.querySelector('.return-button');
    if(returnButton) {
        returnButton.addEventListener('click', (e) => {
            e.preventDefault();
            window.history.back();
        });
    }
}
);
