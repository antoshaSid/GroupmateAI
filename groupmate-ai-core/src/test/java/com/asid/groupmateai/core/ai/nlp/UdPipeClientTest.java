package com.asid.groupmateai.core.ai.nlp;

import com.asid.groupmateai.core.ai.nlp.dto.UdPipeResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UdPipeClientTest {

    private static final String UKR_TEXT = "Привіт, мене звати GroupmateAI!";

    @Test
    public void testProcessDataRequest() {
        final UdPipeClient client = UdPipeClient.builder()
            .model("ukr")
            .tokenizer()
            .tagger()
            .parser()
            .build();

        final UdPipeResponse result = client.processData(UKR_TEXT);

        assertTrue(result.model()
            .startsWith("ukrainian-iu-ud"));
        assertTrue(result.result()
            .startsWith("# generator"));
        assertFalse(result.acknowledgements()
            .isEmpty());
    }
}