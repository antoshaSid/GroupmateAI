package com.asid.groupmateai.core.services.impl;

import com.asid.groupmateai.core.services.GoogleDriveService;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class GoogleDriveServiceImpl implements GoogleDriveService {

    private final Drive driveClient;

    @Autowired
    public GoogleDriveServiceImpl(final Drive driveClient) {
        this.driveClient = driveClient;
    }

    @Override
    public File createPublicFolder(final String folderName) throws IOException {
        final File folderMetadata = new File();
        folderMetadata.setName(folderName);
        folderMetadata.setMimeType("application/vnd.google-apps.folder");
        final File folder = driveClient.files()
            .create(folderMetadata)
            .setFields("id, name")
            .execute();

        final Permission publicPermission = new Permission()
            .setType("anyone")
            .setRole("writer");
        driveClient.permissions()
            .create(folder.getId(), publicPermission)
            .execute();

        return folder;
    }

    @Override
    public File getFolderById(final String folderId) throws IOException {
        return driveClient.files()
            .get(folderId)
            .setFields("id, name")
            .execute();
    }

    @Override
    public void deleteFolder(final String folderId) throws IOException {
        driveClient.files()
            .delete(folderId)
            .execute();
    }

    @Override
    public List<File> getFilesByFolderId(final String folderId) throws IOException {
        return driveClient.files()
            .list()
            .setQ("'" + folderId + "' in parents and trashed=false")
            .setSpaces("drive")
            .setFields("files(id, name)")
            .setPageSize(15)
            .execute()
            .getFiles();
    }

    @Override
    public String getFolderShareableLink(final String folderId) {
        return "https://drive.google.com/drive/folders/" + folderId;
    }
}
