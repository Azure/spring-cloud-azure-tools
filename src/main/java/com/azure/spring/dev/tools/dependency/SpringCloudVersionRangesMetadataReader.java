package com.azure.spring.dev.tools.dependency;

import com.azure.spring.dev.tools.dependency.metadata.CompatibilityInfoMetadata;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Data
@Component
public class SpringCloudVersionRangesMetadataReader {

    private final RestTemplate restTemplate;
    private final DependencyProperties properties;

    public SpringCloudVersionRangesMetadataReader(RestTemplate restTemplate,
                                                  DependencyProperties dependencyProperties) {
        this.restTemplate = restTemplate;
        this.properties = dependencyProperties;
    }

    public  Map<String, String> getSpringCloudRange() {
        CompatibilityInfoMetadata metadata = restTemplate.getForObject(properties.getBomRanges().getUrl(),
            CompatibilityInfoMetadata.class);
        return metadata.getBomRanges().getSpringCloudRange();

    }


}
