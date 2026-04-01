package com.azure.spring.reference.intellij;

import com.azure.spring.reference.intellij.configurationProperties.ClientArtifactSpec;
import com.azure.spring.reference.intellij.configurationProperties.CompatibilitySpec;
import com.azure.spring.reference.intellij.configurationProperties.FeatureSpec;
import com.azure.spring.reference.intellij.configurationProperties.ReferenceConfigurationProperties;
import com.azure.spring.reference.intellij.configurationProperties.Selector;
import com.azure.spring.reference.intellij.configurationProperties.ServiceSpec;
import com.azure.spring.reference.intellij.configurationProperties.SpringArtifactSpec;
import com.azure.spring.reference.intellij.configurationProperties.VersionSpec;
import com.azure.spring.reference.intellij.enums.ArtifactType;
import com.azure.spring.reference.intellij.enums.LibraryType;
import com.azure.spring.reference.intellij.links.GitHubLink;
import com.azure.spring.reference.intellij.links.JavaDocLink;
import com.azure.spring.reference.intellij.links.Link;
import com.azure.spring.reference.intellij.links.MavenRepoLink;
import com.azure.spring.reference.intellij.links.MsDocsLink;
import com.azure.spring.reference.intellij.links.SampleLink;
import com.azure.spring.reference.intellij.records.Artifact;
import com.azure.spring.reference.intellij.records.ClientSource;
import com.azure.spring.reference.intellij.records.CompatibilityMapping;
import com.azure.spring.reference.intellij.records.Feature;
import com.azure.spring.reference.intellij.records.ServiceEntry;
import com.azure.spring.reference.intellij.records.SpringProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableConfigurationProperties(ReferenceConfigurationProperties.class)
public class SpringReferenceIntellijApplication implements CommandLineRunner {

    public static final Logger LOGGER = LoggerFactory.getLogger(SpringReferenceIntellijApplication.class);

    private final ReferenceConfigurationProperties properties;
    private final Map<Selector, List<SpringArtifactSpec>> springArtifactMap;
    private final Map<Selector, List<ClientArtifactSpec>> clientArtifactMap;
    private final Map<String, CompatibilitySpec> compatibilityMap;
    private final Map<String, VersionSpec> versionSpecMap;

    public SpringReferenceIntellijApplication(ReferenceConfigurationProperties properties) {
        this.properties = properties;

        this.properties
            .getSpringArtifacts()
            .stream()
            .filter(spec -> spec.getDescription() != null)
            .forEach(spec -> spec.setDescription(spec.getDescription().trim()));

        this.properties
            .getServices()
            .stream()
            .flatMap(serviceSpec -> serviceSpec.getFeatures().stream())
            .filter(featureSpec -> featureSpec.getDescription() != null)
            .forEach(spec -> spec.setDescription(spec.getDescription().trim()));


        this.springArtifactMap =
            properties.getSpringArtifacts()
                      .stream()
                      .collect(Collectors.groupingBy(SpringReferenceIntellijApplication::buildSelector));

        this.clientArtifactMap =
            properties.getClientArtifacts().
                      stream()
                      .collect(Collectors.groupingBy(SpringReferenceIntellijApplication::buildSelector));

        this.compatibilityMap =
            properties.getCompatibilities()
                      .stream()
                      .collect(Collectors.toMap(CompatibilitySpec::getArtifactId, Function.identity()));

        this.versionSpecMap =
            properties.getVersions()
                      .stream()
                      .collect(Collectors.toMap(VersionSpec::getArtifactId, Function.identity()));
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringReferenceIntellijApplication.class, args);
    }

    @Override
    public void run(String... args) {
        List<ServiceEntry> result = properties.getServices().stream().map(this::buildServiceEntry).toList();
        ObjectMapper objectMapper = new ObjectMapper(
            new YAMLFactory()
                //                .configure(YAMLGenerator.Feature.SPLIT_LINES, false)
                .configure(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE, true)
                .configure(YAMLGenerator.Feature.MINIMIZE_QUOTES, true)
        );
        try {
            File resultFile = new File("./spring-reference.yaml");
            if (!resultFile.exists()) {
                boolean created = resultFile.createNewFile();
                if (!created) {
                    LOGGER.info("File already exists!");
                }
            }
            try (OutputStream fos = new FileOutputStream(resultFile)) {
                String comment =
                    """
                    # Please refer to this link to see design details and how to change:
                    # https://dev.azure.com/SpringOnAzure/Spring%20on%20Azure/_wiki/wikis/spring-integration-private.wiki/280/Spring-Reference-for-Intellij-Plugin
                    """;
                fos.write(comment.getBytes(StandardCharsets.UTF_8));
                objectMapper.writeValue(fos, result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ServiceEntry buildServiceEntry(ServiceSpec serviceSpec) {
        List<Feature> features = buildFeatures(serviceSpec);
        return new ServiceEntry(serviceSpec.getName(), features);
    }

    private List<Feature> buildFeatures(ServiceSpec serviceSpec) {
        List<Feature> features = new ArrayList<>();
        List<FeatureSpec> featureSpecs = serviceSpec.getFeatures();
        for (FeatureSpec featureSpec : featureSpecs) {
            Feature feature = new Feature(
                featureSpec.getName(),
                featureSpec.getDescription(),
                featureSpec.getMsDocs(),
                findClientSource(featureSpec),
                findSpringArtifacts(featureSpec));

            features.add(feature);
        }
        return features;
    }

    private ClientSource findClientSource(FeatureSpec featureSpec) {
        for (Selector artifactSelector : featureSpec.getArtifactSelectors()) {
            Selector selector = new Selector(artifactSelector.getAzureService(),
                artifactSelector.getAzureSubService(), artifactSelector.getLabel());
            if (clientArtifactMap.containsKey(selector)) {
                List<ClientArtifactSpec> clientArtifactSpecs = clientArtifactMap.get(selector);
                if (clientArtifactSpecs.size() > 1) {
                    throw new IllegalStateException("Expect one client artifact, but found " + clientArtifactSpecs.size());
                }
                return convert(clientArtifactSpecs.get(0));
            }
        }
        return null;
    }

    private List<Artifact> findSpringArtifacts(FeatureSpec featureSpec) {
        List<Artifact> artifacts = new ArrayList<>();
        for (Selector artifactSelector : featureSpec.getArtifactSelectors()) {
            Selector selector = new Selector(artifactSelector.getAzureService(),
                artifactSelector.getAzureSubService(), artifactSelector.getLabel());
            if (springArtifactMap.containsKey(selector)) {
                springArtifactMap.get(selector).forEach(a -> artifacts.add(convert(a)));
            }
        }
        return artifacts;
    }

    private static ClientSource convert(ClientArtifactSpec clientArtifact) {
        return new ClientSource(clientArtifact.getGroupId(), clientArtifact.getArtifactId());
    }

    private Artifact convert(SpringArtifactSpec springArtifactSpec) {
        VersionSpec versionSpec = findVersion(springArtifactSpec);
        return new Artifact(
            springArtifactSpec.getGroupId(),
            springArtifactSpec.getArtifactId(),
            versionSpec.getGaVersion(),
            versionSpec.getPreviewVersion(),
            springArtifactSpec.getDescription(),
            springArtifactSpec.getLibraryType(),
            springArtifactSpec.isBom() ? ArtifactType.bom : ArtifactType.jar, springArtifactSpec.getAzureService(),
            buildLinks(springArtifactSpec, versionSpec),
            buildDependencyPattern(springArtifactSpec),
            LibraryType.client.equals(springArtifactSpec.getLibraryType()) ? null : buildSpringProperties(springArtifactSpec));
    }

    private Map<String, String> buildDependencyPattern(SpringArtifactSpec springArtifactSpec) {
        if (springArtifactSpec.isBom()) {
            Map<String, String> dependencyPatternMap = new HashMap<>();
            dependencyPatternMap.put("maven",
                """
                <dependencyManagement>
                    <dependencies>
                      <dependency>
                        <groupId>com.azure.spring</groupId>
                        <artifactId>spring-cloud-azure-dependencies</artifactId>
                        <version>${azure.version}</version>
                        <type>pom</type>
                        <scope>import</scope>
                      </dependency>
                    </dependencies>
                  </dependencyManagement>
                """);
            dependencyPatternMap.put("gradle",
                """
                dependencyManagement {
                  imports {
                    mavenBom "com.azure.spring:spring-cloud-azure-dependencies:${azure.version}"
                  }
                }
                """);
            return dependencyPatternMap;
        } else {
            return null;
        }


    }

    private VersionSpec findVersion(SpringArtifactSpec springArtifactSpec) {
        VersionSpec bomVersion = this.versionSpecMap.get(springArtifactSpec.getBomArtifact());
        VersionSpec artifactVersion = this.versionSpecMap.get(springArtifactSpec.getArtifactId());
        if (!springArtifactSpec.isV4() || !LibraryType.spring.equals(springArtifactSpec.getLibraryType()) || bomVersion == null) {
            if (artifactVersion == null) {
                throw new IllegalStateException("No version found for " + springArtifactSpec.getArtifactId());
            }
            return artifactVersion;
        }
        if (artifactVersion != null && !artifactVersion.isHasPreviewVersion()) {
            artifactVersion.setGaVersion(bomVersion.getGaVersion());
            return  artifactVersion;
        }
        return bomVersion;
    }

    private Map<String, String> buildLinks(SpringArtifactSpec springArtifactSpec, VersionSpec versionSpec) {
        List<Link> result = new ArrayList<>();
        String artifactId = springArtifactSpec.getArtifactId();
        String version = versionSpec.getGaVersion() == null ? versionSpec.getPreviewVersion() : versionSpec.getGaVersion();
        result.add(new MavenRepoLink(springArtifactSpec.getGroupId(), artifactId, version));
        if (springArtifactSpec.isV4()) {
            result.add(new GitHubLink(artifactId, version, springArtifactSpec.isBom()));
            if (springArtifactSpec.isHasSampleLink() && !springArtifactSpec.isBom()) {
                result.add(new SampleLink(artifactId, version, springArtifactSpec.getAzureService()));
            }
        } else {
            if (springArtifactSpec.getGithubLink() != null) {
                result.add(new GitHubLink(springArtifactSpec.getGithubLink()));
            } else {
                LOGGER.warn("No github link set for {}", artifactId);
            }
            if (springArtifactSpec.isHasSampleLink() && springArtifactSpec.getSampleLink() != null) {
                result.add(new SampleLink(springArtifactSpec.getSampleLink()));
            } else {
                LOGGER.warn("No sample link set for {}", artifactId);
            }
        }
        if (springArtifactSpec.getMsDocsLink() != null) {
            result.add(new MsDocsLink(springArtifactSpec.getMsDocsLink()));
        } else {
            LOGGER.warn("No ms docs link set for {}", artifactId);
        }
        if (springArtifactSpec.getJavaDocsLink() != null) {
            result.add(new JavaDocLink(springArtifactSpec.getJavaDocsLink()));
        } else if (springArtifactSpec.isContainsSourceCode()){
            result.add(new JavaDocLink(springArtifactSpec.getJavaDocsPattern().formatted(artifactId, version)));
        } else {
            LOGGER.debug("No java docs link set for {}", artifactId);
        }
        return result.stream().collect(Collectors.toMap(Link::getName, Link::getPath));
    }

    private SpringProperties buildSpringProperties(SpringArtifactSpec springArtifactSpec) {
        String artifactId = springArtifactSpec.getArtifactId();
        if (LibraryType.client.equals(springArtifactSpec.getLibraryType())) {
            LOGGER.info("No spring properties for {}", artifactId);
            return null;
        }
        boolean isBom = springArtifactSpec.isBom();


        CompatibilitySpec compatibility = null;
        if (compatibilityMap.containsKey(artifactId)) {
            compatibility = compatibilityMap.get(artifactId);
        } else if (springArtifactSpec.isV4()) {
            compatibility = compatibilityMap.get(springArtifactSpec.getBomArtifact());
        } else {
            throw new IllegalStateException("A non v4 artifact requires a compatibility spec.");
        }

        return new SpringProperties(
            !isBom,
            isBom ? null : springArtifactSpec.getBomArtifact(),
            "[%s,%s]".formatted(compatibility.getLowestSpringBootVersion(),
                compatibility.getHighestSpringBootVersion()),
            compatibility.getMappings().stream().map(m -> convert(m, springArtifactSpec)).collect(Collectors.toList()));
    }

    private CompatibilityMapping convert(CompatibilitySpec.Mapping mapping, SpringArtifactSpec springArtifactSpec) {
        return new CompatibilityMapping(
            "[%s,%s]".formatted(mapping.getLowestSpringBootVersion(), mapping.getHighestSpringBootVersion()),
            springArtifactSpec.getGroupId(),
            springArtifactSpec.getArtifactId(),
            mapping.getVersion()
        );
    }


    private static Selector buildSelector(SpringArtifactSpec artifact) {
        Selector selector = new Selector();
        selector.setAzureService(artifact.getAzureService());
        selector.setAzureSubService(artifact.getAzureSubService());
        selector.setLabel(artifact.getLabel());
        return selector;
    }

    private static Selector buildSelector(ClientArtifactSpec artifact) {
        Selector selector = new Selector();
        selector.setAzureService(artifact.getAzureService());
        selector.setAzureSubService(artifact.getAzureSubService());
        selector.setLabel(artifact.getLabel());
        return selector;
    }
}
