package com.azure.spring.reference.intellij.configurationProperties;

import java.util.ArrayList;
import java.util.List;

public class ServiceSpec {
    private String name;
    private final List<FeatureSpec> features = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FeatureSpec> getFeatures() {
        return features;
    }
}
