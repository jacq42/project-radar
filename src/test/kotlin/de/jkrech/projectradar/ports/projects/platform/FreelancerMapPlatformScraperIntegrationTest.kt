package de.jkrech.projectradar.ports.projects.platform

import de.jkrech.projectradar.ConfigurationHelper.Companion.configuredFreelancermapPlatformScraper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled("Just for manual testing, not part of the CI pipeline")
class FreelancerMapPlatformScraperIntegrationTest {

    @Test
    fun `should find projects`() {
        // given
        val scraper = configuredFreelancermapPlatformScraper(listOf("kotlin", "devops", "cloud"))

        // when
        val documents = scraper.import()

        // then
        assertThat(documents).isNotEmpty()
    }

}