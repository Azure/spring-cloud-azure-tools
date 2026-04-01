package com.azure.spring.reference.intellij.configurationProperties;

import java.util.ArrayList;
import java.util.List;

public class FeatureSpec {
    private String name;
    private String description;
    private String msDocs;
    private final List<Selector> artifactSelectors = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getMsDocs() {
        return msDocs;
    }

    public void setMsDocs(String msDocs) {
        this.msDocs = msDocs;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Selector> getArtifactSelectors() {
        return artifactSelectors;
    }
}