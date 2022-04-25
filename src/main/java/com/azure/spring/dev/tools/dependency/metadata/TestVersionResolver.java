package com.azure.spring.dev.tools.dependency.metadata;

import io.spring.initializr.versionresolver.DependencyManagementVersionResolver;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.internal.impl.DefaultRepositorySystem;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.repository.SimpleArtifactDescriptorPolicy;
import org.yaml.snakeyaml.Yaml;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestVersionResolver {
    private static final RemoteRepository mavenCentral = new RemoteRepository.Builder("central", "default",
        "https://repo1.maven.org/maven2").build();

    private static final RemoteRepository springMilestones = new RemoteRepository.Builder("spring-milestones",
        "default", "https://repo.spring.io/milestone").build();

    private static final RemoteRepository springSnapshots = new RemoteRepository.Builder("spring-snapshots", "default",
        "https://repo.spring.io/snapshot").build();

    private static final List<RemoteRepository> repositories = Arrays.asList(mavenCentral, springMilestones,
        springSnapshots);

    public static void main(String[] args) throws Exception {
        /**
         * SPRING_BOOT_VERSION = '2.6.5'
         * SPRING_CLOUD_VERSION = '2021.0.1'
         *
         * ROOT_POMS = [
         *     'org.springframework.boot:spring-boot-starter-parent;{}'.format(SPRING_BOOT_VERSION),
         *     'org.springframework.boot:spring-boot-dependencies;{}'.format(SPRING_BOOT_VERSION),
         *     'org.springframework.cloud:spring-cloud-dependencies;{}'.format(SPRING_CLOUD_VERSION)
         * ]
         */
        String groupId = "org.springframework.boot";
        String artifactId = "spring-boot-dependencies";
        String version ="3.0.0-M2";// "2.6.5";
        String springCloudVersion = "2022.0.0-M2";
        String springCloudVersionSpringBoot2= "2021.0.1";

        Path cacheLocation = Paths.get("~/ss"); // Paths.get("~/.m2/repository");//Files.createTempDirectory("version-resolver-cache-");
        System.out.println("Cache location: " + cacheLocation);
        ServiceLocator serviceLocator = createServiceLocator();
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        session.setArtifactDescriptorPolicy(new SimpleArtifactDescriptorPolicy(false, false));
        LocalRepository localRepository = new LocalRepository(cacheLocation.toFile());
        RepositorySystem repositorySystem = serviceLocator.getService(RepositorySystem.class);
        session.setLocalRepositoryManager(repositorySystem.newLocalRepositoryManager(session, localRepository));
        session.setReadOnly();

        DefaultArtifact springBootParent = new DefaultArtifact(groupId + ":spring-boot-starter-parent:" + version);
        DefaultArtifact springBootDependencies = new DefaultArtifact(groupId + ":spring-boot-dependencies:" + version);
        DefaultArtifact springCloudDependencies = new DefaultArtifact("org.springframework"
            + ".cloud:spring-cloud-dependencies:"+springCloudVersion);


        List<String> generated = Stream.of(springBootParent, springBootDependencies, springCloudDependencies)
                                       .flatMap(artifact -> {
                                           try {
                                               return getManagedDependencies(repositorySystem, session, artifact);
                                           } catch (ArtifactDescriptorException e) {
                                               throw new RuntimeException(e);
                                           }
                                       })
                                       .map(Dependency::getArtifact)
                                       .map(d -> String.format("%s:%s;%s", d.getGroupId(), d.getArtifactId(),
                                           d.getVersion()))
                                       .distinct()
                                       .sorted()
                                       //            .forEach(System.out::println);

                                       .collect(Collectors.toList());

        generated.forEach(System.out::println);
    }

    private static ServiceLocator createServiceLocator() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositorySystem.class, DefaultRepositorySystem.class);
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        return locator;
    }

    private static Stream<Dependency> getManagedDependencies(RepositorySystem repositorySystem,
                                                             RepositorySystemSession session, DefaultArtifact artifact) throws ArtifactDescriptorException {
        ArtifactDescriptorResult artifactDescriptorResult = repositorySystem.readArtifactDescriptor(session,
            new ArtifactDescriptorRequest(artifact, repositories, null));
        return artifactDescriptorResult.getManagedDependencies().stream();
    }
}
