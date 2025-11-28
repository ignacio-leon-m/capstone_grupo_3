document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('jwtToken');
    const role = localStorage.getItem('userRole');

    if (!token) {
        window.location.href = '/index.html';
        return;
    }

    if (role !== 'profesor' && role !== 'admin') {
        alert('Acceso denegado. Solo los profesores y administradores pueden acceder a esta función.');
        window.location.href = '/home.html';
        return;
    }

    const fileInput = document.getElementById('fileInput');
    const fileNameSpan = document.getElementById('fileName');
    const subjectSelector = document.getElementById('subjectSelector');
    const uploadButton = document.getElementById('uploadButton');
    const uploadForm = document.getElementById('uploadForm');
    const returnButton = document.querySelector('.return-button');

    if (returnButton) {
        returnButton.addEventListener('click', () => {
            window.location.href = '/home.html';
        });
    }

    // --- Populate Subjects Dropdown ---
    try {
        const meRes = await fetch('/api/users/me', { headers: { 'Authorization': `Bearer ${token}` } });
        if (!meRes.ok) throw new Error('No se pudo obtener el usuario actual.');
        
        const me = await meRes.json();
        const professorId = me.id;

        const subjectsRes = await fetch(`/api/subjects/professor/${professorId}`, { headers: { 'Authorization': `Bearer ${token}` } });
        if (!subjectsRes.ok) throw new Error('Error al cargar asignaturas.');

        const subjects = await subjectsRes.json();
        subjectSelector.innerHTML = '<option value="" selected disabled>-- Elige una asignatura --</option>'; // Placeholder

        if (subjects.length > 0) {
            subjects.forEach(subject => {
                const option = document.createElement('option');
                option.value = subject.id;
                option.textContent = subject.name;
                subjectSelector.appendChild(option);
            });
            subjectSelector.disabled = false;
        } else {
            subjectSelector.innerHTML = '<option selected disabled>No tienes asignaturas asignadas.</option>';
        }
    } catch (error) {
        console.error('Error al cargar asignaturas:', error);
        subjectSelector.innerHTML = '<option selected disabled>Error al cargar asignaturas</option>';
    }

    // --- Enable/Disable Upload Button ---
    const checkFormState = () => {
        const fileSelected = fileInput.files.length > 0;
        const subjectSelected = subjectSelector.value !== '';
        uploadButton.disabled = !(fileSelected && subjectSelected);
    };

    fileInput.addEventListener('change', () => {
        if (fileInput.files.length > 0) {
            fileNameSpan.textContent = `Archivo: ${fileInput.files[0].name}`;
        } else {
            fileNameSpan.textContent = '';
        }
        checkFormState();
    });

    subjectSelector.addEventListener('change', checkFormState);

    // --- Form Submission Logic ---
    uploadForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const subjectId = subjectSelector.value;
        if (!subjectId) {
            alert('Por favor, selecciona una asignatura.');
            return;
        }

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
        formData.append('file', file);
        formData.append('subjectId', subjectId);

        uploadButton.disabled = true;
        uploadButton.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Subiendo...';

        try {
            const res = await fetch('/api/files/upload-excel', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                },
                body: formData
            });

            const responseText = await res.text();
            
            if (res.ok) {
                alert('✓ ' + responseText);
                uploadForm.reset();
                fileNameSpan.textContent = '';
                subjectSelector.selectedIndex = 0;
                uploadButton.disabled = true;
            } else {
                alert('✗ Error (' + res.status + '): ' + responseText);
            }
        } catch (err) {
            console.error('Error de red al subir el archivo:', err);
            alert('✗ Error de red. No se pudo conectar con el servidor.');
        } finally {
            uploadButton.disabled = false;
            uploadButton.innerHTML = '<i class="fas fa-upload me-2"></i>Cargar Alumnos';
        }
    });
});
