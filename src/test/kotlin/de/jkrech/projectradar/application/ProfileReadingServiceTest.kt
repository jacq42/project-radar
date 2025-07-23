package de.jkrech.projectradar.application

import de.jkrech.projectradar.domain.ProfileResource
import de.jkrech.projectradar.ports.profile.ProfileReaderFactory
import de.jkrech.projectradar.ports.profile.markdown.MarkdownReader
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verifySequence
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.ai.document.Document
import org.springframework.core.io.ClassPathResource

@ExtendWith(MockKExtension::class)
class ProfileReadingServiceTest {

    @MockK
    private lateinit var profileReaderFactory: ProfileReaderFactory

    @InjectMockKs
    private lateinit var profileReadingService: ProfileReadingService

    @Test
    fun `should analyze profile`() {
        // given
        val testDocuments = listOf(Document("Some content", mapOf("filename" to "profile-de.md")))
        val profileResource = ProfileResource(ClassPathResource("profile/profile-de.md"))
        val markdownReader = mockk<MarkdownReader>()
        every { profileReaderFactory.findBy(any()) } returns markdownReader
        every { markdownReader.read(any()) } returns testDocuments

        // when
        val documents = profileReadingService.analyze(profileResource)

        // then
        assertThat(documents).isEqualTo(testDocuments)
        verifySequence {
            profileReaderFactory.findBy(profileResource)
            markdownReader.read(profileResource)
        }
    }
}