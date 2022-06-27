package com.azure.spring.dev.tools.dependency.support;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.azure.spring.dev.tools.dependency.support.SpringCloudAzureCurrentVersionReader.SPRING_BOOT_DEPENDENCIES_PATTERN;
import static com.azure.spring.dev.tools.dependency.support.SpringCloudAzureCurrentVersionReader.SPRING_CLOUD_DEPENDENCIES_PATTERN;

class SpringCloudAzureCurrentVersionReaderTest {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String externalDependenciesFileUrl = "https://raw.githubusercontent"
        + ".com/Azure/azure-sdk-for-java/main/eng/versioning/external_dependencies.txt";

    @Test
    void testUrl() {
        ResponseEntity<String> result = restTemplate.getForEntity(externalDependenciesFileUrl, String.class);
        Assertions.assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    void SpringBootGaVersionCanBeFound() {
        String fileContent = StringUtils.joinWith(System.lineSeparator(),
            "## Spring boot dependency versions",
            "org.springframework.boot:spring-boot-dependencies;2.6.2",
            "org.springframework.boot:spring-boot-actuator-autoconfigure;2.6.2"
            );

        String matchedVersion = SpringCloudAzureCurrentVersionReader.findMatchedVersion(fileContent,
            SPRING_BOOT_DEPENDENCIES_PATTERN, 2);
        Assertions.assertEquals("2.6.2", matchedVersion);
    }

    @Test
    void SpringBootGaVersionWithReleaseCanBeFound() {
        String fileContent = StringUtils.joinWith(System.lineSeparator(),
            "## Spring boot dependency versions",
            "org.springframework.boot:spring-boot-dependencies;2.6.2.RELEASE",
            "org.springframework.boot:spring-boot-actuator-autoconfigure;2.6.2"
        );

        String matchedVersion = SpringCloudAzureCurrentVersionReader.findMatchedVersion(fileContent,
            SPRING_BOOT_DEPENDENCIES_PATTERN, 2);
        Assertions.assertEquals("2.6.2.RELEASE", matchedVersion);
    }

    @Test
    void SpringBootMilestoneVersionCanBeFound() {
        String fileContent = StringUtils.joinWith(System.lineSeparator(),
            "## Spring boot dependency versions",
            "org.springframework.boot:spring-boot-dependencies;2.6.2-M3",
            "org.springframework.boot:spring-boot-actuator-autoconfigure;2.6.2"
        );

        String matchedVersion = SpringCloudAzureCurrentVersionReader.findMatchedVersion(fileContent,
            SPRING_BOOT_DEPENDENCIES_PATTERN, 2);
        Assertions.assertEquals("2.6.2-M3", matchedVersion);
    }

    @Test
    void SpringCloudGaVersionCanBeFound() {
        String fileContent = StringUtils.joinWith(System.lineSeparator(),
           "## Spring cloud dependency versions",
            "org.springframework.cloud:spring-cloud-dependencies;2021.0.0",
            " org.springframework.cloud:spring-cloud-starter-bootstrap;3.1.0"
        );

        String matchedVersion = SpringCloudAzureCurrentVersionReader.findMatchedVersion(fileContent,
            SPRING_CLOUD_DEPENDENCIES_PATTERN, 2);
        Assertions.assertEquals("2021.0.0", matchedVersion);
    }

    @Test
    void SpringCloudMillstoneVersionCanBeFound() {
        String fileContent = StringUtils.joinWith(System.lineSeparator(),
            "## Spring cloud dependency versions",
            "org.springframework.cloud:spring-cloud-dependencies;2021.0.0-M1",
            " org.springframework.cloud:spring-cloud-starter-bootstrap;3.1.0"
        );

        String matchedVersion = SpringCloudAzureCurrentVersionReader.findMatchedVersion(fileContent,
            SPRING_CLOUD_DEPENDENCIES_PATTERN, 2);
        Assertions.assertEquals("2021.0.0-M1", matchedVersion);
    }

    @Test
    void SpringCloudOldVersionCanBeFound() {
        String fileContent = StringUtils.joinWith(System.lineSeparator(),
            "## Spring cloud dependency versions",
            "org.springframework.cloud:spring-cloud-dependencies;Hoxton.SR12",
            " org.springframework.cloud:spring-cloud-starter-bootstrap;3.1.0"
        );

        String matchedVersion = SpringCloudAzureCurrentVersionReader.findMatchedVersion(fileContent,
            SPRING_CLOUD_DEPENDENCIES_PATTERN, 2);
        Assertions.assertEquals("Hoxton.SR12", matchedVersion);
    }

}
