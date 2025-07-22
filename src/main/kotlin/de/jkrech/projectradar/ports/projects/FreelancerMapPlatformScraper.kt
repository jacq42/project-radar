package de.jkrech.projectradar.ports.projects

import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document

class FreelancerMapPlatformScraper: PlatformScraper(url()) {

    private val logger = LoggerFactory.getLogger(FreelancerMapPlatformScraper::class.java)

    override fun scrape(url: String): List<Document> {
        logger.info("Scraping projects from $url")
        val documents = mutableListOf<Document>()

        Playwright.create().use { playwright ->
            val browser = playwright.chromium().launch(
                BrowserType.LaunchOptions()
                    .setHeadless(true)
            )

            browser.use {
                val context = browser.newContext()

                context.use {
                    val page = context.newPage()
                    page.navigate(url)
                    page.waitForLoadState()

                    // Warte auf die Projektkarten
                    page.waitForSelector("#project-search-result")

                    // Alle Projektkarten finden und Daten extrahieren
                    val projectCards = page.querySelectorAll("div.project-container")

                    for (card in projectCards) {
                        val title = card.querySelector("a.company")?.textContent()?.trim() ?: ""
                        val description = card.querySelector("div.description")?.textContent()?.trim() ?: ""
                        val skills = card.querySelector("div.keywords-container")?.textContent()?.trim() ?: ""
                        val location = card.querySelector("div.project-location")?.textContent()?.trim() ?: ""
                        val detailUrlElement = card.querySelector("a.company")
                        val detailUrl = detailUrlElement?.getAttribute("href") ?: ""
                        val fullUrl = if (detailUrl.startsWith("http")) detailUrl else "https://www.freelancermap.de$detailUrl"

                        // Dokument mit Metadaten erstellen
                        val metadata = mapOf(
                            "title" to title,
                            "skills" to skills,
                            "location" to location,
                            "url" to fullUrl,
                            "source" to "freelancermap.de"
                        )

                        documents.add(Document(fullUrl, "$title\n\n$description", metadata))
                    }
                }
            }
        }

        logger.info("Found ${documents.size} projects")
        return documents
    }

    companion object {
        const val BASE_URL = "https://www.freelancermap.de/projektboerse.html"
        const val BRACKET_ARRAY_0 = "%5B0%5D"
        const val CONTRACT_TYPES = "contracting"
        const val QUERY = "kotlin+devops+cloud"

        fun url(): String {
            return "$BASE_URL?projectContractTypes$BRACKET_ARRAY_0=$CONTRACT_TYPES&query=$QUERY&countries$BRACKET_ARRAY_0=1&sort=2&pagenr=1"
        }
    }
}