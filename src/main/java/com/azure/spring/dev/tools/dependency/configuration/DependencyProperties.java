package com.azure.spring.dev.tools.dependency.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring")
@Data
public class DependencyProperties {
    private final Metadata metadata = new Metadata();
    private final Initializr initializr = new Initializr();
    private final Azure azure = new Azure();

    @Data
    public static class Metadata {
        private String url;
    }

    @Data
    public static class Initializr {
        private String actuatorInfoUrl;
    }

    @Data
    public static class Azure {
        private String supportMetadataUrl;
    }
}
