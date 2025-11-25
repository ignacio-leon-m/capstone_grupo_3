
const CONCEPTS_ENDPOINT = '/api/ai/pdf/concepts';

document.addEventListener('DOMContentLoaded', async () => {

    const token = localStorage.getItem('jwtToken');
    const role = localStorage.getItem('userRole');
    
    if (!token) {
        window.location.href = '/index.html';
        return;
    }

    if (role !== 'profesor') {
        alert('Acceso denegado. Sólo profesores pueden acceder.');
        window.location.href = '/home.html';
        return;
    }

    const authHeader = { 'Authorization': `Bearer ${token}` };
    const loadingOverlay = document.getElementById('loadingOverlay');
    const subjectSelect = document.getElementById('subjectSelect');
    const topicSelect = document.getElementById('topicSelect');
    const fileInput = document.getElementById('fileInput');
    const uploadButton = document.getElementById('uploadButton');
    const fileNameDisplay = document.getElementById('fileName');
    const dropArea = document.querySelector('.file-drop-area');
    const resultAlert = document.getElementById('resultAlert');
    const resultMessage = document.getElementById('resultMessage');

    let selectedFile = null;
    let myUserId = null;

    try {
        const meRes = await fetch('/api/users/me', { headers: authHeader });
        
        if (!meRes.ok) {
            if (meRes.status === 401 || meRes.status === 403) {
                alert('Sesión expirada. Por favor inicia sesión nuevamente.');
                localStorage.clear();
                window.location.href = '/index.html';
                return;
            }
            throw new Error(`Error ${meRes.status}: ${meRes.statusText}`);
        }
        const me = await meRes.json();
        myUserId = me.id;
    } catch (err) {
        console.error('Error when load user info:', err);
        showError('Error al cargar información del usuario. Verifica tu conexión.');
        return;
    }

    try {
        console.log('Loading subject from:', myUserId);
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 10000);
        
        const subjectsRes = await fetch(`/api/subjects/professor/${myUserId}`, { 
            headers: authHeader,
            signal: controller.signal 
        });
        
        clearTimeout(timeoutId);
        
        console.log('Server response:', subjectsRes.status, subjectsRes.statusText);
        
        if (!subjectsRes.ok) {
            const errorText = await subjectsRes.text();
            console.error('Error while loading subjects:', errorText);
            throw new Error(`Error ${subjectsRes.status}: ${errorText}`);
        }
        
        const subjects = await subjectsRes.json();
        console.log('Total subjects:', subjects.length);

        if (subjects.length === 0) {
            loadingOverlay.classList.add('d-none');
            showWarning('No tienes asignaturas asignadas todavía. Contacta al administrador para que te asigne asignaturas.');
            return;
        }

        // Fill subject selector
        subjects.forEach(subject => {
            const option = document.createElement('option');
            option.value = subject.id;
            option.textContent = subject.name;
            subjectSelect.appendChild(option);
        });

        // Hide loading overlay
        loadingOverlay.classList.add('d-none');
    } catch (err) {
        console.error('Error at subject load:', err);
        loadingOverlay.classList.add('d-none');
        
        if (err.name === 'AbortError') {
            showError('La solicitud tardó demasiado. Por favor verifica tu conexión o que el servidor esté activo.');
        } else {
            showError(`Error al cargar las asignaturas: ${err.message}`);
        }
        return;
    }

    // Event: Subject selected -> Load topics

    subjectSelect.addEventListener('change', async () => {
        const subjectId = subjectSelect.value;
        
        // Reset topic selector and file input
        topicSelect.innerHTML = '<option value="">Selecciona un tema</option>';
        topicSelect.disabled = true;
        selectedFile = null;
        fileNameDisplay.style.display = 'none';
        fileInput.value = '';
        updateButtonState();

        if (!subjectId) return;

        topicSelect.innerHTML = '<option value="">Cargando temas</option>';

        try {
            const topicsRes = await fetch(`/api/subjects/${subjectId}/topics`, { headers: authHeader });
            if (!topicsRes.ok) throw new Error('Error al cargar temas');

            const topics = await topicsRes.json();

            // Clean and fill topic selector
            topicSelect.innerHTML = '<option value="">Selecciona un tema</option>';

            if (topics.length === 0) {
                showWarning('Esta asignatura no tiene temas.');
                return;
            }

            topics.forEach(topic => {
                const option = document.createElement('option');
                option.value = topic.id;
                option.textContent = topic.name;
                topicSelect.appendChild(option);
            });

            topicSelect.disabled = false;

        } catch (err) {
            console.error('Error al cargar temas:', err);
            topicSelect.innerHTML = '<option value="">Error al cargar temas</option>';
            showError('Error al cargar los temas de la asignatura');
        }
    });


//    Event: Topic selection change
    topicSelect.addEventListener('change', () => {
        updateButtonState();
    });


    // Drag & Drop
    dropArea.addEventListener('click', () => fileInput.click());

    dropArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        dropArea.classList.add('drag-over');
    });

    dropArea.addEventListener('dragleave', () => {
        dropArea.classList.remove('drag-over');
    });

    dropArea.addEventListener('drop', (e) => {
        e.preventDefault();
        dropArea.classList.remove('drag-over');
        
        if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
            const file = e.dataTransfer.files[0];
            handleFileSelection(file);
        }
    });

    fileInput.addEventListener('change', () => {
        if (fileInput.files && fileInput.files.length > 0) {
            handleFileSelection(fileInput.files[0]);
        }
    });

    function handleFileSelection(file) {
        // Limpiar alertas anteriores al seleccionar nuevo archivo
        hideAlert();
        
        if (!file.name.toLowerCase().endsWith('.pdf')) {
            showError('Solo se permiten archivos PDF');
            return;
        }

        // Validar tamaño del archivo (50MB máximo)
        const maxSize = 50 * 1024 * 1024; // 50MB en bytes
        if (file.size > maxSize) {
            const sizeMB = (file.size / (1024 * 1024)).toFixed(2);
            showError(`El archivo es demasiado grande (${sizeMB} MB). El tamaño máximo permitido es 50 MB.`);
            return;
        }

        selectedFile = file;
        const sizeMB = (file.size / (1024 * 1024)).toFixed(2);
        fileNameDisplay.textContent = `📄 ${file.name} (${sizeMB} MB)`;
        fileNameDisplay.style.display = 'block';
        console.log('✅ Archivo seleccionado:', file.name, `(${sizeMB} MB)`);
        updateButtonState();
    }

    //  Validate button state
    function updateButtonState() {
        const hasSubject = subjectSelect.value !== '';
        const hasTopic = topicSelect.value !== '';
        const hasFile = selectedFile !== null;

        uploadButton.disabled = !(hasSubject && hasTopic && hasFile);
    }

    // Event: Upload button clicked
    uploadButton.addEventListener('click', async (e) => {
        e.preventDefault();

        const subjectId = subjectSelect.value;
        const topicId = topicSelect.value;

        if (!subjectId || !topicId || !selectedFile) {
            showError('Debes seleccionar asignatura, tema y archivo PDF');
            return;
        }

        // Prepare form data
        const formData = new FormData();
        formData.append('file', selectedFile);
        formData.append('topicId', topicId);
        formData.append('max', '30'); // Máximo 30 conceptos

        // Cancel button and show loading state
        uploadButton.disabled = true;
        const frontSpan = uploadButton.querySelector('.front');
        if (frontSpan) {
            frontSpan.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Procesando PDF con IA';
        }

        console.log('Sending PDF to server for concept generation:');
        console.log('Subject:', subjectSelect.options[subjectSelect.selectedIndex].text);
        console.log('Theme:', topicSelect.options[topicSelect.selectedIndex].text);
        console.log('File:', selectedFile.name);

        try {
            const response = await fetch(CONCEPTS_ENDPOINT, {
                method: 'POST',
                headers: authHeader,
                body: formData
            });

            if (!response.ok) {
                const errorText = await response.text();
                
                // Manejo específico de error de tamaño excedido
                if (response.status === 413 || errorText.includes('Maximum upload size exceeded')) {
                    throw new Error('El archivo excede el tamaño máximo permitido (50 MB). Por favor, selecciona un archivo más pequeño.');
                }
                
                throw new Error(`Error ${response.status}: ${errorText}`);
            }

            const result = await response.json();
            console.log('Server response:', result);

            // Show results
            const inserted = result.inserted || 0;
            const examples = result.examples || [];

            if (inserted > 0) {
                let examplesHTML = '';
                if (examples.length > 0) {
                    examplesHTML = '<div class="mt-3"><strong>Ejemplos de conceptos generados:</strong><ul class="list-unstyled mt-2">';
                    examples.forEach(ex => {
                        examplesHTML += `<li class="mb-1">• <strong>${ex.word}</strong>: ${ex.hint}</li>`;
                    });
                    examplesHTML += '</ul></div>';
                }

                showSuccess(
                    `<strong>Contenido generado exitosamente</strong><br>` +
                    `Se crearon ${inserted} conceptos a partir del PDF.${examplesHTML}`
                );

                // Reset form
                setTimeout(() => {
                    subjectSelect.selectedIndex = 0;
                    topicSelect.innerHTML = '<option value="">Selecciona un tema</option>';
                    topicSelect.disabled = true;
                    selectedFile = null;
                    fileInput.value = '';
                    fileNameDisplay.style.display = 'none';
                    updateButtonState();
                }, 3000);

            } else {
                showWarning('No se pudieron generar conceptos del PDF. Verifica que el archivo contenga texto legible.');
            }

        } catch (err) {
            console.error('❌ Error al procesar archivo:', err);
            
            // Intentar parsear el error como JSON
            let errorMessage = err.message;
            if (err.message.includes('{')) {
                try {
                    const errorJson = JSON.parse(err.message.substring(err.message.indexOf('{')));
                    errorMessage = errorJson.message || errorMessage;
                } catch (parseErr) {
                    // Si no se puede parsear, usar el mensaje original
                }
            }
            
            showError(`Error al procesar el archivo: ${errorMessage}`);
        } finally {
            // Restore button state
            uploadButton.disabled = false;
            if (frontSpan) {
                frontSpan.innerHTML = '<i class="fas fa-paper-plane me-2"></i>Generar Contenido con IA';
            }
            updateButtonState();
        }
    });


    // Alert display functions
    function showSuccess(message) {
        resultAlert.className = 'alert alert-success alert-dismissible fade show mt-4';
        resultMessage.innerHTML = message;
        resultAlert.style.display = 'block';
    }

    function showError(message) {
        resultAlert.className = 'alert alert-danger alert-dismissible fade show mt-4';
        resultMessage.innerHTML = `<strong>Error:</strong> ${message}`;
        resultAlert.style.display = 'block';
    }

    function showWarning(message) {
        resultAlert.className = 'alert alert-warning alert-dismissible fade show mt-4';
        resultMessage.innerHTML = `<strong>Atención:</strong> ${message}`;
        resultAlert.style.display = 'block';
    }

    function hideAlert() {
        resultAlert.style.display = 'none';
        resultMessage.innerHTML = '';
    }

});
