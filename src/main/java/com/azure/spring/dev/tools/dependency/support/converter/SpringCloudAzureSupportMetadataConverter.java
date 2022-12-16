package com.azure.spring.dev.tools.dependency.support.converter;

import com.azure.spring.dev.tools.dependency.metadata.azure.SpringCloudAzureSupportMetadata;
import com.azure.spring.dev.tools.dependency.metadata.spring.ProjectRelease;
import com.azure.spring.dev.tools.dependency.metadata.spring.ReleaseStatus;
import org.springframework.core.convert.converter.Converter;

/**
 * Converter to convert SpringCloudAzureSupportMetadata
 */
public class SpringCloudAzureSupportMetadataConverter implements Converter<ProjectRelease, SpringCloudAzureSupportMetadata> {

    public static final SpringCloudAzureSupportMetadataConverter CONVERTER = new SpringCloudAzureSupportMetadataConverter();

    private SpringCloudAzureSupportMetadataConverter() {

    }

    /**
     * Get information and convert to SpringCloudAzureSupportMetadata
     * @param source of Spring information
     * @return SpringCloudAzureSupportMetadata
     */
    @Override
    public SpringCloudAzureSupportMetadata convert(ProjectRelease source) {
        SpringCloudAzureSupportMetadata springCloudAzureSupportMetadata = new SpringCloudAzureSupportMetadata();
        springCloudAzureSupportMetadata.setCurrent(false);
        springCloudAzureSupportMetadata.setSpringBootVersion(source.getVersion());
        springCloudAzureSupportMetadata.setReleaseStatus(source.getReleaseStatus());
        if (source.getReleaseStatus().equals(ReleaseStatus.GENERAL_AVAILABILITY) &&
            source.getVersion().matches("2\\.7\\.\\d+")) {
            springCloudAzureSupportMetadata.setCurrent(true);
        }
        springCloudAzureSupportMetadata.setSnapshot(source.isSnapshot());

        return springCloudAzureSupportMetadata;
    }

}
