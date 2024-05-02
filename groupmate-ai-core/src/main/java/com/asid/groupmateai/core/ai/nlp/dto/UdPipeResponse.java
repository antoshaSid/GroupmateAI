package com.asid.groupmateai.core.ai.nlp.dto;

import java.util.List;

public record UdPipeResponse(String model, List<String> acknowledgements, String result) {

}
