package de.jkrech.projectradar.application

import org.springframework.ai.document.Document

interface ProjectsImporter {

    fun import(): List<Document>
    fun source(): String
}