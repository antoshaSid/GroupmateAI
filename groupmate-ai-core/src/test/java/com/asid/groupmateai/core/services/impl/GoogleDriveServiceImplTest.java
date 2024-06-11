package com.asid.groupmateai.core.services.impl;

import com.asid.groupmateai.core.TestCoreModuleConfiguration;
import com.asid.groupmateai.core.services.GoogleDriveService;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.model.File;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestCoreModuleConfiguration.class)
class GoogleDriveServiceImplTest {

    public GoogleDriveService googleDriveService;

    @Autowired
    public GoogleDriveServiceImplTest(final GoogleDriveService googleDriveService) {
        this.googleDriveService = googleDriveService;
    }

    @Test
    void testManageFolder() throws IOException {
        final String folderName = "Test Folder";
        final String exceptionMessage = "File not found:";

        final File createdFolder = googleDriveService.createPublicFolder(folderName);
        final String folderId = createdFolder.getId();
        assertNotNull(folderId);
        assertEquals(folderName, createdFolder.getName());

        final File retrievedFolder = googleDriveService.getFolderById(folderId);
        assertEquals(folderId, retrievedFolder.getId());
        assertEquals(folderName, retrievedFolder.getName());

        googleDriveService.deleteFolder(folderId);

        try {
            googleDriveService.getFolderById(folderId);
        } catch (final GoogleJsonResponseException e) {
            assertTrue(e.getMessage().contains(exceptionMessage));
        }
    }

    @Test
    void testGetFolderShareableLink() {
        final String folderId = "1";
        final String expectedShareableLink = "https://drive.google.com/drive/folders/" + folderId;

        final String actualShareableLink = googleDriveService.getFolderShareableLink(folderId);

        assertEquals(expectedShareableLink, actualShareableLink);
    }
}