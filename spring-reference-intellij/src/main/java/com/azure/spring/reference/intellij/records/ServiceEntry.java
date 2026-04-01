package com.azure.spring.reference.intellij.records;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({ "name", "content" })
public record ServiceEntry(String name,
                           @JsonProperty("content") List<Feature> features) {
}
