package de.jkrech.projectradar.ports.profile

import de.jkrech.projectradar.application.ProfileReader
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.reader.markdown.MarkdownDocumentReader
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component

@Component
class MarkdownReader(
    private val properties: MarkdownReaderProperties,
    private val resourceLoader: ResourceLoader
): ProfileReader {

    private val logger = LoggerFactory.getLogger(MarkdownReader::class.java)

    override fun read(): List<Document> {
        logger.info("Reading profile from {}", properties.file)
        return loadMarkdown() ?: emptyList()
    }

    fun loadMarkdown(): List<Document>? {
        val config = MarkdownDocumentReaderConfig.builder()
            .withHorizontalRuleCreateDocument(true)
            .withIncludeCodeBlock(false)
            .withIncludeBlockquote(false)
            .withAdditionalMetadata("filename", "profile.md")
            .build()

        val profileResource = resourceLoader.getResource(properties.file)

        val reader = MarkdownDocumentReader(profileResource, config)
        return reader.get()
    }
}