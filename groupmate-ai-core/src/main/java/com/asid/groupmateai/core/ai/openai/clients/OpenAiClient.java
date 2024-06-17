package com.asid.groupmateai.core.ai.openai.clients;

import io.github.sashirestela.openai.SimpleOpenAI;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

@Slf4j
public abstract class OpenAiClient {

    private static final short MAX_RETRIES = 3;

    protected final SimpleOpenAI openAiClient;

    protected OpenAiClient(final SimpleOpenAI openAiClient) {
        this.openAiClient = openAiClient;
    }

    protected <T> CompletableFuture<T> handleRequestWithRetry(final Function<SimpleOpenAI, CompletableFuture<T>> request) {
        return withRetry(request, MAX_RETRIES);
    }

    private <T> CompletableFuture<T> withRetry(final Function<SimpleOpenAI, CompletableFuture<T>> request,
                                               int maxRetries) {
        return request.apply(this.openAiClient)
            .exceptionally(ex -> {
                if (maxRetries > 0) {
                    log.warn("OpenAI client request failed, retrying...");
                    return withRetry(request, maxRetries - 1).join();
                } else {
                    throw new CompletionException(ex);
                }
        });
    }
}
