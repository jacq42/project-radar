package de.jkrech.projectradar.application

import de.jkrech.projectradar.ports.profile.MarkdownReader
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.ai.document.Document
import org.springframework.core.io.ClassPathResource
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class MatchingServiceTest {

    @MockK
    private lateinit var profileReader: ProfileReader

    @InjectMockKs
    private lateinit var matchingService: MatchingService

    @Test
    fun `should find matches`() {
        // given
        val testDocuments = listOf(Document("Some content", mapOf("filename" to "profile-de.md")))
        every { profileReader.read() } returns testDocuments

        // when
        matchingService.findMatches()

        // then
        verify { profileReader.read() }
    }

    @Test
    fun `should find matches with real markdown file`() {
        // given
        val profile = ClassPathResource("profile/profile-test.md")
        val realProfileReader = MarkdownReader(profile)
        val serviceWithRealReader = MatchingService(realProfileReader)

        // when
        serviceWithRealReader.findMatches()

        // then
        val documents = realProfileReader.read()
        assertEquals(3, documents.size, "Every headline should produce a document")
    }

}