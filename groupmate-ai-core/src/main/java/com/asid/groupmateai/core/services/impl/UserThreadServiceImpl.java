package com.asid.groupmateai.core.services.impl;

import com.asid.groupmateai.core.ai.openai.clients.ThreadOpenAiClient;
import com.asid.groupmateai.core.exceptions.ResponseGenerationException;
import com.asid.groupmateai.core.services.UserThreadService;
import io.github.sashirestela.openai.common.content.ContentPart;
import io.github.sashirestela.openai.common.content.FileAnnotation;
import io.github.sashirestela.openai.domain.assistant.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Service
@Slf4j
public class UserThreadServiceImpl implements UserThreadService {

    @Value("${OPENAI_ASSISTANT_ID}")
    private String assistantId;

    private final ThreadOpenAiClient threadClient;

    @Autowired
    public UserThreadServiceImpl(final ThreadOpenAiClient threadClient) {
        this.threadClient = threadClient;
    }

    @Override
    public String generateResponse(final String threadId, final String messageText) throws ResponseGenerationException {
        final ThreadMessageRequest threadMessageRequest = ThreadMessageRequest.builder()
            .role(ThreadMessageRole.USER)
            .content(messageText)
            .build();

        threadClient.createThreadMessage(threadId, threadMessageRequest).join();

        final ThreadRunRequest runRequest = ThreadRunRequest.builder()
            .assistantId(assistantId)
            .tool(AssistantTool.fileSearch())
            .build();

        final ThreadRun threadRun = threadClient.runThread(threadId, runRequest).join();

        return switch (threadRun.getStatus()) {
            case COMPLETED -> {
                final ContentPart messageContent = threadClient.listThreadMessagesByRunId(threadId, threadRun.getId())
                    .join()
                    .stream()
                    .findFirst()
                    .flatMap(tMessage -> tMessage.getContent()
                        .stream()
                        .findFirst())
                    .orElse(null);

                if (messageContent instanceof ContentPart.ContentPartTextAnnotation textContent) {
                    final ContentPart.ContentPartTextAnnotation.TextAnnotation text = textContent.getText();
                    final String textWithoutAnnotations = removeTextAnnotations(text.getValue(), text.getAnnotations());
                    yield filterMarkdown(textWithoutAnnotations);
                } else if (messageContent instanceof ContentPart.ContentPartImageFile imageContent) {
                    throw new ResponseGenerationException(format("Image content is not supported: %s. User query: %s.",
                            imageContent, messageText));
                }

                throw new ResponseGenerationException("Unexpected message content type.");
            }
            case FAILED -> {
                final LastError lastError = threadRun.getLastError();
                throw new ResponseGenerationException(format("%s: %s", lastError.getCode(), lastError.getMessage()));
            }
            default -> throw new ResponseGenerationException(format("Unexpected thread run status: %s", threadRun.getStatus()));
        };
    }

    private String removeTextAnnotations(String text, final List<FileAnnotation> annotations) {
        for(final FileAnnotation annotation : annotations) {
            text = text.replace(annotation.getText(), "");
        }

        return text;
    }

    private String filterMarkdown(String input) {
        // Escape characters that don't represent markdown
        input = escapeNonMarkdownCharacters(input);

        // Replace **Bold Text** with *bold text*
        input = input.replaceAll("\\*\\*(.*?)\\*\\*", "*$1*");

        // Replace Horizontal Line ---
        input = input.replaceAll("---", "——————————————");

        return input;
    }

    private String escapeNonMarkdownCharacters(String input) {
        // Replace single asterisks that are not part of a double asterisk
        input = input.replaceAll("(?<!\\*)\\*(?!\\*)", "\\\\*");

        input = input.replace("_", "\\_")
            .replace("[", "\\[");

        return input;
    }
}
