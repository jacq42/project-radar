package de.jkrech.projectradar.domain.extensions

import com.microsoft.playwright.Page

fun Page.selectAndTrim(selector: String): String {
    return this.querySelector(selector)
        .extractAndTrim()
}

fun Page.selectAllAndJoin(selector: String, separator: String = ", "): String {
    return this.querySelectorAll(selector)
        .mapNotNull { it.textContent().trim() }
        .filter { it.isNotEmpty() }
        .joinToString(separator)
}