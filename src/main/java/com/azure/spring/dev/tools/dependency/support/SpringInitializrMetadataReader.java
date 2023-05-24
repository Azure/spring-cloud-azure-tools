package com.azure.spring.dev.tools.dependency.support;

import com.azure.spring.dev.tools.dependency.configuration.DependencyProperties;
import com.azure.spring.dev.tools.dependency.metadata.maven.Version;
import com.azure.spring.dev.tools.dependency.metadata.maven.VersionRange;
import com.azure.spring.dev.tools.dependency.metadata.spring.initializr.ActuatorInfoMetadata;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Read Spring Initializr information from https://start.spring.io/actuator/info
 */
public class SpringInitializrMetadataReader {

    private final RestTemplate restTemplate;
    private final String actuatorInfoUrl;
    static final Pattern VERSION_RANGE_PATTERN = Pattern.compile("^([><])(=*)(.*)");

    public SpringInitializrMetadataReader(RestTemplate restTemplate,
                                          DependencyProperties dependencyProperties) {
        this.restTemplate = restTemplate;
        this.actuatorInfoUrl = dependencyProperties.getInitializr().getActuatorInfoUrl();
    }

    /**
     * Get the compatible Spring Boot versions for a project bom, like "spring-cloud", "spring-cloud-azure",
     * "spring-cloud-gcp", etc.
     * @param projectId The project id.
     * @return The compatibility version between the project bom and Spring Boot versions.
     */
    public Map<String, VersionRange> getCompatibleSpringBootVersions(String projectId) {
        ActuatorInfoMetadata metadata = restTemplate.getForObject(actuatorInfoUrl, ActuatorInfoMetadata.class);

        Objects.requireNonNull(metadata);
        return metadata.getServiceBomMap()
                       .get(projectId)
                       .entrySet()
                       .stream()
                       .map(entry -> Map.entry(entry.getKey(), parseVersionRange(entry.getValue())))
                       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    VersionRange parseVersionRange(String range) {
        Version lowerVersion = null, higherVersion = null;
        boolean lowerInclusive = false, higherInclusive = false;
        String[] splits = range.split(" ");
        for (String s : splits) {
            Matcher matcher = VERSION_RANGE_PATTERN.matcher(s);
            if (matcher.matches()) {
                boolean isInclusive = Objects.equals("=", matcher.group(2));
                String version = matcher.group(3);
                if (Objects.equals(">", matcher.group(1))) {
                    lowerVersion = Version.parse(version);
                    lowerInclusive = isInclusive;
                } else if (Objects.equals("<", matcher.group(1))) {
                    higherVersion = Version.parse(version);
                    higherInclusive = isInclusive;
                }
            }
        }
        if (lowerVersion == null && higherVersion == null) {
            throw new IllegalStateException("Fail to parse version range from " + range);
        }
        return new VersionRange(lowerVersion, lowerInclusive, higherVersion, higherInclusive);
    }

}
