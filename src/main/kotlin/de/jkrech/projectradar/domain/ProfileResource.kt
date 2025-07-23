package de.jkrech.projectradar.domain

import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Mono
import java.io.File
import java.nio.file.Files

@JvmInline
value class ProfileResource(private val input: Resource) {

    val value: Resource
        get() = input

    enum class Type {
        MARKDOWN, PDF, UNKNOWN
    }

    fun type(): Type {
        val filename = value.filename ?: return Type.UNKNOWN
        return when {
            filename.endsWith(".md", true) || filename.endsWith(".markdown") -> Type.MARKDOWN
            filename.endsWith(".pdf", true) -> Type.PDF
            else -> Type.UNKNOWN
        }
    }

    companion object {
        fun from(profile: FilePart): Mono<ProfileResource> {
            return profile.filename()
                .let { filename ->
                    when {
                        filename.endsWith(".md", true) || filename.endsWith(".markdown") || filename.endsWith(".pdf", true) -> {
                            val suffix = filename.substringAfterLast(".")
                            val tempFile = Files.createTempFile("profile-", ".$suffix")
                            profile.transferTo(tempFile)
                                .then(Mono.just(ProfileResource(FileSystemResource(tempFile))))
                                .doOnError { Files.deleteIfExists(tempFile) }
                        }
                        else -> Mono.error(IllegalArgumentException("Unsupported file format: $filename"))
                    }
                }
        }
    }
}