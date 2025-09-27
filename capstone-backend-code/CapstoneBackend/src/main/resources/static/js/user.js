document.addEventListener('DOMContentLoaded', () => {
    const fileInput = document.getElementById('fileInput');
    const fileNameSpan = document.getElementById('fileName');
    const uploadButton = document.getElementById('uploadButton');

    fileInput.addEventListener('change', () => {
        if (fileInput.files.length > 0) {
            fileNameSpan.textContent = fileInput.files[0].name;
            uploadButton.disabled = false;
        } else {
            fileNameSpan.textContent = 'Ningún archivo seleccionado';
            uploadButton.disabled = true;
        }
    });
});