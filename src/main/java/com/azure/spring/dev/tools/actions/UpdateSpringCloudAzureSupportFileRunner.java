package com.azure.spring.dev.tools.actions;

import com.azure.spring.dev.tools.dependency.metadata.azure.SpringCloudAzureSupportMetadata;
import com.azure.spring.dev.tools.dependency.metadata.azure.SupportStatus;
import com.azure.spring.dev.tools.dependency.metadata.maven.Version;
import com.azure.spring.dev.tools.dependency.metadata.maven.VersionRange;
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
 * such as: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/spring/spring-cloud-azure-supported-spring.json
 */
@ConditionalOnProperty("update-spring-cloud-azure-support-file")
@Component
public class UpdateSpringCloudAzureSupportFileRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateSpringCloudAzureSupportFileRunner.class);
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
            .getProjectReleases("spring-boot")
            .stream()
            .map(CONVERTER::convert)
            .filter(Objects::nonNull)
            .peek(s -> s.setSpringCloudVersion(findCompatibleSpringCloudVersion(s.getSpringBootVersion())))
            .peek(s -> s.setSupportStatus(findSupportStatus(s.getSpringBootVersion())))
            .peek(s -> activeSpringBootVersions.add(s.getSpringBootVersion()))
            .collect(Collectors.toList());

        List<SpringCloudAzureSupportMetadata> snapshot = azureSupportMetadataMap.values()
            .stream()
            .filter(s -> !activeSpringBootVersions.contains(s.getSpringBootVersion()))
            .peek(s -> s.setCurrent(false))
            .peek(s -> s.setSupportStatus(SupportStatus.END_OF_LIFE))
            .collect(Collectors.toList());

        List<SpringCloudAzureSupportMetadata> result = Stream.concat(current.stream(), snapshot.stream())
                                                             .sorted((o1, o2) -> {
                                                                 Version v1 = Version.parse(o1.getSpringBootVersion());
                                                                 Version v2 = Version.parse(o2.getSpringBootVersion());
                                                                 return v2.compareTo(v1);
                                                             }).collect(Collectors.toList());

        writeToFile(result);
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

    private String findCompatibleSpringCloudVersion(String springBootVersion) {
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
            .stream().findFirst().orElseThrow();
    }

}
