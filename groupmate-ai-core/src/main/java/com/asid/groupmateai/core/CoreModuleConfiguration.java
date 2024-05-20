package com.asid.groupmateai.core;

import com.asid.groupmateai.storage.StorageModuleConfiguration;
import io.github.sashirestela.openai.SimpleOpenAI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import(StorageModuleConfiguration.class)
public class CoreModuleConfiguration {

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
