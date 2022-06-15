package com.azure.spring.dev.tools.dependency.metadata.spring;

import lombok.Data;

/**
 * https://spring.io/project_metadata/spring-boot
 *
 *     {
 *       "version": "2.6.8",
 *       "versionDisplayName": "2.6.8",
 *       "current": false,
 *       "releaseStatus": "GENERAL_AVAILABILITY",
 *       "snapshot": false
 *     }
 */
@Data
public class ProjectRelease {
    private String version;
    private String versionDisplayName;
    private boolean current;
    private ReleaseStatus releaseStatus;
    private boolean snapshot;

}
