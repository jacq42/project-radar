package de.jkrech.projectradar.domain.extensions

import com.microsoft.playwright.ElementHandle

fun ElementHandle?.extractAndTrim(): String {
    return this?.textContent()
        ?.replace(Regex("\\s+"), " ")
        ?.trim() ?: ""
}