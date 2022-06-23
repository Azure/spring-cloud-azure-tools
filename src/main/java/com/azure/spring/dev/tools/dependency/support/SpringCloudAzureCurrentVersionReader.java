package com.azure.spring.dev.tools.dependency.support;

import com.azure.spring.dev.tools.dependency.configuration.DependencyProperties;
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
    static final Pattern SPRING_CLOUD_DEPENDENCIES =
        Pattern.compile("(org.springframework.cloud:spring-cloud-dependencies;)(\\d+.+\\d+\\W[A-Z]*)");

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
        String file = getExternalDependenciesFile();
        Matcher matcher = SPRING_BOOT_DEPENDENCIES.matcher(file);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return null;
    }

    /**
     * Get file from external-dependencies.txt in Spring Cloud Azure
     * @return the External Dependencies file information
     */
    private String getExternalDependenciesFile() {
        return this.restTemplate.getForObject(dependencyProperties.getAzure().getExternalDependenciesFileUrl(),
            String.class);
    }

    /**
     * Get current Spring Cloud Version in Spring Cloud Azure
     * @return the current Spring Cloud Version
     */
    public String getCurrentSupportedSpringCloudVersion() {
        String file = getExternalDependenciesFile();
        Matcher matcher = SPRING_CLOUD_DEPENDENCIES.matcher(file);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return null;
    }

}
