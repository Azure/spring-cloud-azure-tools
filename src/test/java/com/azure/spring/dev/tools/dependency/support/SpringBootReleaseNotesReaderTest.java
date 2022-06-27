// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.dev.tools.dependency.support; 

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class SpringBootReleaseNotesReaderTest {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String releaseNotesUrl = "https://github.com/spring-projects/spring-boot/releases/tag/v2.7.0";

    @Test
    void testUrl() {
        ResponseEntity<String> result = restTemplate.getForEntity(releaseNotesUrl, String.class);
        Assertions.assertEquals(200, result.getStatusCodeValue());
    }
        
}
