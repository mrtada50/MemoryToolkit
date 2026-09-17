package com.mrtada.memorytoolkit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrtada.memorytoolkit.data.ChunkGroup
import com.mrtada.memorytoolkit.data.Repository
import com.mrtada.memorytoolkit.logic.Chunker
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ChunkingViewModel(private val repo: Repository) : androidx.lifecycle.ViewModel() {
    val groups = repo.observeChunkGroups()

    fun save(title: String, source: String, chunks: List<List<String>>, size: Int) {
        viewModelScope.launch {
            repo.saveChunkGroup(
                ChunkGroup(title = title, sourceText = source, chunksJoined = Chunker.joinChunks(chunks), chunkSize = size)
            )
        }
    }

    fun delete(group: ChunkGroup) {
        viewModelScope.launch { repo.deleteChunkGroup(group) }
    }

    class Factory(private val repo: Repository) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T = ChunkingViewModel(repo) as T
    }
}

@Composable
fun ChunkingScreen(repo: Repository) {
    val vm: ChunkingViewModel = viewModel(factory = ChunkingViewModel.Factory(repo))
    val groups by vm.groups.collectAsState(initial = emptyList())

    var title by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("") }
    var size by remember { mutableStateOf(4f) }
    val preview = remember(source, size) { Chunker.chunk(source, size.toInt()) }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("التقطيع الذكي", style = MaterialTheme.typography.headlineMedium)
        Text(
            "قسّم أي قائمة أو نص طويل لمجموعات صغيرة أسهل بالحفظ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title, onValueChange = { title = it },
            label = { Text("عنوان القائمة") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = source, onValueChange = { source = it },
            label = { Text("العناصر (كل عنصر بسطر أو مفصول بفاصلة)") },
            modifier = Modifier.fillMaxWidth(), minLines = 3
        )
        Spacer(Modifier.height(8.dp))
        Text("حجم كل مجموعة: ${size.toInt()}", style = MaterialTheme.typography.bodyMedium)
        Slider(value = size, onValueChange = { size = it }, valueRange = 2f..7f, steps = 4)

        if (preview.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            preview.forEachIndexed { i, group ->
                Text("مجموعة ${i + 1}: ${group.joinToString(" · ")}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(10.dp))
            Button(
                enabled = title.isNotBlank() && preview.isNotEmpty(),
                onClick = {
                    vm.save(title.trim(), source.trim(), preview, size.toInt())
                    title = ""; source = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("حفظ التقسيم") }
        }

        Spacer(Modifier.height(20.dp))
        if (groups.isNotEmpty()) {
            Text("تقسيمات محفوظة", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(groups, key = { it.id }) { group ->
                    ChunkGroupCard(group) { vm.delete(group) }
                }
            }
        }
    }
}

@Composable
private fun ChunkGroupCard(group: ChunkGroup, onDelete: () -> Unit) {
    Surface(shape = RoundedCornerShape(14.dp), tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp)) {
            Column(Modifier.weight(1f)) {
                Text(group.title, style = MaterialTheme.typography.titleMedium)
                Chunker.parseJoined(group.chunksJoined).forEachIndexed { i, chunk ->
                    Text("• ${chunk.joinToString(" · ")}", style = MaterialTheme.typography.bodyMedium)
                }
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "حذف") }
        }
    }
}
