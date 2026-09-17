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
import com.mrtada.memorytoolkit.data.FeynmanExplanation
import com.mrtada.memorytoolkit.data.Repository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FeynmanViewModel(private val repo: Repository) : androidx.lifecycle.ViewModel() {
    val concepts = repo.observeConcepts()

    fun explanationsFor(conceptId: Long) = repo.observeFeynmanFor(conceptId)

    fun save(conceptId: Long, text: String, gaps: String) {
        viewModelScope.launch { repo.addFeynmanExplanation(conceptId, text, gaps, null) }
    }

    class Factory(private val repo: Repository) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T = FeynmanViewModel(repo) as T
    }
}

@Composable
fun FeynmanScreen(repo: Repository) {
    val vm: FeynmanViewModel = viewModel(factory = FeynmanViewModel.Factory(repo))
    val concepts by vm.concepts.collectAsState(initial = emptyList())
    var selected by remember { mutableStateOf<Concept?>(null) }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("تقنية فاينمان", style = MaterialTheme.typography.headlineMedium)
        Text(
            "اشرح المفهوم بكلماتك وكأنك تعلّمه لشخص مبتدئ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(16.dp))

        if (concepts.isEmpty()) {
            EmptyHint("أضف مفهوم أولاً من مكتبة المفاهيم عشان تقدر تشرحه.")
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
            FeynmanDetail(vm, selected!!) { selected = null }
        }
    }
}

@Composable
private fun FeynmanDetail(vm: FeynmanViewModel, concept: Concept, onBack: () -> Unit) {
    val explanations by vm.explanationsFor(concept.id).collectAsState(initial = emptyList())
    var text by remember { mutableStateOf("") }
    var gaps by remember { mutableStateOf("") }

    TextButton(onClick = onBack) { Text("‹ رجوع") }
    Text(concept.title, style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(12.dp))

    OutlinedTextField(
        value = text, onValueChange = { text = it },
        label = { Text("اشرح المفهوم بكلماتك") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 4
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = gaps, onValueChange = { gaps = it },
        label = { Text("وين تعثرت أو ما كان واضح لك؟ (اختياري)") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 2
    )
    Spacer(Modifier.height(10.dp))
    Button(
        enabled = text.isNotBlank(),
        onClick = { vm.save(concept.id, text.trim(), gaps.trim()); text = ""; gaps = "" },
        modifier = Modifier.fillMaxWidth()
    ) { Text("حفظ الشرح") }

    Spacer(Modifier.height(20.dp))
    if (explanations.isNotEmpty()) {
        Text("شروحات سابقة", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(explanations, key = { it.id }) { exp -> FeynmanCard(exp) }
        }
    }
}

@Composable
private fun FeynmanCard(exp: FeynmanExplanation) {
    Surface(shape = RoundedCornerShape(12.dp), tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text(exp.explanationText, style = MaterialTheme.typography.bodyMedium)
            if (exp.gapsNoted.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    "فجوة: ${exp.gapsNoted}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
