package com.asid.groupmateai.core.services;


import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface GoogleDriveService {

    File createPublicFolder(String folderName) throws IOException;

    File getFolderById(String folderId) throws IOException;

    void deleteFolder(String folderId) throws IOException;

    List<File> getFilesByFolderId(String folderId) throws IOException;

    InputStream readFile(String fileId) throws IOException;

    String getFolderShareableLink(String folderId);
}
