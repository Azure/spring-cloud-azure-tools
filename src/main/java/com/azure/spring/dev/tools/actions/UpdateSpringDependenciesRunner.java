package com.azure.spring.dev.tools.actions;

import com.azure.spring.dev.tools.dependency.metadata.spring.ReleaseStatus;
import com.azure.spring.dev.tools.dependency.support.SpringProjectMetadataReader;
import io.spring.initializr.versionresolver.DependencyManagementVersionResolver;
import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConditionalOnProperty("update-spring-dependencies")
@Component
public class UpdateSpringDependenciesRunner implements CommandLineRunner {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UpdateSpringDependenciesRunner.class);
    private final SpringProjectMetadataReader metadataReader;
    private final DependencyManagementVersionResolver versionResolver;

    public UpdateSpringDependenciesRunner(SpringProjectMetadataReader metadataReader,
                                          DependencyManagementVersionResolver versionResolver) {
        this.metadataReader = metadataReader;
        this.versionResolver = versionResolver;
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("---------- starting {} ----------", UpdateSpringDependenciesRunner.class.getSimpleName());
        metadataReader.getProjectReleases("spring-boot").forEach(release -> {
            if (release.isSnapshot()) {
                LOGGER.info("{} is snapshot, skipping", release);
            } else {
                if (ReleaseStatus.GENERAL_AVAILABILITY == release.getReleaseStatus()) {
                    LOGGER.info("Updating {}", release);
                    Map<String, String> dependencies = versionResolver.resolve("org.springframework.boot", "spring"
                        + "-boot-dependencies", release.getVersion());
                    dependencies.forEach((key, value) -> {
                        LOGGER.info("{} -> {}", key, value);
                    });
                }
            }
        });
    }

}