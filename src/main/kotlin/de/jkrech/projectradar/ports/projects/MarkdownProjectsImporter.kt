package de.jkrech.projectradar.ports.projects

import de.jkrech.projectradar.application.ProjectsImporter
import org.springframework.ai.document.Document
import org.springframework.ai.reader.markdown.MarkdownDocumentReader
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource

class MarkdownProjectsImporter(
    @Value("classpath:projects/project.md") val project: Resource?
): ProjectsImporter {

    override fun import(): List<Document> {
        return loadMarkdown() ?: emptyList()
    }

    fun loadMarkdown(): List<Document>? {
        val config = MarkdownDocumentReaderConfig.builder()
            .withHorizontalRuleCreateDocument(true)
            .withIncludeCodeBlock(false)
            .withIncludeBlockquote(false)
            .withAdditionalMetadata("filename", "code.md")
            .build()

        val reader = MarkdownDocumentReader(project, config)
        return reader.get()
    }
}