package com.azure.spring.reference.intellij.links;

import com.azure.spring.reference.intellij.records.Artifact;

public class JavaDocLink implements Link {

    private static final String PATH_PATTERN = "https://azuresdkdocs.z19.web.core.windows.net/java/%s/%s/index.html";
    private final String path;

    public JavaDocLink(String path) {
        this.path = path;
    }

    public JavaDocLink(Artifact artifact) {
        this.path = PATH_PATTERN.formatted(artifact.artifactId(), artifact.latestGaVersion());
    }

    @Override
    public String getName() {
        return "javadoc";
    }

    @Override
    public String getPath() {
        return path;
    }
}
