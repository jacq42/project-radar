package de.jkrech.projectradar.ports.projects.pdf

import de.jkrech.projectradar.application.ProjectsImporter
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.reader.pdf.PagePdfDocumentReader
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["projects.importer.pdf.enabled"], havingValue = "true", matchIfMissing = false)
class PdfProjectsImporter(
    private val properties: PdfProjectsImporterProperties,
    private val resourceLoader: ResourceLoader
): ProjectsImporter {

    private val logger = LoggerFactory.getLogger(PdfProjectsImporter::class.java)

    override fun source(): String {
        return "PDF file: ${properties.file}"
    }

    override fun import(): List<Document> {
        logger.info("Reading projects from {}", properties.file)
        return loadPdf() ?: emptyList()
    }

    fun loadPdf(): List<Document>? {
        val config = PdfDocumentReaderConfig.builder()
            .withPagesPerDocument(0)
            .build()

        val projectResource = resourceLoader.getResource(properties.file)

        val reader = PagePdfDocumentReader(projectResource, config)
        return reader.get()
    }
}