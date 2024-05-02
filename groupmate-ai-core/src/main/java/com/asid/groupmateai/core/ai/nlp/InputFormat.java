package com.asid.groupmateai.core.ai.nlp;

public enum InputFormat {

    CONLLU("conllu"),
    GENERIC_TOKENIZER("generic_tokenizer"),
    HORIZONTAL("horizontal"),
    VERTICAL("vertical");

    private final String format;

    InputFormat(final String format) {
        this.format = format;
    }

    public String getFormat() {
        return this.format;
    }
}
