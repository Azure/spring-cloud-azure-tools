package com.azure.spring.dev.tools.dependency;

import com.azure.spring.dev.tools.dependency.metadata.ProjectRelease;
import com.azure.spring.dev.tools.dependency.metadata.SpringReleaseMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class SpringBootReleaseMetadataReader {


    private final RestTemplate restTemplate;
    private final DependencyProperties properties;

    public SpringBootReleaseMetadataReader(RestTemplate restTemplate,
                                           DependencyProperties dependencyProperties) {
        this.restTemplate = restTemplate;
        this.properties = dependencyProperties;
    }

    public List<ProjectRelease> getProjectReleases(String projectId) {

        SpringReleaseMetadata metadata = restTemplate.getForObject(properties.getMetadata().getUrl()+projectId,
            SpringReleaseMetadata.class);
        return metadata.getProjectReleases();
    }

}
