package com.asid.groupmateai.core.services;

import com.asid.groupmateai.core.exceptions.ResponseGenerationException;

public interface UserThreadService {

    String generateResponse(String threadId, String messageText) throws ResponseGenerationException;
}
