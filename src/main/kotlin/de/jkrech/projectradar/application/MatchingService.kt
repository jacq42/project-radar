package de.jkrech.projectradar.application

import de.jkrech.projectradar.domain.ProfileResource
import de.jkrech.projectradar.domain.ProjectMatch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MatchingService(
    private val embeddingService: EmbeddingService,
    private val profileReadingService: ProfileReadingService,
    private val projectsImporters: List<ProjectsImporter>,
    private val similarityService: SimilarityService
) {
    private val logger = LoggerFactory.getLogger(MatchingService::class.java)

    fun findMatches(profileResource: ProfileResource): List<ProjectMatch> {
        val profileData = profileReadingService.analyze(profileResource)
        logger.info("Found ${profileData.size} documents in profile")
        val embeddingProfile = embeddingService.embedDocuments(profileData)
        logger.info("Embedding response: $embeddingProfile")

        // TODO: extract most relevant skills from profile and use it for importing projects

        projectsImporters.forEach { importer ->
            logger.info("Importing projects with ${importer::class.simpleName}")
            val projectData = importer.import()
            logger.info("Found ${projectData.size} documents in project")
            val embeddingProject = embeddingService.embedDocuments(projectData)
            logger.info("Embedding response: $embeddingProject")
            val similarity = similarityService.cosineSimilarity(embeddingProfile, embeddingProject)
            logger.info("Cosine similarity: $similarity")
        }

        return listOf(ProjectMatch(
            title ="dummy-project",
            profileType = profileResource.type()
        ))
    }
}