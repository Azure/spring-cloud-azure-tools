package com.azure.spring.dev.tools.dependency.metadata;

import lombok.Data;

@Data
public class SpringCloudAzureSupportedSpring {
    private boolean current;
    private String springBootVersion;
    private String springCloudVersion;
    private ReleaseStatus releaseStatus;
    private boolean snapshot;
    private SupportStatus supportStatus;
}
