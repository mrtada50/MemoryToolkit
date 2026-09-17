package com.mrtada.memorytoolkit.data.db

import androidx.room.*
import com.mrtada.memorytoolkit.data.ReviewCard
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewCardDao {
    @Insert
    suspend fun insert(card: ReviewCard): Long

    @Update
    suspend fun update(card: ReviewCard)

    @Query("SELECT * FROM review_cards WHERE conceptId = :conceptId LIMIT 1")
    suspend fun getForConcept(conceptId: Long): ReviewCard?

    @Query("SELECT * FROM review_cards WHERE dueAt <= :now ORDER BY dueAt ASC")
    fun observeDue(now: Long = System.currentTimeMillis()): Flow<List<ReviewCard>>

    @Query("SELECT COUNT(*) FROM review_cards WHERE dueAt <= :now")
    fun observeDueCount(now: Long = System.currentTimeMillis()): Flow<Int>

    @Query("SELECT * FROM review_cards")
    fun observeAll(): Flow<List<ReviewCard>>
}
