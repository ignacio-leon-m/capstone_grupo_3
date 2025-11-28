document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('jwtToken');
    const role = localStorage.getItem('userRole');

    // Redireccionar si no hay token
    if (!token) {
        window.location.href = '/index.html';
        return;
    }
    
    if (role !== 'profesor') {
        alert('Acceso denegado. Solo los profesores pueden acceder a la carga masiva de alumnos.');
        window.location.href = '/home.html';
        return;
    }

    const fileInput = document.getElementById('fileInput');
    const fileNameSpan = document.getElementById('fileName');
    const uploadButton = document.getElementById('uploadButton');
    const uploadForm = document.getElementById('uploadForm');
    const returnButton = document.querySelector('.return-button');

    if (returnButton) {
        returnButton.addEventListener('click', () => {
            window.location.href = '/home.html';
        });
    }

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
                    'Authorization': `Bearer ${token}`
                },
                body: formData
            });

            // Leer la respuesta como texto
            const responseText = await res.text();
            
            console.log('Status:', res.status);
            console.log('Response:', responseText);

            if (res.ok) {
                // Verificar que la respuesta contenga información de procesamiento exitoso
                if (responseText.includes('procesado') || responseText.includes('creados') || responseText.includes('actualizados')) {
                    alert('✓ ' + responseText);
                    fileInput.value = '';
                    fileNameSpan.textContent = 'Ningún archivo seleccionado';
                    uploadButton.disabled = true;
                } else {
                    // Respuesta 200/201 pero sin confirmación clara de procesamiento
                    console.warn('Respuesta inesperada:', responseText);
                    alert('Advertencia: ' + responseText);
                }
            } else {
                // Error HTTP (4xx, 5xx)
                alert('✗ Error (' + res.status + '): ' + responseText);
            }
        } catch (err) {
            console.error('Error de red al subir el archivo:', err);
            alert('✗ Error de red. No se pudo conectar con el servidor.');
        } finally {
            uploadButton.disabled = false;
            uploadButton.textContent = 'Cargar';
        }
    });
    }
);
