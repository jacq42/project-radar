package de.jkrech.projectradar.application

import de.jkrech.projectradar.application.scoring.relevance.RelevanceScoreEngine
import de.jkrech.projectradar.application.scoring.similarity.SimilarityScoreEngine
import de.jkrech.projectradar.domain.ProfileResource
import de.jkrech.projectradar.domain.ProjectMatch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MatchingService(
    private val profileReadingService: ProfileReadingService,
    private val similarityScoreEngine: SimilarityScoreEngine,
    private val relevanceScoreEngine: RelevanceScoreEngine
) {
    private val logger = LoggerFactory.getLogger(MatchingService::class.java)

    fun findMatches(profileResource: ProfileResource): List<ProjectMatch> {
        val profileData = profileReadingService.analyze(profileResource)
        logger.info("Found ${profileData.size} documents in profile")

        if (profileData.isEmpty()) {
            throw MatchingServiceException("No documents found in profile: ${profileResource.value.filename}")
        }

        val mostSimilarProjects = similarityScoreEngine.findMostSimilarProjectsFor(profileData).take(3)
        val mostRelevantProjects = relevanceScoreEngine.findMostRelevant(profileData, mostSimilarProjects)
        logger.info("Found ${mostRelevantProjects.size} projects for profile: ${profileResource.value.filename}")
        return mostRelevantProjects
            .map {
                ProjectMatch(
                    title = it.title(),
                    source = it.source(),
                    score = it.calculateScore(),
                    profileType = profileResource.type()
                )
            }
    }
}