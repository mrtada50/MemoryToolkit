package com.mrtada.memorytoolkit.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mrtada.memorytoolkit.data.ChunkGroup
import com.mrtada.memorytoolkit.data.Concept
import com.mrtada.memorytoolkit.data.ElaborativeQuestion
import com.mrtada.memorytoolkit.data.FeynmanExplanation
import com.mrtada.memorytoolkit.data.ReviewCard

@Database(
    entities = [
        Concept::class,
        ReviewCard::class,
        FeynmanExplanation::class,
        ElaborativeQuestion::class,
        ChunkGroup::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conceptDao(): ConceptDao
    abstract fun reviewCardDao(): ReviewCardDao
    abstract fun feynmanDao(): FeynmanDao
    abstract fun elaborativeDao(): ElaborativeDao
    abstract fun chunkDao(): ChunkDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "memory_toolkit.db"
                ).build().also { INSTANCE = it }
            }
    }
}
