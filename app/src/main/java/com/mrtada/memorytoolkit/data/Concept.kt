package com.mrtada.memorytoolkit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * المفهوم أو المعلومة الأساسية اللي تُبنى عليها كل الأدوات:
 * بطاقة المراجعة (SM-2)، شرح فاينمان، أسئلة الاستجواب التوضيحي، والترميز المزدوج.
 */
@Entity(tableName = "concepts")
data class Concept(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val imageUri: String? = null, // للترميز المزدوج (Dual Coding)
    val createdAt: Long = System.currentTimeMillis()
)
