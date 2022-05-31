package com.azure.spring.dev.tools.dependency.support;

import com.azure.spring.dev.tools.dependency.configuration.DependencyProperties;
import com.azure.spring.dev.tools.dependency.metadata.azure.SpringCloudAzureSupportMetadata;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import javax.xml.transform.Source;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Read SpringCloudAzure supportStatus from https://raw.githubusercontent
 * .com/Azure/azure-sdk-for-java/main/sdk/spring/spring-cloud-azure-supported-spring.json
 */
public class SpringCloudAzureSupportMetadataReader {

    private final RestTemplate restTemplate;
    private final DependencyProperties dependencyProperties;
    private final ObjectMapper objectMapper;

    public SpringCloudAzureSupportMetadataReader(RestTemplate restTemplate,
                                                 DependencyProperties dependencyProperties,
                                                 ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.dependencyProperties = dependencyProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * Get the SpringCloudAzure supportStatus of SpringBoot versions, such as "SUPPORTED", "END_OF_LIFE", "TODO"
     * @return list of SpringCloudAzureSupportMetadata
     */
    public List<SpringCloudAzureSupportMetadata> getAzureSupportStatus() {
        // Github always return text/plain
        String response = this.restTemplate.getForObject(dependencyProperties.getAzure().getSupportMetadataUrl(), String.class);
        try {
            return this.objectMapper.readValue(response, new TypeReference<List<SpringCloudAzureSupportMetadata>>() { });
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

}
