package de.jkrech.projectradar.ports.rest

import de.jkrech.projectradar.application.MatchingService
import de.jkrech.projectradar.domain.ProfileResource
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@RestController
@RequestMapping("/api")
class MatchingController(
    private val matchingService: MatchingService
) {

    @PostMapping("/matches")
    fun getMatches(@RequestPart("profile") profile: FilePart): Mono<ResponseEntity.BodyBuilder?> {
        return ProfileResource.from(profile)
            .subscribeOn(Schedulers.boundedElastic())
            .map { matchingService.findMatches(it) }
            .map { ResponseEntity.ok() }
    }
}