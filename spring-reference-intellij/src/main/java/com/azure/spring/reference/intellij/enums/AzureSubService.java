package com.azure.spring.reference.intellij.enums;

public enum AzureSubService {

    blobs(AzureService.Storage),
    queues(AzureService.Storage),
    fileShare(AzureService.Storage),

    secrets(AzureService.KeyVault),
    certificates(AzureService.KeyVault),
    ;

    private final AzureService service;

    AzureSubService(AzureService service) {
        this.service = service;
    }

    public AzureService getService() {
        return service;
    }
}
