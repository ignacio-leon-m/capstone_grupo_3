document.addEventListener('DOMContentLoaded', () => {
    const fileDropArea = document.querySelector('.file-drop-area');
    const fileInput = document.getElementById('fileInput');
    const uploadButton = document.getElementById('uploadButton');
    const contentInput = document.getElementById('contentInput');

    function checkContent() {
        // Habilita el botón si hay texto o un archivo seleccionado
        if (contentInput.value.length > 0 || fileInput.files.length > 0) {
            uploadButton.disabled = false;
        } else {
            uploadButton.disabled = true;
        }
    }

    // Eventos para el área de arrastrar y soltar
    fileDropArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        fileDropArea.classList.add('is-active');
    });

    fileDropArea.addEventListener('dragleave', () => {
        fileDropArea.classList.remove('is-active');
    });

    fileDropArea.addEventListener('drop', (e) => {
        e.preventDefault();
        fileDropArea.classList.remove('is-active');
        fileInput.files = e.dataTransfer.files;
        checkContent();
    });

    // Evento para el botón de seleccionar archivo
    fileDropArea.addEventListener('click', () => {
        fileInput.click();
    });

    // Eventos para la validación del botón
    fileInput.addEventListener('change', checkContent);
    contentInput.addEventListener('input', checkContent);
});