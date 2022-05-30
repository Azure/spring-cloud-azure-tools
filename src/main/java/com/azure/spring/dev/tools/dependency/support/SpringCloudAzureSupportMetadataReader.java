package com.azure.spring.dev.tools.dependency.support;

import com.azure.spring.dev.tools.dependency.configuration.DependencyProperties;
import com.azure.spring.dev.tools.dependency.metadata.azure.SpringCloudAzureSupportMetadata;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SpringCloudAzureSupportMetadataReader {

    private final RestTemplate restTemplate;
    private final DependencyProperties dependencyProperties;

    public SpringCloudAzureSupportMetadataReader(RestTemplate restTemplate,
                                                 DependencyProperties dependencyProperties) {
        this.restTemplate = restTemplate;
        this.dependencyProperties = dependencyProperties;
    }

    public List<SpringCloudAzureSupportMetadata> getAzureSupportStatus() {
        SpringCloudAzureSupportMetadata[] azureSupportMetadata = this.restTemplate.getForObject(
            dependencyProperties.getAzure().getSupportMetadataUrl(),
            SpringCloudAzureSupportMetadata[].class);

        Objects.requireNonNull(azureSupportMetadata);
        return Arrays.asList(azureSupportMetadata);
    }

}
