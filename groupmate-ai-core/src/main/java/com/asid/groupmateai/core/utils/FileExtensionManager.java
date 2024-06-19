package com.asid.groupmateai.core.utils;

import java.util.Map;

import static java.util.Map.entry;

public final class FileExtensionManager {

    /**
     * List of allowed file extensions
     */
    private static final Map<String, String> MIME_TYPE_TO_EXTENSION = Map.ofEntries(
            entry("text/x-c", ".c"),
            entry("text/x-csrc", ".c"),
            entry("text/x-csharp", ".cs"),
            entry("application/octet-stream", ".txt"),
            entry("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx"),
            entry("text/html", ".html"),
            entry("text/x-java", ".java"),
            entry("application/json", ".json"),
            entry("text/markdown", ".md"),
            entry("application/pdf", ".pdf"),
            entry("application/vnd.openxmlformats-officedocument.presentationml.presentation", ".pptx"),
            entry("text/x-python", ".py"),
            entry("text/x-script.python", ".py"),
            entry("text/x-python-script", ".py"),
            entry("text/x-tex", ".tex"),
            entry("text/plain", ".txt"),
            entry("text/javascript", ".js"),
            entry("application/typescript", ".ts"),
            entry("text/texmacs", ".ts")
    );

    public static String getExtension(final String mimeType) {
        return MIME_TYPE_TO_EXTENSION.getOrDefault(mimeType, ".txt"); // TODO: remove default value
    }
}
