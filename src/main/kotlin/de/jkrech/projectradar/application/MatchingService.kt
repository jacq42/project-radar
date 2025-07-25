package de.jkrech.projectradar.application

import de.jkrech.projectradar.domain.ProfileResource
import de.jkrech.projectradar.domain.ProjectMatch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MatchingService(
    private val scoreEngine: ScoreEngine
) {
    private val logger = LoggerFactory.getLogger(MatchingService::class.java)

    fun findMatches(profileResource: ProfileResource): List<ProjectMatch> {
        val sortedProjects = scoreEngine.findScores(profileResource)
        logger.info("Found ${sortedProjects.size} projects for profile: ${profileResource.value.filename}")
        return sortedProjects
            .map {
                ProjectMatch(
                    title = it.title(),
                    source = it.source(),
                    similarity = it.similarity,
                    profileType = profileResource.type()
                )
            }
    }
}