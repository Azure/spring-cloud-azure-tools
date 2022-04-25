package com.azure.spring.dev.tools.poc;


import com.azure.spring.dev.tools.dependency.maven.MavenResolver;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingResult;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/***
 * Idea
 *
 * 1. generate pom from https://start.spring.io with its api, we can specify which version of spring boot and spring
 * cloud is required
 * 2. parse the pom and get the dependencies
 * 3. we use this dependencies to test our code
 */
public class TestParseMavenPom {

    public static void main(String[] args) throws Exception {
        Path cacheLocation = Paths.get("~/.m2/repository");//Files.createTempDirectory("version-resolver-cache-");

        MavenResolver resolver = new MavenResolver(cacheLocation);
        resolver.afterPropertiesSet();

        File projectPomFile = Paths.get("/Users/stliu/pom.xml").toAbsolutePath().toFile();
        DefaultModelBuildingRequest modelBuildingRequest = new DefaultModelBuildingRequest()
            .setPomFile(projectPomFile)
            .setProcessPlugins(false)
            .setTwoPhaseBuilding(false)
            ;

        ModelBuilder modelBuilder = new DefaultModelBuilderFactory().newInstance();


        ModelBuildingResult modelBuildingResult = modelBuilder.build(modelBuildingRequest);

        Model model = modelBuildingResult.getEffectiveModel();
        System.out.printf("Maven model resolved: %s, parsing its dependencies..\n", model);
        model.getDependencies().forEach(d -> {
            System.out.printf("processing dependency: %s\n", d);
            Artifact artifact = new DefaultArtifact(d.getGroupId(), d.getArtifactId(), d.getType(),
                d.getVersion());
            ArtifactRequest artifactRequest = new ArtifactRequest();
            artifactRequest.setArtifact(artifact);
            artifactRequest.setRepositories(MavenResolver.getRepositories());

            try {
                ArtifactResult artifactResult = resolver.getRepositorySystem()
                                                        .resolveArtifact(resolver.getRepositorySystemSession(),
                                                            artifactRequest);
                artifact = artifactResult.getArtifact();
                System.out.printf("artifact %s resolved to %s\n", artifact, artifact.getFile());
            } catch (ArtifactResolutionException e) {
                System.err.printf("error resolving artifact: %s\n", e.getMessage());
            }
        });

    }
}
