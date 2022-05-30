package com.azure.spring.dev.tools.dependency.support;

import com.azure.spring.dev.tools.dependency.configuration.DependencyProperties;
import com.azure.spring.dev.tools.dependency.metadata.spring.ProjectRelease;
import com.azure.spring.dev.tools.dependency.metadata.spring.SpringReleaseMetadata;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

/**
 * Read a Spring project's metadata from https://spring.io/project_metadata endpoint.
 */
public class SpringProjectMetadataReader {

    private final RestTemplate restTemplate;
    private final DependencyProperties properties;

    public SpringProjectMetadataReader(RestTemplate restTemplate,
                                       DependencyProperties dependencyProperties) {
        this.restTemplate = restTemplate;
        this.properties = dependencyProperties;
    }

    /**
     * Get {@link SpringReleaseMetadata} from https://spring.io/project_metadata/{projectId}
     * @param projectId the project id, such as "spring-boot", "spring-cloud"
     * @return the list of releases of the given project
     */
    public List<ProjectRelease> getProjectReleases(String projectId) {

        SpringReleaseMetadata metadata = restTemplate.getForObject(properties.getMetadata().getUrl() + projectId,
            SpringReleaseMetadata.class);

        Objects.requireNonNull(metadata);

        return metadata.getProjectReleases();
    }

}
