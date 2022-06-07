package com.azure.spring.dev.tools.dependency.support;

import com.azure.spring.dev.tools.dependency.configuration.DependencyProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Read current Spring Boot version from
 * https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/eng/versioning/external_dependencies.txt
 */
public class SpringCloudAzureCurrentVersionReader {

    private final RestTemplate restTemplate;
    private final DependencyProperties dependencyProperties;
    static final Pattern SPRING_BOOT_DEPENDENCIES =
        Pattern.compile("(org.springframework.boot:spring-boot-dependencies;)(\\d+.+\\d+\\W[A-Z]*)");

    public SpringCloudAzureCurrentVersionReader(RestTemplate restTemplate,
                                                DependencyProperties dependencyProperties) {
        this.restTemplate = restTemplate;
        this.dependencyProperties = dependencyProperties;
    }

    /**
     * Get current Spring Boot Version in Spring Cloud Azure
     * @return the current Spring Boot Version
     */
    public String getCurrentSupportedSpringBootVersion() {
        String file = this.restTemplate.getForObject(dependencyProperties.getAzure().getCurrentVersionUrl(), String.class);
        Matcher matcher = SPRING_BOOT_DEPENDENCIES.matcher(file);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return null;
    }

}
