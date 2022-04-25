package com.azure.spring.dev.tools.dependency;

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
    public SpringBootReleaseMetadataReader springBootReleaseMetadataReader(RestTemplate restTemplate,
                                                                           DependencyProperties properties)  {
        return new SpringBootReleaseMetadataReader(restTemplate, properties);
    }

}
