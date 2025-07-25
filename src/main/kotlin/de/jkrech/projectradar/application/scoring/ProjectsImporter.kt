package de.jkrech.projectradar.application.scoring

import org.springframework.ai.document.Document

interface ProjectsImporter {

    fun import(): List<Document>
    fun source(): String
}