package com.azure.spring.dev.tools.dependency.configuration;

import com.azure.spring.dev.tools.dependency.support.SpringCloudAzureCurrentVersionReader;
import com.azure.spring.dev.tools.dependency.support.SpringCloudAzureSupportMetadataReader;
import com.azure.spring.dev.tools.dependency.support.SpringInitializrMetadataReader;
import com.azure.spring.dev.tools.dependency.support.SpringProjectMetadataReader;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                                                                                         DependencyProperties properties,
                                                                                         ObjectMapper objectMapper) {
        return new SpringCloudAzureSupportMetadataReader(restTemplate, properties, objectMapper);
    }

    @Bean
    public SpringInitializrMetadataReader springCloudVersionRangesMetadataReader(RestTemplate restTemplate,
                                                                                 DependencyProperties properties) {
        return new SpringInitializrMetadataReader(restTemplate, properties);
    }

    @Bean
    public SpringCloudAzureCurrentVersionReader springCloudAzureCurrentVersionReader(RestTemplate restTemplate,
                                                                                     DependencyProperties properties) {
        return new SpringCloudAzureCurrentVersionReader(restTemplate, properties);
    }

}
