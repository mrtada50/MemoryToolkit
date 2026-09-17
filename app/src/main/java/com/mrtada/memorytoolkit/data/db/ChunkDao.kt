package com.mrtada.memorytoolkit.data.db

import androidx.room.*
import com.mrtada.memorytoolkit.data.ChunkGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface ChunkDao {
    @Insert
    suspend fun insert(group: ChunkGroup): Long

    @Delete
    suspend fun delete(group: ChunkGroup)

    @Query("SELECT * FROM chunk_groups ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ChunkGroup>>
}
