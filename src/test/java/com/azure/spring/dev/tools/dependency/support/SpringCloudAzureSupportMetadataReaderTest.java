package com.azure.spring.dev.tools.dependency.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class SpringCloudAzureSupportMetadataReaderTest {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String supportMetadataUrl = "https://raw.githubusercontent"
        + ".com/Azure/azure-sdk-for-java/main/sdk/spring/pipeline/spring-cloud-azure-supported-spring.json";

    @Test
    void testUrl() {
        ResponseEntity<String> result = restTemplate.getForEntity(supportMetadataUrl, String.class);
        Assertions.assertEquals(200, result.getStatusCodeValue());
    }

}
