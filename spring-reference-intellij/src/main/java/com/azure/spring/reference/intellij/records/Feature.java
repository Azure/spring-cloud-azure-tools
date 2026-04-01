package com.azure.spring.reference.intellij.records;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Feature(String name,
                      String description,
                      String msDocs,
                      ClientSource clientSource,
                      List<Artifact> artifacts) {
}
