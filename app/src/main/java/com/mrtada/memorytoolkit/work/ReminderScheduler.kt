package com.mrtada.memorytoolkit.work

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    fun scheduleDaily(context: Context) {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_review_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
