package com.mrtada.memorytoolkit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrtada.memorytoolkit.data.Concept
import com.mrtada.memorytoolkit.data.Repository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ConceptsViewModel(private val repo: Repository) : androidx.lifecycle.ViewModel() {
    val concepts = repo.observeConcepts()

    fun add(title: String, content: String, onDone: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repo.addConcept(title, content)
            onDone(id)
        }
    }

    fun delete(concept: Concept) {
        viewModelScope.launch { repo.deleteConcept(concept) }
    }

    class Factory(private val repo: Repository) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
            ConceptsViewModel(repo) as T
    }
}

@Composable
fun ConceptsScreen(repo: Repository) {
    val vm: ConceptsViewModel = viewModel(factory = ConceptsViewModel.Factory(repo))
    val concepts by vm.concepts.collectAsState(initial = emptyList())
    var showAdd by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(20.dp)) {
            Text("مكتبة المفاهيم", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                "كل مفهوم تضيفه هنا يُنشأ له تلقائياً بطاقة مراجعة وأسئلة استجواب توضيحي",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(16.dp))

            if (concepts.isEmpty()) {
                EmptyHint("ما أضفت أي مفهوم بعد. اضغط + بالأسفل عشان تبدأ.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(concepts, key = { it.id }) { concept ->
                        ConceptRow(concept, onDelete = { vm.delete(concept) })
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAdd = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "إضافة مفهوم")
        }
    }

    if (showAdd) {
        AddConceptDialog(
            onDismiss = { showAdd = false },
            onConfirm = { title, content ->
                vm.add(title, content)
                showAdd = false
            }
        )
    }
}

@Composable
fun EmptyHint(text: String) {
    Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
        Text(text, style = MaterialTheme.typography.bodyMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
private fun ConceptRow(concept: Concept, onDelete: () -> Unit) {
    Surface(shape = RoundedCornerShape(14.dp), tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(concept.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    concept.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "حذف")
            }
        }
    }
}

@Composable
fun AddConceptDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("مفهوم جديد") },
        text = {
            Column {
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("العنوان") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = content, onValueChange = { content = it },
                    label = { Text("المحتوى / التعريف") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank() && content.isNotBlank(),
                onClick = { onConfirm(title.trim(), content.trim()) }
            ) { Text("إضافة") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}
