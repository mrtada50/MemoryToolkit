package com.mrtada.memorytoolkit.logic

import com.mrtada.memorytoolkit.data.ReviewCard
import java.util.concurrent.TimeUnit

/**
 * خوارزمية SM-2 القياسية (نفس المستخدمة بـ Anki) لحساب موعد المراجعة القادم
 * بناءً على مدى سهولة تذكّر البطاقة آخر مرة.
 *
 * quality: تقييم المستخدم لذاكرته لهذه البطاقة من 0 إلى 5
 *   0-2 = نسيان (تُعاد البطاقة من الصفر تقريباً)
 *   3   = تذكّر بصعوبة
 *   4   = تذكّر جيد
 *   5   = تذكّر ممتاز وفوري
 */
object Sm2 {
    fun review(card: ReviewCard, quality: Int): ReviewCard {
        require(quality in 0..5) { "quality يجب أن يكون بين 0 و5" }

        var ease = card.easeFactor
        var repetitions = card.repetitions
        var interval = card.intervalDays

        if (quality < 3) {
            // نسيان: نرجع للبداية، لكن نحافظ على ease factor المخفّض
            repetitions = 0
            interval = 1
        } else {
            interval = when (repetitions) {
                0 -> 1
                1 -> 6
                else -> Math.round(interval * ease).toInt().coerceAtLeast(1)
            }
            repetitions += 1
        }

        ease = (ease + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02)))
            .coerceAtLeast(1.3)

        val dueAt = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(interval.toLong())

        return card.copy(
            easeFactor = ease,
            intervalDays = interval,
            repetitions = repetitions,
            dueAt = dueAt,
            lastReviewedAt = System.currentTimeMillis()
        )
    }
}
