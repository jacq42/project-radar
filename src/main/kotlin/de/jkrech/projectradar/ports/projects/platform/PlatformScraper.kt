package de.jkrech.projectradar.ports.projects.platform

import de.jkrech.projectradar.application.ProjectsImporter
import org.springframework.ai.document.Document

abstract class PlatformScraper(val url: String): ProjectsImporter {

    override fun import(): List<Document> {
        return scrape(url)
    }

    abstract fun scrape(url: String): List<Document>
}