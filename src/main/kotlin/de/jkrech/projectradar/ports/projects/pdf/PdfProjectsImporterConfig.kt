package de.jkrech.projectradar.ports.projects.pdf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "projects.importer.pdf")
data class PdfProjectsImporterProperties(
    val enabled: Boolean = false,
    val file: String = ""
)

@Configuration
@EnableConfigurationProperties(PdfProjectsImporterProperties::class)
class PdfProjectsImporterConfiguration