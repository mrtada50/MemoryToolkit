package com.mrtada.memorytoolkit.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mrtada.memorytoolkit.data.Concept
import com.mrtada.memorytoolkit.data.Repository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DualCodingViewModel(private val repo: Repository) : androidx.lifecycle.ViewModel() {
    val concepts = repo.observeConcepts()

    fun attachImage(concept: Concept, uri: String) {
        viewModelScope.launch {
            // نعيد إدخال المفهوم بنفس المحتوى مع رابط الصورة الجديد
            repo.deleteConcept(concept)
            repo.addConcept(concept.title, concept.content, uri)
        }
    }

    class Factory(private val repo: Repository) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T = DualCodingViewModel(repo) as T
    }
}

@Composable
fun DualCodingScreen(repo: Repository) {
    val vm: DualCodingViewModel = viewModel(factory = DualCodingViewModel.Factory(repo))
    val concepts by vm.concepts.collectAsState(initial = emptyList())
    val context = LocalContext.current

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("الترميز المزدوج", style = MaterialTheme.typography.headlineMedium)
        Text(
            "اربط كل مفهوم بصورة أو رسم — يخلق مسار تذكّر بصري إضافي",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(16.dp))

        if (concepts.isEmpty()) {
            EmptyHint("أضف مفهوم أولاً من مكتبة المفاهيم.")
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(concepts, key = { it.id }) { concept ->
                DualCodingCard(concept) { uri ->
                    context.contentResolver.takePersistableUriPermission(
                        android.net.Uri.parse(uri), Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    vm.attachImage(concept, uri)
                }
            }
        }
    }
}

@Composable
private fun DualCodingCard(concept: Concept, onImagePicked: (String) -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { onImagePicked(it.toString()) }
    }

    Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text(concept.title, style = MaterialTheme.typography.titleMedium)
            Text(
                concept.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(10.dp))
            if (concept.imageUri != null) {
                AsyncImage(
                    model = concept.imageUri,
                    contentDescription = concept.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.height(8.dp))
            }
            OutlinedButton(onClick = { launcher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (concept.imageUri == null) "أضف صورة" else "استبدال الصورة")
            }
        }
    }
}
