package de.jkrech.projectradar.application

import de.jkrech.projectradar.ports.profile.MarkdownReader
import de.jkrech.projectradar.ports.projects.MarkdownProjectsImporter
import org.junit.jupiter.api.Test
import org.springframework.ai.openai.OpenAiEmbeddingModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import kotlin.test.assertEquals

@SpringBootTest
class MatchingServiceIntegrationTest {

    @Autowired
    lateinit var openAiEmbeddingModel: OpenAiEmbeddingModel

    @Test
    fun `should find matches with real markdown files`() {
        // given
        val profile = ClassPathResource("profile/profile-test.md")
        val realProfileReader = MarkdownReader(profile)

        val project = ClassPathResource("projects/project-test.md")
        val realProjectsImporter = MarkdownProjectsImporter(project)

        val matchingService = MatchingService(openAiEmbeddingModel, realProfileReader, listOf(realProjectsImporter))

        // when
        matchingService.findMatches()
    }

}