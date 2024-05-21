package com.asid.groupmateai.core.services.impl;

import com.asid.groupmateai.core.ai.openai.clients.ThreadOpenAiClient;
import com.asid.groupmateai.core.exceptions.ResponseGenerationException;
import com.asid.groupmateai.core.services.UserThreadService;
import io.github.sashirestela.openai.domain.assistant.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.github.sashirestela.openai.domain.assistant.AssistantTool.FILE_SEARCH;
import static java.lang.String.format;

@Service
@Slf4j
public class UserThreadServiceImpl implements UserThreadService {

    @Value("${OPENAI_ASSISTANT_ID}")
    private String assistantId;

    private final ThreadOpenAiClient threadOpenAiClient;

    @Autowired
    public UserThreadServiceImpl(final ThreadOpenAiClient threadOpenAiClient) {
        this.threadOpenAiClient = threadOpenAiClient;
    }

    @Override
    public String generateResponse(final String threadId, final String messageText) throws ResponseGenerationException {
        final ThreadMessageRequest threadMessageRequest = ThreadMessageRequest.builder()
            .role(ThreadMessageRole.USER)
            .content(messageText)
            .build();

        threadOpenAiClient.createThreadMessage(threadId, threadMessageRequest).join();

        final ThreadRunRequest runRequest = ThreadRunRequest.builder()
            .assistantId(assistantId)
            .tool(FILE_SEARCH)
            .build();

        final ThreadRun threadRun = threadOpenAiClient.runThread(threadId, runRequest);

        return switch (threadRun.getStatus()) {
            case COMPLETED -> {
                final ThreadMessageContent messageContent = threadOpenAiClient.listThreadMessagesByRunId(threadId, threadRun.getId())
                    .join()
                    .stream()
                    .findFirst()
                    .flatMap(tMessage -> tMessage.getContent()
                        .stream()
                        .findFirst())
                    .orElse(null);

                if (messageContent instanceof ThreadMessageContent.TextContent textContent) {
                    final ThreadMessageContent.TextContent.TextAnnotation text = textContent.getText();
                    yield removeTextAnnotations(text.getValue(), text.getAnnotations());
                } else if (messageContent instanceof ThreadMessageContent.ImageFileContent imageContent) {
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
}
