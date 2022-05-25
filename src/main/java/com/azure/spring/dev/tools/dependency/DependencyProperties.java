package com.azure.spring.dev.tools.dependency;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring")
@Data
public class DependencyProperties {
    private Metadata metadata;
    private BomRanges bomRanges;

    @Data
    public static class Metadata{
        String url;
    }

    @Data
    public static class BomRanges{
        String url;
    }
}
