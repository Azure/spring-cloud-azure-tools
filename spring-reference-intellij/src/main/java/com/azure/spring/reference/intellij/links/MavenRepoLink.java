package com.azure.spring.reference.intellij.links;

public class MavenRepoLink implements Link {

    private static final String PATH_PATTERN = "https://search.maven.org/artifact/%s/%s";
    private final String path;

    public MavenRepoLink(String path) {
        this.path = path;
    }

    public MavenRepoLink(String groupId, String artifactId, String version) {
        this.path = PATH_PATTERN.formatted(groupId, artifactId);
    }

    @Override
    public String getName() {
        return "repopath";
    }

    @Override
    public String getPath() {
        return this.path;
    }
}
