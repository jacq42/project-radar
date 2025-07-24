package de.jkrech.projectradar.application

import de.jkrech.projectradar.domain.ProfileResource
import de.jkrech.projectradar.domain.ImportedProject
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
        if (projectsImporters.isEmpty()) {
            throw MatchingServiceException("No projects importers configured")
        }
        val embeddingProfile = embeddingsForProfile(profileResource)

        val importedProjects = mutableListOf<ImportedProject>()
        projectsImporters.forEach { importer ->
            logger.info("Importing projects with ${importer::class.simpleName}")
            val projects = importer.import()
            logger.info("Found ${projects.size} projects")

            projects.forEach { project ->
                val embeddingProject = embeddingService.embedDocuments(listOf(project))
                val similarity = similarityService.cosineSimilarity(embeddingProfile, embeddingProject)
                val importedProject = ImportedProject(
                    importerSource = importer.source(),
                    documents = listOf(project),
                    embeddings = embeddingProject,
                    similarity = similarity
                )
                importedProjects.add(importedProject)
            }
        }
        importedProjects.sortByDescending(ImportedProject::similarity)

        return importedProjects.map { ProjectMatch(
            title = it.title(),
            source = it.source(),
            similarity = it.similarity,
            profileType = profileResource.type()
        ) }
    }

    private fun embeddingsForProfile(profileResource: ProfileResource): List<FloatArray> {
        val profileData = profileReadingService.analyze(profileResource)
        logger.info("Found ${profileData.size} documents in profile")
        val embeddingProfile = embeddingService.embedDocuments(profileData)
        return embeddingProfile
    }
}