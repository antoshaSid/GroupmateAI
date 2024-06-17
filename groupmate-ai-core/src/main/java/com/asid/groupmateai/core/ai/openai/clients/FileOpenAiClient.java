package com.asid.groupmateai.core.ai.openai.clients;

import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.common.DeletedObject;
import io.github.sashirestela.openai.domain.file.FileRequest;
import io.github.sashirestela.openai.domain.file.FileResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * FileOpenAiClient is a client for OpenAI File API.
 * <p>
 * Docs: <a href="https://platform.openai.com/docs/api-reference/files">...</a>
 * <p>
 * Main functionalities:
 * <p>
 * 1. Upload file
 * 2. Retrieve file
 * 3. Delete file
 */

@Service
@Slf4j
public class FileOpenAiClient extends OpenAiClient {

    @Autowired
    public FileOpenAiClient(final SimpleOpenAI openAiClient) {
        super(openAiClient);
    }

    public CompletableFuture<FileResponse> uploadFile(final Path path) {
        final FileRequest fileRequest = FileRequest.builder()
            .file(path)
            .purpose(FileRequest.PurposeType.ASSISTANTS)
            .build();

        return handleRequestWithRetry(client -> client.files()
            .create(fileRequest)
            .thenApply(file -> {
                log.debug("File was uploaded: {}", file.getId());
                return file;
            }));
    }

    public CompletableFuture<FileResponse> getFile(final String fileId) {
        return handleRequestWithRetry(client -> client.files()
            .getOne(fileId));
    }

    public CompletableFuture<DeletedObject> deleteFile(final String fileId) {
        return handleRequestWithRetry(client -> client.files()
            .delete(fileId)
            .thenApply(deleted -> {
                if (deleted.getDeleted()) {
                    log.debug("File was deleted: {}", deleted.getId());
                } else {
                    log.debug("File failed to delete: {}", deleted.getId());
                }

                return deleted;
            }));
    }
}
