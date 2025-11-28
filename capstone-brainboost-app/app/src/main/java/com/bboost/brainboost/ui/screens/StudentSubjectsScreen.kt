package com.bboost.brainboost.ui.subjects

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bboost.brainboost.dto.SubjectInfoDto
import com.bboost.brainboost.network.RetrofitClient
import com.bboost.brainboost.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentSubjectsScreen(
    onSubjectClick: (SubjectInfoDto) -> Unit
) {
    val session = SessionManager(LocalContext.current)
    val scope = rememberCoroutineScope()

    var subjects by remember { mutableStateOf<List<SubjectInfoDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val token = session.getToken() ?: ""
        val bearer = "Bearer $token"
        val role = session.getRole()?.lowercase() ?: "alumno"
        val userId = session.getUserId() ?: ""

        val resp = withContext(Dispatchers.IO) {
            when (role) {
                "alumno" -> RetrofitClient.instance.getStudentSubjects(bearer, userId)
                "profesor" -> RetrofitClient.instance.getProfessorSubjects(bearer, userId)
                else -> null
            }
        }

        loading = false
        subjects = resp?.body().orEmpty()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Asignaturas") }
            )
        }
    ) { padding ->

        if (loading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (subjects.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                Alignment.Center
            ) {
                Text("No tienes asignaturas registradas")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(subjects) { subject ->
                SubjectCard(subject, onSubjectClick)
            }
        }
    }
}

@Composable
fun SubjectCard(
    item: SubjectInfoDto,
    onClick: (SubjectInfoDto) -> Unit
) {
    ElevatedCard(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onClick(item) }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Inscripción: ${item.enrollmentDate}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Text(
                text = "Activa: ${if (item.active) "Sí" else "No"}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (item.active) Color(0xFF2E7D32) else Color.Red
            )
        }
    }
}
