package de.jkrech.projectradar.application

class MatchingServiceException: RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
}