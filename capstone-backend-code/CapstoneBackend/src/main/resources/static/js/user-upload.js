// Lógica para la página user-upload.html
// Ajusta UPLOAD_URL a la ruta real del backend que maneja la carga de Excel de alumnos.
const UPLOAD_URL = '/api/users/upload'; // <-- Cambia esto si tu endpoint es distinto

document.addEventListener('DOMContentLoaded', () => {
  const fileInput = document.getElementById('fileInput');
  const fileNameSpan = document.getElementById('fileName');
  const uploadButton = document.getElementById('uploadButton');
  const uploadForm = document.getElementById('uploadForm');

  // Cuando el usuario selecciona un archivo
  fileInput.addEventListener('change', () => {
    if (fileInput.files && fileInput.files.length > 0) {
      const f = fileInput.files[0];
      fileNameSpan.textContent = f.name;
      uploadButton.disabled = false;
    } else {
      fileNameSpan.textContent = 'Ningún archivo seleccionado';
      uploadButton.disabled = true;
    }
  });

  // Submit del formulario: enviar multipart/form-data
  uploadForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    if (!fileInput.files || fileInput.files.length === 0) {
      alert('Selecciona un archivo .xlsx antes de subir.');
      return;
    }

    const file = fileInput.files[0];
    // Validación básica de extensión
    if (!file.name.toLowerCase().endsWith('.xlsx')) {
      alert('El archivo debe tener extensión .xlsx');
      return;
    }

    const formData = new FormData();
    // El nombre del campo "file" debe coincidir con lo que espera el backend
    formData.append('file', file);

    try {
      uploadButton.disabled = true;
      uploadButton.textContent = 'Subiendo...';

      const resp = await fetch(UPLOAD_URL, {
        method: 'POST',
        credentials: 'include',
        body: formData
      });

      if (!resp.ok) {
        const text = await resp.text();
        console.error('Error al subir archivo:', resp.status, text);
        alert('Error al subir archivo: ' + (text || resp.status));
        return;
      }

      // Si el backend devuelve JSON con detalles, podemos parsearlo
      let resultText = 'Archivo subido correctamente.';
      try {
        const json = await resp.json();
        if (json && json.message) resultText = json.message;
      } catch (err) {
        // no es JSON, ignorar
      }

      alert(resultText);

      // Actualizar UI: limpiar input y desactivar botón
      fileInput.value = '';
      fileNameSpan.textContent = 'Ningún archivo seleccionado';
      uploadButton.disabled = true;

      // Opcional: recargar la página o actualizar la tabla de historial si el backend devuelve el nuevo registro
      // location.reload();

    } catch (err) {
      console.error('Fallo de red al subir archivo:', err);
      alert('Fallo de red al subir archivo');
    } finally {
      uploadButton.disabled = false;
      uploadButton.textContent = 'Cargar';
    }
  });
});
