package com.asid.groupmateai.core.ai.openai.clients;

import io.github.sashirestela.cleverclient.Event;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.common.DeletedObject;
import io.github.sashirestela.openai.domain.assistant.Thread;
import io.github.sashirestela.openai.domain.assistant.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * ThreadOpenAiClient is a client for OpenAI Thread API, Thread Messages API, and Thread Runs API.
 * <p>
 * Docs: <a href="https://platform.openai.com/docs/api-reference/threads">...</a>
 * <p>
 * Threads
 * <p>
 * 1. Create thread
 * 2. Retrieve thread
 * 3. Delete thread
 * <p>
 * Messages
 * <p>
 * 1. Create message
 * 2. Delete message | TODO: Not implemented yet
 * <p>
 * Runs
 * <p>
 * 1. Create run
 * 2. Retrieve run
 */

@Service
@Slf4j
public class ThreadOpenAiClient extends OpenAiClient {

    @Autowired
    public ThreadOpenAiClient(SimpleOpenAI openAiClient) {
        super(openAiClient);
    }

    public CompletableFuture<Thread> createThread(ThreadRequest threadRequest) {
        return this.openAiClient.threads()
            .create(threadRequest)
            .thenApply(thread -> {
                log.debug("Thread was created: {}", thread.getId());
                return thread;
            });
    }

    public CompletableFuture<Thread> createThreadWithVectorStore(final String vectorStoreId) {
        final ToolResourceFull toolResource = ToolResourceFull.builder()
            .fileSearch(ToolResourceFull.FileSearch.builder()
                .vectorStoreId(vectorStoreId)
                .build())
            .build();
        final ThreadRequest threadRequest = ThreadRequest.builder()
            .toolResources(toolResource)
            .build();

        return this.createThread(threadRequest)
            .thenApply(thread -> {
                log.debug("Vector Store ({}) was attached to thread: {}", vectorStoreId, thread.getId());
                return thread;
            });
    }

    public CompletableFuture<Thread> getThread(final String threadId) {
        return this.openAiClient.threads()
            .getOne(threadId);
    }

    public CompletableFuture<DeletedObject> deleteThread(final String threadId) {
        return this.openAiClient.threads()
            .delete(threadId)
            .thenApply(deleted -> {
                if (deleted.getDeleted()) {
                    log.debug("Thread was deleted: {}", deleted.getId());
                } else {
                    log.debug("Thread failed to delete: {}", deleted.getId());
                }

                return deleted;
            });
    }

    public CompletableFuture<ThreadMessage> createThreadMessage(final String threadId,
                                                                final ThreadMessageRequest threadMessageRequest) {
        return this.openAiClient.threadMessages()
            .create(threadId, threadMessageRequest)
            .thenApply(threadMessage -> {
                log.debug("Thread message was created: {}", threadMessage.getId());
                return threadMessage;
            });
    }

    public CompletableFuture<Stream<Event>> createThreadRun(final String threadId,
                                                            final ThreadRunRequest threadRunRequest) {
        return this.openAiClient.threadRuns()
            .createStream(threadId, threadRunRequest)
            .thenApply(events -> {
                log.debug("Thread run was created for thread: {}", threadId);
                return events;
            });
    }

}
