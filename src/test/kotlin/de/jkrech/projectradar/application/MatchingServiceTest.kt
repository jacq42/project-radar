package de.jkrech.projectradar.application

import de.jkrech.projectradar.application.scoring.relevance.RelevanceScoreEngine
import de.jkrech.projectradar.application.scoring.similarity.SimilarityScoreEngine
import de.jkrech.projectradar.domain.ImportedProject
import de.jkrech.projectradar.domain.ProfileResource
import de.jkrech.projectradar.domain.ProjectMatch
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.ai.document.Document
import org.springframework.core.io.ClassPathResource

@ExtendWith(MockKExtension::class)
class MatchingServiceTest {

    @MockK
    private lateinit var profileReadingService: ProfileReadingService

    @MockK
    private lateinit var similarityScoreEngine: SimilarityScoreEngine

    @MockK
    private lateinit var relevanceScoreEngine: RelevanceScoreEngine

    @InjectMockKs
    private lateinit var matchingService: MatchingService


    @Test
    fun `should find matches for profile`() {
        // given
        val profileResource = ProfileResource(ClassPathResource("profile/profile-de.md"))
        val testDocument1 = documentWithText("project 1")
        val testDocument2 = documentWithText("project 2")
        val testDocument3 = documentWithText("project 3")
        val testDocument4 = documentWithText("project 4")
        val profileData = listOf(testDocument1, testDocument2, testDocument3, testDocument4)
        val importedProject1 = importedProject(testDocument1, 0.4)
        val importedProject2 = importedProject(testDocument2, 0.8)
        val importedProject3 = importedProject(testDocument3, 0.6)
        val importedProject4 = importedProject(testDocument4, 0.9)

        every { profileReadingService.analyze(any()) } returns profileData
        every { similarityScoreEngine.findMostSimilarProjectsFor(any()) } returns listOf(importedProject4, importedProject2, importedProject3, importedProject1)
        every { relevanceScoreEngine.findMostRelevant(any(), any()) } returns listOf(importedProject2, importedProject4)

        // when
        val matches = matchingService.findMatches(profileResource)

        // then
        assertThat(matches).containsExactly(
            ProjectMatch(
                title = "test-source",
                source = "test-source",
                score = 80,
                profileType = profileResource.type()
            ),
            ProjectMatch(
                title = "test-source",
                source = "test-source",
                score = 90,
                profileType = profileResource.type()
            )
        )

        verify {
            profileReadingService.analyze(profileResource)
            similarityScoreEngine.findMostSimilarProjectsFor(profileData)
            relevanceScoreEngine.findMostRelevant(profileData, listOf(importedProject4, importedProject2, importedProject3))
        }
    }

    @Test
    fun `should throw exception when no documents found in profile`() {
        // given
        val profileResource = ProfileResource(ClassPathResource("profile/empty-profile.md"))
        every { profileReadingService.analyze(any()) } returns emptyList()

        // when & then
        val exception = assertThrows(MatchingServiceException::class.java) {
            matchingService.findMatches(profileResource)
        }
        assertThat(exception.message).isEqualTo("No documents found in profile: empty-profile.md")

        verify { profileReadingService.analyze(profileResource) }
    }

    private fun documentWithText(text: String): Document {
        return Document(text, mapOf("filename" to "profile-de.md"))
    }

    private fun importedProject(document: Document, similarity: Double): ImportedProject {
        return ImportedProject(
            importerSource = "test-source",
            documents = listOf(document),
            embeddings = emptyList(),
            similarity = similarity,
            relevance = (similarity * 100).toInt()
        )
    }

}