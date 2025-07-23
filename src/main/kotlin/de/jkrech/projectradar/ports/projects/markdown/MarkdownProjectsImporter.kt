package de.jkrech.projectradar.ports.projects.markdown

import de.jkrech.projectradar.application.ProjectsImporter
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.reader.markdown.MarkdownDocumentReader
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["projects.importer.markdown.enabled"], havingValue = "true", matchIfMissing = false)
class MarkdownProjectsImporter(
    private val properties: MarkdownProjectsImporterProperties,
    private val resourceLoader: ResourceLoader
): ProjectsImporter {

    private val logger = LoggerFactory.getLogger(MarkdownProjectsImporter::class.java)

    override fun import(): List<Document> {
        logger.info("Reading projects from {}", properties.file)
        return loadMarkdown() ?: emptyList()
    }

    fun loadMarkdown(): List<Document>? {
        val config = MarkdownDocumentReaderConfig.builder()
            .withHorizontalRuleCreateDocument(true)
            .withIncludeCodeBlock(false)
            .withIncludeBlockquote(false)
            .withAdditionalMetadata("filename", "profile.md")
            .build()

        val projectResource = resourceLoader.getResource(properties.file)

        val reader = MarkdownDocumentReader(projectResource, config)
        return reader.get()
    }
}