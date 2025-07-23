package de.jkrech.projectradar.ports.profile.pdf

import de.jkrech.projectradar.application.ProfileReader
import de.jkrech.projectradar.domain.ProfileResource
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.reader.pdf.PagePdfDocumentReader
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["profile.reader.pdf.enabled"], havingValue = "true", matchIfMissing = false)
class PdfReader: ProfileReader {

    private val logger = LoggerFactory.getLogger(PdfReader::class.java)

    override fun supports(type: ProfileResource.Type): Boolean {
        return type == ProfileResource.Type.PDF
    }

    override fun read(profileResource: ProfileResource): List<Document> {
        logger.info("Reading profile from {}", profileResource.value.filename)
        return loadPdf(profileResource) ?: emptyList()
    }

    fun loadPdf(profileResource: ProfileResource): List<Document>? {
        val config = PdfDocumentReaderConfig.builder()
            .withPagesPerDocument(0)
            .build()

        val reader = PagePdfDocumentReader(profileResource.value, config)
        return reader.get()
    }
}