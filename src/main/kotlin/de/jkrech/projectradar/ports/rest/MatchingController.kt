package de.jkrech.projectradar.ports.rest

import de.jkrech.projectradar.application.MatchingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class MatchingController(
    private val matchingService: MatchingService
) {

    @GetMapping("/matches")
    fun getMatches(): ResponseEntity<String> {
        matchingService.findMatches()
        return ResponseEntity.ok("Matching process completed")
    }
}