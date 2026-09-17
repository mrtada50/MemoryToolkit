package com.mrtada.memorytoolkit.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrtada.memorytoolkit.data.Concept
import com.mrtada.memorytoolkit.data.Repository
import com.mrtada.memorytoolkit.data.ReviewCard
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ReviewViewModel(private val repo: Repository) : androidx.lifecycle.ViewModel() {
    val dueCards = repo.observeDueCards()

    fun submit(card: ReviewCard, quality: Int) {
        viewModelScope.launch { repo.submitReview(card, quality) }
    }

    class Factory(private val repo: Repository) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
            ReviewViewModel(repo) as T
    }
}

@Composable
fun ReviewScreen(repo: Repository) {
    val vm: ReviewViewModel = viewModel(factory = ReviewViewModel.Factory(repo))
    val dueCards by vm.dueCards.collectAsState(initial = emptyList())
    var concepts by remember { mutableStateOf<Map<Long, Concept>>(emptyMap()) }
    var flipped by remember { mutableStateOf(false) }

    LaunchedEffect(dueCards) {
        val ids = dueCards.map { it.conceptId }
        concepts = repo.getConcepts(ids).associateBy { it.id }
        flipped = false
    }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("المراجعة المتباعدة", style = MaterialTheme.typography.headlineMedium)
        Text(
            "خوارزمية SM-2 · ${dueCards.size} بطاقة متبقية",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(20.dp))

        val card = dueCards.firstOrNull()
        val concept = card?.let { concepts[it.conceptId] }

        if (card == null) {
            EmptyHint("ما فيه بطاقات جاهزة للمراجعة الحين 🎉\nأضف مفاهيم جديدة من مكتبة المفاهيم.")
        } else if (concept != null) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth().weight(1f).clickable(enabled = !flipped) { flipped = true }
            ) {
                Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(concept.title, style = MaterialTheme.typography.titleLarge)
                    if (flipped) {
                        Spacer(Modifier.height(16.dp))
                        Text(concept.content, style = MaterialTheme.typography.bodyLarge)
                    } else {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "اضغط للكشف عن الإجابة",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (flipped) {
                Text("كيف كان تذكّرك؟", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    QualityButton("نسيت", 1, Modifier.weight(1f)) { vm.submit(card, it) }
                    QualityButton("بصعوبة", 3, Modifier.weight(1f)) { vm.submit(card, it) }
                    QualityButton("جيد", 4, Modifier.weight(1f)) { vm.submit(card, it) }
                    QualityButton("ممتاز", 5, Modifier.weight(1f)) { vm.submit(card, it) }
                }
            }
        }
    }
}

@Composable
private fun QualityButton(label: String, quality: Int, modifier: Modifier, onClick: (Int) -> Unit) {
    OutlinedButton(onClick = { onClick(quality) }, modifier = modifier) {
        Text(label)
    }
}
