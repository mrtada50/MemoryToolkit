package com.mrtada.memorytoolkit.data.db

import androidx.room.*
import com.mrtada.memorytoolkit.data.FeynmanExplanation
import kotlinx.coroutines.flow.Flow

@Dao
interface FeynmanDao {
    @Insert
    suspend fun insert(explanation: FeynmanExplanation): Long

    @Update
    suspend fun update(explanation: FeynmanExplanation)

    @Query("SELECT * FROM feynman_explanations WHERE conceptId = :conceptId ORDER BY createdAt DESC")
    fun observeForConcept(conceptId: Long): Flow<List<FeynmanExplanation>>

    @Query("SELECT * FROM feynman_explanations ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<FeynmanExplanation>>
}
