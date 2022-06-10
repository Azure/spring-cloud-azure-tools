package com.azure.spring.dev.tools.dependency.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class SpringCloudAzureCurrentVersionReaderTest {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String currentVersionUrl="https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/eng/versioning/external_dependencies.txt";

    @Test
    void testUrl() {
        ResponseEntity<String> result = restTemplate.getForEntity(currentVersionUrl, String.class);
        Assertions.assertEquals(200, result.getStatusCodeValue());
    }

}
