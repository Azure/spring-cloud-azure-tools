package com.azure.spring.reference.intellij.links;

public class GitHubLink implements Link {

    private static final String PATH_PATTERN = "https://github.com/Azure/azure-sdk-for-java/tree/spring-cloud-azure_%s/sdk/spring/%s";
    private static final String BOM_PATH_PATTERN = "https://github.com/Azure/azure-sdk-for-java/tree/spring-cloud-azure_%s/sdk/boms/%s";
    private final String path;

    public GitHubLink(String path) {
        this.path = path;
    }

    public GitHubLink(String artifactId, String version, boolean isBom) {
        this.path = (isBom ? BOM_PATH_PATTERN : PATH_PATTERN).formatted(version, artifactId);
    }

    @Override
    public String getName() {
        return "github";
    }

    @Override
    public String getPath() {
        return path;
    }
}
