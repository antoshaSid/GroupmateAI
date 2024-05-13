package com.asid.groupmateai.core.ai.openai.clients;

import com.asid.groupmateai.core.ai.openai.clients.configs.OpenAiClientConfiguration;
import io.github.sashirestela.cleverclient.Event;
import io.github.sashirestela.openai.common.DeletedObject;
import io.github.sashirestela.openai.domain.assistant.Thread;
import io.github.sashirestela.openai.domain.assistant.*;
import io.github.sashirestela.openai.domain.assistant.events.EventName;
import io.github.sashirestela.openai.domain.file.FileRequest;
import io.github.sashirestela.openai.domain.file.FileResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.stream.Stream;

import static io.github.sashirestela.openai.domain.assistant.AssistantTool.FILE_SEARCH;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = { OpenAiClientConfiguration.class, ThreadOpenAiClient.class, VectorStoreOpenAiClient.class, FileOpenAiClient.class })
public class TestOpenAiClients {

    private static final String ASSISTANT_ID = "asst_qOwNbEcb4SbB1hEjO5HNH2tU";

    @Autowired
    private FileOpenAiClient fileOpenAiClient;

    @Autowired
    private ThreadOpenAiClient threadOpenAiClient;

    @Autowired
    private VectorStoreOpenAiClient vectorStoreOpenAiClient;

    private String fileId;
    private String vectorStoreId;
    private String threadId;

    @TempDir
    private Path tempDir;

    @Test
    public void testRunConversation() {
        try {
            this.fileId = createFile();
            this.vectorStoreId = createVectorStoreWithFile(fileId);
            this.threadId = createThreadWithVectorStore(vectorStoreId);

            final String myMessage = "Do you know how many subjects are on Monday?";

            final ThreadMessageRequest threadMessageRequest = ThreadMessageRequest.builder()
                .role(ThreadMessageRole.USER)
                .content(myMessage)
                .build();

            final ThreadMessage threadMessage = threadOpenAiClient.createThreadMessage(threadId, threadMessageRequest)
                .join();
            assertNotNull(threadMessage.getId());

            final ThreadRunRequest runRequest = ThreadRunRequest.builder()
                .assistantId(ASSISTANT_ID)
                .tool(FILE_SEARCH)
                .build();

            final Stream<Event> runStream = threadOpenAiClient.createThreadRun(threadId, runRequest)
                .join();

            handleRunEvents(runStream);
        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            cleanConversation(fileId, vectorStoreId, threadId);
        }
    }

    public void cleanConversation(final String fileId, final String vectorStoreId, final String threadId) {
        final DeletedObject deletedFile = fileOpenAiClient.deleteFile(fileId)
            .join();
        final DeletedObject deletedVectorStore = vectorStoreOpenAiClient.deleteVectorStore(vectorStoreId)
            .join();
        final DeletedObject deletedThread = threadOpenAiClient.deleteThread(threadId)
            .join();

        assertTrue(deletedFile.getDeleted());
        assertTrue(deletedVectorStore.getDeleted());
        assertTrue(deletedThread.getDeleted());

        System.out.println("File was deleted: " + deletedFile.getDeleted());
        System.out.println("Vector Store was deleted: " + deletedVectorStore.getDeleted());
        System.out.println("Thread was deleted: " + deletedThread.getDeleted());
    }

    private String createFile() throws IOException {
        final String fileName = "user-names.txt";
        final Path filePath = tempDir.resolve(fileName);
        Files.createFile(filePath);
        Files.write(filePath, Collections.singleton("There are 3 subjects on Monday"), StandardOpenOption.APPEND);

        final FileRequest fileRequest = FileRequest.builder()
            .file(filePath)
            .purpose(FileRequest.PurposeType.ASSISTANTS)
            .build();

        final FileResponse file = fileOpenAiClient.uploadFile(fileRequest)
            .join();

        assertNotNull(file.getId());
        assertTrue(file.getFilename()
            .contains(fileName));
        System.out.println("File was created with id: " + file.getId());

        return file.getId();
    }

    private String createVectorStoreWithFile(final String fileId) {
        final VectorStore vectorStore = vectorStoreOpenAiClient.createVectorStore()
            .join();

        assertNotNull(vectorStore.getId());
        System.out.println("Vector Store was created with id: " + vectorStore.getId());

        final VectorStoreFile vectorStoreFile = vectorStoreOpenAiClient.createVectorStoreFile(vectorStore.getId(),
                fileId)
            .join();

        assertNotNull(vectorStoreFile.getId());
        assertEquals(vectorStoreFile.getStatus(), FileStatus.COMPLETED);
        System.out.println("Vector Store File was created with id: " + vectorStoreFile.getId());

        return vectorStore.getId();
    }

    private String createThreadWithVectorStore(final String vectorStoreId) {
        final ToolResourceFull toolResource = ToolResourceFull.builder()
            .fileSearch(ToolResourceFull.FileSearch.builder()
                .vectorStoreId(vectorStoreId)
                .build())
            .build();
        final ThreadRequest threadRequest = ThreadRequest.builder()
            .toolResources(toolResource)
            .build();

        final Thread thread = threadOpenAiClient.createThread(threadRequest)
            .join();

        assertNotNull(thread.getId());
        assertEquals(vectorStoreId, thread.getToolResources()
            .getFileSearch()
            .getVectorStoreIds()
            .stream()
            .findFirst()
            .orElse(""));
        System.out.println("Thread was created with id: " + thread.getId());

        return thread.getId();
    }

    private void handleRunEvents(Stream<Event> runStream) {
        final StringBuilder stringBuilder = new StringBuilder();
        final boolean isThreadCompleted = runStream.peek(event -> {
                if (event.getName()
                    .equals(EventName.THREAD_MESSAGE_DELTA)) {
                    final ThreadMessageDelta msgDelta = (ThreadMessageDelta) event.getData();
                    final ThreadMessageContent content = msgDelta.getDelta()
                        .getContent()
                        .get(0);
                    if (content instanceof ThreadMessageContent.TextContent textContent) {
                        stringBuilder.append(textContent.getText()
                            .getValue());
                    }
                } else if (event.getName()
                    .equals(EventName.THREAD_RUN_FAILED)) {
                    final ThreadRun runFailed = (ThreadRun) event.getData();
                    final LastError error = runFailed.getLastError();

                    System.out.println("Thread run failed. See stack trace for more details.");
                    fail(String.format("Thread run failed with %s: %s",
                        error.getCode().toString(), error.getMessage()));
                }
            })
            .anyMatch(event -> event.getName()
                .equals(EventName.THREAD_MESSAGE_COMPLETED));

        final String generatedResponse = stringBuilder.toString();

        assertTrue(isThreadCompleted);
        assertTrue(generatedResponse.contains("3 subjects"));

        System.out.println("Thread was completed with response: " + generatedResponse);
    }
}
