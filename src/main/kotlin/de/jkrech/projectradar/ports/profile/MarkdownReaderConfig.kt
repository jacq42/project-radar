package de.jkrech.projectradar.ports.profile

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "profile.reader.markdown")
data class MarkdownReaderProperties(
    val enabled: Boolean = false,
    val file: String = ""
)

@Configuration
@EnableConfigurationProperties(MarkdownReaderProperties::class)
class MarkdownReaderConfiguration