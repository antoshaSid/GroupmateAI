package com.asid.groupmateai.core.ai.nlp;

import com.asid.groupmateai.core.ai.nlp.dto.UdPipeResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UdPipeClient {

    public static final String BASE_URL = "https://lindat.mff.cuni.cz/services/udpipe/api";

    private final RestClient client;

    public static UdPipeClientBuilder builder() {
        return new UdPipeClientBuilder();
    }

    public UdPipeResponse processData(final String data) {
        final String dataQueryParam = UriComponentsBuilder.newInstance()
            .queryParam("data", data)
            .toUriString();

        return this.client.get()
            .uri(dataQueryParam)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(UdPipeResponse.class);
    }

    public static class UdPipeClientBuilder {

        private final UriComponentsBuilder uriComponentsBuilder;

        public UdPipeClientBuilder() {
            this.uriComponentsBuilder = UriComponentsBuilder.fromUriString(BASE_URL)
                .pathSegment("process");
        }

        public UdPipeClientBuilder path(@NonNull final String path) {
            this.uriComponentsBuilder.pathSegment(path);
            return this;
        }

        public UdPipeClientBuilder model(@NonNull final String model) {
            this.uriComponentsBuilder.queryParam("model", model);
            return this;
        }

        public UdPipeClientBuilder tokenizer() {
            this.uriComponentsBuilder.queryParam("tokenizer");
            return this;
        }

        public UdPipeClientBuilder tagger() {
            this.uriComponentsBuilder.queryParam("tagger");
            return this;
        }

        public UdPipeClientBuilder parser() {
            this.uriComponentsBuilder.queryParam("parser");
            return this;
        }

        public UdPipeClientBuilder inputFormat(@NonNull final InputFormat format) {
            this.uriComponentsBuilder.queryParam("input", format.getFormat());
            return this;
        }

        public UdPipeClientBuilder outputFormat(@NonNull final OutputFormat format) {
            this.uriComponentsBuilder.queryParam("output", format.getFormat());
            return this;
        }

        public UdPipeClient build() {
            return new UdPipeClient(RestClient.create(this.uriComponentsBuilder.toUriString()));
        }
    }
}
