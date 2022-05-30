package com.azure.spring.dev.tools.dependency.configuration;

import com.azure.spring.dev.tools.dependency.support.SpringCloudAzureSupportMetadataReader;
import com.azure.spring.dev.tools.dependency.support.SpringInitializrMetadataReader;
import com.azure.spring.dev.tools.dependency.support.SpringProjectMetadataReader;
import io.spring.initializr.versionresolver.DependencyManagementVersionResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DependencyProperties.class)
public class DependencyAutoConfiguration {

    @Bean
    public DependencyManagementVersionResolver dependencyManagementVersionResolver() throws IOException {
        return DependencyManagementVersionResolver.withCacheLocation(Files.createTempDirectory("version-resolver"
            + "-cache-"));
    }

    @Bean
    public SpringProjectMetadataReader springBootReleaseMetadataReader(RestTemplate restTemplate,
                                                                       DependencyProperties properties)  {
        return new SpringProjectMetadataReader(restTemplate, properties);
    }

    @Bean
    public SpringCloudAzureSupportMetadataReader springCloudAzureSupportedMetadataReader(RestTemplate restTemplate,
                                                                                         DependencyProperties properties) {
        return new SpringCloudAzureSupportMetadataReader(restTemplate, properties);
    }

    @Bean
    public SpringInitializrMetadataReader springCloudVersionRangesMetadataReader(RestTemplate restTemplate,
                                                                                 DependencyProperties properties) {
        return new SpringInitializrMetadataReader(restTemplate, properties);
    }

}
