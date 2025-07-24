package de.jkrech.projectradar.ports.projects.platform

class PlatformScrapingException: RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
}