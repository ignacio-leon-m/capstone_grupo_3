const CONCEPTS_ENDPOINT = '/api/ai/pdf/concepts';
const SUBJECTS_ENDPOINT = '/api/subjects';

document.addEventListener('DOMContentLoaded', async () => {
    const role = localStorage.getItem('userRole');
    if (role !== 'profesor') {
        alert('Acceso denegado. Sólo profesores pueden acceder.');
        window.location.href = '/home.html';
        return;
    }

    const ui = {
        loadingModal: new bootstrap.Modal(document.getElementById('loadingModal')),
        subject: document.getElementById('subjectSelect'),
        topic: document.getElementById('topicSelect'),
        fileInput: document.getElementById('fileInput'),
        uploadBtn: document.getElementById('uploadButton'),
        fileName: document.getElementById('fileName'),
        dropArea: document.querySelector('.file-drop-area'),
        alert: document.getElementById('resultAlert'),
        msg: document.getElementById('resultMessage')
    };

    let selectedFile = null;
    let subjectsCache = [];
    let topicsCache = new Map();

    const showAlert = (type, msg) => {
        ui.alert.className = `alert alert-${type} alert-dismissible fade show mt-4`;
        ui.msg.innerHTML = msg;
        ui.alert.style.display = 'block';
    };
    const hideAlert = () => (ui.alert.style.display = 'none');

    const updateButtonState = () => {
        const isValid = ui.subject.value && ui.topic.value && selectedFile;
        ui.uploadBtn.disabled = !isValid;
    };

    const resetFileState = () => {
        selectedFile = null;
        ui.fileInput.value = '';
        ui.fileName.style.display = 'none';
        updateButtonState();
    };
    const withTimeout = (promise, ms = 20000) => Promise.race([
        promise,
        new Promise((_, reject) => setTimeout(() => reject(new Error('Tiempo de espera excedido')), ms))
    ]);

    const loadTopics = async (subjectId) => {
        hideAlert();
        ui.topic.disabled = true;
        ui.topic.innerHTML = '<option value="">Cargando temas...</option>';
        if (!subjectId) {
            ui.topic.innerHTML = '<option value="">-- Selecciona un tema --</option>';
            return;
        }
        try {
            let topics = topicsCache.get(subjectId);
            if (!topics) {
                topics = await withTimeout(fetchAPI(`/api/subjects/${subjectId}/topics`));
                topicsCache.set(subjectId, topics);
            }
            if (!topics.length) {
                ui.topic.innerHTML = '<option value="">(Sin temas)</option>';
                showAlert('warning', 'La asignatura seleccionada no tiene temas.');
            } else {
                ui.topic.innerHTML = '<option value="">-- Selecciona un tema --</option>' + topics.map(t => `<option value="${t.id}">${t.name}</option>`).join('');
                ui.topic.disabled = false;
            }
        } catch (e) {
            console.error('Error al cargar temas:', e);
            ui.topic.innerHTML = '<option value="">Error al cargar</option>';
            showAlert('danger', 'No se pudieron cargar los temas.');
        } finally {
            updateButtonState();
        }
    };

    const initView = async () => {
        ui.loadingModal.show();
        try {
            await withTimeout(fetchAPI('/api/users/me'));
            subjectsCache = await withTimeout(fetchAPI(SUBJECTS_ENDPOINT));
            if (!subjectsCache.length) {
                showAlert('warning', '<strong>Atención:</strong> No hay asignaturas disponibles.');
            } else {
                ui.subject.innerHTML = subjectsCache.map(s => `<option value="${s.id}">${s.name}</option>`).join('');
                await loadTopics(ui.subject.value);
            }
        } catch (e) {
            console.error('Error en carga inicial:', e);
            showAlert('danger', `<strong>Error:</strong> Falló la carga inicial (${e.message}).`);
        } finally {
            ui.loadingModal.hide();
            updateButtonState();
        }
    };

    ui.subject.addEventListener('change', async () => {
        ui.loadingModal.show();
        await loadTopics(ui.subject.value);
        ui.loadingModal.hide();
    });

    const setFile = (file) => {
        if (!file) return;
        if (file.type !== 'application/pdf') {
            showAlert('warning', 'Sólo se aceptan archivos PDF.');
            resetFileState();
            return;
        }
        if (file.size > 20 * 1024 * 1024) {
            showAlert('warning', 'El archivo supera el límite de 20MB.');
            resetFileState();
            return;
        }
        selectedFile = file;
        ui.fileName.textContent = `Archivo seleccionado: ${file.name}`;
        ui.fileName.style.display = 'block';
        updateButtonState();
    };

    ui.dropArea.addEventListener('click', () => ui.fileInput.click());
    ui.fileInput.addEventListener('change', (e) => setFile(e.target.files[0]));
    ['dragenter', 'dragover'].forEach(evt => ui.dropArea.addEventListener(evt, (e) => { e.preventDefault(); ui.dropArea.classList.add('drag-over'); }));
    ['dragleave', 'drop'].forEach(evt => ui.dropArea.addEventListener(evt, (e) => { e.preventDefault(); ui.dropArea.classList.remove('drag-over'); }));
    ui.dropArea.addEventListener('drop', (e) => { const file = e.dataTransfer.files[0]; setFile(file); });

    ui.uploadBtn.addEventListener('click', async () => {
        hideAlert();
        if (!selectedFile || !ui.topic.value) {
            showAlert('warning', 'Debe seleccionar tema y archivo PDF.');
            return;
        }
        const formData = new FormData();
        formData.append('file', selectedFile);
        formData.append('topicId', ui.topic.value);
        formData.append('max', '30');
        
        ui.loadingModal.show();
        ui.uploadBtn.disabled = true;

        try {
            const res = await withTimeout(fetchAPI(CONCEPTS_ENDPOINT, { method: 'POST', body: formData }), 60000);
            const inserted = res.inserted || 0;
            const examplesHtml = (res.examples || []).map(ex => `<li><strong>${ex.word}</strong>: ${ex.hint}</li>`).join('');
            showAlert('success', `Se generaron <strong>${inserted}</strong> conceptos.<br/>Ejemplos:<ul class="mb-0">${examplesHtml}</ul>`);
            resetFileState();
        } catch (e) {
            console.error('Error al generar conceptos:', e);
            showAlert('danger', `Error al generar conceptos: ${e.message}`);
        } finally {
            ui.loadingModal.hide();
            ui.uploadBtn.disabled = false;
            updateButtonState();
        }
    });

    await initView();
});
