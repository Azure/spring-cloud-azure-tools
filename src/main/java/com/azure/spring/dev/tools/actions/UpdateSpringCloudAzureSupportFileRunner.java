package com.azure.spring.dev.tools.actions;

import com.azure.spring.dev.tools.dependency.metadata.azure.SpringCloudAzureSupportMetadata;
import com.azure.spring.dev.tools.dependency.metadata.azure.SupportStatus;
import com.azure.spring.dev.tools.dependency.metadata.maven.Version;
import com.azure.spring.dev.tools.dependency.metadata.maven.VersionRange;
import com.azure.spring.dev.tools.dependency.support.SpringCloudAzureSupportMetadataReader;
import com.azure.spring.dev.tools.dependency.support.SpringInitializrMetadataReader;
import com.azure.spring.dev.tools.dependency.support.SpringProjectMetadataReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.azure.spring.dev.tools.dependency.support.converter.SpringCloudAzureSupportMetadataConverter.CONVERTER;

@Component
public class UpdateSpringCloudAzureSupportFileRunner implements CommandLineRunner {

    private final SpringProjectMetadataReader springProjectMetadataReader;
    private final Map<String, VersionRange> springCloudCompatibleSpringBootVersionRanges;
    private final SpringCloudAzureSupportMetadataReader azureSupportMetadataReader;
    private final ObjectMapper objectMapper;

    public UpdateSpringCloudAzureSupportFileRunner(SpringProjectMetadataReader springProjectMetadataReader,
                                                   SpringInitializrMetadataReader springInitializrMetadataReader,
                                                   SpringCloudAzureSupportMetadataReader azureSupportMetadataReader,
                                                   ObjectMapper objectMapper) {
        this.springProjectMetadataReader = springProjectMetadataReader;
        this.azureSupportMetadataReader = azureSupportMetadataReader;
        this.objectMapper = objectMapper;

        this.springCloudCompatibleSpringBootVersionRanges = springInitializrMetadataReader.getCompatibleSpringBootVersions("spring-cloud");
    }

    @Override
    public void run(String... args) throws Exception {
        List<SpringCloudAzureSupportMetadata> azureSupportStatus = azureSupportMetadataReader.getAzureSupportStatus();

        List<SpringCloudAzureSupportMetadata> result = springProjectMetadataReader
            .getProjectReleases("spring-boot")
            .stream()
            .map(CONVERTER::convert)
            .filter(Objects::nonNull)
            .peek(s -> s.setSpringCloudVersion(findCompatibleSpringCloudVersion(s.getSpringBootVersion())))
            .peek(s -> s.setSupportStatus(findSupportStatus(azureSupportStatus, s.getSpringBootVersion()).orElse(null)))
            .collect(Collectors.toList());

        writeToFile(result);
    }

    private void writeToFile(List<SpringCloudAzureSupportMetadata> result) throws IOException {
        try (FileWriter fileWriter = new FileWriter("spring-cloud-azure-support.json")) {
            fileWriter.write(this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
        }
    }

    private Optional<SupportStatus> findSupportStatus(List<SpringCloudAzureSupportMetadata> azureSupportStatus,
                                                      String springBootVersion) {
        return azureSupportStatus
            .stream()
            .filter(s -> springBootVersion.equals(s.getSpringBootVersion()))
            .map(SpringCloudAzureSupportMetadata::getSupportStatus)
            .findFirst();
    }

    private String findCompatibleSpringCloudVersion(String springBootVersion) {
        return this.springCloudCompatibleSpringBootVersionRanges
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().match(Version.parse(springBootVersion)))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList())
            .stream().findFirst().orElseThrow();
    }

}
