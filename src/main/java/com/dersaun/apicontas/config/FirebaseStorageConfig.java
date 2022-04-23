package com.dersaun.apicontas.config;

import com.dersaun.apicontas.dto.FirebaseCredential;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseStorageConfig {

    @Value("${FIREBASE_PROJECT_ID}")
    private String projectId;

    @Autowired
    private Environment env;

    @Bean()
    public Storage storage() throws IOException {
        InputStream serviceAccount = createCredential();
        Credentials credentials = GoogleCredentials.fromStream(serviceAccount);
        return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(credentials)
                .build()
                .getService();
    }


    /**
     * FIREBASE_BUCKET_NAME=<add-the-value-from-service-account-file.json>
     * FIREBASE_PROJECT_ID=<add-the-value-from-service-account-file.json>
     * FIREBASE_TYPE=<add-the-value-from-service-account-file.json>
     * FIREBASE_PRIVATE_KEY_ID=<add-the-value-from-service-account-file.json>
     * FIREBASE_PRIVATE_KEY=<add-the-value-from-service-account-file.json>
     * FIREBASE_CLIENT_EMAIL=<add-the-value-from-service-account-file.json>
     * FIREBASE_CLIENT_ID=<add-the-value-from-service-account-file.json>
     * FIREBASE_AUTH_URI=<add-the-value-from-service-account-file.json>
     * FIREBASE_TOKEN_URI=<add-the-value-from-service-account-file.json>
     * FIREBASE_AUTH_PROVIDER_X509_CERT_URL=<add-the-value-from-service-account-file.json>
     * FIREBASE_CLIENT_X509_CERT_URL=<add-the-value-from-service-account-file.json>
     */
    private FirebaseCredential firebaseCredential() {
        var firebaseCredential = new FirebaseCredential();
        String privateKey = env.getRequiredProperty("FIREBASE_PRIVATE_KEY").replace("\\n", "\n");
        firebaseCredential.setType(env.getRequiredProperty("FIREBASE_TYPE"));
        firebaseCredential.setProject_id(projectId);
        firebaseCredential.setPrivate_key_id(env.getRequiredProperty("FIREBASE_PRIVATE_KEY_ID"));
        firebaseCredential.setPrivate_key(privateKey);
        firebaseCredential.setClient_email(env.getRequiredProperty("FIREBASE_CLIENT_EMAIL"));
        firebaseCredential.setClient_id(env.getRequiredProperty("FIREBASE_CLIENT_ID"));
        firebaseCredential.setAuth_uri(env.getRequiredProperty("FIREBASE_AUTH_URI"));
        firebaseCredential.setToken_uri(env.getRequiredProperty("FIREBASE_TOKEN_URI"));
        firebaseCredential.setAuth_provider_x509_cert_url(env.getRequiredProperty("FIREBASE_AUTH_PROVIDER_X509_CERT_URL"));
        firebaseCredential.setClient_x509_cert_url(env.getRequiredProperty("FIREBASE_CLIENT_X509_CERT_URL"));

        return firebaseCredential;
    }

    private InputStream createCredential() throws JsonProcessingException {
        final FirebaseCredential credential = firebaseCredential();
        String json = credential.toJson();
        return IOUtils.toInputStream(json);
    }
}
