package de.jkrech.projectradar.ports.projects.platform

import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.ElementHandle
import de.jkrech.projectradar.domain.extensions.extractAndTrim
import de.jkrech.projectradar.domain.extensions.selectAllAndJoin
import de.jkrech.projectradar.domain.extensions.selectAndTrim
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["projects.importer.platform.freelancermap.enabled"], havingValue = "true", matchIfMissing = false)
class FreelancerMapPlatformScraper(
    val properties: FreelancerMapProperties
): PlatformScraper(url(properties.keywords)) {

    private val logger = LoggerFactory.getLogger(FreelancerMapPlatformScraper::class.java)

    override fun source(): String {
        return "Freelancermap: ${url(properties.keywords)}"
    }

    override fun createTeaserDocument(card: ElementHandle): Document {
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

    override fun fetchProjectDetails(context: BrowserContext, url: String, teaserDocument: Document): Document {
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

    override fun selectorForSearchResults(): String {
        return "#project-search-result"
    }

    override fun selectorForTeaserCards(): String {
        return "div.project-container"
    }

    override fun printLog(document: Document) {
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

    override fun logger(): Logger {
        return logger
    }

    companion object {
        const val BASE_URL = "https://www.freelancermap.de"
        const val URI_PROJECTS = "projektboerse.html"
        const val URI_QUERY_PARAM_CONTRACTING = "projectContractTypes%5B0%5D=contracting"
        const val URI_QUERY_PARAM_COUNTRY = "countries%5B0%5D=1"
        const val URI_QUERY_PARAM_CREATED = "created=7" // last 7 days
        const val URI_QUERY_PARAM_SORTING = "sort=2" // 1 - newest, 2 - relevance
        const val URI_QUERY_PARAMS = "$URI_QUERY_PARAM_CONTRACTING&$URI_QUERY_PARAM_COUNTRY&$URI_QUERY_PARAM_CREATED&$URI_QUERY_PARAM_SORTING&pagenr=1"

        fun url(keywords: List<String>): String {
            val searchQueries = keywords.joinToString("+")
            return "$BASE_URL/$URI_PROJECTS?$URI_QUERY_PARAMS&query=$searchQueries"
        }
    }
}