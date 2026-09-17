package com.mrtada.memorytoolkit.ui

object Routes {
    const val HOME = "home"
    const val CONCEPTS = "concepts"
    const val REVIEW = "review"
    const val FEYNMAN = "feynman"
    const val FEYNMAN_DETAIL = "feynman/{conceptId}"
    const val INTERLEAVING = "interleaving"
    const val INTERLEAVING_SESSION = "interleaving_session"
    const val ELABORATIVE = "elaborative"
    const val ELABORATIVE_DETAIL = "elaborative/{conceptId}"
    const val DUAL_CODING = "dual_coding"
    const val DUAL_CODING_DETAIL = "dual_coding/{conceptId}"
    const val CHUNKING = "chunking"

    fun feynmanDetail(id: Long) = "feynman/$id"
    fun elaborativeDetail(id: Long) = "elaborative/$id"
    fun dualCodingDetail(id: Long) = "dual_coding/$id"
}
