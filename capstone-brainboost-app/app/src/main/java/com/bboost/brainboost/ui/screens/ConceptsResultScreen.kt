package com.bboost.brainboost.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bboost.brainboost.dto.ConceptExampleDto

@Composable
fun ConceptsResultScreen(
    inserted: Int,
    examples: List<ConceptExampleDto>,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text("Conceptos Insertados", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        Text("Total insertados: $inserted")
        Spacer(Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(examples) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("• ${item.word}", style = MaterialTheme.typography.titleMedium)
                        Text(item.hint ?: "(sin pista) ", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
