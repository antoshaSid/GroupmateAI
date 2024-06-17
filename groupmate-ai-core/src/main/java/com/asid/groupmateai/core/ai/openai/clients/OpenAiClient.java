package com.asid.groupmateai.core.ai.openai.clients;

import io.github.sashirestela.openai.SimpleOpenAI;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Slf4j
public abstract class OpenAiClient {

    private static final short MAX_RETRIES = 3;

    protected final SimpleOpenAI openAiClient;

    protected OpenAiClient(final SimpleOpenAI openAiClient) {
        this.openAiClient = openAiClient;
    }

    protected <T> CompletableFuture<T> handleRequestWithRetry(final Function<SimpleOpenAI, CompletableFuture<T>> request) {
        short attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                return request.apply(this.openAiClient);
            } catch (final Exception e) {
                attempts++;
                log.warn("OpenAi client request failed, retrying... Attempt: {}", attempts, e);
            }
        }

        throw new RuntimeException("Max retries exceeded");
    }
}
