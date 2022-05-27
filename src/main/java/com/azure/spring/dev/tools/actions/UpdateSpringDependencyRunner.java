package com.azure.spring.dev.tools.actions;

import com.azure.spring.dev.tools.dependency.SpringBootReleaseMetadataReader;
import com.azure.spring.dev.tools.dependency.metadata.ReleaseStatus;
import io.spring.initializr.versionresolver.DependencyManagementVersionResolver;
import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UpdateSpringDependencyRunner implements CommandLineRunner {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UpdateSpringDependencyRunner.class);
    private final SpringBootReleaseMetadataReader metadataReader;
    private final DependencyManagementVersionResolver versionResolver;

    public UpdateSpringDependencyRunner(SpringBootReleaseMetadataReader metadataReader,
                                        DependencyManagementVersionResolver versionResolver) {
        this.metadataReader = metadataReader;
        this.versionResolver = versionResolver;
    }

    @Override
    public void run(String... args) throws Exception {
        boolean update = false;
        if (update) {
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


}
