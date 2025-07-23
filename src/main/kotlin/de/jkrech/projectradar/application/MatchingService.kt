package de.jkrech.projectradar.application

import org.slf4j.LoggerFactory
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

    fun findMatches() {
        val profileData = profileReader.read()
        logger.info("Found ${profileData.size} documents in profile")

//        val embeddingProfile = embeddingModel.embed(
//            profileData,
//            OpenAiEmbeddingOptions.builder().build(),
//            TokenCountBatchingStrategy()
//        )
//        logger.info("Embedding response: $embeddingProfile")

        projectsImporters.forEach { importer ->
            logger.info("Importing projects with ${importer::class.simpleName}")
            val projectData = importer.import()
            logger.info("Found ${projectData.size} documents in project")
//            val embeddingProject = embeddingModel.embed(
//                projectData,
//                OpenAiEmbeddingOptions.builder().build(),
//                TokenCountBatchingStrategy()
//            )
//            logger.info("Embedding response: $embeddingProject")
        }

    }
}