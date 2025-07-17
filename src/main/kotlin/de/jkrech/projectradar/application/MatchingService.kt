package de.jkrech.projectradar.application

import org.slf4j.LoggerFactory

class MatchingService(
    val profileReader: ProfileReader
) {
    private val logger = LoggerFactory.getLogger(MatchingService::class.java)

    fun findMatches() {
        val documents = profileReader.read()
        logger.info("Found ${documents.size} documents in profile")
    }
}