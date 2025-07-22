package de.jkrech.projectradar.ports.projects

import de.jkrech.projectradar.application.ProjectsImporter
import org.springframework.ai.document.Document
import org.springframework.ai.reader.pdf.PagePdfDocumentReader
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource

class PdfProjectsImporter(
    @Value("classpath:projects/project.pdf") val project: Resource?
): ProjectsImporter {

    override fun import(): List<Document> {
        return loadPdf() ?: emptyList()
    }

    fun loadPdf(): List<Document>? {
        val config = PdfDocumentReaderConfig.builder()
            .withPagesPerDocument(0)
            .build()

        val reader = PagePdfDocumentReader(project, config)
        return reader.get()
    }
}