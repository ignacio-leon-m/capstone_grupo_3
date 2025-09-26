// Lógica para la página content-upload.html
// Ajusta UPLOAD_URL a la ruta real del backend que maneja la carga de contenido/documentos.
const UPLOAD_URL = 'http://localhost:8080/api/content/upload'; // <-- Cambia esto si tu endpoint es distinto

document.addEventListener('DOMContentLoaded', () => {
  const fileInput = document.getElementById('fileInput');
  const uploadButton = document.getElementById('uploadButton');
  const contentInput = document.getElementById('contentInput');

  // Habilitar botón si hay texto o archivos
  const updateButtonState = () => {
    const hasText = contentInput && contentInput.value.trim().length > 0;
    const hasFiles = fileInput && fileInput.files && fileInput.files.length > 0;
    uploadButton.disabled = !(hasText || hasFiles);
  };

  if (contentInput) contentInput.addEventListener('input', updateButtonState);
  if (fileInput) fileInput.addEventListener('change', updateButtonState);

  // Soporte drag & drop para file-drop-area
  const dropArea = document.querySelector('.file-drop-area');
  if (dropArea) {
    dropArea.addEventListener('click', () => fileInput.click());

    dropArea.addEventListener('dragover', (e) => {
      e.preventDefault();
      dropArea.classList.add('drag-over');
    });
    dropArea.addEventListener('dragleave', (e) => {
      e.preventDefault();
      dropArea.classList.remove('drag-over');
    });
    dropArea.addEventListener('drop', (e) => {
      e.preventDefault();
      dropArea.classList.remove('drag-over');
      if (e.dataTransfer && e.dataTransfer.files && e.dataTransfer.files.length > 0) {
        fileInput.files = e.dataTransfer.files;
        updateButtonState();
      }
    });
  }

  // Envío al backend
  uploadButton.addEventListener('click', async (e) => {
    e.preventDefault();

    const formData = new FormData();
    // Si hay texto en textarea, enviarlo como campo 'content'
    if (contentInput && contentInput.value.trim().length > 0) {
      formData.append('content', contentInput.value.trim());
    }

    // Añadir archivos si hay
    if (fileInput && fileInput.files && fileInput.files.length > 0) {
      for (let i = 0; i < fileInput.files.length; i++) {
        // nombre del campo 'files' — ajusta según espera el backend
        formData.append('files', fileInput.files[i]);
      }
    }

    try {
      uploadButton.disabled = true;
      uploadButton.textContent = 'Enviando...';

      const resp = await fetch(UPLOAD_URL, {
        method: 'POST',
        credentials: 'include',
        body: formData
      });

      if (!resp.ok) {
        const text = await resp.text();
        console.error('Error al enviar contenido:', resp.status, text);
        alert('Error al enviar contenido: ' + (text || resp.status));
        return;
      }

      let resultText = 'Contenido enviado correctamente.';
      try {
        const json = await resp.json();
        if (json && json.message) resultText = json.message;
      } catch (err) {}

      alert(resultText);

      // limpiar inputs
      if (contentInput) contentInput.value = '';
      if (fileInput) fileInput.value = '';
      updateButtonState();

    } catch (err) {
      console.error('Fallo de red al enviar contenido:', err);
      alert('Fallo de red al enviar contenido');
    } finally {
      uploadButton.disabled = false;
      uploadButton.textContent = 'Enviar';
    }
  });
});

