package de.jkrech.projectradar.application.scoring.similarity

import org.springframework.stereotype.Component
import kotlin.math.pow
import kotlin.math.sqrt

@Component
class SimilarityService {

    /**
     * TODO What is cosine similarity?
     *
     * Ein Wert von 1,0 bedeutet perfekte Ähnlichkeit (gleiche Richtung)
     * Ein Wert von 0 bedeutet keine Ähnlichkeit (orthogonale Vektoren)
     * Ein Wert von -1 bedeutet maximale Unähnlichkeit (entgegengesetzte Richtung)
     */
    fun cosineSimilarity(embeddingProfile: List<FloatArray>, embeddingProject: List<FloatArray>): Double {
        if (embeddingProfile.isEmpty() && embeddingProject.isEmpty()) {
            return 1.0
        }
        if (embeddingProfile.isEmpty() || embeddingProject.isEmpty()) {
            return 0.0
        }

        val flatProfileEmbedding = convertToDouble(embeddingProfile)
        val flatProjectEmbedding = convertToDouble(embeddingProject)

        var dot = 0.0
        var normA = 0.0
        var normB = 0.0

        for (i in flatProfileEmbedding.indices) {
            val a: Double = flatProfileEmbedding[i]!!
            val b: Double = flatProjectEmbedding[i]!!
            dot += a * b
            normA += a.pow(2.0)
            normB += b.pow(2.0)
        }

        return dot / (sqrt(normA) * sqrt(normB))
    }

    private fun convertToDouble(embeddings: List<FloatArray>): List<Double?> {
        return embeddings.flatMap { floatArray ->
            floatArray.map { it.toDouble() as Double? }.toMutableList()
        }.toMutableList()
    }

}