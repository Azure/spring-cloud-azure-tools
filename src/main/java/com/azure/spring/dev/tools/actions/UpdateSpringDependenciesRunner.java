package com.azure.spring.dev.tools.actions;

import com.azure.spring.dev.tools.dependency.metadata.maven.Version;
import com.azure.spring.dev.tools.dependency.metadata.maven.VersionRange;
import com.azure.spring.dev.tools.dependency.support.SpringBootReleaseNotesReader;
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
 * This Runner is used to get versions of Spring Boot and Spring Cloud for updating spring dependencies in sdk repo and
 * releaseNotes of Spring Boot fot Pr description. It will output two files "spring-versions.txt" and
 * "pr-descriptions.txt". "spring-versions.txt" contains three versions $latest_spring_boot_version,
 * $latest_spring_cloud_version and $current_azure_supported_spring_boot_version. "pr-descriptions.txt" contains Spring
 * Boot releaseNotes' information.
 */
@ConditionalOnProperty("update-spring-dependencies")
@Component
public class UpdateSpringDependenciesRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateSpringDependenciesRunner.class);
    private final SpringProjectMetadataReader metadataReader;
    private final Map<String, VersionRange> springCloudCompatibleSpringBootVersionRanges;
    private final SpringCloudAzureCurrentVersionReader azureCurrentVersionReader;
    private final SpringBootReleaseNotesReader springBootReleaseNotesReader;

    public UpdateSpringDependenciesRunner(SpringProjectMetadataReader metadataReader,
                                          SpringInitializrMetadataReader springInitializrMetadataReader,
                                          SpringCloudAzureCurrentVersionReader azureCurrentVersionReader,
                                          SpringBootReleaseNotesReader springBootReleaseNotesReader) {
        this.metadataReader = metadataReader;
        this.azureCurrentVersionReader = azureCurrentVersionReader;
        this.springCloudCompatibleSpringBootVersionRanges =
            springInitializrMetadataReader.getCompatibleSpringBootVersions("spring-cloud");
        this.springBootReleaseNotesReader = springBootReleaseNotesReader;
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("---------- starting {} ----------", UpdateSpringDependenciesRunner.class.getSimpleName());
        String latestSpringBootVersion = metadataReader.getCurrentVersion();
        String azureSupportedVersion = azureCurrentVersionReader.getCurrentSupportedSpringBootVersion();
        String releaseNotesContents = springBootReleaseNotesReader.getReleaseNotes(latestSpringBootVersion);
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
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("pr-descriptions.txt"))) {
                bufferedWriter.write(String.format("<details><summary>Release notes</summary><p><em>Sourced from <a "
                        + "href='https://github.com/spring-projects/spring-boot/releases/tag/v{}'>spring-boot "
                        + "releases</a>.</em></p>", latestSpringBootVersion));
                bufferedWriter.write(releaseNotesContents);
                bufferedWriter.write("</details>");
            }
        }
    }

}
