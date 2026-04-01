package com.azure.spring.reference.intellij.configurationProperties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("spring.cloud.azure.reference")
public class ReferenceConfigurationProperties {

    @NestedConfigurationProperty
    private final List<SpringArtifactSpec> springArtifacts = new ArrayList<>();
    @NestedConfigurationProperty
    private final List<ClientArtifactSpec> clientArtifacts = new ArrayList<>();
    @NestedConfigurationProperty
    private final List<ServiceSpec> services = new ArrayList<>();
    @NestedConfigurationProperty
    private final List<CompatibilitySpec> compatibilities = new ArrayList<>();
    @NestedConfigurationProperty
    private final List<VersionSpec> versions = new ArrayList<>();


    public List<SpringArtifactSpec> getSpringArtifacts() {
        return springArtifacts;
    }

    public List<ClientArtifactSpec> getClientArtifacts() {
        return clientArtifacts;
    }

    public List<ServiceSpec> getServices() {
        return services;
    }

    public List<CompatibilitySpec> getCompatibilities() {
        return compatibilities;
    }

    public List<VersionSpec> getVersions() {
        return versions;
    }
}
