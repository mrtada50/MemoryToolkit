package com.mrtada.memorytoolkit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * نتيجة تقسيم نص/قائمة طويلة لمجموعات صغيرة منطقية (Chunking).
 * chunksJoined: كل مجموعة بسطر، والعناصر داخلها مفصولة بفواصل.
 */
@Entity(tableName = "chunk_groups")
data class ChunkGroup(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val sourceText: String,
    val chunksJoined: String,
    val chunkSize: Int,
    val createdAt: Long = System.currentTimeMillis()
)
