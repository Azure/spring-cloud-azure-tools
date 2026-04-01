package com.azure.spring.reference.intellij.configurationProperties;

import com.azure.spring.reference.intellij.enums.AzureService;
import com.azure.spring.reference.intellij.enums.AzureSubService;

public class ClientArtifactSpec {
    private String artifactId;
    private String groupId = "com.azure";
    private AzureService azureService;
    private AzureSubService azureSubService;
    private String label;

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public AzureService getAzureService() {
        return azureService;
    }

    public void setAzureService(AzureService azureService) {
        this.azureService = azureService;
    }

    public AzureSubService getAzureSubService() {
        return azureSubService;
    }

    public void setAzureSubService(AzureSubService azureSubService) {
        this.azureSubService = azureSubService;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}