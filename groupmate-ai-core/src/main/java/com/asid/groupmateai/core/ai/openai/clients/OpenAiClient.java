package com.asid.groupmateai.core.ai.openai.clients;

import io.github.sashirestela.openai.SimpleOpenAI;

public abstract class OpenAiClient {

    protected final SimpleOpenAI openAiClient;

    public OpenAiClient(final SimpleOpenAI openAiClient) {
        this.openAiClient = openAiClient;
    }
}
