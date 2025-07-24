package de.jkrech.projectradar.domain

data class ProjectMatch(
    val title: String,
    val source: String,
    val similarity: Double,
    val profileType: ProfileResource.Type
)