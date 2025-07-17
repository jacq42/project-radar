package de.jkrech.projectradar

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProjectRadarApplication

fun main(args: Array<String>) {
	runApplication<ProjectRadarApplication>(*args)
}
