package com.azure.spring.dev.tools.dependency.metadata.spring;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent data return by the Spring release metadata API.
 *
 *  curl https://spring.io/project_metadata/spring-boot
 *
 *  {
 *   "id": "spring-boot",
 *   "name": "Spring Boot",
 *   "projectReleases": [
 *     {
 *       "version": "3.0.0-SNAPSHOT",
 *       "versionDisplayName": "3.0.0-SNAPSHOT",
 *       "current": false,
 *       "releaseStatus": "SNAPSHOT",
 *       "snapshot": true
 *     },
 *     {
 *       "version": "3.0.0-M3",
 *       "versionDisplayName": "3.0.0-M3",
 *       "current": false,
 *       "releaseStatus": "PRERELEASE",
 *       "snapshot": false
 *     },
 *     {
 *       "version": "2.7.1-SNAPSHOT",
 *       "versionDisplayName": "2.7.1-SNAPSHOT",
 *       "current": false,
 *       "releaseStatus": "SNAPSHOT",
 *       "snapshot": true
 *     },
 *     {
 *       "version": "2.7.0",
 *       "versionDisplayName": "2.7.0",
 *       "current": true,
 *       "releaseStatus": "GENERAL_AVAILABILITY",
 *       "snapshot": false
 *     },
 *     {
 *       "version": "2.6.9-SNAPSHOT",
 *       "versionDisplayName": "2.6.9-SNAPSHOT",
 *       "current": false,
 *       "releaseStatus": "SNAPSHOT",
 *       "snapshot": true
 *     },
 *     {
 *       "version": "2.6.8",
 *       "versionDisplayName": "2.6.8",
 *       "current": false,
 *       "releaseStatus": "GENERAL_AVAILABILITY",
 *       "snapshot": false
 *     },
 *     {
 *       "version": "2.5.14",
 *       "versionDisplayName": "2.5.14",
 *       "current": false,
 *       "releaseStatus": "GENERAL_AVAILABILITY",
 *       "snapshot": false
 *     },
 *     {
 *       "version": "2.4.13",
 *       "versionDisplayName": "2.4.13",
 *       "current": false,
 *       "releaseStatus": "GENERAL_AVAILABILITY",
 *       "snapshot": false
 *     },
 *     {
 *       "version": "2.3.12.RELEASE",
 *       "versionDisplayName": "2.3.12.RELEASE",
 *       "current": false,
 *       "releaseStatus": "GENERAL_AVAILABILITY",
 *       "snapshot": false
 *     }
 *   ]
 * }
 */
@Data
public class SpringReleaseMetadata {
    private String id;
    private String name;
    private List<ProjectRelease> projectReleases = new ArrayList<>();


}
