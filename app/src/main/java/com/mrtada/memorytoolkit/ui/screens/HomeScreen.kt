package com.mrtada.memorytoolkit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mrtada.memorytoolkit.data.Repository

data class ToolItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

@Composable
fun HomeScreen(repo: Repository, onNavigate: (String) -> Unit) {
    val dueCount by repo.observeDueCount().collectAsState(initial = 0)

    val tools = listOf(
        ToolItem("المراجعة المتباعدة", "SM-2 · راجع بطاقاتك بالتوقيت الأمثل", Icons.Default.Schedule, Color(0xFF5B6CFF), "review"),
        ToolItem("مكتبة المفاهيم", "أضف معلومة جديدة تُستخدم بكل الأدوات", Icons.Default.Description, Color(0xFF00B894), "concepts"),
        ToolItem("تقنية فاينمان", "اشرح المفهوم بكلماتك واكتشف فجواتك", Icons.Default.RecordVoiceOver, Color(0xFFFF7A59), "feynman"),
        ToolItem("المذاكرة المتداخلة", "اخلط بين مواضيع مختلفة بجلسة وحدة", Icons.Default.Shuffle, Color(0xFF9B59B6), "interleaving"),
        ToolItem("الاستجواب التوضيحي", "اسأل نفسك ليش وكيف عن كل معلومة", Icons.AutoMirrored.Filled.HelpOutline, Color(0xFFE17055), "elaborative"),
        ToolItem("الترميز المزدوج", "اربط النص بصورة تخلق مسار تذكر إضافي", Icons.Default.Image, Color(0xFF0984E3), "dual_coding"),
        ToolItem("التقطيع الذكي", "قسّم أي قائمة طويلة لمجموعات صغيرة", Icons.Default.ViewModule, Color(0xFFFDA7DF), "chunking")
    )

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.padding(20.dp, 28.dp, 20.dp, 8.dp)) {
            Text("صندوق أدوات الذاكرة", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                "٦ تقنيات مثبتة علمياً بمكان واحد",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            if (dueCount > 0) {
                Spacer(Modifier.height(12.dp))
                DueBanner(dueCount) { onNavigate("review") }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(20.dp, 8.dp, 20.dp, 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tools) { tool ->
                ToolCard(tool) { onNavigate(tool.route) }
            }
        }
    }
}

@Composable
private fun DueBanner(count: Int, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
            Spacer(Modifier.width(10.dp))
            Text(
                "عندك $count بطاقة جاهزة للمراجعة الآن",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun ToolCard(tool: ToolItem, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(48.dp)
                    .background(tool.color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(tool.icon, contentDescription = null, tint = tool.color)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(tool.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    tool.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}
