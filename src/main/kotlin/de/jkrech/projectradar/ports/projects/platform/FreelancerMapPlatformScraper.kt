package de.jkrech.projectradar.ports.projects.platform

import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.ElementHandle
import com.microsoft.playwright.Playwright
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["projects.importer.platform.freelancermap.enabled"], havingValue = "true", matchIfMissing = false)
class FreelancerMapPlatformScraper(
    properties: FreelancerMapProperties
): PlatformScraper(url(properties.keywords)) {

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

                    page.waitForSelector("#project-search-result")

                    val projectCards = page.querySelectorAll("div.project-container")
                    for (card in projectCards) {
                        val document = createDocument(card)
                        documents.add(document)
                    }
                }
            }
        }

        logger.info("Found ${documents.size} projects")
        return documents
    }

    private fun createDocument(card: ElementHandle): Document {
        val title = card.querySelector("a.company")?.textContent()?.trim() ?: ""
        val description = card.querySelector("div.description")?.textContent()?.trim() ?: ""
        val skills = card.querySelector("div.keywords-container")?.textContent()?.trim() ?: ""
        val location = card.querySelector("div.project-location")?.textContent()?.trim() ?: ""
        val detailUrlElement = card.querySelector("a.company")
        val detailUrl = detailUrlElement?.getAttribute("href") ?: ""
        val fullUrl = if (detailUrl.startsWith("http")) detailUrl else "$BASE_URL$detailUrl"

        val metadata = mapOf(
            "title" to title,
            "skills" to skills,
            "location" to location,
            "url" to fullUrl,
            "source" to "freelancermap"
        )

        return Document(fullUrl, "$title\n\n$description", metadata)
    }

    companion object {
        const val BASE_URL = "https://www.freelancermap.de"
        const val URI_PROJECTS = "projektboerse.html"
        const val URI_QUERY_PARAMS = "projectContractTypes%5B0%5D=contracting&countries%5B0%5D=1&sort=2&pagenr=1"

        fun url(keywords: List<String>): String {
            val searchQueries = keywords.joinToString("+")
            return "$BASE_URL/$URI_PROJECTS?$URI_QUERY_PARAMS&query=$searchQueries"
        }
    }
}