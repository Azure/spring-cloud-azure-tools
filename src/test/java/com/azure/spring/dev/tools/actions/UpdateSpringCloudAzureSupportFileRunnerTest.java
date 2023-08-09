package com.azure.spring.dev.tools.actions;

import com.azure.spring.dev.tools.dependency.metadata.azure.SpringCloudAzureSupportMetadata;
import com.azure.spring.dev.tools.dependency.metadata.azure.SupportStatus;
import com.azure.spring.dev.tools.dependency.metadata.maven.Version;
import com.azure.spring.dev.tools.dependency.metadata.maven.VersionRange;
import com.azure.spring.dev.tools.dependency.metadata.spring.ReleaseStatus;
import com.azure.spring.dev.tools.dependency.support.SpringCloudAzureSupportMetadataReader;
import com.azure.spring.dev.tools.dependency.support.SpringInitializrMetadataReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
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

    @Test
    void testSetNewStatusWithSupport() {
        SpringCloudAzureSupportMetadata metadata = new SpringCloudAzureSupportMetadata();
        metadata.setReleaseStatus(ReleaseStatus.GENERAL_AVAILABILITY);
        List<SpringCloudAzureSupportMetadata> lists = List.of(metadata);

        runner.setNewStatus(lists);

        Assertions.assertEquals(lists.get(0).getSupportStatus(), SupportStatus.SUPPORTED);
    }

    @Test
    void testSetNewStatusWithTodo() {
        SpringCloudAzureSupportMetadata metadata = new SpringCloudAzureSupportMetadata();
        metadata.setReleaseStatus(ReleaseStatus.PRERELEASE);
        List<SpringCloudAzureSupportMetadata> lists = List.of(metadata);

        runner.setNewStatus(lists);

        Assertions.assertEquals(lists.get(0).getSupportStatus(), SupportStatus.TODO);
    }

    @Test
    void testMaintainVersions() {
        SpringCloudAzureSupportMetadata data1 = new SpringCloudAzureSupportMetadata();
        data1.setSpringBootVersion("2.5.15");
        SpringCloudAzureSupportMetadata data2 = new SpringCloudAzureSupportMetadata();
        data2.setSpringBootVersion("2.6.15");
        SpringCloudAzureSupportMetadata data3 = new SpringCloudAzureSupportMetadata();
        data3.setSpringBootVersion("2.7.15");
        Map<String, SpringCloudAzureSupportMetadata> map = Map.of("2.5.15", data1, "2.6.15", data2, "2.7.15", data3);
        List<SpringCloudAzureSupportMetadata> lists = new ArrayList<>();

        runner.maintainVersions(lists, map);

        Assertions.assertTrue(lists.contains(data1));
        Assertions.assertTrue(lists.contains(data2));
        Assertions.assertFalse(lists.contains(data3));
    }
}
