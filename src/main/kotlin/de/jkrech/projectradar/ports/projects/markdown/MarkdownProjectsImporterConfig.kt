package de.jkrech.projectradar.ports.projects.markdown

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "projects.importer.markdown")
data class MarkdownProjectsImporterProperties(
    val enabled: Boolean = false,
    val file: String = ""
)

@Configuration
@EnableConfigurationProperties(MarkdownProjectsImporterProperties::class)
class MarkdownProjectsImporterConfiguration