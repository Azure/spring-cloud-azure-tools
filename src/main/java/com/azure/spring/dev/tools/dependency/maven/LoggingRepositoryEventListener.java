package com.azure.spring.dev.tools.dependency.maven;

import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.RepositoryEvent;
import org.slf4j.Logger;

public class LoggingRepositoryEventListener extends AbstractRepositoryListener {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LoggingRepositoryEventListener.class);



    @Override
    public void artifactDownloaded(RepositoryEvent event) {
        LOGGER.debug("artifact {} downloaded to file {}", event.getArtifact(), event.getFile());
    }

    @Override
    public void artifactDownloading(RepositoryEvent event) {
        LOGGER.debug("artifact {} downloading to file {}", event.getArtifact(), event.getFile());

    }


    @Override
    public void artifactResolved(RepositoryEvent event) {
        LOGGER.debug("artifact {} resolved from repository {}", event.getArtifact(), event.getRepository());

    }

    @Override
    public void artifactResolving(RepositoryEvent event) {
        LOGGER.debug("artifact {} resolving from repository {}", event.getArtifact(), event.getRepository());

    }


}
