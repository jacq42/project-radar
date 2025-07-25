package de.jkrech.projectradar.domain

data class ProjectMatch(
    val title: String,
    val source: String,
    val score: Int,
    val profileType: ProfileResource.Type
)