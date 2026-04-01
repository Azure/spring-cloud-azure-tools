package com.azure.spring.reference.intellij.records;

import com.azure.spring.reference.intellij.enums.ArtifactType;
import com.azure.spring.reference.intellij.enums.AzureService;
import com.azure.spring.reference.intellij.enums.LibraryType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "artifactId", "groupId", "versionGA", "versionPreview", "description", "type", "links",
    "dependencyPattern", "springProperties" })
public record Artifact(String groupId,
                       String artifactId,
                       @JsonProperty("versionGA") String latestGaVersion,
                       @JsonProperty("versionPreview") String latestPreviewVersion,
                       String description,
                       @JsonProperty("type") LibraryType libraryType,
                       @JsonIgnore ArtifactType artifactType,
                       @JsonIgnore AzureService service,
                       Map<String, String> links,
                       Map<String, String> dependencyPattern,
                       SpringProperties springProperties) {


}
