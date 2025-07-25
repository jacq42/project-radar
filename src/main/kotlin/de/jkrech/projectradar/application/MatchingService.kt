package de.jkrech.projectradar.application

import de.jkrech.projectradar.application.scoring.ScoreEngine
import de.jkrech.projectradar.domain.ProfileResource
import de.jkrech.projectradar.domain.ProjectMatch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MatchingService(
    private val profileReadingService: ProfileReadingService,
    private val scoreEngine: ScoreEngine
) {
    private val logger = LoggerFactory.getLogger(MatchingService::class.java)

    fun findMatches(profileResource: ProfileResource): List<ProjectMatch> {
        val profileData = profileReadingService.analyze(profileResource)
        logger.info("Found ${profileData.size} documents in profile")

        if (profileData.isEmpty()) {
            throw MatchingServiceException("No documents found in profile: ${profileResource.value.filename}")
        }

        val sortedProjects = scoreEngine.findScoresFor(profileData)
        logger.info("Found ${sortedProjects.size} projects for profile: ${profileResource.value.filename}")
        return sortedProjects
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