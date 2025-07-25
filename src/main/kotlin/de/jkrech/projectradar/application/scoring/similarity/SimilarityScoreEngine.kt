package de.jkrech.projectradar.application.scoring.similarity

import de.jkrech.projectradar.application.MatchingServiceException
import de.jkrech.projectradar.application.scoring.ProjectsImporter
import de.jkrech.projectradar.application.scoring.ScoreEngine
import de.jkrech.projectradar.application.scoring.similarity.embedding.EmbeddingService
import de.jkrech.projectradar.domain.ImportedProject
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import kotlin.time.Duration.Companion.minutes

@Component
@ConditionalOnProperty(name = ["service.scoring.engine"], havingValue = "similarity", matchIfMissing = false)
class SimilarityScoreEngine(
    private val embeddingService: EmbeddingService,
    private val projectsImporters: List<ProjectsImporter>,
    private val similarityService: SimilarityService
): ScoreEngine {

    private val logger = LoggerFactory.getLogger(SimilarityScoreEngine::class.java)

    override fun findScoresFor(profileData: List<Document>): List<ImportedProject> {
        if (projectsImporters.isEmpty()) {
            throw MatchingServiceException("No projects importers configured")
        }
        val embeddingProfile = embeddingService.embedDocuments(profileData)

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

                if (importedProjects.isEmpty()) {
                    throw MatchingServiceException("No projects imported from any source")
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
            } catch (e: Exception) {
                logger.error("Failure while finding profile to project matches: ${e.message}", e)
                emptyList()
            }
        }
    }
}