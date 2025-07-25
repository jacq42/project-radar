package de.jkrech.projectradar.application.scoring.relevance

import de.jkrech.projectradar.application.MatchingServiceException
import de.jkrech.projectradar.application.scoring.ProjectsImporter
import de.jkrech.projectradar.domain.ImportedProject
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.chat.prompt.SystemPromptTemplate
import org.springframework.ai.document.Document
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import kotlin.time.Duration.Companion.minutes

@Component
class RelevanceScoreEngine(
    private val projectsImporters: List<ProjectsImporter>,
    @Qualifier("openAiChatClient") private val chatClient: ChatClient
) {

    private val logger = LoggerFactory.getLogger(RelevanceScoreEngine::class.java)

    @Value("classpath:/prompts/profile-project-relevance-system.st")
    private val relevanceSystemPrompt: Resource? = null

    @Value("classpath:/prompts/profile-project-relevance-user.st")
    private val relevanceUserPrompt: Resource? = null

    fun findMostRelevant(profileData: List<Document>, importedProjects: List<ImportedProject>): List<ImportedProject> {
        if (projectsImporters.isEmpty()) {
            throw MatchingServiceException("No projects importers configured")
        }

        return runBlocking {
            try {
                if (importedProjects.isEmpty()) {
                    throw MatchingServiceException("No projects imported from any source")
                }

                val projectsWithRelevance = withTimeout(1.minutes) {
                    importedProjects
                        .map { project ->
                            async(Dispatchers.Default) { // Default for CPU intensive tasks
                                try {
                                    val systemMessage = createSystemMessage()
                                    val userMessage = createUserMessage(profileData, project.documents)

                                    val prompt = Prompt(listOf(systemMessage, userMessage))
                                    val relevance = chatClient.prompt(prompt).call().entity(RelevanceResponse::class.java)
                                    logger.info("Calculated relevance for ${project.source()}: $relevance")
                                    ImportedProject(
                                        importerSource = project.source(),
                                        documents = project.documents,
                                        relevance = relevance?.relevanceOrScore()
                                    )
                                } catch (exception: Exception) {
                                    logger.error("Could not calculate relevance for ${project.source()}: ${exception.message}", exception)
                                    null
                                }
                            }
                        }
                        .awaitAll()
                        .filterNotNull()
                }

                projectsWithRelevance
                    .sortedByDescending { it.relevance }
            } catch (e: Exception) {
                logger.error("Failure while finding profile to project matches: ${e.message}", e)
                emptyList()
            }
        }
    }

    private fun createSystemMessage(): Message? {
        val systemPromptTemplate = SystemPromptTemplate(relevanceSystemPrompt)
        return systemPromptTemplate.createMessage()
    }

    private fun createUserMessage(profile: List<Document>, project: List<Document>): Message? {
        val profileData = extractText(profile)
        val projectData = extractText(project)
        val userPromptTemplate = PromptTemplate(relevanceUserPrompt)
        return userPromptTemplate.createMessage(mapOf(
            "profile" to profileData,
            "project" to projectData
        ))
    }

    private fun extractText(documents: List<Document>): String {
        return cleanAndTruncateText(documents.joinToString("\n") { it.text ?: "" })
    }

    private fun cleanAndTruncateText(text: String): String {
        return text
            .replace(Regex("\\s{2,}"), " ")  // replace multiple " " with a single one
            .replace(Regex("-{2,}"), "-")    // replace multiple - with a single one
            .trim()
            .take(2000)
    }
}