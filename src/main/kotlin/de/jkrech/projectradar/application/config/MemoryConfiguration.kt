package de.jkrech.projectradar.application.config

import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.ChatMemoryRepository
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MemoryConfiguration {

    @Bean
    fun chatMemory(chatMemoryRepository: ChatMemoryRepository): ChatMemory {
        return MessageWindowChatMemory.builder()
            .chatMemoryRepository(chatMemoryRepository)
            .maxMessages(10)
            .build();
    }
}