package de.jkrech.projectradar.ports.projects.markdown

import de.jkrech.projectradar.ConfigurationHelper.Companion.configuredMarkdownProjectsImporter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.ai.document.Document

class MarkdownProjectsImporterIntegrationTest {

    @Test
    fun `should find projects in markdown file`() {
        // given
        val markdownProjectsImporter = configuredMarkdownProjectsImporter()

        // when
        val documents = markdownProjectsImporter.import()

        // then
        assertThat(documents).hasSize(3)
        assertParagraph(documents[0], "Skills")
        assertParagraph(documents[1], "Arbeitsweise")
        assertParagraph(documents[2], "Sprachen")
    }

    private fun assertParagraph(paragraph: Document, expectedTitle: String) {
        assertThat(paragraph.id).isNotEmpty
        assertThat(paragraph.text).isNotEmpty
        assertThat(paragraph.metadata)
            .containsEntry("title", expectedTitle)
            .containsEntry("filename", "profile.md")
    }
}