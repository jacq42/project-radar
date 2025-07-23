package de.jkrech.projectradar

import de.jkrech.projectradar.ports.projects.markdown.MarkdownProjectsImporter
import de.jkrech.projectradar.ports.projects.markdown.MarkdownProjectsImporterProperties
import de.jkrech.projectradar.ports.projects.pdf.PdfProjectsImporter
import de.jkrech.projectradar.ports.projects.pdf.PdfProjectsImporterProperties
import de.jkrech.projectradar.ports.projects.platform.FreelancerMapPlatformScraper
import de.jkrech.projectradar.ports.projects.platform.FreelancerMapProperties
import io.mockk.every
import io.mockk.mockk
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.ResourceLoader

class ConfigurationHelper {

    companion object {

        val resourceLoaderMock = mockk<ResourceLoader>()

        fun configuredMarkdownProjectsImporter(): MarkdownProjectsImporter {
            val markdownProjectsImporterProperties = MarkdownProjectsImporterProperties(
                enabled = true,
                file = "classpath:projects/project-test.md"
            )
            every { resourceLoaderMock.getResource(eq(markdownProjectsImporterProperties.file)) }returns
                    ClassPathResource(markdownProjectsImporterProperties.file.removePrefix("classpath:"))
            return MarkdownProjectsImporter(markdownProjectsImporterProperties, resourceLoaderMock)
        }

        fun configuredPdfProjectsImporter(): PdfProjectsImporter {
            val pdfProjectsImporterProperties = PdfProjectsImporterProperties(
                enabled = true,
                file = "classpath:projects/project-dummy.pdf"
            )
            every { resourceLoaderMock.getResource(eq(pdfProjectsImporterProperties.file)) }returns
                    ClassPathResource(pdfProjectsImporterProperties.file.removePrefix("classpath:"))
            return PdfProjectsImporter(pdfProjectsImporterProperties, resourceLoaderMock)
        }

        fun configuredFreelancermapPlatformScraper(keywords: List<String>): FreelancerMapPlatformScraper {
            val freelancerMapProperties = FreelancerMapProperties(
                enabled = true,
                keywords = keywords
            )
            return FreelancerMapPlatformScraper(freelancerMapProperties)
        }
    }
}