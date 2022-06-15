package com.azure.spring.dev.tools.actions;

import com.azure.spring.dev.tools.dependency.metadata.maven.Version;
import com.azure.spring.dev.tools.dependency.metadata.maven.VersionRange;
import com.azure.spring.dev.tools.dependency.support.SpringCloudAzureCurrentVersionReader;
import com.azure.spring.dev.tools.dependency.support.SpringInitializrMetadataReader;
import com.azure.spring.dev.tools.dependency.support.SpringProjectMetadataReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map;

/**
 * This Runner was used to get versions of Spring Boot and Spring Cloud for updating spring dependencies in sdk repo.
 */
@ConditionalOnProperty("update-spring-dependencies")
@Component
public class UpdateSpringDependenciesRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateSpringDependenciesRunner.class);
    private final SpringProjectMetadataReader metadataReader;
    private final Map<String, VersionRange> springCloudCompatibleSpringBootVersionRanges;
    private final SpringCloudAzureCurrentVersionReader azureCurrentVersionReader;

    public UpdateSpringDependenciesRunner(SpringProjectMetadataReader metadataReader,
                                          SpringInitializrMetadataReader springInitializrMetadataReader,
                                          SpringCloudAzureCurrentVersionReader azureCurrentVersionReader) {
        this.metadataReader = metadataReader;
        this.azureCurrentVersionReader = azureCurrentVersionReader;
        this.springCloudCompatibleSpringBootVersionRanges =
            springInitializrMetadataReader.getCompatibleSpringBootVersions("spring-cloud");
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("---------- starting {} ----------", UpdateSpringDependenciesRunner.class.getSimpleName());
        String latestSpringBootVersion = metadataReader.getCurrentVersion();
        String azureSupportedVersion = azureCurrentVersionReader.getCurrentSupportedSpringBootVersion();
        if (!azureSupportedVersion.equals(latestSpringBootVersion)) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("spring-versions.txt"))) {
                bufferedWriter.write(latestSpringBootVersion);
                bufferedWriter.newLine();
                bufferedWriter.write(springCloudCompatibleSpringBootVersionRanges
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().match(Version.parse(latestSpringBootVersion)))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .get());
                bufferedWriter.newLine();
                bufferedWriter.write(azureSupportedVersion);
            }
        }
    }

}
