package de.jkrech.projectradar.application

import de.jkrech.projectradar.domain.ImportedProject
import de.jkrech.projectradar.domain.ProfileResource
import de.jkrech.projectradar.domain.ProjectMatch

interface ScoreEngine {

    fun findScores(profileResource: ProfileResource): List<ImportedProject>
}