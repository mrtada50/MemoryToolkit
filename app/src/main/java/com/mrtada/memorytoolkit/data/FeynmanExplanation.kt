package com.mrtada.memorytoolkit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * شرح المستخدم للمفهوم بكلماته الخاصة (تقنية فاينمان)،
 * مع ملاحظاته عن أي فجوات فهم لاحظها أثناء الشرح.
 */
@Entity(tableName = "feynman_explanations")
data class FeynmanExplanation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conceptId: Long,
    val explanationText: String,
    val gapsNoted: String = "",
    val clarityScore: Int? = null, // 1-5 تقييم ذاتي لوضوح شرحه
    val createdAt: Long = System.currentTimeMillis()
)
