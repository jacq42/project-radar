package de.jkrech.projectradar.application

import de.jkrech.projectradar.ports.profile.MarkdownReader
import de.jkrech.projectradar.ports.projects.FreelancerMapPlatformScraper
import de.jkrech.projectradar.ports.projects.MarkdownProjectsImporter
import de.jkrech.projectradar.ports.projects.PdfProjectsImporter
import org.junit.jupiter.api.Test
import org.springframework.ai.openai.OpenAiEmbeddingModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource

@SpringBootTest
class MatchingServiceIntegrationTest {

    @Autowired
    lateinit var openAiEmbeddingModel: OpenAiEmbeddingModel

    @Test
    fun `should find matches with real markdown and pdf files`() {
        // given
        val profileMarkdown = ClassPathResource("profile/profile-test.md")
        val markdownProfileReader = MarkdownReader(profileMarkdown)

        val projectMarkdown = ClassPathResource("projects/project-test.md")
        val markdownProjectsImporter = MarkdownProjectsImporter(projectMarkdown)

        val projectPdf = ClassPathResource("projects/project-dummy.pdf")
        val pdfProjectsImporter = PdfProjectsImporter(projectPdf)

        val matchingService = MatchingService(openAiEmbeddingModel, markdownProfileReader, listOf(markdownProjectsImporter, pdfProjectsImporter))

        // when
        matchingService.findMatches()
    }

    @Test
    fun `should find matches with real markdown files and platform scrapes`() {
        // given
        val profileMarkdown = ClassPathResource("profile/profile-test.md")
        val markdownProfileReader = MarkdownReader(profileMarkdown)

        val freelancerMapScraper = FreelancerMapPlatformScraper()

        val matchingService = MatchingService(openAiEmbeddingModel, markdownProfileReader, listOf(freelancerMapScraper))

        // when
        matchingService.findMatches()
    }

}