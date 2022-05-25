package com.azure.spring.dev.tools.dependency.metadata;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.Map;

@Data
public class BomRangesMetadata {
    @JsonAlias("codecentric-spring-boot-admin")
    private Map<String,String> codecentricSpringBootAdmin;
    @JsonAlias("solace-spring-boot")
    private Map<String,String> solaceSpringBoot;
    @JsonAlias("solace-spring-cloud")
    private Map<String,String> solaceSpringCloud;
    @JsonAlias("spring-cloud")
    private Map<String,String> springCloudRange;
    @JsonAlias("spring-cloud-azure")
    private Map<String,String> springCloudAzure;
    @JsonAlias("spring-cloud-gcp")
    private Map<String,String> springCloudGcp;
    @JsonAlias("spring-cloud-services")
    private Map<String,String> springCloudServices;
    @JsonAlias("spring-geode")
    private Map<String,String> springGeode;
    private Map<String,String> vaadin;
    private Map<String,String> wavefront;
}
