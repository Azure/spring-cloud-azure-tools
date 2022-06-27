// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.dev.tools.dependency.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SpringBootReleaseNotesReaderTest {
    private final String releaseNotesUrl = "https://github.com/spring-projects/spring-boot/releases/tag/v2.7.0";
    private final String htmlContents = "<h2>\n Bug Fixes\n <ul>\n <li>\n Contributors \n </li>\n";
    private final SpringBootReleaseNotesReader springBootReleaseNotesReader =
        new SpringBootReleaseNotesReader(null, null);

    @Test
    void testConvertOutputString() {
        String releaseNotesContents = springBootReleaseNotesReader.convertOutputString(releaseNotesUrl, htmlContents);

        Assertions.assertEquals(1, releaseNotesContents.trim().split("\n").length);
        Assertions.assertEquals(-1,releaseNotesContents.indexOf("h2"));
        Assertions.assertEquals(-1,releaseNotesContents.indexOf("Contributors"));
        Assertions.assertNotEquals(-1,releaseNotesContents.indexOf(releaseNotesUrl));
        Assertions.assertTrue(releaseNotesContents.startsWith("<details><summary>Release notes</summary>"));
        Assertions.assertTrue(releaseNotesContents.endsWith("</details>"));
    }
}
