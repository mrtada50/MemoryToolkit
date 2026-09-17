package com.mrtada.memorytoolkit.logic

/**
 * يقسّم قائمة عناصر (كل عنصر بسطر أو مفصول بفاصلة) لمجموعات صغيرة منطقية
 * وفق قاعدة "7 زائد أو ناقص 2" الشهيرة بعلم النفس المعرفي، مع إمكانية تخصيص الحجم.
 */
object Chunker {
    fun chunk(sourceText: String, chunkSize: Int = 4): List<List<String>> {
        val items = sourceText
            .split("\n", ",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (items.isEmpty()) return emptyList()

        return items.chunked(chunkSize.coerceAtLeast(1))
    }

    fun joinChunks(chunks: List<List<String>>): String =
        chunks.joinToString("\n") { it.joinToString(", ") }

    fun parseJoined(joined: String): List<List<String>> =
        joined.split("\n")
            .filter { it.isNotBlank() }
            .map { line -> line.split(",").map { it.trim() } }
}
