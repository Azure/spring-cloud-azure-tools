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
            azureSupportMetadataReader, null, false);
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
    void testRetainsLastKnownSpringCloudVersionWhenInitializrDropsGeneration() {
        // Initializr no longer matches the still-SUPPORTED 3.5.16, so it keeps its recorded Spring Cloud version.
        UpdateSpringCloudAzureSupportFileRunner runnerUnderTest = createRunner(springCloudRangesFor4x(),
            List.of(createSupportMetadata("3.5.16", "2025.0.3", SupportStatus.SUPPORTED)));

        Assertions.assertEquals("2025.0.3", runnerUnderTest.findCompatibleSpringCloudVersion("3.5.16"));
    }

    @Test
    void testPrefersInitializrMatchOverLastKnownSpringCloudVersion() {
        // An Initializr match still wins over the recorded value (auto-upgrade preserved, no regression).
        Map<String, VersionRange> ranges = Collections.singletonMap("2025.0.4",
            new VersionRange(Version.parse("3.5.0"), true, Version.parse("3.6.0-M1"), false));
        UpdateSpringCloudAzureSupportFileRunner runnerUnderTest = createRunner(ranges,
            List.of(createSupportMetadata("3.5.16", "2025.0.3", SupportStatus.SUPPORTED)));

        Assertions.assertEquals("2025.0.4", runnerUnderTest.findCompatibleSpringCloudVersion("3.5.16"));
    }

    @Test
    void testFallsBackToHighestSpringCloudVersionFromSameMinorLine() {
        // A new patch (3.5.17) with no entry and no Initializr match reuses the highest known 3.5.x value.
        UpdateSpringCloudAzureSupportFileRunner runnerUnderTest = createRunner(springCloudRangesFor4x(),
            List.of(
                createSupportMetadata("3.5.16", "2025.0.3", SupportStatus.SUPPORTED),
                createSupportMetadata("3.5.15", "2025.0.3", SupportStatus.END_OF_LIFE),
                createSupportMetadata("3.5.14", "2025.0.2", SupportStatus.END_OF_LIFE)));

        Assertions.assertEquals("2025.0.3", runnerUnderTest.findCompatibleSpringCloudVersion("3.5.17"));
    }

    @Test
    void testReturnsNoneWhenNoSameMinorLineIsKnown() {
        // No Initializr match and no recorded 3.5.x entry to fall back to, so the result stays NONE.
        UpdateSpringCloudAzureSupportFileRunner runnerUnderTest = createRunner(springCloudRangesFor4x(),
            List.of(createSupportMetadata("4.0.7", "2025.1.2", SupportStatus.SUPPORTED)));

        Assertions.assertEquals(UpdateSpringCloudAzureSupportFileRunner.NONE_SUPPORTED_VERSION,
            runnerUnderTest.findCompatibleSpringCloudVersion("3.5.99"));
    }

    @Test
    void testEndOfLifeVersionKeepsRecordedSpringCloudVersion() {
        // END_OF_LIFE versions always keep their recorded Spring Cloud version, regardless of Initializr.
        UpdateSpringCloudAzureSupportFileRunner runnerUnderTest = createRunner(springCloudRangesFor4x(),
            List.of(createSupportMetadata("3.4.0", "2023.0.5", SupportStatus.END_OF_LIFE)));

        Assertions.assertEquals("2023.0.5", runnerUnderTest.findCompatibleSpringCloudVersion("3.4.0"));
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

        Assertions.assertEquals(lists.get(0).getSupportStatus(), SupportStatus.SUPPORTED);
    }

    @Test
    void testIsVersionSupported() {
        // Test versions that should be supported (3.5.0 or above)
        Assertions.assertTrue(runner.isVersionSupported("3.5.0"));
        Assertions.assertTrue(runner.isVersionSupported("3.5.1"));
        Assertions.assertTrue(runner.isVersionSupported("3.5.12"));
        Assertions.assertTrue(runner.isVersionSupported("3.6.0"));
        Assertions.assertTrue(runner.isVersionSupported("4.0.0"));

        // Test versions that should not be supported (below 3.5.0)
        Assertions.assertFalse(runner.isVersionSupported("3.4.9"));
        Assertions.assertFalse(runner.isVersionSupported("3.4.0"));
        Assertions.assertFalse(runner.isVersionSupported("3.3.13"));
        Assertions.assertFalse(runner.isVersionSupported("2.7.18"));
    }

    @Test
    void testIsSnapshotOrMilestone() {
        SpringCloudAzureSupportMetadata metadata = new SpringCloudAzureSupportMetadata();
        metadata.setSpringBootVersion("3.5.0");
        Assertions.assertFalse(runner.isSnapshotOrMilestoneOrRC(metadata));
        metadata.setSpringBootVersion("3.5.0-SNAPSHOT");
        Assertions.assertTrue(runner.isSnapshotOrMilestoneOrRC(metadata));
        metadata.setSpringBootVersion("3.5.0-M1");
        Assertions.assertTrue(runner.isSnapshotOrMilestoneOrRC(metadata));
        metadata.setSpringBootVersion("3.5.0-RC1");
        Assertions.assertTrue(runner.isSnapshotOrMilestoneOrRC(metadata));
    }

    @Test
    void testIsSnapshotOrMilestoneWhenRcIncluded() {
        UpdateSpringCloudAzureSupportFileRunner runnerWithRc = new UpdateSpringCloudAzureSupportFileRunner(null,
            springInitializrMetadataReader, azureSupportMetadataReader, null, true);

        SpringCloudAzureSupportMetadata metadata = new SpringCloudAzureSupportMetadata();
        metadata.setSpringBootVersion("3.5.0-RC1");
        Assertions.assertFalse(runnerWithRc.isSnapshotOrMilestoneOrRC(metadata));

        metadata.setSpringBootVersion("3.5.0-SNAPSHOT");
        Assertions.assertTrue(runnerWithRc.isSnapshotOrMilestoneOrRC(metadata));

        metadata.setSpringBootVersion("3.5.0-M1");
        Assertions.assertTrue(runnerWithRc.isSnapshotOrMilestoneOrRC(metadata));
    }

    private UpdateSpringCloudAzureSupportFileRunner createRunner(Map<String, VersionRange> springCloudRanges,
                                                                 List<SpringCloudAzureSupportMetadata> supportMetadataList) {
        when(this.springInitializrMetadataReader.getCompatibleSpringBootVersions("spring-cloud"))
            .thenReturn(springCloudRanges);
        when(this.azureSupportMetadataReader.getAzureSupportMetadata()).thenReturn(supportMetadataList);
        return new UpdateSpringCloudAzureSupportFileRunner(null, springInitializrMetadataReader,
            azureSupportMetadataReader, null, false);
    }

    private static SpringCloudAzureSupportMetadata createSupportMetadata(String springBootVersion,
                                                                         String springCloudVersion,
                                                                         SupportStatus supportStatus) {
        SpringCloudAzureSupportMetadata metadata = new SpringCloudAzureSupportMetadata();
        metadata.setSpringBootVersion(springBootVersion);
        metadata.setSpringCloudVersion(springCloudVersion);
        metadata.setSupportStatus(supportStatus);
        return metadata;
    }

    private static Map<String, VersionRange> springCloudRangesFor4x() {
        // Mirrors the current start.spring.io payload, which only exposes the Spring Boot 4.x compatible train.
        return Collections.singletonMap("2025.1.2",
            new VersionRange(Version.parse("4.0.0"), true, Version.parse("4.2.0-M1"), false));
    }
}
