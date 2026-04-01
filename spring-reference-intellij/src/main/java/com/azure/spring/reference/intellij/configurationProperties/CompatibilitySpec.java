package com.azure.spring.reference.intellij.configurationProperties;

import java.util.ArrayList;
import java.util.List;

public class CompatibilitySpec {

    private String lowestSpringBootVersion;
    private String highestSpringBootVersion;
    private String artifactId = "spring-cloud-azure-dependencies";
    private final List<Mapping> mappings = new ArrayList<>();

    public static class Mapping {
        private String version;
        private String lowestSpringBootVersion;
        private String highestSpringBootVersion;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getLowestSpringBootVersion() {
            return lowestSpringBootVersion;
        }

        public void setLowestSpringBootVersion(String lowestSpringBootVersion) {
            this.lowestSpringBootVersion = lowestSpringBootVersion;
        }

        public String getHighestSpringBootVersion() {
            return highestSpringBootVersion;
        }

        public void setHighestSpringBootVersion(String highestSpringBootVersion) {
            this.highestSpringBootVersion = highestSpringBootVersion;
        }
    }

    public String getLowestSpringBootVersion() {
        return lowestSpringBootVersion;
    }

    public void setLowestSpringBootVersion(String lowestSpringBootVersion) {
        this.lowestSpringBootVersion = lowestSpringBootVersion;
    }

    public String getHighestSpringBootVersion() {
        return highestSpringBootVersion;
    }

    public void setHighestSpringBootVersion(String highestSpringBootVersion) {
        this.highestSpringBootVersion = highestSpringBootVersion;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public List<Mapping> getMappings() {
        return mappings;
    }
}
