package com.azure.spring.dev.tools.actions;

import com.azure.spring.dev.tools.dependency.metadata.azure.SpringCloudAzureSupportMetadata;
import com.azure.spring.dev.tools.dependency.metadata.azure.SupportStatus;
import com.azure.spring.dev.tools.dependency.metadata.maven.Version;
import com.azure.spring.dev.tools.dependency.metadata.maven.VersionRange;
import com.azure.spring.dev.tools.dependency.support.SpringCloudAzureSupportMetadataReader;
import com.azure.spring.dev.tools.dependency.support.SpringInitializrMetadataReader;
import com.azure.spring.dev.tools.dependency.support.SpringProjectMetadataReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.azure.spring.dev.tools.dependency.support.converter.SpringCloudAzureSupportMetadataConverter.CONVERTER;

@ConditionalOnProperty("update-spring-cloud-azure-support-file")
@Component
public class UpdateSpringCloudAzureSupportFileRunner implements CommandLineRunner {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UpdateSpringCloudAzureSupportFileRunner.class);
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
        this.springCloudCompatibleSpringBootVersionRanges =
            springInitializrMetadataReader.getCompatibleSpringBootVersions("spring-cloud");
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("---------- starting {} ----------", UpdateSpringCloudAzureSupportFileRunner.class.getSimpleName());
        List<SpringCloudAzureSupportMetadata> azureSupportMetadata = azureSupportMetadataReader.getAzureSupportMetadata();

        List<SpringCloudAzureSupportMetadata> result = springProjectMetadataReader
            .getProjectReleases("spring-boot")
            .stream()
            .map(CONVERTER::convert)
            .filter(Objects::nonNull)
            .peek(s -> s.setSpringCloudVersion(findCompatibleSpringCloudVersion(s.getSpringBootVersion())))
            .peek(s -> s.setSupportStatus(
                findSupportStatus(azureSupportMetadata, s.getSpringBootVersion()).orElse(null)))
            .collect(Collectors.toList());
        result.addAll(azureSupportMetadata
        .stream()
        .filter(m -> !result.stream().map(SpringCloudAzureSupportMetadata::getSpringBootVersion)
                            .collect(Collectors.toList()).contains(m.getSpringBootVersion()))
        .peek(m -> m.setSupportStatus(SupportStatus.END_OF_LIFE))
        .peek(m -> m.setCurrent(false))
        .collect(Collectors.toList()));
        writeToFile(result);
    }

    private void writeToFile(List<SpringCloudAzureSupportMetadata> result) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("spring-cloud-azure-supported-spring.json"))) {
            bufferedWriter.write(this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
            bufferedWriter.newLine();
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
