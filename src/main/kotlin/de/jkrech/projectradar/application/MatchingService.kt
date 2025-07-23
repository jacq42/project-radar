package de.jkrech.projectradar.application

import de.jkrech.projectradar.domain.ProfileResource
import de.jkrech.projectradar.ports.profile.ProfileReaderFactory
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.embedding.TokenCountBatchingStrategy
import org.springframework.ai.openai.OpenAiEmbeddingOptions
import org.springframework.stereotype.Service

@Service
class MatchingService(
    private val embeddingModel: EmbeddingModel,
    private val profileReaderFactory: ProfileReaderFactory,
    private val projectsImporters: List<ProjectsImporter>
) {
    private val logger = LoggerFactory.getLogger(MatchingService::class.java)

    private val embeddingOptions = OpenAiEmbeddingOptions.builder().build()
    private val batchingStrategy= TokenCountBatchingStrategy()

    fun findMatches(profileResource: ProfileResource) {
        val profileReader = profileReaderFactory.findBy(profileResource)
        val profileData = profileReader.read(profileResource)
        logger.info("Found ${profileData.size} documents in profile")

        val embeddingProfile = embedDocuments(profileData)
        logger.info("Embedding response: $embeddingProfile")

        // TODO: extract most relevant skills from profile and use it for importing projects

        projectsImporters.forEach { importer ->
            logger.info("Importing projects with ${importer::class.simpleName}")
            val projectData = importer.import()
            logger.info("Found ${projectData.size} documents in project")
            val embeddingProject = embedDocuments(projectData)
            logger.info("Embedding response: $embeddingProject")
        }
    }

    private fun embedDocuments(documents: List<Document>): MutableList<FloatArray> {
//        return embeddingModel.embed(
//            documents,
//            embeddingOptions,
//            batchingStrategy
//        )
        return mutableListOf()
    }
}