package de.jkrech.projectradar.application

import org.springframework.ai.document.Document

interface ProfileReader {

    fun read(): List<Document>
}