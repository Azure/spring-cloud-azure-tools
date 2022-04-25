package com.azure.spring.dev.tools.dependency.metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent data return by the Spring release metadata API.
 *
 *  curl https://spring.io/project_metadata/spring-boot
 * {"id":"spring-boot","name":"Spring Boot","projectReleases":[{"version":"3.0.0-SNAPSHOT","versionDisplayName":"3.0
 * .0-SNAPSHOT","current":false,"releaseStatus":"SNAPSHOT","snapshot":true},{"version":"3.0.0-M2",
 * "versionDisplayName":"3.0.0-M2","current":false,"releaseStatus":"PRERELEASE","snapshot":false},{"version":"2.7
 * .0-SNAPSHOT","versionDisplayName":"2.7.0-SNAPSHOT","current":false,"releaseStatus":"SNAPSHOT","snapshot":true},{
 * "version":"2.7.0-M3","versionDisplayName":"2.7.0-M3","current":false,"releaseStatus":"PRERELEASE","snapshot":false
 * },{"version":"2.6.7-SNAPSHOT","versionDisplayName":"2.6.7-SNAPSHOT","current":false,"releaseStatus":"SNAPSHOT",
 * "snapshot":true},{"version":"2.6.6","versionDisplayName":"2.6.6","current":true,
 * "releaseStatus":"GENERAL_AVAILABILITY","snapshot":false},{"version":"2.5.13-SNAPSHOT","versionDisplayName":"2.5
 * .13-SNAPSHOT","current":false,"releaseStatus":"SNAPSHOT","snapshot":true},{"version":"2.5.12",
 * "versionDisplayName":"2.5.12","current":false,"releaseStatus":"GENERAL_AVAILABILITY","snapshot":false},{"version
 * ":"2.4.13","versionDisplayName":"2.4.13","current":false,"releaseStatus":"GENERAL_AVAILABILITY","snapshot":false},
 * {"version":"2.3.12.RELEASE","versionDisplayName":"2.3.12.RELEASE","current":false,
 * "releaseStatus":"GENERAL_AVAILABILITY","snapshot":false}]}%
 */
@Data
public class SpringReleaseMetadata {
    private String id;
    private String name;
    private List<ProjectRelease> projectReleases = new ArrayList<>();


}
