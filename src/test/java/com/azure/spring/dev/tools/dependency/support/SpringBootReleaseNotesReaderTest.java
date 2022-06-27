// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.dev.tools.dependency.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

class SpringBootReleaseNotesReaderTest {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String releaseNotesUrl = "https://github.com/spring-projects/spring-boot/releases/tag/v2.7.0";
    private final SpringBootReleaseNotesReader springBootReleaseNotesReader =
        new SpringBootReleaseNotesReader(restTemplate, null);

    @Test
    void testGetReleaseNotes() {
        String htmlContents = restTemplate.getForObject(releaseNotesUrl, String.class);
        String releaseNotesContents = springBootReleaseNotesReader.convertOutputString(releaseNotesUrl, htmlContents);

        Assertions.assertEquals(1, releaseNotesContents.trim().split("\n").length);
        Assertions.assertTrue(releaseNotesContents.startsWith("<details><summary>Release notes</summary>"));
        Assertions.assertTrue(releaseNotesContents.endsWith("</details>"));
    }
}
