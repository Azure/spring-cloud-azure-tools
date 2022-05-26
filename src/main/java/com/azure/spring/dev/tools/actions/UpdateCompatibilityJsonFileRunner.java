package com.azure.spring.dev.tools.actions;

import com.azure.spring.dev.tools.dependency.SpringBootReleaseMetadataReader;
import com.azure.spring.dev.tools.dependency.SpringCloudVersionRangesMetadataReader;
import com.azure.spring.dev.tools.dependency.metadata.SpringCloudAzureSupportedSpring;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.versionresolver.DependencyManagementVersionResolver;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class UpdateCompatibilityJsonFileRunner implements CommandLineRunner {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UpdateCompatibilityJsonFileRunner.class);
    private final SpringBootReleaseMetadataReader metadataReader;
    private final DependencyManagementVersionResolver versionResolver;
    private final SpringCloudVersionRangesMetadataReader versionMatrixMetadataReader;

    public UpdateCompatibilityJsonFileRunner(SpringBootReleaseMetadataReader metadataReader,
                                             DependencyManagementVersionResolver versionResolver,
                                             SpringCloudVersionRangesMetadataReader versionMatrixMetadataReader) {
        this.metadataReader = metadataReader;
        this.versionResolver = versionResolver;
        this.versionMatrixMetadataReader = versionMatrixMetadataReader;
    }

    @Override
    public void run(String... args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<SpringCloudAzureSupportedSpring> jsonList = new ArrayList<>();
        Map<String, DefaultArtifactVersion> springCloudRange = new LinkedHashMap<>();
        versionMatrixMetadataReader.getSpringCloudRange().forEach((cv, bv) -> {
            springCloudRange.put(cv, new DefaultArtifactVersion(bv.split("<")[1]));
        });
        metadataReader.getProjectReleases("spring-boot").forEach(release -> {
            SpringCloudAzureSupportedSpring springCloudAzureSupportedSpring = new SpringCloudAzureSupportedSpring();
            springCloudAzureSupportedSpring.setCurrent(release.isCurrent());
            springCloudAzureSupportedSpring.setSpringBootVersion(release.getVersion());
            springCloudAzureSupportedSpring.setReleaseStatus(release.getReleaseStatus());
            springCloudAzureSupportedSpring.setSnapshot(release.isSnapshot());
            for (Map.Entry<String, DefaultArtifactVersion> entry : springCloudRange.entrySet()) {
                String cv = entry.getKey();
                DefaultArtifactVersion bv = entry.getValue();
                if (new DefaultArtifactVersion(release.getVersion()).compareTo(bv) == -1) {
                    springCloudAzureSupportedSpring.setSpringCloudVersion(cv);
                    break;
                }
            }
            jsonList.add(springCloudAzureSupportedSpring);
        });
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonList));

    }


}
