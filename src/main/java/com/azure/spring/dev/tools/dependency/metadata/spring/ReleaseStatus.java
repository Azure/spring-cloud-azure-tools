package com.azure.spring.dev.tools.dependency.metadata.spring;

/**
 *
 * https://spring.io/project_metadata/spring-boot
 *
 * The release status returned like:
 *     {
 *       "version": "2.6.8",
 *       "versionDisplayName": "2.6.8",
 *       "current": false,
 *       "releaseStatus": "GENERAL_AVAILABILITY",
 *       "snapshot": false
 *     }
 */
public enum ReleaseStatus {

    GENERAL_AVAILABILITY,
    PRERELEASE,
    SNAPSHOT
}
