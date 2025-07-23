package de.jkrech.projectradar.ports.projects.platform

import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.ElementHandle
import com.microsoft.playwright.Playwright
import de.jkrech.projectradar.domain.extensions.extractAndTrim
import de.jkrech.projectradar.domain.extensions.selectAllAndJoin
import de.jkrech.projectradar.domain.extensions.selectAndTrim
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
                    if (projectCards.size > 10) {
                        logger.warn("More than 10 projects found, only processing the first 10 for performance reasons.")
                    }
                    val limitedCards = if (projectCards.size > 10) projectCards.toList().take(10) else projectCards.toList()

                    for (card in limitedCards) {
                        val teaserDocument = createTeaserDocument(card)
                        val detailUrl = teaserDocument.metadata["url"] as String

                        try {
                            val fullDocument = fetchProjectDetails(context, detailUrl, teaserDocument)
                            documents.add(fullDocument)
                        } catch (e: Exception) {
                            logger.error("Failed to fetch details for $detailUrl: ${e.message}")
                            documents.add(teaserDocument)
                        }

                        Thread.sleep(500)
                    }
                }
            }
        }

        logger.info("Found ${documents.size} projects")
        for (document in documents) {
            logger.info("Found Project: ${document.metadata["title"]} by ${document.metadata["company"]}")
            logger.info("URI: ${document.metadata["url"]}")
            logger.info("Start: ${document.metadata["startDate"]}")
            logger.info("Duration: ${document.metadata["duration"]}")
            logger.info("Location: ${document.metadata["location"]}")
            logger.info("Skills: ${document.metadata["skills"]}")
            logger.info("Industry: ${document.metadata["industry"]}")
            logger.info("Contract Type: ${document.metadata["contractType"]}")
            logger.info("Usage Type: ${document.metadata["usageType"]}")
        }
        return documents
    }

    private fun createTeaserDocument(card: ElementHandle): Document {
        val company = card.querySelector("a.company").extractAndTrim()
        val title = card.querySelector("a.project-title").extractAndTrim()
        val description = card.querySelector("div.description").extractAndTrim()
        val skills = card.querySelector("div.keywords-container").extractAndTrim()
        val location = card.querySelector("div.project-location").extractAndTrim()
        val detailUrlElement = card.querySelector("a.project-title")
        val detailUrl = detailUrlElement?.getAttribute("href") ?: ""
        val fullUrl = if (detailUrl.startsWith("http")) detailUrl else "$BASE_URL$detailUrl"

        val metadata = mapOf(
            "company" to company,
            "title" to title,
            "skills" to skills,
            "location" to location,
            "url" to fullUrl,
            "source" to "freelancermap"
        )

        return Document(fullUrl, "$title\n\n$description", metadata)
    }

    private fun fetchProjectDetails(context: BrowserContext, url: String, teaserDocument: Document): Document {
        val detailPage = context.newPage()
        try {
            detailPage.navigate(url)
            detailPage.waitForLoadState()

            val fullDescription = detailPage.selectAndTrim("div.content > div.description")
            val skills = detailPage.selectAllAndJoin("div.projectcontent > div.keywords-container > span.keyword")
            val startDate = detailPage.selectAndTrim("dt[data-translatable='startHeader'] + dd div.project-detail-description")
            val projectDuration = detailPage.selectAndTrim("dt[data-translatable='durationHeader'] + dd div.project-detail-description")
            val industry = detailPage.selectAndTrim("dt[data-translatable='industryHeader'] + dd.project-detail-description")
            val contractType = detailPage.selectAndTrim("dt[data-translatable='contractTypeHeader'] + dd.project-detail-description")
            val usageType = detailPage.selectAndTrim("dt[data-translatable='usageTypeHeader'] + dd.project-detail-description")

            val enhancedMetadata = teaserDocument.metadata.toMutableMap().apply {
                put("fullDescription", fullDescription)
                put("startDate", startDate)
                put("duration", projectDuration)
                put("industry", industry)
                put("contractType", contractType)
                put("usageType", usageType)
                put("skills", skills)
            }

            return Document(url, "$fullDescription\n${teaserDocument.text}", enhancedMetadata)
        } finally {
            detailPage.close()
        }
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