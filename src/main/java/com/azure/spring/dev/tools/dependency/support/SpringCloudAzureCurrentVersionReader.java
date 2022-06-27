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

    static final Pattern SPRING_BOOT_DEPENDENCIES_PATTERN =
        Pattern.compile("(org.springframework.boot:spring-boot-dependencies;)([.\\-\\w]+)");
    static final Pattern SPRING_CLOUD_DEPENDENCIES_PATTERN =
        Pattern.compile("(org.springframework.cloud:spring-cloud-dependencies;)([.\\-\\w]+)");
    private final RestTemplate restTemplate;
    private final String externalDependenciesFileUrl;
    private String externalDependenciesFileContent;

    public SpringCloudAzureCurrentVersionReader(RestTemplate restTemplate,
                                                DependencyProperties dependencyProperties) {
        this.restTemplate = restTemplate;
        this.externalDependenciesFileUrl = dependencyProperties.getAzure().getExternalDependenciesFileUrl();
    }

    /**
     * Get current Spring Boot Version in Spring Cloud Azure
     * @return the current Spring Boot Version
     */
    public String getCurrentSupportedSpringBootVersion() {
        return findMatchedVersion(getExternalDependenciesFileContent(), SPRING_BOOT_DEPENDENCIES_PATTERN, 2);
    }

    /**
     * Get current Spring Cloud Version in Spring Cloud Azure
     * @return the current Spring Cloud Version
     */
    public String getCurrentSupportedSpringCloudVersion() {
        return findMatchedVersion(getExternalDependenciesFileContent(), SPRING_CLOUD_DEPENDENCIES_PATTERN, 2);
    }

    /**
     * According to the pattern ,return the matched version
     * @param content external-dependencies.txt
     * @param pattern Spring Boot or Spring Cloud version pattern
     * @return the matched version
     */
    static String findMatchedVersion(String content, Pattern pattern, int group) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(group);
        }
        return null;
    }

    /**
     * Get file content from external-dependencies.txt in Spring Cloud Azure
     * @return the External Dependencies file information
     */
    private String getExternalDependenciesFileContent() {
        if (this.externalDependenciesFileContent == null) {
            this.externalDependenciesFileContent = this.restTemplate.getForObject(this.externalDependenciesFileUrl,
                String.class);
        }
        return this.externalDependenciesFileContent;
    }

}
