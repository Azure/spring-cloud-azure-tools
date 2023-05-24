package com.azure.spring.dev.tools.dependency.support;

import com.azure.spring.dev.tools.dependency.configuration.DependencyProperties;
import com.azure.spring.dev.tools.dependency.metadata.maven.VersionRange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class SpringInitializrMetadataReaderTest {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String actuatorInfoUrl = "https://start.spring.io/actuator/info";
    private final SpringInitializrMetadataReader springInitializrMetadataReader =
        new SpringInitializrMetadataReader(restTemplate, new DependencyProperties());

    @Test
    void lowerInclusive() {
        VersionRange versionRange = springInitializrMetadataReader.parseVersionRange("Spring Boot >=2.2.0.RELEASE and"
            + " <2.4.0.M1");
        Assertions.assertEquals("[2.2.0.RELEASE,2.4.0.M1)", versionRange.toRangeString());
    }

    @Test
    void lowerExclusive() {
        VersionRange versionRange = springInitializrMetadataReader.parseVersionRange("Spring Boot >2.2.0.RELEASE and"
            + " <2.4.0.M1");
        Assertions.assertEquals("(2.2.0.RELEASE,2.4.0.M1)", versionRange.toRangeString());
    }

    @Test
    void higherInclusive() {
        VersionRange versionRange = springInitializrMetadataReader.parseVersionRange("Spring Boot >2.2.0.RELEASE and"
            + " <=2.4.0.M1");
        Assertions.assertEquals("(2.2.0.RELEASE,2.4.0.M1]", versionRange.toRangeString());
    }

    @Test
    void higherExclusive() {
        VersionRange versionRange = springInitializrMetadataReader.parseVersionRange("Spring Boot >2.2.0.RELEASE and"
            + " <2.4.0.M1");
        Assertions.assertEquals("(2.2.0.RELEASE,2.4.0.M1)", versionRange.toRangeString());
    }

    @Test
    void testUrl() {
        ResponseEntity<String> result = restTemplate.getForEntity(actuatorInfoUrl, String.class);
        Assertions.assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    void onlyLowerVersion() {
        VersionRange versionRange = springInitializrMetadataReader.parseVersionRange("Spring Boot >=3.1.0-M1");
        Assertions.assertEquals("3.1.0-M1", versionRange.toRangeString());
    }
}
