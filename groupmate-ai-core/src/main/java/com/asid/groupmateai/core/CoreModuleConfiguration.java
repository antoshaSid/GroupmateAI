package com.asid.groupmateai.core;

import com.asid.groupmateai.storage.StorageModuleConfiguration;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import io.github.sashirestela.openai.SimpleOpenAI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

@Configuration
@ComponentScan
@Import(StorageModuleConfiguration.class)
public class CoreModuleConfiguration {

    private static final String APPLICATION_NAME = "GroupmateAI Telegram Bot";

    @Bean
    public SimpleOpenAI configureOpenAiClient(@Value("${OPENAI_API_KEY}") final String apiKey,
                                              @Value("${OPENAI_ORGANIZATION_ID}") final String organizationId,
                                              @Value("${OPENAI_PROJECT_ID}") final String projectId) {
        return SimpleOpenAI.builder()
            .apiKey(apiKey)
            .organizationId(organizationId)
            .projectId(projectId)
            .build();
    }

    @Bean
    public Drive configureDriveClient(@Value("${GOOGLE_SERVICE_ACCOUNT_JSON_KEY}") final String serviceAccountKey)
        throws IOException, GeneralSecurityException {
        final HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        final GoogleCredentials credentials = this.authorizeDriveClient(serviceAccountKey);

        return new Drive.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(credentials))
            .setApplicationName(APPLICATION_NAME)
            .build();
    }

    private GoogleCredentials authorizeDriveClient(final String serviceAccountKey) throws IOException {
        final InputStream in = new ByteArrayInputStream(serviceAccountKey.getBytes());
        return ServiceAccountCredentials.fromStream(in).createScoped(DriveScopes.DRIVE_FILE);
    }
}
