package de.jkrech.projectradar

import de.jkrech.projectradar.ports.profile.markdown.MarkdownReader
import de.jkrech.projectradar.ports.profile.markdown.MarkdownReaderProperties
import de.jkrech.projectradar.ports.projects.markdown.MarkdownProjectsImporter
import de.jkrech.projectradar.ports.projects.markdown.MarkdownProjectsImporterProperties
import de.jkrech.projectradar.ports.projects.pdf.PdfProjectsImporter
import de.jkrech.projectradar.ports.projects.pdf.PdfProjectsImporterProperties
import de.jkrech.projectradar.ports.projects.platform.FreelancerMapPlatformScraper
import de.jkrech.projectradar.ports.projects.platform.FreelancerMapProperties
import org.springframework.core.io.ResourceLoader

class ConfigurationHelper {

    companion object {

        fun configuredMarkdownProfileReader(resourceLoader: ResourceLoader): MarkdownReader {
            val markdownProfileReaderProperties = MarkdownReaderProperties(
                enabled = true,
                file = "classpath:profile/profile-test.md"
            )
            return MarkdownReader(markdownProfileReaderProperties, resourceLoader)
        }

        fun configuredMarkdownProjectsImporter(resourceLoader: ResourceLoader): MarkdownProjectsImporter {
            val markdownProjectsImporterProperties = MarkdownProjectsImporterProperties(
                enabled = true,
                file = "classpath:projects/project-test.md"
            )
            return MarkdownProjectsImporter(markdownProjectsImporterProperties, resourceLoader)
        }

        fun configuredPdfProjectsImporter(resourceLoader: ResourceLoader): PdfProjectsImporter {
            val pdfProjectsImporterProperties = PdfProjectsImporterProperties(
                enabled = true,
                file = "classpath:projects/project-dummy.pdf"
            )
            return PdfProjectsImporter(pdfProjectsImporterProperties, resourceLoader)
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