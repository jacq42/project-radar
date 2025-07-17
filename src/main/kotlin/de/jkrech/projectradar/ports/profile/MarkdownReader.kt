package de.jkrech.projectradar.ports.profile

import de.jkrech.projectradar.application.ProfileReader
import org.springframework.ai.document.Document
import org.springframework.ai.reader.markdown.MarkdownDocumentReader
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource


class MarkdownReader(@Value("classpath:profile/profile-de.md") val profile: Resource?): ProfileReader {

    override fun read(): List<Document> {
        return loadMarkdown() ?: emptyList()
    }

    fun loadMarkdown(): List<Document>? {
        val config = MarkdownDocumentReaderConfig.builder()
            .withHorizontalRuleCreateDocument(true)
            .withIncludeCodeBlock(false)
            .withIncludeBlockquote(false)
            .withAdditionalMetadata("filename", "code.md")
            .build()

        val reader = MarkdownDocumentReader(profile, config)
        return reader.get()
    }
}