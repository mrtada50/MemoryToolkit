package com.mrtada.memorytoolkit.logic

import com.mrtada.memorytoolkit.data.Concept
import com.mrtada.memorytoolkit.data.ElaborativeQuestion

/**
 * قوالب أسئلة "ليش/كيف" (Elaborative Interrogation) تُولَّد تلقائياً لأي مفهوم جديد.
 */
object ElaborativeQuestionGenerator {
    fun generate(concept: Concept): List<ElaborativeQuestion> = listOf(
        "ليش هذه المعلومة صحيحة أو منطقية؟",
        "كيف ترتبط \"${concept.title}\" بشي تعرفه مسبقاً؟",
        "شنو يصير لو ما كانت هذه المعلومة صحيحة؟",
        "أعطِ مثال واقعي من حياتك يوضح \"${concept.title}\"."
    ).map { q ->
        ElaborativeQuestion(conceptId = concept.id, questionText = q)
    }
}

/**
 * يبني جلسة مذاكرة متداخلة (Interleaving): يخلط بين مفاهيم من مواضيع مختلفة
 * بترتيب عشوائي بدل تجميعها حسب الموضوع، وهو ما يحسّن الاحتفاظ طويل المدى.
 */
object InterleavingSessionBuilder {
    fun build(concepts: List<Concept>, seed: Long = System.currentTimeMillis()): List<Concept> =
        concepts.shuffled(kotlin.random.Random(seed))
}
