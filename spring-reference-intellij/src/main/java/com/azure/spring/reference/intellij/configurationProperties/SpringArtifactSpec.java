package com.azure.spring.reference.intellij.configurationProperties;

import com.azure.spring.reference.intellij.enums.AzureService;
import com.azure.spring.reference.intellij.enums.AzureSubService;
import com.azure.spring.reference.intellij.enums.LibraryType;

public class SpringArtifactSpec {
    private String groupId = "com.azure.spring";
    private String artifactId;
    private String description;
    private boolean isBom = false;
    private String bomArtifact = "spring-cloud-azure-dependencies";
    private AzureService azureService;
    private AzureSubService azureSubService;
    private boolean isV4 = true;
    private LibraryType libraryType = LibraryType.spring;
    private String label;
    private String githubLink;
    private String sampleLink;
    private String msDocsLink;
    private String javaDocsLink;
    private String javaDocsPattern = "https://azuresdkdocs.z19.web.core.windows.net/java/%s/%s/index.html";
    private boolean containsSourceCode = false;

    private boolean hasSampleLink = true;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isBom() {
        return isBom;
    }

    public void setBom(boolean bom) {
        isBom = bom;
    }

    public String getBomArtifact() {
        return bomArtifact;
    }

    public void setBomArtifact(String bomArtifact) {
        this.bomArtifact = bomArtifact;
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

    public boolean isV4() {
        return isV4;
    }

    public void setV4(boolean v4) {
        isV4 = v4;
    }

    public LibraryType getLibraryType() {
        return libraryType;
    }

    public void setLibraryType(LibraryType libraryType) {
        this.libraryType = libraryType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getGithubLink() {
        return githubLink;
    }

    public void setGithubLink(String githubLink) {
        this.githubLink = githubLink;
    }

    public String getSampleLink() {
        return sampleLink;
    }

    public void setSampleLink(String sampleLink) {
        this.sampleLink = sampleLink;
    }

    public String getMsDocsLink() {
        return msDocsLink;
    }

    public void setMsDocsLink(String msDocsLink) {
        this.msDocsLink = msDocsLink;
    }

    public String getJavaDocsLink() {
        return javaDocsLink;
    }

    public void setJavaDocsLink(String javaDocsLink) {
        this.javaDocsLink = javaDocsLink;
    }

    public String getJavaDocsPattern() {
        return javaDocsPattern;
    }

    public void setJavaDocsPattern(String javaDocsPattern) {
        this.javaDocsPattern = javaDocsPattern;
    }

    public boolean isContainsSourceCode() {
        return containsSourceCode;
    }

    public void setContainsSourceCode(boolean containsSourceCode) {
        this.containsSourceCode = containsSourceCode;
    }

    public boolean isHasSampleLink() {
        return hasSampleLink;
    }

    public void setHasSampleLink(boolean hasSampleLink) {
        this.hasSampleLink = hasSampleLink;
    }
}