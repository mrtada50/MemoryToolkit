package com.mrtada.memorytoolkit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * حالة المراجعة المتباعدة (Spaced Repetition) لمفهوم معيّن، وفق خوارزمية SM-2.
 */
@Entity(tableName = "review_cards")
data class ReviewCard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conceptId: Long,
    val easeFactor: Double = 2.5,
    val intervalDays: Int = 0,
    val repetitions: Int = 0,
    val dueAt: Long = System.currentTimeMillis(),
    val lastReviewedAt: Long? = null
)
