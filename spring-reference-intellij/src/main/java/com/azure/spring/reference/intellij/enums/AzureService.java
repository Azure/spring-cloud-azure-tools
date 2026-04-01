package com.azure.spring.reference.intellij.enums;

public enum AzureService {

    AppConfiguration("appconfiguration"),
    Aad("aad"),
    AadB2c("aad"),
    Cosmos("cosmos"),
    EventGrid("eventgrid"),
    EventHubs("eventhubs"),
    ServiceBus("servicebus"),
    Storage("storage"),
    KeyVault("keyvault"),
    MySQL("mysql"),
    PostgreSQL("postgresql"),
    Redis("redis"),
    Testcontainers("testcontainers"),
    Dockercompose("dockercompose");

    private final String sampleRepoIdentifier;

    AzureService(String sampleRepoIdentifier) {
        this.sampleRepoIdentifier = sampleRepoIdentifier;
    }

    public String getSampleRepoIdentifier() {
        return sampleRepoIdentifier;
    }
}
