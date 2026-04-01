package com.azure.spring.reference.intellij.links;

import com.azure.spring.reference.intellij.enums.AzureService;

public class SampleLink implements Link {

    private static final String PATH_PATTERN = "https://github.com/Azure-Samples/azure-spring-boot-samples/tree/spring-cloud-azure_%s/%s/%s/";
    private final String path;

    public SampleLink(String artifactId, String version, AzureService service) {
        this.path = PATH_PATTERN.formatted(version, service.getSampleRepoIdentifier(), artifactId);
    }

    public SampleLink(String path) {
        this.path = path;
    }

    @Override
    public String getName() {
        return "sample";
    }

    @Override
    public String getPath() {
        return path;
    }
}
