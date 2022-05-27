package com.azure.spring.dev.tools.actions;

import com.azure.spring.dev.tools.dependency.SpringBootReleaseMetadataReader;
import com.azure.spring.dev.tools.dependency.SpringCloudAzureSupportedMetadataReader;
import com.azure.spring.dev.tools.dependency.SpringCloudVersionRangesMetadataReader;
import com.azure.spring.dev.tools.dependency.metadata.SpringCloudAzureSupportedSpring;
import com.azure.spring.dev.tools.dependency.metadata.SupportStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.versionresolver.DependencyManagementVersionResolver;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class UpdateCompatibilityJsonFileRunner implements CommandLineRunner {
    private final SpringBootReleaseMetadataReader metadataReader;
    private final DependencyManagementVersionResolver versionResolver;
    private final SpringCloudVersionRangesMetadataReader versionMatrixMetadataReader;
    private final SpringCloudAzureSupportedMetadataReader azureSupportedMetadataReader;

    public UpdateCompatibilityJsonFileRunner(SpringBootReleaseMetadataReader metadataReader,
                                             DependencyManagementVersionResolver versionResolver,
                                             SpringCloudVersionRangesMetadataReader versionMatrixMetadataReader,
                                             SpringCloudAzureSupportedMetadataReader azureSupportedMetadataReader) {
        this.metadataReader = metadataReader;
        this.versionResolver = versionResolver;
        this.versionMatrixMetadataReader = versionMatrixMetadataReader;
        this.azureSupportedMetadataReader = azureSupportedMetadataReader;
    }

    @Override
    public void run(String... args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<SpringCloudAzureSupportedSpring> jsonList = new ArrayList<>();
        Map<String, DefaultArtifactVersion> springCloudRange = versionMatrixMetadataReader.getSpringCloudRange();
        Map<String, SupportStatus> azureSupportStatus = azureSupportedMetadataReader.getAzureSupportStatus();
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
            azureSupportStatus.forEach((bv,ss) -> {
                if(springCloudAzureSupportedSpring.getSpringBootVersion().equals(bv)) {
                    springCloudAzureSupportedSpring.setSupportStatus(ss);
                }
            });
            jsonList.add(springCloudAzureSupportedSpring);
        });
        FileWriter fileWriter = new FileWriter("updated.json");
        fileWriter.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonList));
        fileWriter.close();
    }


}
