package com.azure.spring.dev.tools.dependency.maven;

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
import org.springframework.beans.factory.InitializingBean;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class MavenResolver implements InitializingBean {
    private static final RemoteRepository mavenCentral = new RemoteRepository.Builder("central", "default",
        "https://repo1.maven.org/maven2").build();

    private static final RemoteRepository springMilestones = new RemoteRepository.Builder("spring-milestones",
        "default", "https://repo.spring.io/milestone").build();

    private static final RemoteRepository springSnapshots = new RemoteRepository.Builder("spring-snapshots", "default",
        "https://repo.spring.io/snapshot").build();

    private static final List<RemoteRepository> repositories = Arrays.asList(mavenCentral, springMilestones,
        springSnapshots);

    private RepositorySystemSession repositorySystemSession;

    private RepositorySystem repositorySystem;
    private final Path cacheLocation;

    public MavenResolver(Path cacheLocation) {
        this.cacheLocation = cacheLocation;

    }

    public static List<RemoteRepository> getRepositories(){
        return repositories;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ServiceLocator serviceLocator = createServiceLocator();
        this.repositorySystem = serviceLocator.getService(RepositorySystem.class);
        this.repositorySystemSession = getRepositorySystemSession(cacheLocation, repositorySystem);

    }

    public DefaultRepositorySystemSession getRepositorySystemSession(Path cacheLocation,
                                                                     RepositorySystem repositorySystem) {
        DefaultRepositorySystemSession repositorySystemSession = MavenRepositorySystemUtils
            .newSession();

        LocalRepository localRepository = new LocalRepository(cacheLocation.toFile());
        repositorySystemSession.setLocalRepositoryManager(
            repositorySystem.newLocalRepositoryManager(repositorySystemSession, localRepository));
        repositorySystemSession.setArtifactDescriptorPolicy(new SimpleArtifactDescriptorPolicy(false, false));

        repositorySystemSession.setRepositoryListener(new LoggingRepositoryEventListener());
        repositorySystemSession.setReadOnly();

        return repositorySystemSession;
    }

    public RepositorySystem getRepositorySystem() {
        return repositorySystem;
    }


    public RepositorySystemSession getRepositorySystemSession() {
        return repositorySystemSession;
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
                                                             RepositorySystemSession session,
                                                             DefaultArtifact artifact) throws ArtifactDescriptorException {
        ArtifactDescriptorResult artifactDescriptorResult = repositorySystem.readArtifactDescriptor(session,
            new ArtifactDescriptorRequest(artifact, repositories, null));
        return artifactDescriptorResult.getManagedDependencies().stream();
    }
}
