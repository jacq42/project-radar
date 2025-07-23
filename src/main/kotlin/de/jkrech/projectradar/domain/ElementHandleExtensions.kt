package de.jkrech.projectradar.domain

import com.microsoft.playwright.ElementHandle

fun ElementHandle?.extractAndTrim(): String {
    return this?.textContent()
        ?.replace(Regex("\\s+"), " ")
        ?.trim() ?: ""
}