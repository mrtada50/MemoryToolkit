package com.mrtada.memorytoolkit.work

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mrtada.memorytoolkit.data.Repository
import kotlinx.coroutines.flow.first

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val repo = Repository.get(applicationContext)
        val dueCount = repo.observeDueCount().first()
        if (dueCount > 0) {
            notify(dueCount)
        }
        return Result.success()
    }

    private fun notify(count: Int) {
        val channelId = "daily_review"
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "تذكير المراجعة اليومية", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("عندك $count بطاقة للمراجعة اليوم")
            .setContentText("راجعها الحين قبل ما تنسى — التكرار المتباعد يحتاج انتظام")
            .setAutoCancel(true)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            manager.notify(1001, notification)
        }
    }
}
