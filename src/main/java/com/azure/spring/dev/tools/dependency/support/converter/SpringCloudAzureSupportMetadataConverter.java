package com.azure.spring.dev.tools.dependency.support.converter;

import com.azure.spring.dev.tools.dependency.metadata.azure.SpringCloudAzureSupportMetadata;
import com.azure.spring.dev.tools.dependency.metadata.spring.ProjectRelease;
import org.springframework.core.convert.converter.Converter;

public class SpringCloudAzureSupportMetadataConverter implements Converter<ProjectRelease, SpringCloudAzureSupportMetadata> {

    public static final SpringCloudAzureSupportMetadataConverter CONVERTER = new SpringCloudAzureSupportMetadataConverter();

    private SpringCloudAzureSupportMetadataConverter() {

    }

    @Override
    public SpringCloudAzureSupportMetadata convert(ProjectRelease source) {
        SpringCloudAzureSupportMetadata springCloudAzureSupportMetadata = new SpringCloudAzureSupportMetadata();
        springCloudAzureSupportMetadata.setCurrent(source.isCurrent());
        springCloudAzureSupportMetadata.setSpringBootVersion(source.getVersion());
        springCloudAzureSupportMetadata.setReleaseStatus(source.getReleaseStatus());
        springCloudAzureSupportMetadata.setSnapshot(source.isSnapshot());

        return springCloudAzureSupportMetadata;
    }

}
