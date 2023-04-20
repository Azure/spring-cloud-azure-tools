package com.azure.spring.dev.tools.actions;

import com.azure.spring.dev.tools.dependency.metadata.azure.SpringCloudAzureSupportMetadata;
import com.azure.spring.dev.tools.dependency.metadata.maven.Version;
import com.azure.spring.dev.tools.dependency.metadata.maven.VersionRange;
import com.azure.spring.dev.tools.dependency.support.SpringCloudAzureSupportMetadataReader;
import com.azure.spring.dev.tools.dependency.support.SpringInitializrMetadataReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdateSpringCloudAzureSupportFileRunnerTest {

    private final SpringInitializrMetadataReader springInitializrMetadataReader =
        mock(SpringInitializrMetadataReader.class);
    private final SpringCloudAzureSupportMetadataReader azureSupportMetadataReader =
        mock(SpringCloudAzureSupportMetadataReader.class);
    private final Map<String, VersionRange> ranges = Collections.singletonMap("2022.0.0-M1",
        new VersionRange(Version.parse("3.0.0-M1"), true, Version.parse("3.0.0-M2"), false));
    private UpdateSpringCloudAzureSupportFileRunner runner = null;

    @BeforeEach
    void before() {
        MockitoAnnotations.openMocks(this);
        when(this.springInitializrMetadataReader.getCompatibleSpringBootVersions("spring-cloud")).thenReturn(ranges);
        when(this.azureSupportMetadataReader.getAzureSupportMetadata()).thenReturn(List.of(new SpringCloudAzureSupportMetadata()));
        runner = new UpdateSpringCloudAzureSupportFileRunner(null, springInitializrMetadataReader,
            azureSupportMetadataReader, null);
    }

    @Test
    void testFindCompatibleSpringCloudVersion() {
        Assertions.assertEquals("2022.0.0-M1", runner.findCompatibleSpringCloudVersion("3.0.0-M1"));
    }

    @Test
    void testNotFindCompatibleSpringCloudVersion() {
        Assertions.assertEquals(UpdateSpringCloudAzureSupportFileRunner.NONE_SUPPORTED_VERSION,
            runner.findCompatibleSpringCloudVersion("2.5.14"));
    }
}
