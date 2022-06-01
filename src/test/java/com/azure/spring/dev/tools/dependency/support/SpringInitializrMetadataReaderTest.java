package com.azure.spring.dev.tools.dependency.support;

import com.azure.spring.dev.tools.dependency.metadata.maven.VersionRange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SpringInitializrMetadataReaderTest {

    private final SpringInitializrMetadataReader springInitializrMetadataReader = new SpringInitializrMetadataReader(null, null);

    @Test
    void lowerInclusive() {
        VersionRange versionRange = springInitializrMetadataReader.parseVersionRange("Spring Boot >=2.2.0.RELEASE and"
            + " <2.4.0.M1");
        Assertions.assertEquals("[2.2.0.RELEASE,2.4.0.M1)", versionRange.toRangeString());
    }

    @Test
    void lowerExclusive() {
        VersionRange versionRange = springInitializrMetadataReader.parseVersionRange("Spring Boot >2.2.0.RELEASE and"
            + " <2.4.0.M1");
        Assertions.assertEquals("(2.2.0.RELEASE,2.4.0.M1)", versionRange.toRangeString());
    }

    @Test
    void higherInclusive() {
        VersionRange versionRange = springInitializrMetadataReader.parseVersionRange("Spring Boot >2.2.0.RELEASE and"
            + " <=2.4.0.M1");
        Assertions.assertEquals("(2.2.0.RELEASE,2.4.0.M1]", versionRange.toRangeString());
    }

    @Test
    void higherExclusive() {
        VersionRange versionRange = springInitializrMetadataReader.parseVersionRange("Spring Boot >2.2.0.RELEASE and"
            + " <2.4.0.M1");
        Assertions.assertEquals("(2.2.0.RELEASE,2.4.0.M1)", versionRange.toRangeString());
    }

}
