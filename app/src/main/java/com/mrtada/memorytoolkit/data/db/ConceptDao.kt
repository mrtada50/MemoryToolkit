package com.mrtada.memorytoolkit.data.db

import androidx.room.*
import com.mrtada.memorytoolkit.data.Concept
import kotlinx.coroutines.flow.Flow

@Dao
interface ConceptDao {
    @Insert
    suspend fun insert(concept: Concept): Long

    @Update
    suspend fun update(concept: Concept)

    @Delete
    suspend fun delete(concept: Concept)

    @Query("SELECT * FROM concepts ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Concept>>

    @Query("SELECT * FROM concepts WHERE id = :id")
    suspend fun getById(id: Long): Concept?

    @Query("SELECT * FROM concepts WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<Concept>
}
