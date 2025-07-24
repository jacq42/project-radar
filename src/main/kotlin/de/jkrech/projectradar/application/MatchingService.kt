package de.jkrech.projectradar.application

import de.jkrech.projectradar.domain.ImportedProject
import de.jkrech.projectradar.domain.ProfileResource
import de.jkrech.projectradar.domain.ProjectMatch
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.time.Duration.Companion.minutes

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

        return runBlocking {
            try {
                val importedProjects = withTimeout(1.minutes) { // 60 Sekunden Timeout für alle Imports
                    projectsImporters
                        .map { importer ->
                            async(Dispatchers.IO) {
                                try {
                                    importer.import().map { project -> importer to project }
                                } catch (e: Exception) {
                                    logger.error("Import from ${importer.source()} failed: ${e.message}", e)
                                    emptyList()
                                }
                            }
                        }
                        .awaitAll()
                        .flatten()
                }

                val projectsWithSimilarity = withTimeout(1.minutes) {
                    importedProjects
                        .map { (importer, project) ->
                            async(Dispatchers.Default) { // Default for CPU intensive tasks
                                try {
                                    val embeddingProject = embeddingService.embedDocuments(listOf(project))
                                    val similarity = similarityService.cosineSimilarity(embeddingProfile, embeddingProject)
                                    ImportedProject(
                                        importerSource = importer.source(),
                                        documents = listOf(project),
                                        embeddings = embeddingProject,
                                        similarity = similarity
                                    )
                                } catch (e: Exception) {
                                    logger.error("Could not calculate similarity for ${importer.source()}: ${e.message}", e)
                                    null
                                }
                            }
                        }
                        .awaitAll()
                        .filterNotNull()
                }

                projectsWithSimilarity
                    .sortedByDescending { it.similarity }
                    .map {
                        ProjectMatch(
                            title = it.title(),
                            source = it.source(),
                            similarity = it.similarity,
                            profileType = profileResource.type()
                        )
                    }
            } catch (e: Exception) {
                logger.error("Failure while finding profile to project matches: ${e.message}", e)
                emptyList()
            }
        }

//        return runBlocking {
//            projectsImporters
//                .map { importer ->
//                    async(Dispatchers.IO) {
//                        try {
//                            importer.import().map { project -> importer to project }
//                        } catch (e: Exception) {
//                            logger.error("Import von ${importer.source()} fehlgeschlagen: ${e.message}")
//                            emptyList()
//                        }
//                    }
//                }
//                .awaitAll()
//                .flatten()
//                .asSequence()
//                .map { (importer, project) ->
//                    val embeddingProject = embeddingService.embedDocuments(listOf(project))
//                    val similarity = similarityService.cosineSimilarity(embeddingProfile, embeddingProject)
//                    ImportedProject(
//                        importerSource = importer.source(),
//                        documents = listOf(project),
//                        embeddings = embeddingProject,
//                        similarity = similarity
//                    )
//                }
//                .sortedByDescending { it.similarity }
//                .map {
//                    ProjectMatch(
//                        title = it.title(),
//                        source = it.source(),
//                        similarity = it.similarity,
//                        profileType = profileResource.type()
//                    )
//                }
//                .toList()
//        }
    }

    private fun embeddingsForProfile(profileResource: ProfileResource): List<FloatArray> {
        val profileData = profileReadingService.analyze(profileResource)
        logger.info("Found ${profileData.size} documents in profile")
        val embeddingProfile = embeddingService.embedDocuments(profileData)
        return embeddingProfile
    }
}