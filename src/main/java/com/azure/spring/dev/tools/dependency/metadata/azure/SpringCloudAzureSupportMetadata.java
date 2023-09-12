package com.azure.spring.dev.tools.dependency.metadata.azure;

import com.azure.spring.dev.tools.dependency.metadata.spring.ReleaseStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/spring/pipeline/spring-cloud-azure-supported-spring.json
 *
 * [
 *   {
 *     "current": true,
 *     "spring-boot-version": "2.7.0",
 *     "spring-cloud-version": "2021.0.3-SNAPSHOT",
 *     "releaseStatus": "GENERAL_AVAILABILITY",
 *     "snapshot": false,
 *     "supportStatus": "SUPPORTED"
 *   },
 *   {
 *     "current": false,
 *     "spring-boot-version": "2.6.8",
 *     "spring-cloud-version": "2021.0.2",
 *     "releaseStatus": "GENERAL_AVAILABILITY",
 *     "snapshot": false,
 *     "supportStatus": "SUPPORTED"
 *   },
 *   {
 *     "current": false,
 *     "spring-boot-version": "2.5.14",
 *     "spring-cloud-version": "2020.0.5",
 *     "releaseStatus": "GENERAL_AVAILABILITY",
 *     "snapshot": false,
 *     "supportStatus": "SUPPORTED"
 *   }
 * ]
 */
@Data
public class SpringCloudAzureSupportMetadata {

    //SpringCloudAzure currently supports the versions of SpringBoot and SpringCloud or not
    private boolean current;
    @JsonProperty("spring-boot-version")
    private String springBootVersion;
    @JsonProperty("spring-cloud-version")
    private String springCloudVersion;
    //The release status of SpringBoot version
    private ReleaseStatus releaseStatus;
    private boolean snapshot;
    //The support status of SpringCloudAzure to SpringBoot
    private SupportStatus supportStatus;
}
