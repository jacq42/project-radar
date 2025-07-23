package de.jkrech.projectradar.application

import de.jkrech.projectradar.domain.ProfileResource
import org.springframework.ai.document.Document

interface ProfileReader {

    fun supports(type: ProfileResource.Type): Boolean
    fun read(profileResource: ProfileResource): List<Document>
}