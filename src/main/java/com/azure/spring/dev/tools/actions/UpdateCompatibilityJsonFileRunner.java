package com.azure.spring.dev.tools.actions;

import com.azure.spring.dev.tools.dependency.DependencyProperties;
import com.azure.spring.dev.tools.dependency.SpringBootReleaseMetadataReader;
import com.azure.spring.dev.tools.dependency.SpringCloudAzureSupportedMetadataReader;
import com.azure.spring.dev.tools.dependency.SpringCloudVersionRangesMetadataReader;
import com.azure.spring.dev.tools.dependency.metadata.SpringCloudAzureSupportedSpring;
import com.azure.spring.dev.tools.dependency.metadata.SupportStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.versionresolver.DependencyManagementVersionResolver;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class UpdateCompatibilityJsonFileRunner {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        DependencyProperties properties = new DependencyProperties();
        SpringBootReleaseMetadataReader metadataReader = new SpringBootReleaseMetadataReader(restTemplate,properties);
        SpringCloudVersionRangesMetadataReader versionMatrixMetadataReader = new SpringCloudVersionRangesMetadataReader(restTemplate,properties);
        SpringCloudAzureSupportedMetadataReader azureSupportedMetadataReader = new SpringCloudAzureSupportedMetadataReader();
        ObjectMapper mapper = new ObjectMapper();
        List<SpringCloudAzureSupportedSpring> jsonList = new ArrayList<>();
        Map<String, DefaultArtifactVersion> springCloudRange = versionMatrixMetadataReader.getSpringCloudRange();
        Map<String, SupportStatus> azureSupportStatus = azureSupportedMetadataReader.getAzureSupportStatus();
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
