package de.jkrech.projectradar.application

import de.jkrech.projectradar.ConfigurationHelper.Companion.configuredFreelancermapPlatformScraper
import de.jkrech.projectradar.ConfigurationHelper.Companion.configuredMarkdownProfileReader
import de.jkrech.projectradar.ConfigurationHelper.Companion.configuredMarkdownProjectsImporter
import de.jkrech.projectradar.ConfigurationHelper.Companion.configuredPdfProjectsImporter
import org.junit.jupiter.api.Test
import org.springframework.ai.openai.OpenAiEmbeddingModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ResourceLoader

@SpringBootTest
class MatchingServiceIntegrationTest {

    @Autowired
    lateinit var openAiEmbeddingModel: OpenAiEmbeddingModel

    @Autowired
    lateinit var resourceLoader: ResourceLoader

    @Test
    fun `should find matches with real markdown and pdf files`() {
        // given
        val markdownProfileReader = configuredMarkdownProfileReader(resourceLoader)
        val markdownProjectsImporter = configuredMarkdownProjectsImporter(resourceLoader)
        val pdfProjectsImporter = configuredPdfProjectsImporter(resourceLoader)

        val matchingService = MatchingService(
            embeddingModel = openAiEmbeddingModel,
            profileReader = markdownProfileReader,
            projectsImporters = listOf(markdownProjectsImporter, pdfProjectsImporter)
        )

        // when
        matchingService.findMatches()
    }

    @Test
    fun `should find matches with real markdown files and platform scrapes`() {
        // given
        val markdownProfileReader = configuredMarkdownProfileReader(resourceLoader)
        val freelancerMapScraper = configuredFreelancermapPlatformScraper(listOf("kotlin", "devops", "cloud"))

        val matchingService = MatchingService(
            embeddingModel = openAiEmbeddingModel,
            profileReader = markdownProfileReader,
            projectsImporters = listOf(freelancerMapScraper))

        // when
        matchingService.findMatches()
    }
}