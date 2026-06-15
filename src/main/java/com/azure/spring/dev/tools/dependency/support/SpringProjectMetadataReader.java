package com.azure.spring.dev.tools.dependency.support;

import com.azure.spring.dev.tools.dependency.configuration.DependencyProperties;
import com.azure.spring.dev.tools.dependency.metadata.maven.Version;
import com.azure.spring.dev.tools.dependency.metadata.maven.VersionParser;
import com.azure.spring.dev.tools.dependency.metadata.spring.ProjectRelease;
import com.azure.spring.dev.tools.dependency.metadata.spring.ReleaseStatus;
import com.azure.spring.dev.tools.dependency.metadata.spring.SpringReleaseMetadata;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
     * @return the list of releases of the given project
     */
    public List<ProjectRelease> getProjectReleases() {

        SpringReleaseMetadata metadata = restTemplate.getForObject(properties.getMetadata().getUrl(),
            SpringReleaseMetadata.class);

        Objects.requireNonNull(metadata);

        return metadata.getProjectReleases();
    }

    public Optional<String> getCurrentVersion(ReleaseStatus releaseStatus) {
        return getProjectReleases()
            .stream()
            .filter(p -> p.getReleaseStatus().equals(releaseStatus))
            .filter(p -> p.getVersion().startsWith("4"))
            .map(ProjectRelease::getVersion)
            .map(VersionParser.DEFAULT::parse)
            .filter(Objects::nonNull)
            .sorted(Comparator.reverseOrder())
            .findFirst()
            .map(Version::toString);
    }
}
