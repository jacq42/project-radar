package de.jkrech.projectradar.application.scoring.relevance

data class RelevanceResponse(
    val relevance: Double? = null, // 0-100, where 100 is most relevant
    val score: Double? = null,    // Alternative Bezeichnung
    val explanation: String? = null // Optional explanation for the relevance score
) {
    fun relevanceOrScore(): Int {
        return when {
            relevance != null -> (relevance).toInt()
            score != null -> (score).toInt()
            else -> 0
        }
    }
}