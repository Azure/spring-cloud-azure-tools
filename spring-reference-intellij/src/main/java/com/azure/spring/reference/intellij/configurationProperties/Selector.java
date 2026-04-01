package com.azure.spring.reference.intellij.configurationProperties;

import com.azure.spring.reference.intellij.enums.AzureService;
import com.azure.spring.reference.intellij.enums.AzureSubService;

import java.util.Objects;

public class Selector {
    public Selector() {
    }

    public Selector(AzureService azureService, AzureSubService azureSubService, String label) {
        this.azureService = azureService;
        this.azureSubService = azureSubService;
        this.label = label;
    }

    private AzureService azureService;
    private AzureSubService azureSubService;
    private String label;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Selector selector = (Selector) o;
        return azureService == selector.azureService && azureSubService == selector.azureSubService && Objects.equals(label, selector.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(azureService, azureSubService, label);
    }
}