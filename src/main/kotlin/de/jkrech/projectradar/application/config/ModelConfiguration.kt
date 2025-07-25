package de.jkrech.projectradar.application.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.time.Duration

@AutoConfiguration(beforeName = ["org.springframework.ai.model.chat.client.autoconfigure.ChatClientAutoConfiguration"])
class ModelConfiguration {

    final val logger: Logger = LoggerFactory.getLogger(ModelConfiguration::class.java)

    @Bean
    fun openAiChatClient(chatModel: OpenAiChatModel): ChatClient {
        return ChatClient.create(chatModel)
    }


    @Bean
    fun openAiChatClientWithMemory(chatModel: OpenAiChatModel, chatMemory: ChatMemory): ChatClient {
        return ChatClient.builder(chatModel)
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
            .build();
    }

    /**
     * Creates a RestClient. Builder with custom connection and read timeouts.
     * Can not be configured via application.yml
     */
    @Bean
    fun restClientBuilder(
        @Value("\${spring.ai.openai.http-client.connection-timeout:60s}") conTimeout: Duration,
        @Value("\${spring.ai.openai.http-client.read-timeout:2m}") readTimeout: Duration
    ): RestClient.Builder {
        val requestFactory = SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(conTimeout)
            setReadTimeout(readTimeout)
        }

        return RestClient.builder()
            .requestFactory(requestFactory)
    }
}