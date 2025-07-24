package de.jkrech.projectradar.ports.projects.platform

import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.ElementHandle
import com.microsoft.playwright.Playwright
import de.jkrech.projectradar.application.ProjectsImporter
import org.slf4j.Logger
import org.springframework.ai.document.Document
import kotlin.use

abstract class PlatformScraper(val url: String): ProjectsImporter {

    override fun import(): List<Document> {
        return scrape(url)
    }

    abstract fun createTeaserDocument(card: ElementHandle): Document
    abstract fun fetchProjectDetails(context: BrowserContext, url: String, teaserDocument: Document): Document
    abstract fun selectorForSearchResults(): String
    abstract fun selectorForTeaserCards(): String
    abstract fun printLog(document: Document)
    abstract fun logger(): Logger

    fun scrape(url: String): List<Document> {
        logger().info("Scraping projects from $url")
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

                    page.waitForSelector(selectorForSearchResults())

                    val projectCards = page.querySelectorAll(selectorForTeaserCards())
                    if (projectCards.size > 10) {
                        logger().warn("More than 10 projects found, only processing the first 10 for performance reasons.")
                    }
                    val limitedCards = if (projectCards.size > 10) projectCards.toList().take(10) else projectCards.toList()

                    for (card in limitedCards) {
                        val teaserDocument = createTeaserDocument(card)
                        val detailUrl = teaserDocument.metadata["url"] as String

                        try {
                            val fullDocument = fetchProjectDetails(context, detailUrl, teaserDocument)
                            documents.add(fullDocument)
                        } catch (e: Exception) {
                            logger().error("Failed to fetch details for $detailUrl: ${e.message}")
                            documents.add(teaserDocument)
                        }

                        Thread.sleep(500)
                    }
                }
            }
        }

        logger().info("Found ${documents.size} projects")
        documents.forEach { printLog(it) }
        return documents
    }
}