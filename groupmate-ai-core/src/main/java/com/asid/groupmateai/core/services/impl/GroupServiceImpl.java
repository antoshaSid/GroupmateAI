package com.asid.groupmateai.core.services.impl;

import com.asid.groupmateai.core.ai.openai.clients.FileOpenAiClient;
import com.asid.groupmateai.core.ai.openai.clients.VectorStoreOpenAiClient;
import com.asid.groupmateai.core.services.GoogleDriveService;
import com.asid.groupmateai.core.services.GroupService;
import com.asid.groupmateai.storage.entities.GroupEntity;
import com.asid.groupmateai.storage.repositories.GroupRepository;
import com.google.api.services.drive.model.File;
import io.github.sashirestela.openai.domain.assistant.FileStatus;
import io.github.sashirestela.openai.domain.assistant.VectorStoreFile;
import io.github.sashirestela.openai.domain.file.FileResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final VectorStoreOpenAiClient vectorStoreClient;
    private final FileOpenAiClient fileOpenAiClient;
    private final GoogleDriveService googleDriveService;

    @Autowired
    public GroupServiceImpl(final GroupRepository groupRepository,
                            final VectorStoreOpenAiClient vectorStoreClient,
                            final FileOpenAiClient fileOpenAiClient,
                            final GoogleDriveService googleDriveService) {
        this.groupRepository = groupRepository;
        this.vectorStoreClient = vectorStoreClient;
        this.fileOpenAiClient = fileOpenAiClient;
        this.googleDriveService = googleDriveService;
    }

    @Override
    public GroupEntity addGroup(final String name) throws IOException {
        String vectorStoreId = null;
        String folderId = null;
        try {
            folderId = googleDriveService.createPublicFolder(name)
                .getId();
            vectorStoreId = vectorStoreClient.createVectorStore(name)
                .join()
                .getId();
            final GroupEntity groupEntity = GroupEntity.builder()
                .name(name)
                .vectorStoreId(vectorStoreId)
                .driveFolderId(folderId)
                .build();

            return groupRepository.save(groupEntity);
        } catch (final Exception e) {
            if (vectorStoreId != null) {
                vectorStoreClient.deleteVectorStore(vectorStoreId);
            }
            if (folderId != null) {
                googleDriveService.deleteFolder(folderId);
            }

            throw e;
        }
    }

    @Override
    public GroupEntity getGroup(final Long groupId) {
        return groupRepository.findById(groupId)
            .orElse(null);
    }

    @Override
    public boolean groupExists(final Long groupId) {
        return groupRepository.existsById(groupId);
    }

    @Override
    public void updateGroup(final GroupEntity groupEntity) {
        groupRepository.save(groupEntity);
    }

    @Override
    public CompletableFuture<Boolean> updateGroupContext(final Long groupId) {
        final GroupEntity groupEntity = getGroup(groupId);

        if (groupEntity != null) {
            final String folderId = groupEntity.getDriveFolderId();
            final String vectorStoreId = groupEntity.getVectorStoreId();

            return CompletableFuture.supplyAsync(() -> {
                try {
                    final List<File> driveFiles = googleDriveService.getFilesByFolderId(folderId);
                    final List<FileResponse> vectorFilesToDelete = vectorStoreClient.listVectorStoreFiles(vectorStoreId)
                        .join()
                        .parallelStream()
                        .map(vectorStoreFile -> fileOpenAiClient.getFile(vectorStoreFile.getId()).join())
                        .collect(Collectors.toList());

                    for (final File dFile : driveFiles) {
                        final FileResponse vFile = vectorFilesToDelete.stream()
                            .filter(vf -> vf.getFilename().contains(dFile.getId()))
                            .findFirst()
                            .orElse(null);

                        if (vFile != null) {
                            // Remove existing files if updated_at of drive file is newer than created_at of vector store file
                            final Date vFileCreatedDate = new Date(vFile.getCreatedAt() * 1000);
                            final Date dFileModifiedDate = new Date(dFile.getModifiedTime().getValue());

                            if (dFileModifiedDate.after(vFileCreatedDate)) {
                                fileOpenAiClient.deleteFile(vFile.getId()).join();
                                this.uploadDriveFileToVectorStore(dFile, vectorStoreId);
                            }

                            vectorFilesToDelete.remove(vFile);
                        } else {
                            // Add files that are not in the vector store
                            this.uploadDriveFileToVectorStore(dFile, vectorStoreId);
                        }
                    }

                    // Remove files that are not in the drive
                    vectorFilesToDelete.parallelStream().forEach(vFile -> fileOpenAiClient.deleteFile(vFile.getId()).join());

                    return true;
                } catch (final IOException e) {
                    log.warn("Failed to update group context", e);
                    return false;
                }
            });
        } else {
            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    public void removeGroup(final Long groupId) throws IOException {
        final GroupEntity groupEntity = getGroup(groupId);

        if (groupEntity != null) {
            final String vectorStoreId = groupEntity.getVectorStoreId();
            final String driveFolderId = groupEntity.getDriveFolderId();

            vectorStoreClient.listVectorStoreFiles(vectorStoreId)
                .thenAccept(files -> files.forEach(f -> fileOpenAiClient.deleteFile(f.getId())));
            vectorStoreClient.deleteVectorStore(vectorStoreId);
            googleDriveService.deleteFolder(driveFolderId);
            groupRepository.deleteById(groupId);
        }
    }

    private void uploadDriveFileToVectorStore(final File file, final String vectorStoreId) throws IOException {
        final Path tempFile = Files.createTempFile(file.getId(), getFileExtension(file.getMimeType()));

        try (final InputStream in = googleDriveService.readFile(file.getId())) {
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);

            VectorStoreFile vectorStoreFile = fileOpenAiClient.uploadFile(tempFile)
                .thenCompose(uploadedFile -> vectorStoreClient.createVectorStoreFile(vectorStoreId, uploadedFile.getId()))
                .join();

            while (vectorStoreFile.getStatus() == FileStatus.IN_PROGRESS) {
                TimeUnit.MILLISECONDS.sleep(500);
                vectorStoreFile = vectorStoreClient.getVectorStoreFile(vectorStoreId, vectorStoreFile.getId())
                    .join();
            }
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private String getFileExtension(final String mimeType) {
        try {
            final MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
            return allTypes.forName(mimeType).getExtension();
        } catch (final MimeTypeException e) {
            return ".txt";
        }
    }
}