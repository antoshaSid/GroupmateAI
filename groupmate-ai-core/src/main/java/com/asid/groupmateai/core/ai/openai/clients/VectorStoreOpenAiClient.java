package com.asid.groupmateai.core.ai.openai.clients;

import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.common.DeletedObject;
import io.github.sashirestela.openai.domain.assistant.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * VectorStoreOpenAiClient is a client for OpenAI Vector Store API, Vector Store Files API, and Vector Store File Batches API.
 * <p>
 * Docs: <a href="https://platform.openai.com/docs/api-reference/vector-stores">...</a>
 * <p>
 * Main functionalities:
 * <p>
 * Vector Store
 * 1. Create vector store
 * 2. Delete vector store
 * <p>
 * Vector Store Files
 * 1. Create vector store file
 * 2. Retrieve vector store file
 * 3. List vector store files
 * 4. Delete vector store file
 * <p>
 * Vector Store File Batches
 * 1. Create vector store file batch (multiple files)
 * 2. Retrieve vector store file batch (multiple files)
 * 3. List vector store files in batch
 */

@Service
@Slf4j
public class VectorStoreOpenAiClient extends OpenAiClient {

    @Autowired
    public VectorStoreOpenAiClient(final SimpleOpenAI openAiClient) {
        super(openAiClient);
    }

    public CompletableFuture<VectorStore> createVectorStore(final String name) {
        return handleRequestWithRetry(client -> client.vectorStores()
            .create(VectorStoreRequest.builder().name(name).build())
            .thenApply(vs -> {
                log.debug("Vector store was created: {}", vs.getId());
                return vs;
            }));
    }

    public CompletableFuture<DeletedObject> deleteVectorStore(final String vectorStoreId) {
        return handleRequestWithRetry(client -> client.vectorStores()
            .delete(vectorStoreId)
            .thenApply(deleted -> {
                if (deleted.getDeleted()) {
                    log.debug("Vector store was deleted: {}", deleted.getId());
                } else {
                    log.debug("Vector store failed to delete: {}", deleted.getId());
                }

                return deleted;
            }));
    }

    public CompletableFuture<VectorStoreFile> createVectorStoreFile(final String vectorStoreId, final String fileId) {
        return handleRequestWithRetry(client -> client.vectorStoreFiles()
            .create(vectorStoreId, fileId)
            .thenApply(this::waitUntilVectorStoreFileInProgress));
    }

    public CompletableFuture<VectorStoreFile> getVectorStoreFile(final String vectorStoreId, final String fileId) {
        return handleRequestWithRetry(client -> client.vectorStoreFiles()
            .getOne(vectorStoreId, fileId)
            .thenApply(file -> {
                log.debug("Vector store file was retrieved: {}", file.getId());
                return file;
            }));
    }

    public CompletableFuture<List<VectorStoreFile>> listVectorStoreFiles(final String vectorStoreId) {
        return handleRequestWithRetry(client -> client.vectorStoreFiles()
            .getList(vectorStoreId)
            .thenApply(files -> {
                log.debug("Vector store files were retrieved: {}", vectorStoreId);
                return files;
            }));
    }

    public CompletableFuture<DeletedObject> deleteVectorStoreFile(final String vectorStoreId, final String fileId) {
        return handleRequestWithRetry(client -> client.vectorStoreFiles()
            .delete(vectorStoreId, fileId)
            .thenApply(deleted -> {
                if (deleted.getDeleted()) {
                    log.debug("Vector store file was deleted: {}", deleted.getId());
                } else {
                    log.debug("Vector store file failed to delete: {}", deleted.getId());
                }

                return deleted;
            }));
    }

    public CompletableFuture<VectorStoreFileBatch> createVectorStoreFileBatch(final String vectorStoreId,
                                                                              final List<String> fileIds) {
        return handleRequestWithRetry(client -> client.vectorStoreFileBatches()
            .create(vectorStoreId, fileIds)
            .thenApply(this::waitUntilVectorStoreFileBatchInProgress));
    }

    public CompletableFuture<VectorStoreFileBatch> getVectorStoreFileBatch(final String vectorStoreId,
                                                                           final String fileBatchId) {
        return handleRequestWithRetry(client -> client.vectorStoreFileBatches()
            .getOne(vectorStoreId, fileBatchId)
            .thenApply(fileBatch -> {
                log.debug("Vector store file batch was retrieved: {}", fileBatch.getId());
                return fileBatch;
            }));
    }

    public CompletableFuture<List<VectorStoreFile>> listVectorStoreFilesInBatch(final String vectorStoreId,
                                                                                final String fileBatchId) {
        return handleRequestWithRetry(client -> client.vectorStoreFileBatches()
            .getFiles(vectorStoreId, fileBatchId)
            .thenApply(files -> {
                log.debug("Vector store files in batch were retrieved: {}", fileBatchId);
                return files;
            }));
    }

    private VectorStoreFile waitUntilVectorStoreFileInProgress(VectorStoreFile file) {
        try {
            while (file.getStatus() == FileStatus.IN_PROGRESS) {
                TimeUnit.SECONDS.sleep(1);
                file = this.getVectorStoreFile(file.getVectorStoreId(), file.getId())
                    .get();
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to wait for vector store file to be in progress", e);
        }

        return file;
    }

    private VectorStoreFileBatch waitUntilVectorStoreFileBatchInProgress(VectorStoreFileBatch fileBatch) {
        try {
            while (fileBatch.getStatus() == FileStatus.IN_PROGRESS) {
                TimeUnit.SECONDS.sleep(1);
                fileBatch = this.getVectorStoreFileBatch(fileBatch.getVectorStoreId(), fileBatch.getId())
                    .get();
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to wait for vector store file batch to be in progress", e);
        }

        return fileBatch;
    }
}
