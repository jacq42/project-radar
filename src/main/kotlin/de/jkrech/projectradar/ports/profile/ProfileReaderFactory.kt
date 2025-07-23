package de.jkrech.projectradar.ports.profile

import de.jkrech.projectradar.application.ProfileReader
import de.jkrech.projectradar.domain.ProfileResource
import org.springframework.stereotype.Component

@Component
class ProfileReaderFactory(
    private val profileReaders: List<ProfileReader>,
) {

    fun findBy(profileResource: ProfileResource): ProfileReader {
        val type = profileResource.type()
        return profileReaders.find { it.supports(type) }
            ?: throw IllegalArgumentException("Unsupported profile resource type: $type")
    }

}