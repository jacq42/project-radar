package de.jkrech.projectradar.application

import de.jkrech.projectradar.application.scoring.similarity.SimilarityService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Test

class SimilarityServiceTest {

    private val similarityService = SimilarityService()

    @Test
    fun `should not be similar when using different vectors`() {
        // when
        val cosineSimilarity = similarityService.cosineSimilarity(EMBEDDING_PROFILE, EMBEDDING_PROJECT)

        // then
        assertThat(cosineSimilarity).isCloseTo(-0.3, within(TOLERANCE_THRESHOLD))
    }

    @Test
    fun `should be similar when using same vectors`() {
        // when
        val cosineSimilarity = similarityService.cosineSimilarity(EMBEDDING_PROFILE, EMBEDDING_PROFILE)

        // then
        assertThat(cosineSimilarity).isCloseTo(1.0, within(TOLERANCE_THRESHOLD))
    }

    @Test
    fun `should be similar when both embeddings are empty list`() {
        // when
        val cosineSimilarity = similarityService.cosineSimilarity(mutableListOf(), mutableListOf())

        // then
        assertThat(cosineSimilarity).isCloseTo(1.0, within(TOLERANCE_THRESHOLD))
    }

    @Test
    fun `should not be similar when one embeddings is empty list`() {
        // when
        val cosineSimilarity = similarityService.cosineSimilarity(EMBEDDING_PROFILE, mutableListOf())

        // then
        assertThat(cosineSimilarity).isCloseTo(0.0, within(TOLERANCE_THRESHOLD))
    }

    companion object {
        private const val TOLERANCE_THRESHOLD = 0.01
        private val EMBEDDING_PROFILE = mutableListOf(
            floatArrayOf(1.0f, 2.0f, 3.0f),
            floatArrayOf(4.0f, 5.0f, 6.0f)
        )
        val EMBEDDING_PROJECT = mutableListOf(
            floatArrayOf(7.0f, -8.0f, 9.0f),
            floatArrayOf(10.0f, -11.0f, -12.0f)
        )
    }

}