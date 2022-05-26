package com.azure.spring.dev.tools.dependency.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SpringCloudAzureSupportedSpring {
    private boolean current;
    @JsonProperty("spring-boot-version")
    private String springBootVersion;
    @JsonProperty("spring-cloud-version")
    private String springCloudVersion;
    private ReleaseStatus releaseStatus;
    private boolean snapshot;
    private SupportStatus supportStatus;
}
