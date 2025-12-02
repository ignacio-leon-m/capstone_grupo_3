// Refactored content-upload.js (clean, modern, stable)

const SUBJECTS_ENDPOINT = '/api/subjects';
const CONCEPTS_ENDPOINT = '/api/concepts';

document.addEventListener('DOMContentLoaded', async () => {
    // UI SELECTORS
    const ui = {
        subject: document.getElementById('subjectSelect'),
        topic: document.getElementById('topicSelect'),
        fileInput: document.getElementById('fileInput'),
        uploadBtn: document.getElementById('uploadButton'),
        fileName: document.getElementById('fileName'),
        dropArea: document.querySelector('.file-drop-area'),
        alert: document.getElementById('resultAlert'),
        msg: document.getElementById('resultMessage')
    };


    // STATE
    let selectedFile = null;
    const originalUploadText = ui.uploadBtn.innerHTML;


    // ALERT HELPERS
    const showAlert = (type, html) => {
        ui.alert.className = `alert alert-${type} alert-dismissible fade show mt-4`;
        ui.msg.innerHTML = html;
        ui.alert.style.display = 'block';
    };

    const hideAlert = () => {
        ui.alert.style.display = 'none';
    };

    // BUTTON STATE
    const updateButtonState = () => {
        const ready = ui.subject.value && ui.topic.value && selectedFile;
        ui.uploadBtn.disabled = !ready;
    };

    // UI SPINNER TOGGLE
    const toggleProcessing = (loading, text = 'Procesando...') => {
        if (loading) {
            ui.uploadBtn.disabled = true;
            ui.uploadBtn.innerHTML = `<span class="spinner-border spinner-border-sm me-2"></span>${text}`;
        } else {
            ui.uploadBtn.innerHTML = originalUploadText;
        }
    };

    // RESET FILE SELECTOR
    const resetFile = () => {
        selectedFile = null;
        ui.fileInput.value = '';
        ui.fileName.style.display = 'none';
        updateButtonState();
    };

    // SET FILE HANDLER
    const setFile = (file) => {
        if (!file) return;
        if (file.type !== 'application/pdf') {
            showAlert('warning', 'Sólo se permiten archivos PDF.');
            return resetFile();
        }
        if (file.size > 20 * 1024 * 1024) {
            showAlert('warning', 'El archivo supera el límite de 20MB.');
            return resetFile();
        }

        selectedFile = file;
        ui.fileName.textContent = `Archivo seleccionado: ${file.name}`;
        ui.fileName.style.display = 'block';
        updateButtonState();
    };


    // TOPIC LIST
    const loadTopics = async (subjectId) => {
        hideAlert();
        ui.topic.disabled = true;

        if (!subjectId) {
            ui.topic.innerHTML = '<option value="">Selecciona un tema</option>';
            return;
        }

        ui.topic.innerHTML = '<option>Cargando temas...</option>';

        try {
            const topics = await window.fetchAPI(`/api/subjects/${subjectId}/topics`);

            if (!topics || !topics.length) {
                ui.topic.innerHTML = '<option value="">Sin temas</option>';
                showAlert('warning', 'La asignatura seleccionada no tiene temas.');
                return;
            }

            ui.topic.innerHTML =
                '<option value="">Selecciona un tema</option>' +
                topics.map(t => `<option value="${t.id}">${t.name}</option>`).join('');

            ui.topic.disabled = false;
        } catch (e) {
            console.error('Error cargando temas:', e);
            ui.topic.innerHTML = '<option>Error al cargar</option>';
            showAlert('danger', 'No se pudieron cargar los temas.');
        } finally {
            updateButtonState();
        }
    };

     // INITIAL VIEW
    const init = async () => {
        ui.subject.disabled = true;
        ui.topic.disabled = true;

        try {
            const subjects = await window.fetchAPI(SUBJECTS_ENDPOINT);

            if (!subjects.length) {
                showAlert('warning', 'No hay asignaturas disponibles.');
                return;
            }

            ui.subject.innerHTML = '<option value="">Seleccione una asignatura</option>' +
                subjects.map(s => `<option value="${s.id}">${s.name}</option>`).join('');
        } catch (e) {
            console.error('Error en carga inicial:', e);
            showAlert('danger', 'Error en carga inicial.');
        } finally {
            ui.subject.disabled = false;
        }
    };


    // EVENT LISTENERS
    ui.subject.addEventListener('change', async () => {
        ui.topic.innerHTML = '<option>...</option>';
        ui.topic.disabled = true;
        await loadTopics(ui.subject.value);
    });

    ui.fileInput.addEventListener('change', (e) => setFile(e.target.files[0]));

    ui.dropArea.addEventListener('click', () => ui.fileInput.click());

    ui.dropArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        ui.dropArea.classList.add('drag-over');
    });

    ui.dropArea.addEventListener('dragleave', () => ui.dropArea.classList.remove('drag-over'));

    ui.dropArea.addEventListener('drop', (e) => {
        e.preventDefault();
        ui.dropArea.classList.remove('drag-over');
        setFile(e.dataTransfer.files[0]);
    });

    ui.uploadBtn.addEventListener('click', async () => {
        hideAlert();
        if (!selectedFile || !ui.topic.value) {
            showAlert('warning', 'Debe seleccionar un tema y un archivo PDF.');
            return;
        }

        const form = new FormData();
        form.append('file', selectedFile);
        form.append('topicId', ui.topic.value);
        form.append('max', '30');

        toggleProcessing(true, 'Subiendo...');

        try {
            const res = await window.fetchAPI(CONCEPTS_ENDPOINT, { method: 'POST', body: form });

            const inserted = res.inserted || 0;
            const list = (res.examples || [])
                .map(ex => `<li><strong>${ex.word}</strong>: ${ex.hint}</li>`)\n                .join('');

            showAlert('success', `Se generaron <strong>${inserted}</strong> conceptos:<ul>${list}</ul>`);
            resetFile();
        } catch (e) {
            console.error('Error al subir contenido:', e);
            showAlert('danger', `Error: ${e.message}`);
        } finally {
            toggleProcessing(false);
            updateButtonState();
        }
    });

    // START
    await init();
});
