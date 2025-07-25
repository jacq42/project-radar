package de.jkrech.projectradar.application.scoring

import de.jkrech.projectradar.domain.ImportedProject
import org.springframework.ai.document.Document

interface ScoreEngine {

    fun findScoresFor(profileData: List<Document>): List<ImportedProject>
}