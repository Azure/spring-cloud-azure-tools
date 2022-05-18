package com.azure.spring.dev.tools.actions;

import com.azure.spring.dev.tools.dependency.SpringBootReleaseMetadataReader;
import com.azure.spring.dev.tools.dependency.SupportedVersionMatrixMetadataReader;
import com.azure.spring.dev.tools.dependency.metadata.ReleaseStatus;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import io.spring.initializr.versionresolver.DependencyManagementVersionResolver;
import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;

@Component
public class UpdateSpringDependencyRunner implements CommandLineRunner {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UpdateSpringDependencyRunner.class);
    private final SpringBootReleaseMetadataReader metadataReader;
    private final DependencyManagementVersionResolver versionResolver;
    private final SupportedVersionMatrixMetadataReader versionMatrixMetadataReader;

    public UpdateSpringDependencyRunner(SpringBootReleaseMetadataReader metadataReader,
                                        DependencyManagementVersionResolver versionResolver,
                                        SupportedVersionMatrixMetadataReader versionMatrixMetadataReader) {
        this.metadataReader = metadataReader;
        this.versionResolver = versionResolver;
        this.versionMatrixMetadataReader = versionMatrixMetadataReader;
    }

    @Override
    public void run(String... args) throws Exception {
        ArrayList<String> releasedList = new ArrayList<>();
        metadataReader.getProjectReleases("spring-boot").forEach(release -> {
            if (release.isSnapshot()) {
                LOGGER.info("{} is snapshot, skipping", release);

            } else {
                if (ReleaseStatus.GENERAL_AVAILABILITY == release.getReleaseStatus()) {
                    LOGGER.info("Updating {}", release);
                    releasedList.add(release.getVersion());

//                    Map<String, String> dependencies = versionResolver.resolve("org.springframework.boot", "spring"
//                        + "-boot-dependencies", release.getVersion());
//                    dependencies.forEach((key, value) -> {
//                        LOGGER.info("{} -> {}", key, value);
//                    });
                }
            }

        });

        ArrayList<String> testedList = versionMatrixMetadataReader.getTestedList(releasedList);
        versionMatrixMetadataReader.UpdateMatrixJson(testedList);
        JsonNode springCloudRange = versionMatrixMetadataReader.getSpringCloudRange();
        versionMatrixMetadataReader.UpdateVersionManagement(springCloudRange, testedList);

    }


}
