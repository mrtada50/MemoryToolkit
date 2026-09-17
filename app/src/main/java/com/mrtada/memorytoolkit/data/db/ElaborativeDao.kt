package com.mrtada.memorytoolkit.data.db

import androidx.room.*
import com.mrtada.memorytoolkit.data.ElaborativeQuestion
import kotlinx.coroutines.flow.Flow

@Dao
interface ElaborativeDao {
    @Insert
    suspend fun insertAll(questions: List<ElaborativeQuestion>)

    @Update
    suspend fun update(question: ElaborativeQuestion)

    @Query("SELECT * FROM elaborative_questions WHERE conceptId = :conceptId ORDER BY createdAt ASC")
    fun observeForConcept(conceptId: Long): Flow<List<ElaborativeQuestion>>
}
