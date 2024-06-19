package com.asid.groupmateai.core.ai.openai.clients;

import com.asid.groupmateai.core.TestCoreModuleConfiguration;
import com.asid.groupmateai.core.services.UserThreadService;
import io.github.sashirestela.openai.common.DeletedObject;
import io.github.sashirestela.openai.domain.assistant.FileStatus;
import io.github.sashirestela.openai.domain.assistant.Thread;
import io.github.sashirestela.openai.domain.assistant.VectorStore;
import io.github.sashirestela.openai.domain.assistant.VectorStoreFile;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestCoreModuleConfiguration.class)
public class OpenAiClientsTest {

    @Autowired
    private FileOpenAiClient fileOpenAiClient;

    @Autowired
    private ThreadOpenAiClient threadOpenAiClient;

    @Autowired
    private VectorStoreOpenAiClient vectorStoreOpenAiClient;

    @Autowired
    private UserThreadService userThreadService;

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

            final String message = "Do you know how many subjects are on Monday?";

            final String response = userThreadService.generateResponse(threadId, message);

            assertTrue(response.contains("3 subjects"));
            System.out.println("Thread was completed with response: " + response);
        } catch (final Exception e) {
            fail(e.getMessage());
        } finally {
            cleanConversation(fileId, vectorStoreId, threadId);
        }
    }

    public void cleanConversation(final String fileId, final String vectorStoreId, final String threadId) {
        if (fileId != null) {
            final DeletedObject deletedFile = fileOpenAiClient.deleteFile(fileId)
                .join();
            assertTrue(deletedFile.getDeleted());
            System.out.println("File was deleted: " + deletedFile.getDeleted());
        }

        if (vectorStoreId != null) {
            final DeletedObject deletedVectorStore = vectorStoreOpenAiClient.deleteVectorStore(vectorStoreId)
                .join();
            assertTrue(deletedVectorStore.getDeleted());
            System.out.println("Vector Store was deleted: " + deletedVectorStore.getDeleted());
        }

        if (threadId != null) {
            final DeletedObject deletedThread = threadOpenAiClient.deleteThread(threadId)
                .join();
            assertTrue(deletedThread.getDeleted());
            System.out.println("Thread was deleted: " + deletedThread.getDeleted());
        }
    }

    private String createFile() throws IOException {
        final String fileName = "user-names.txt";
        final Path filePath = tempDir.resolve(fileName);
        Files.createFile(filePath);
        Files.write(filePath, Collections.singleton("There are 3 subjects on Monday"), StandardOpenOption.APPEND);

        final FileResponse file = fileOpenAiClient.uploadFile(filePath)
            .join();

        assertNotNull(file.getId());
        assertTrue(file.getFilename()
            .contains(fileName));
        System.out.println("File was created with id: " + file.getId());

        return file.getId();
    }

    private String createVectorStoreWithFile(final String fileId) {
        final VectorStore vectorStore = vectorStoreOpenAiClient.createVectorStore("Test Vector Store")
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
        final Thread thread = threadOpenAiClient.createThreadWithVectorStore(vectorStoreId).join();

        assertNotNull(thread.getId());
        assertEquals(vectorStoreId, thread.getToolResources()
            .getFileSearch()
            .getVectorStoreIds()
            .stream()
            .findFirst()
            .orElse(""));
        System.out.println("Thread was created with id: " + thread.getId());

        final List<String> vectorStoreIds = thread.getToolResources()
            .getFileSearch()
            .getVectorStoreIds();

        assertEquals(1, vectorStoreIds.size());

        System.out.printf("Vector Store (%s) was attached to thread: %s%n", vectorStoreIds.get(0), thread.getId());

        return thread.getId();
    }
}
