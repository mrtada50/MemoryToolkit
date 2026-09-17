package com.mrtada.memorytoolkit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrtada.memorytoolkit.data.Concept
import com.mrtada.memorytoolkit.data.Repository
import com.mrtada.memorytoolkit.logic.InterleavingSessionBuilder

@Composable
fun InterleavingScreen(repo: Repository) {
    val concepts by repo.observeConcepts().collectAsState(initial = emptyList())
    val selected = remember { mutableStateListOf<Long>() }
    var session by remember { mutableStateOf<List<Concept>?>(null) }
    var index by remember { mutableStateOf(0) }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("المذاكرة المتداخلة", style = MaterialTheme.typography.headlineMedium)
        Text(
            "اختر عدة مفاهيم من مواضيع مختلفة، وبنخلط بينها عشوائياً بجلسة وحدة",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(16.dp))

        val active = session
        if (active == null) {
            if (concepts.size < 2) {
                EmptyHint("تحتاج مفهومين على الأقل عشان تسوي جلسة تداخل. أضف مفاهيم من مكتبة المفاهيم.")
            } else {
                LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(concepts, key = { it.id }) { concept ->
                        val isChecked = selected.contains(concept.id)
                        Surface(
                            onClick = {
                                if (isChecked) selected.remove(concept.id) else selected.add(concept.id)
                            },
                            shape = RoundedCornerShape(14.dp),
                            tonalElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = isChecked, onCheckedChange = null)
                                Spacer(Modifier.width(8.dp))
                                Text(concept.title, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    enabled = selected.size >= 2,
                    onClick = {
                        session = InterleavingSessionBuilder.build(concepts.filter { it.id in selected })
                        index = 0
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("ابدأ الجلسة المتداخلة (${selected.size})") }
            }
        } else {
            val concept = active.getOrNull(index)
            if (concept == null) {
                EmptyHint("خلصت الجلسة! 🎉")
                Spacer(Modifier.height(12.dp))
                Button(onClick = { session = null; selected.clear() }, modifier = Modifier.fillMaxWidth()) {
                    Text("رجوع")
                }
            } else {
                Text("${index + 1} / ${active.size}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                Spacer(Modifier.height(8.dp))
                Surface(shape = RoundedCornerShape(20.dp), tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
                        Text(concept.title, style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(12.dp))
                        Text(concept.content, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Button(onClick = { index++ }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (index == active.lastIndex) "إنهاء" else "التالي")
                }
            }
        }
    }
}
