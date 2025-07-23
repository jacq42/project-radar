package de.jkrech.projectradar.ports.projects.pdf

import de.jkrech.projectradar.ConfigurationHelper.Companion.configuredPdfProjectsImporter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ResourceLoader

@SpringBootTest
class PdfProjectsImporterIntegrationTest {

    @Autowired
    lateinit var resourceLoader: ResourceLoader

    @Test
    fun `should find projects in pdf file`() {
        // given
        val pdfProjectsImporter = configuredPdfProjectsImporter(resourceLoader)

        // when
        val documents = pdfProjectsImporter.import()

        // then
        assertThat(documents).hasSize(1)
        val document = documents.first()
        assertThat(document.id).isNotEmpty
        assertThat(document.text).isNotEmpty
        assertThat(document.metadata)
            .containsEntry("file_name", "project-dummy.pdf")
            .containsEntry("end_page_number", 2)
    }
}