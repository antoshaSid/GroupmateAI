package com.asid.groupmateai.core.ai.nlp;

public enum OutputFormat {

    CONLLU("conllu"),
    HORIZONTAL("horizontal"),
    MATXIN("matxin"),
    PLAINTEXT("plaintext"),
    VERTICAL("vertical");

    private final String format;

    OutputFormat(final String format) {
        this.format = format;
    }

    public String getFormat() {
        return this.format;
    }
}
