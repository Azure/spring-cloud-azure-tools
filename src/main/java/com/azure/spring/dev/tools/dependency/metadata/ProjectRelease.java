package com.azure.spring.dev.tools.dependency.metadata;

import lombok.Data;

/**
 * {
 * 			"version": "2.6.6",
 * 			"versionDisplayName": "2.6.6",
 * 			"current": true,
 * 			"releaseStatus": "GENERAL_AVAILABILITY",
 * 			"snapshot": false
 *                },
 */
@Data
public class ProjectRelease {
    private String version;
    private String versionDisplayName;
    private boolean current;
    private ReleaseStatus releaseStatus;
    private boolean snapshot;

}
