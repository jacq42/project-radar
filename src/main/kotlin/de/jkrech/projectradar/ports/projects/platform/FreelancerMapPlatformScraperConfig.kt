package de.jkrech.projectradar.ports.projects.platform

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "projects.importer.platform.freelancermap")
data class FreelancerMapProperties(
    val enabled: Boolean = false,
    val keywords: List<String> = emptyList()
)

@Configuration
@EnableConfigurationProperties(FreelancerMapProperties::class)
class FreelancerMapConfiguration