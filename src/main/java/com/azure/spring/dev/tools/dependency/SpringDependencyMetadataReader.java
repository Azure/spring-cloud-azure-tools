package com.azure.spring.dev.tools.dependency;

import com.azure.spring.dev.tools.dependency.metadata.DefaultMetadataElement;
import com.azure.spring.dev.tools.dependency.metadata.Version;
import com.azure.spring.dev.tools.dependency.metadata.VersionParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SpringDependencyMetadataReader {
    private static final Comparator<DefaultMetadataElement> VERSION_METADATA_ELEMENT_COMPARATOR = new VersionMetadataElementComparator();

    private final JsonNode content;

    /**
     * Parse the content of the metadata at the specified url.
     * @param objectMapper the object mapper
     * @param restTemplate the rest template
     * @param url the metadata URL
     * @throws IOException on load error
     */
    public SpringDependencyMetadataReader(ObjectMapper objectMapper, RestTemplate restTemplate, String url) throws IOException {
        this.content = objectMapper.readTree(restTemplate.getForObject(url, String.class));
    }

    /**
     * Return the boot versions parsed by this instance.
     * @return the versions
     */
    public List<DefaultMetadataElement> getBootVersions() {
        ArrayNode releases = (ArrayNode) this.content.get("projectReleases");
        List<DefaultMetadataElement> list = new ArrayList<>();
        for (JsonNode node : releases) {
            DefaultMetadataElement versionMetadata = parseVersionMetadata(node);
            if (versionMetadata != null) {
                list.add(versionMetadata);
            }
        }
        list.sort(VERSION_METADATA_ELEMENT_COMPARATOR.reversed());
        return list;
    }

    private DefaultMetadataElement parseVersionMetadata(JsonNode node) {
        String versionId = node.get("version").textValue();
        Version version = VersionParser.DEFAULT.safeParse(versionId);
        if (version == null) {
            return null;
        }
        DefaultMetadataElement versionMetadata = new DefaultMetadataElement();
        versionMetadata.setId(versionId);
        versionMetadata.setName(determineDisplayName(version));
        versionMetadata.setDefault(node.get("current").booleanValue());
        return versionMetadata;
    }

    private String determineDisplayName(Version version) {
        StringBuilder sb = new StringBuilder();
        sb.append(version.getMajor()).append(".").append(version.getMinor()).append(".").append(version.getPatch());
        if (version.getQualifier() != null) {
            sb.append(determineSuffix(version.getQualifier()));
        }
        return sb.toString();
    }

    private String determineSuffix(Version.Qualifier qualifier) {
        String id = qualifier.getId();
        if (id.equals("RELEASE")) {
            return "";
        }
        StringBuilder sb = new StringBuilder(" (");
        if (id.contains("SNAPSHOT")) {
            sb.append("SNAPSHOT");
        }
        else {
            sb.append(id);
            if (qualifier.getVersion() != null) {
                sb.append(qualifier.getVersion());
            }
        }
        sb.append(")");
        return sb.toString();
    }

    private static class VersionMetadataElementComparator implements Comparator<DefaultMetadataElement> {

        private static final VersionParser versionParser = VersionParser.DEFAULT;

        @Override
        public int compare(DefaultMetadataElement o1, DefaultMetadataElement o2) {
            Version o1Version = versionParser.parse(o1.getId());
            Version o2Version = versionParser.parse(o2.getId());
            return o1Version.compareTo(o2Version);
        }

    }

}
