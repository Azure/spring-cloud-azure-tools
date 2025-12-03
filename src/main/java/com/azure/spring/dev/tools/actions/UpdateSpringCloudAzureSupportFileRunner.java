package com.azure.spring.dev.tools.actions;

import com.azure.spring.dev.tools.dependency.metadata.azure.SpringCloudAzureSupportMetadata;
import com.azure.spring.dev.tools.dependency.metadata.azure.SupportStatus;
import com.azure.spring.dev.tools.dependency.metadata.maven.Version;
import com.azure.spring.dev.tools.dependency.metadata.maven.VersionRange;
import com.azure.spring.dev.tools.dependency.metadata.spring.ReleaseStatus;
import com.azure.spring.dev.tools.dependency.support.SpringCloudAzureSupportMetadataReader;
import com.azure.spring.dev.tools.dependency.support.SpringInitializrMetadataReader;
import com.azure.spring.dev.tools.dependency.support.SpringProjectMetadataReader;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.azure.spring.dev.tools.dependency.support.converter.SpringCloudAzureSupportMetadataConverter.CONVERTER;

/**
 * This Runner is used to generate the newest spring-cloud-azure-supported-spring.json file for
 * spring compatibility tests, which contains information about Spring Boot and Spring Cloud supported by Azure,
 * such as: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/spring/pipeline/spring-cloud-azure-supported-spring.json
 */
@ConditionalOnProperty("update-spring-cloud-azure-support-file")
@Component
public class UpdateSpringCloudAzureSupportFileRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateSpringCloudAzureSupportFileRunner.class);
    static final List<String> SUPPORTED_VERSIONS = Stream.of("2.5.15", "2.6.15", "2.7.18", "3.0.13", "3.1.12", "3.2.12", "3.3.13").collect(Collectors.toList());
    static final String NONE_SUPPORTED_VERSION = "NONE_SUPPORTED_SPRING_CLOUD_VERSION";
    private final SpringProjectMetadataReader springProjectMetadataReader;
    private final Map<String, VersionRange> springCloudCompatibleSpringBootVersionRanges;
    private final Map<String, SpringCloudAzureSupportMetadata> azureSupportMetadataMap;
    private final ObjectMapper objectMapper;

    public UpdateSpringCloudAzureSupportFileRunner(SpringProjectMetadataReader springProjectMetadataReader,
                                                   SpringInitializrMetadataReader springInitializrMetadataReader,
                                                   SpringCloudAzureSupportMetadataReader azureSupportMetadataReader,
                                                   ObjectMapper objectMapper) {
        this.springProjectMetadataReader = springProjectMetadataReader;
        this.objectMapper = objectMapper;
        this.springCloudCompatibleSpringBootVersionRanges =
            springInitializrMetadataReader.getCompatibleSpringBootVersions("spring-cloud");
        this.azureSupportMetadataMap = azureSupportMetadataReader.getAzureSupportMetadata().stream().collect(
            Collectors.toMap(SpringCloudAzureSupportMetadata::getSpringBootVersion, Function.identity()));
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("---------- starting {} ----------", UpdateSpringCloudAzureSupportFileRunner.class.getSimpleName());

        final Set<String> activeSpringBootVersions = new HashSet<>();

        List<SpringCloudAzureSupportMetadata> current = springProjectMetadataReader
            .getProjectReleases()
            .stream()
            .map(CONVERTER::convert)
            .filter(Objects::nonNull)
            .filter(s -> isVersionSupported(s.getSpringBootVersion()))
            .peek(s -> s.setSpringCloudVersion(findCompatibleSpringCloudVersion(s.getSpringBootVersion())))
            .peek(s -> s.setSupportStatus(findSupportStatus(s.getSpringBootVersion())))
            .peek(s -> activeSpringBootVersions.add(s.getSpringBootVersion()))
            .collect(Collectors.toList());

        List<SpringCloudAzureSupportMetadata> snapshot = azureSupportMetadataMap
            .values()
            .stream()
            .filter(s -> !activeSpringBootVersions.contains(s.getSpringBootVersion()))
            .filter(s -> !SUPPORTED_VERSIONS.contains(s.getSpringBootVersion()))
            .peek(s -> s.setCurrent(false))
            .peek(s -> s.setSupportStatus(SupportStatus.END_OF_LIFE))
            .collect(Collectors.toList());

        maintainVersions(snapshot, azureSupportMetadataMap);

        List<SpringCloudAzureSupportMetadata> result = Stream
            .concat(current.stream(), snapshot.stream())
            .sorted((o1, o2) -> {
                Version v1 = Version.parse(o1.getSpringBootVersion());
                Version v2 = Version.parse(o2.getSpringBootVersion());
                return v2.compareTo(v1);
            }).collect(Collectors.toList());

        setNewStatus(result);

        writeToFile(result);
    }

    void maintainVersions(List<SpringCloudAzureSupportMetadata> snapshot, Map<String, SpringCloudAzureSupportMetadata> map) {
        SUPPORTED_VERSIONS.forEach(v -> snapshot.add(map.get(v)));
    }

    void setNewStatus(List<SpringCloudAzureSupportMetadata> result) {
        for (SpringCloudAzureSupportMetadata metadata : result) {
            if (metadata.getSupportStatus() == null) {
                if (metadata.getReleaseStatus().equals(ReleaseStatus.GENERAL_AVAILABILITY)) {
                    metadata.setSupportStatus(SupportStatus.SUPPORTED);
                } else {
                    metadata.setSupportStatus(SupportStatus.TODO);
                }
            }
        }
    }

    private void writeToFile(List<SpringCloudAzureSupportMetadata> result) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(
            new FileWriter("spring-cloud-azure-supported-spring.json"))) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
            prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

            bufferedWriter.write(this.objectMapper.writer(prettyPrinter).writeValueAsString(result));
            bufferedWriter.newLine();
        }
    }

    private SupportStatus findSupportStatus(String springBootVersion) {
        SpringCloudAzureSupportMetadata supportMetadata = this.azureSupportMetadataMap.get(springBootVersion);
        return supportMetadata == null ? null : supportMetadata.getSupportStatus();
    }

    String findCompatibleSpringCloudVersion(String springBootVersion) {
        SpringCloudAzureSupportMetadata supportMetadata = this.azureSupportMetadataMap.get(springBootVersion);
        if (supportMetadata != null && supportMetadata.getSupportStatus() == SupportStatus.END_OF_LIFE) {
            return supportMetadata.getSpringCloudVersion();
        }
        return this.springCloudCompatibleSpringBootVersionRanges
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().match(Version.parse(springBootVersion)))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList())
            .stream().findFirst().orElse(NONE_SUPPORTED_VERSION);
    }

    /**
     * Checks if the given Spring Boot version is supported (3.5.0 or above).
     * @param springBootVersion the Spring Boot version string to check
     * @return true if the version is 3.5.0 or above, false otherwise
     */
    boolean isVersionSupported(String springBootVersion) {
        Version version = Version.parse(springBootVersion);
        Version minVersion = Version.parse("3.5.0");
        return version.compareTo(minVersion) >= 0;
    }

}
