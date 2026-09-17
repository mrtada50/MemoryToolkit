package com.mrtada.memorytoolkit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mrtada.memorytoolkit.data.Repository
import com.mrtada.memorytoolkit.ui.screens.*
import com.mrtada.memorytoolkit.ui.theme.MemoryToolkitTheme
import com.mrtada.memorytoolkit.work.ReminderScheduler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReminderScheduler.scheduleDaily(applicationContext)

        val repo = Repository.get(applicationContext)
        setContent {
            MemoryToolkitTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavHost(repo)
                }
            }
        }
    }
}

private data class ScreenInfo(val title: String, val showBack: Boolean)

@Composable
fun AppNavHost(repo: Repository) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route ?: "home"

    val screenInfo = when (route) {
        "home" -> ScreenInfo("صندوق أدوات الذاكرة", false)
        "concepts" -> ScreenInfo("مكتبة المفاهيم", true)
        "review" -> ScreenInfo("المراجعة المتباعدة", true)
        "feynman" -> ScreenInfo("تقنية فاينمان", true)
        "interleaving" -> ScreenInfo("المذاكرة المتداخلة", true)
        "elaborative" -> ScreenInfo("الاستجواب التوضيحي", true)
        "dual_coding" -> ScreenInfo("الترميز المزدوج", true)
        "chunking" -> ScreenInfo("التقطيع الذكي", true)
        else -> ScreenInfo("", true)
    }

    Scaffold(
        topBar = {
            if (screenInfo.showBack) {
                TopAppBar(
                    title = { Text(screenInfo.title) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") { HomeScreen(repo) { route -> navController.navigate(route) } }
            composable("concepts") { ConceptsScreen(repo) }
            composable("review") { ReviewScreen(repo) }
            composable("feynman") { FeynmanScreen(repo) }
            composable("interleaving") { InterleavingScreen(repo) }
            composable("elaborative") { ElaborativeScreen(repo) }
            composable("dual_coding") { DualCodingScreen(repo) }
            composable("chunking") { ChunkingScreen(repo) }
        }
    }
}
