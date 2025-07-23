package de.jkrech.projectradar.application

import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.embedding.TokenCountBatchingStrategy
import org.springframework.ai.openai.OpenAiEmbeddingOptions
import org.springframework.stereotype.Service

@Service
class MatchingService(
    val embeddingModel: EmbeddingModel,
    val profileReader: ProfileReader,
    val projectsImporters: List<ProjectsImporter>
) {
    private val logger = LoggerFactory.getLogger(MatchingService::class.java)

    private val embeddingOptions = OpenAiEmbeddingOptions.builder().build()
    private val batchingStrategy= TokenCountBatchingStrategy()

    fun findMatches() {
        val profileData = profileReader.read()
        logger.info("Found ${profileData.size} documents in profile")

        val embeddingProfile = embedDocuments(profileData)
        logger.info("Embedding response: $embeddingProfile")

        projectsImporters.forEach { importer ->
            logger.info("Importing projects with ${importer::class.simpleName}")
            val projectData = importer.import()
            logger.info("Found ${projectData.size} documents in project")
            val embeddingProject = embedDocuments(projectData)
            logger.info("Embedding response: $embeddingProject")
        }
    }

    private fun embedDocuments(documents: List<Document>): MutableList<FloatArray> {
        return embeddingModel.embed(
            documents,
            embeddingOptions,
            batchingStrategy
        )
    }
}