package com.example.front.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.front.data.remote.ApiClient
import com.example.front.data.remote.dto.MateriaDto
import com.example.front.data.remote.dto.TemaListItemDto
import kotlinx.coroutines.launch

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import com.example.front.data.remote.dto.QuizDto
import com.example.front.data.remote.dto.TemaDetailDto
import com.example.front.data.remote.dto.DocumentoDto

import com.example.front.util.toTextPart
import com.example.front.util.uriToMultipartIO
import androidx.compose.ui.platform.LocalContext

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MateriasScreen(
    onOpenMateria: (materiaId: String, materiaName: String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var items by remember { mutableStateOf<List<MateriaDto>>(emptyList()) }
    val api = remember { ApiClient.api }

    LaunchedEffect(Unit) {
        loading = true
        error = null
        try {
            val res = api.materias()
            if (res.isSuccessful && res.body() != null) {
                items = res.body()!!
            } else {
                error = "Error ${res.code()}"
            }
        } catch (e: Exception) {
            error = e.message ?: "Error de red"
        } finally {
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Materias") })
        }
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
        ) {
            when {
                loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                error != null -> Text("Error: $error", color = MaterialTheme.colorScheme.error)
                else -> {
                    if (items.isEmpty()) {
                        Text("No hay materias disponibles")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(items) { m ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onOpenMateria(m.id, m.name)
                                        },
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text(m.name, style = MaterialTheme.typography.titleMedium)
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            m.carrera,
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemasScreen(
    materiaId: String,
    materiaName: String,
    onOpenTema: (temaId: String, temaTitulo: String) -> Unit
) {
    val api = remember { ApiClient.api }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var items by remember { mutableStateOf<List<TemaListItemDto>>(emptyList()) }
    var esProfe by remember { mutableStateOf(false) }

    // Dialog de subir
    var showUpload by remember { mutableStateOf(false) }
    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var subiendo by remember { mutableStateOf(false) }
    var subirError by remember { mutableStateOf<String?>(null) }

    // Picker de archivos (PDF/DOCX)
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        pickedUri = uri
    }

    fun cargarTemas() {
        loading = true
        error = null
        scope.launch {
            try {
                // rol
                run {
                    val me = api.me()
                    esProfe = me.isSuccessful && me.body()?.role?.name in listOf("ROLE_ADMIN", "ROLE_PROFESOR")
                }
                // lista
                val res = api.listarTemas(materiaId)
                if (res.isSuccessful && res.body() != null) {
                    items = res.body()!!
                } else {
                    error = "Error ${res.code()}"
                }
            } catch (e: Exception) {
                error = e.message ?: "Error de red"
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(materiaId) { cargarTemas() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Temas • $materiaName") },
                actions = {
                    if (esProfe) {
                        TextButton(onClick = {
                            titulo = ""
                            descripcion = ""
                            pickedUri = null
                            subirError = null
                            showUpload = true
                        }) {
                            Text("Subir")
                        }
                    }
                }
            )
        }
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
        ) {
            when {
                loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                error != null -> Text("Error: $error", color = MaterialTheme.colorScheme.error)
                else -> {
                    if (items.isEmpty()) {
                        Text("No hay temas aún en esta materia")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(items) { t ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onOpenTema(t.id, t.titulo) },
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text(t.titulo, style = MaterialTheme.typography.titleMedium)
                                        if (!t.resumenPreview.isNullOrBlank()) {
                                            Spacer(Modifier.height(6.dp))
                                            Text(
                                                t.resumenPreview!!,
                                                style = MaterialTheme.typography.bodySmall,
                                                maxLines = 3,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            "Documentos: ${t.documentos}",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog para subir documento
    if (showUpload) {
        AlertDialog(
            onDismissRequest = { if (!subiendo) showUpload = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (titulo.isBlank() || pickedUri == null) {
                            subirError = "Título y archivo son obligatorios"
                            return@TextButton
                        }
                        subiendo = true
                        subirError = null
                        scope.launch {
                            try {
                                // importante: persistir permiso de lectura temporal
                                pickedUri?.let {
                                    context.contentResolver.takePersistableUriPermission(
                                        it,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    )
                                }
                            } catch (_: Exception) { /* no-op */ }

                            try {
                                val filePart = context.uriToMultipartIO("file", pickedUri!!)
                                val res = api.crearTemaConDocumento(
                                    materiaId = materiaId,
                                    titulo = titulo.toTextPart(),
                                    descripcion = (if (descripcion.isBlank()) "Documento subido desde Android" else descripcion).toTextPart(),
                                    file = filePart
                                )
                                if (res.isSuccessful && res.body() != null) {
                                    showUpload = false
                                    subiendo = false
                                    // refrescar lista
                                    cargarTemas()
                                    // opcional: abrir detalle recién creado
                                    val nuevo = res.body()!!
                                    onOpenTema(nuevo.id, nuevo.titulo)
                                } else {
                                    subiendo = false
                                    subirError = "Error ${res.code()}"
                                }
                            } catch (e: Exception) {
                                subiendo = false
                                subirError = e.message ?: "Error de red"
                            }
                        }
                    },
                    enabled = !subiendo
                ) { Text(if (subiendo) "Subiendo..." else "Subir") }
            },
            dismissButton = {
                TextButton(onClick = { if (!subiendo) showUpload = false }) { Text("Cancelar") }
            },
            title = { Text("Subir documento") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título del tema") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción (opcional)") }
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = {
                            // PDF y Word
                            picker.launch(arrayOf(
                                "application/pdf",
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                "application/msword"
                            ))
                        }) { Text(if (pickedUri == null) "Elegir archivo" else "Cambiar archivo") }
                        Text(if (pickedUri == null) "Sin archivo" else "Archivo seleccionado")
                    }
                    if (subirError != null) {
                        Text(subirError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        )
    }
}

// -------------------------------------------------------------
// --- TemaDetailScreen: muestra resumen y permite rendir el quiz ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemaDetailScreen(
    temaId: String,
    temaTitulo: String,
    onBack: () -> Unit = {}
) {
    // --- LÓGICA MOVIDA AQUÍ ---
    val api = remember { ApiClient.api }
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    var detail by remember { mutableStateOf<TemaDetailDto?>(null) }
    var quiz by remember { mutableStateOf<QuizDto?>(null) }

    // estados del quiz
    var selected by remember { mutableStateOf<List<Int?>>(emptyList()) }
    var submitted by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }

    // rol y AI
    var esProfe by remember { mutableStateOf(false) }
    var generando by remember { mutableStateOf(false) }
    var aiError by remember { mutableStateOf<String?>(null) }
    // estados
    var confirmBorrarAi by remember { mutableStateOf(false) }
    var confirmBorrarTema by remember { mutableStateOf(false) }
    var operando by remember { mutableStateOf(false) }

    // Función para cargar los datos del tema y el quiz
    suspend fun cargarTodo() {
        runCatching { api.me() }.onSuccess { r ->
            esProfe = r.isSuccessful && r.body()?.role?.name in listOf("ROLE_ADMIN", "ROLE_PROFESOR")
        }
        val d = api.temaDetalle(temaId)
        if (!d.isSuccessful || d.body() == null) {
            throw RuntimeException("Error detalle ${d.code()}")
        }
        detail = d.body()

        val q = api.temaQuiz(temaId)
        if (q.isSuccessful && q.body() != null) {
            quiz = q.body()
            selected = List(quiz!!.preguntas.size) { null }
            submitted = false
            score = 0
        } else if (q.code() == 204) {
            quiz = null
            selected = emptyList()
            submitted = false
            score = 0
        } else if (!q.isSuccessful) {
            throw RuntimeException("Error quiz ${q.code()}")
        }
    }

    // Función para manejar la selección de respuestas
    fun onSelect(i: Int, idx: Int) {
        if (submitted) return
        selected = selected.toMutableList().also { it[i] = idx }
    }

    // Función para enviar las respuestas
    fun onSubmit() {
        val q = quiz ?: return
        if (selected.any { it == null }) return
        score = q.preguntas.indices.count { i -> selected[i] == q.preguntas[i].respuestaIndex }
        submitted = true
    }

    // Función para manejar la eliminación de un documento
    fun onEliminarDocumento(docId: String) {
        scope.launch {
            try {
                val response = api.eliminarDocumento(temaId, docId)
                if (response.isSuccessful) {
                    cargarTodo()
                    snackbar.showSnackbar("Documento eliminado correctamente")
                } else {
                    snackbar.showSnackbar("Error al eliminar documento: ${response.code()}")
                }
            } catch (e: Exception) {
                snackbar.showSnackbar("Error de red: ${e.message}")
            }
        }
    }

    // Efecto para cargar datos iniciales
    LaunchedEffect(temaId) {
        loading = true
        error = null
        runCatching { cargarTodo() }
            .onFailure { error = it.message ?: "Error de red" }
        loading = false
    }

    // Diálogo para confirmar la eliminación de AI
    if (confirmBorrarAi) {
        AlertDialog(
            onDismissRequest = { if (!operando) confirmBorrarAi = false },
            confirmButton = {
                TextButton(onClick = {
                    operando = true
                    scope.launch {
                        val r = api.borrarAi(temaId)
                        operando = false
                        confirmBorrarAi = false
                        if (r.isSuccessful) {
                            runCatching { cargarTodo() }
                            snackbar.showSnackbar("Resumen/quiz eliminados")
                        } else {
                            snackbar.showSnackbar("Error ${r.code()} al borrar AI")
                        }
                    }
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { if (!operando) confirmBorrarAi = false }) { Text("Cancelar") } },
            title = { Text("Eliminar resumen y quiz") },
            text = { Text("Se borrará el resumen y las preguntas del tema. ¿Continuar?") }
        )
    }

    // Diálogo para confirmar la eliminación del tema completo
    if (confirmBorrarTema) {
        AlertDialog(
            onDismissRequest = { if (!operando) confirmBorrarTema = false },
            confirmButton = {
                TextButton(onClick = {
                    operando = true
                    scope.launch {
                        val r = api.borrarTema(temaId)
                        operando = false
                        confirmBorrarTema = false
                        if (r.isSuccessful) {
                            snackbar.showSnackbar("Tema eliminado")
                            onBack()
                        } else {
                            snackbar.showSnackbar("Error ${r.code()} al borrar tema")
                        }
                    }
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { if (!operando) confirmBorrarTema = false }) { Text("Cancelar") } },
            title = { Text("Eliminar tema") },
            text = { Text("Se borrará el tema y sus documentos. Esta acción no se puede deshacer. ¿Continuar?") }
        )
    }

    // Estructura principal de la pantalla
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(temaTitulo) },
                actions = {
                    if (esProfe) {
                        TextButton(
                            onClick = {
                                if (generando) return@TextButton
                                generando = true
                                aiError = null
                                scope.launch {
                                    runCatching { api.generarResumenYQuiz(temaId) }
                                        .onSuccess { resp ->
                                            if (!resp.isSuccessful) {
                                                aiError = "Error AI ${resp.code()}"
                                            } else {
                                                runCatching { cargarTodo() }
                                                    .onFailure { aiError = it.message ?: "Error refrescando" }
                                            }
                                        }
                                        .onFailure { aiError = it.message ?: "Error de red" }
                                    generando = false
                                    aiError?.let { snackbar.showSnackbar(it) }
                                    if (aiError == null) snackbar.showSnackbar("Resumen/quiz actualizados")
                                }
                            }
                        ) {
                            Text(if (generando) "Generando..." else "Generar AI")
                        }
                        TextButton(onClick = { if (!generando) confirmBorrarAi = true }) { Text("Borrar AI") }
                        Spacer(Modifier.width(6.dp))
                        TextButton(onClick = { if (!generando) confirmBorrarTema = true }) { Text("Borrar tema") }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
        ) {
            when {
                loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                error != null -> Text("Error: $error", color = MaterialTheme.colorScheme.error)
                detail == null -> Text("No se encontró el tema")
                else -> {
                    val d = detail!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Resumen
                        Card(elevation = CardDefaults.cardElevation(4.dp)) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Resumen", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(8.dp))
                                Text(d.resumen?.ifBlank { "Sin resumen aún." } ?: "Sin resumen aún.")
                            }
                        }

                        // Documentos
                        if (d.documentos.isNotEmpty()) {
                            Card(elevation = CardDefaults.cardElevation(4.dp)) {
                                Column(Modifier.padding(16.dp)) {
                                    Text("Documentos", style = MaterialTheme.typography.titleMedium)
                                    Spacer(Modifier.height(8.dp))

                                    d.documentos.forEach { doc ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(Modifier.weight(1f)) {
                                                Text(doc.fileName, fontWeight = FontWeight.SemiBold)
                                                Text(
                                                    "${(doc.sizeBytes / 1024)} KB • ${doc.mimeType}",
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                            if (esProfe) {
                                                var confirm by remember { mutableStateOf(false) }

                                                if (confirm) {
                                                    AlertDialog(
                                                        onDismissRequest = { confirm = false },
                                                        confirmButton = {
                                                            TextButton(onClick = {
                                                                confirm = false
                                                                onEliminarDocumento(doc.id)
                                                            }) { Text("Eliminar") }
                                                        },
                                                        dismissButton = {
                                                            TextButton(onClick = { confirm = false }) { Text("Cancelar") }
                                                        },
                                                        title = { Text("Eliminar documento") },
                                                        text = { Text("¿Seguro que deseas eliminar '${doc.fileName}'?") }
                                                    )
                                                }

                                                IconButton(onClick = { confirm = true }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                                }
                                            }
                                        }
                                        Spacer(Modifier.height(10.dp))
                                    }
                                }
                            }
                        }

                        // Quiz
                        Card(elevation = CardDefaults.cardElevation(4.dp)) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Quiz", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(8.dp))

                                val q = quiz
                                if (q == null) {
                                    Text(if (generando) "Generando..." else "Este tema aún no tiene quiz.")
                                } else {
                                    q.preguntas.forEachIndexed { i, p ->
                                        Column(Modifier.padding(bottom = 16.dp)) {
                                            Text(
                                                text = "Pregunta ${i + 1}: ${p.enunciado}",
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Spacer(Modifier.height(8.dp))
                                            p.opciones.forEachIndexed { idx, opt ->
                                                val selectedHere = selected.getOrNull(i)
                                                val isChecked = selectedHere == idx
                                                val isCorrect = submitted && idx == p.respuestaIndex
                                                val isWrong = submitted && isChecked && !isCorrect
                                                val color = when {
                                                    isCorrect -> MaterialTheme.colorScheme.primary
                                                    isWrong -> MaterialTheme.colorScheme.error
                                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                                }

                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    RadioButton(
                                                        selected = isChecked,
                                                        onClick = { onSelect(i, idx) },
                                                        enabled = !submitted
                                                    )
                                                    Text(
                                                        text = opt,
                                                        color = color,
                                                        modifier = Modifier.padding(start = 8.dp)
                                                    )
                                                }
                                            }
                                            if (submitted) {
                                                val correctIdx = p.respuestaIndex
                                                Text(
                                                    text = "Respuesta correcta: ${p.opciones[correctIdx]}",
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = { onSubmit() },
                                            enabled = !submitted && selected.all { it != null }
                                        ) { Text("Enviar respuestas") }
                                        if (submitted) {
                                            Text(
                                                "Puntaje: $score / ${q.preguntas.size}",
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            OutlinedButton(onClick = {
                                                selected = List(q.preguntas.size) { null }
                                                submitted = false
                                                score = 0
                                            }) { Text("Reintentar") }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}