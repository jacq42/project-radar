package de.jkrech.projectradar.ports.profile.markdown

import de.jkrech.projectradar.application.ProfileReader
import de.jkrech.projectradar.domain.ProfileResource
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.reader.markdown.MarkdownDocumentReader
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["profile.reader.markdown.enabled"], havingValue = "true", matchIfMissing = false)
class MarkdownReader: ProfileReader {

    private val logger = LoggerFactory.getLogger(MarkdownReader::class.java)

    override fun supports(type: ProfileResource.Type): Boolean {
        return type == ProfileResource.Type.MARKDOWN
    }

    override fun read(profileResource: ProfileResource): List<Document> {
        logger.info("Reading profile from {}", profileResource.value.filename)
        return loadMarkdown(profileResource) ?: emptyList()
    }

    fun loadMarkdown(profileResource: ProfileResource): List<Document>? {
        val config = MarkdownDocumentReaderConfig.builder()
            .withHorizontalRuleCreateDocument(true)
            .withIncludeCodeBlock(false)
            .withIncludeBlockquote(false)
            .withAdditionalMetadata("filename", "profile.md")
            .build()

        val reader = MarkdownDocumentReader(profileResource.value, config)
        return reader.get()
    }
}