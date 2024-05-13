package com.asid.groupmateai.core.ai.openai.clients.configs;

import io.github.sashirestela.openai.SimpleOpenAI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiClientConfiguration {

    @Bean
    public SimpleOpenAI configureOpenAiClient(@Value("${OPENAI_API_KEY}") final String apiKey,
                                              @Value("${OPENAI_ORGANIZATION_ID}") final String organizationId,
                                              @Value("${OPENAI_PROJECT_ID}") final String projectId) {
        return SimpleOpenAI.builder()
            .apiKey(apiKey)
            .organizationId(organizationId)
            .projectId(projectId)
            .build();
    }
}
