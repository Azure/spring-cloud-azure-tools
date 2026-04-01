package com.azure.spring.reference.intellij.records;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({ "starter", "bom", "compatibilityRange", "mappings" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SpringProperties(@JsonProperty("starter") boolean isBootStarter,
                               String bom,
                               String compatibilityRange,
                               List<CompatibilityMapping> mappings) {

}