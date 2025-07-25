package de.jkrech.projectradar.ports.profile

import de.jkrech.projectradar.application.ProfileReader
import de.jkrech.projectradar.domain.ProfileResource
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.core.io.ClassPathResource

@ExtendWith(MockKExtension::class)
class ProfileReaderFactoryTest {

    @MockK
    private lateinit var profileReader: ProfileReader

    @Test
    fun `should find profile reader by type`() {
        // given
        every { profileReader.supports(any()) } returns true
        val factory = ProfileReaderFactory(listOf(profileReader))

        // when
        val reader = factory.findBy(ProfileResource(ClassPathResource("profile/profile-de.md")))

        // then
        assertThat(reader).isNotNull

        verify { profileReader.supports(ProfileResource.Type.MARKDOWN) }
    }

    @Test
    fun `should throw exception for unsupported profile type`() {
        // given
        val factory = ProfileReaderFactory(emptyList())

        // when & then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            factory.findBy(ProfileResource(ClassPathResource("profile/unsupported.txt")))
        }
        assertThat(exception.message).isEqualTo("Unsupported profile resource type: UNKNOWN")
    }
}