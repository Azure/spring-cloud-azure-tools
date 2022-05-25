package com.azure.spring.dev.tools.dependency.metadata;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;


@Data
public class CompatibilityInfoMetadata {
    private Object git;
    private Object build;
    @JsonAlias("bom-ranges")
    private BomRangesMetadata bomRanges;
    @JsonAlias("dependency-ranges")
    private Object dependencyRanges;
}
