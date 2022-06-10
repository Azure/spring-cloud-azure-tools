package com.azure.spring.dev.tools.dependency.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.swing.Spring;

class SpringProjectMetadataReaderTest {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String springMetadataUrl="https://spring.io/project_metadata/";

    @Test
    void testUrl() {
        ResponseEntity<String> result = restTemplate.getForEntity(springMetadataUrl+"spring-boot", String.class);
        Assertions.assertEquals(200, result.getStatusCodeValue());
    }

}
