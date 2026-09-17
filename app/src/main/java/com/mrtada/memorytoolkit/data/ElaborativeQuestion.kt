package com.mrtada.memorytoolkit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * سؤال "ليش/كيف" مرتبط بمفهوم (Elaborative Interrogation)، مع إجابة المستخدم.
 */
@Entity(tableName = "elaborative_questions")
data class ElaborativeQuestion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conceptId: Long,
    val questionText: String,
    val answerText: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
