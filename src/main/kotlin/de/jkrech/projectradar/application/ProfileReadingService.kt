package de.jkrech.projectradar.application

import de.jkrech.projectradar.domain.ProfileResource
import de.jkrech.projectradar.ports.profile.ProfileReaderFactory
import org.springframework.ai.document.Document
import org.springframework.stereotype.Component

@Component
class ProfileReadingService(
    private val profileReaderFactory: ProfileReaderFactory,
) {

    fun analyze(profileResource: ProfileResource): List<Document> {
        val profileReader = profileReaderFactory.findBy(profileResource)
        return profileReader.read(profileResource)
    }

}