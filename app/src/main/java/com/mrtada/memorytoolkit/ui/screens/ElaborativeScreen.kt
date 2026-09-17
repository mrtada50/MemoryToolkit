package com.mrtada.memorytoolkit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrtada.memorytoolkit.data.Concept
import com.mrtada.memorytoolkit.data.ElaborativeQuestion
import com.mrtada.memorytoolkit.data.Repository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ElaborativeViewModel(private val repo: Repository) : androidx.lifecycle.ViewModel() {
    val concepts = repo.observeConcepts()
    fun questionsFor(conceptId: Long) = repo.observeElaborativeFor(conceptId)
    fun answer(question: ElaborativeQuestion, text: String) {
        viewModelScope.launch { repo.answerQuestion(question, text) }
    }

    class Factory(private val repo: Repository) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T = ElaborativeViewModel(repo) as T
    }
}

@Composable
fun ElaborativeScreen(repo: Repository) {
    val vm: ElaborativeViewModel = viewModel(factory = ElaborativeViewModel.Factory(repo))
    val concepts by vm.concepts.collectAsState(initial = emptyList())
    var selected by remember { mutableStateOf<Concept?>(null) }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("الاستجواب التوضيحي", style = MaterialTheme.typography.headlineMedium)
        Text(
            "اسأل نفسك ليش وكيف عن كل معلومة، واربطها بشي تعرفه",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(16.dp))

        if (concepts.isEmpty()) {
            EmptyHint("أضف مفهوم أولاً من مكتبة المفاهيم عشان تتولّد له الأسئلة تلقائياً.")
            return@Column
        }

        if (selected == null) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(concepts, key = { it.id }) { concept ->
                    Surface(
                        onClick = { selected = concept },
                        shape = RoundedCornerShape(14.dp),
                        tonalElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(concept.title, Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        } else {
            val concept = selected!!
            val questions by vm.questionsFor(concept.id).collectAsState(initial = emptyList())
            TextButton(onClick = { selected = null }) { Text("‹ رجوع") }
            Text(concept.title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(questions, key = { it.id }) { q -> QuestionCard(q) { text -> vm.answer(q, text) } }
            }
        }
    }
}

@Composable
private fun QuestionCard(question: ElaborativeQuestion, onSave: (String) -> Unit) {
    var text by remember(question.id) { mutableStateOf(question.answerText) }
    Surface(shape = RoundedCornerShape(14.dp), tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text(question.questionText, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = text, onValueChange = { text = it },
                label = { Text("إجابتك") }, modifier = Modifier.fillMaxWidth(), minLines = 2
            )
            Spacer(Modifier.height(6.dp))
            TextButton(onClick = { onSave(text.trim()) }, enabled = text != question.answerText) {
                Text("حفظ")
            }
        }
    }
}
