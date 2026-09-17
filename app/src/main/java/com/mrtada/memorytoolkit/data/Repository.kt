package com.mrtada.memorytoolkit.data

import android.content.Context
import com.mrtada.memorytoolkit.data.db.AppDatabase
import com.mrtada.memorytoolkit.logic.ElaborativeQuestionGenerator
import com.mrtada.memorytoolkit.logic.Sm2
import kotlinx.coroutines.flow.Flow

class Repository(context: Context) {
    private val db = AppDatabase.get(context)

    // ---- Concepts ----
    fun observeConcepts(): Flow<List<Concept>> = db.conceptDao().observeAll()
    suspend fun getConcept(id: Long) = db.conceptDao().getById(id)
    suspend fun getConcepts(ids: List<Long>) = db.conceptDao().getByIds(ids)

    /** يضيف مفهوم جديد وينشئ تلقائياً: بطاقة مراجعة SM-2 + أسئلة استجواب توضيحي */
    suspend fun addConcept(title: String, content: String, imageUri: String? = null): Long {
        val conceptId = db.conceptDao().insert(Concept(title = title, content = content, imageUri = imageUri))
        db.reviewCardDao().insert(ReviewCard(conceptId = conceptId))
        val concept = db.conceptDao().getById(conceptId)
        if (concept != null) {
            db.elaborativeDao().insertAll(ElaborativeQuestionGenerator.generate(concept))
        }
        return conceptId
    }

    suspend fun deleteConcept(concept: Concept) = db.conceptDao().delete(concept)

    // ---- Spaced Repetition (SM-2) ----
    fun observeDueCount(): Flow<Int> = db.reviewCardDao().observeDueCount()
    fun observeDueCards(): Flow<List<ReviewCard>> = db.reviewCardDao().observeDue()
    fun observeAllCards(): Flow<List<ReviewCard>> = db.reviewCardDao().observeAll()

    suspend fun submitReview(card: ReviewCard, quality: Int) {
        db.reviewCardDao().update(Sm2.review(card, quality))
    }

    // ---- Feynman ----
    fun observeFeynmanFor(conceptId: Long) = db.feynmanDao().observeForConcept(conceptId)
    fun observeAllFeynman() = db.feynmanDao().observeAll()
    suspend fun addFeynmanExplanation(conceptId: Long, text: String, gaps: String, clarity: Int?) {
        db.feynmanDao().insert(
            FeynmanExplanation(conceptId = conceptId, explanationText = text, gapsNoted = gaps, clarityScore = clarity)
        )
    }

    // ---- Elaborative Interrogation ----
    fun observeElaborativeFor(conceptId: Long) = db.elaborativeDao().observeForConcept(conceptId)
    suspend fun answerQuestion(question: ElaborativeQuestion, answer: String) {
        db.elaborativeDao().update(question.copy(answerText = answer))
    }

    // ---- Chunking ----
    fun observeChunkGroups() = db.chunkDao().observeAll()
    suspend fun saveChunkGroup(group: ChunkGroup) = db.chunkDao().insert(group)
    suspend fun deleteChunkGroup(group: ChunkGroup) = db.chunkDao().delete(group)

    companion object {
        @Volatile private var INSTANCE: Repository? = null
        fun get(context: Context): Repository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Repository(context).also { INSTANCE = it }
            }
    }
}
