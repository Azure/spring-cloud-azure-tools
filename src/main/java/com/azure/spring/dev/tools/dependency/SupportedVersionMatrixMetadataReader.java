package com.azure.spring.dev.tools.dependency;

import com.azure.spring.dev.tools.actions.UpdateSpringDependencyRunner;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

@Data
@Component
public class SupportedVersionMatrixMetadataReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SupportedVersionMatrixMetadataReader.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final SpringBootReleaseMetadataReader metadataReader;

    public ArrayList<String> getTestedList(ArrayList<String> releasedList) {
        ArrayList<String> testedList = new ArrayList<>();
        List existList = null;
        DefaultArtifactVersion lowerSupportedVersion = new DefaultArtifactVersion("2.5.0");
        for (int i = 0; i < releasedList.size(); i++) {
            String releasedVersion = releasedList.get(i);
            DefaultArtifactVersion version = new DefaultArtifactVersion(releasedVersion);
            if (version.compareTo(lowerSupportedVersion) == 1) {
                testedList.add(releasedVersion);
            }
        }
        try {
            JsonNode root = mapper.readTree(new File("supported-version-matrix.json"));
            JsonNode jsonVersion = root.get("matrix").get("SPRING_CLOUD_AZURE_TEST_SUPPORTED_SPRING_BOOT_VERSION");
            String version = jsonVersion.toString().replaceAll("\"", Matcher.quoteReplacement(""))
                                        .replace("[", "").replace("]","");

            existList = Arrays.asList(version.split(","));
            testedList.removeAll(existList);
            testedList.addAll(existList);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return testedList;
    }

    public void UpdateMatrixJson(ArrayList<String> testedList) {
        LOGGER.info("start updating ");
        try (JsonGenerator generator = mapper.getFactory().createGenerator(new File("supported-version-matrix.json"),
            JsonEncoding.UTF8)) {
            generator.writeStartObject();                      // {
            generator.writeFieldName("displayNames");           // "displayNames":
            generator.writeStartObject();                      // {
            for (String str: testedList) {
                generator.writeStringField(str,"springboot"+str.replaceAll("\\.","_"));
            }
            generator.writeEndObject();                        // }
            generator.writeFieldName("matrix");           // "matrix":
            generator.writeStartObject();                      // {
            generator.writeFieldName("Agent");           // "Agent":
            generator.writeStartObject();                      // {
            generator.writeFieldName("ubuntu-20.04");           // "ubuntu-20.04":
            generator.writeStartObject();                      // {
            generator.writeStringField("OSVmImage", "MMSUbuntu20.04");             //    "OSVmImage": "MMSUbuntu20.04",
            generator.writeStringField("Pool", "azsdk-pool-mms-ubuntu-2004-general");             //    "Pool": "azsdk-pool-mms-ubuntu-2004-general",
            generator.writeEndObject();                        // }
            generator.writeEndObject();                        // }
            generator.writeStringField("JavaTestVersion", "1.11");             //    "JavaTestVersion" : "1.11",
            generator.writeFieldName("SPRING_CLOUD_AZURE_TEST_SUPPORTED_SPRING_BOOT_VERSION");          //    "SPRING_CLOUD_AZURE_TEST_SUPPORTED_SPRING_BOOT_VERSION" :
            generator.writeStartArray();                       //    [
            for (String str: testedList) {
                generator.writeString(str);
            }
            generator.writeEndArray();                         //    ]
//            generator.writeFieldName("SPRING_CLOUD_AZURE_TEST_SUPPORTED_SPRING_CLOUD_VERSION");          //    "SPRING_CLOUD_AZURE_TEST_SUPPORTED_SPRING_CLOUD_VERSION" :
//            generator.writeStartArray();                       //    [
//            generator.writeString("2021.0.2");
//            generator.writeString("2020.0.5");
//            generator.writeEndArray();                         //    ]
            generator.writeEndObject();                        // }
            generator.writeEndObject();                        // }
            LOGGER.info(" update success ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonNode getSpringCloudRange() {
        try {
            JsonNode root = mapper.readTree(new URL("https://start.spring.io/actuator/info"));
            JsonNode springCloudRange = root.get("bom-ranges").get("spring-cloud");
            return springCloudRange;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void UpdateVersionManagement(JsonNode springCloudRange, ArrayList<String> testedList) {
        LOGGER.info("start updating ");
        List<String> springCloud = new ArrayList<>();
        List<String> springBootRange = new ArrayList<>();
        Iterator<String> fieldNames = springCloudRange.fieldNames();
        fieldNames.forEachRemaining(e -> springCloud.add(e));
        springCloud.forEach(e -> springBootRange.add(springCloudRange.get(e).toString().
            replaceAll("\"", Matcher.quoteReplacement(""))));

        try {
            ObjectNode root = mapper.createObjectNode();
            for (int i = 0; i < testedList.size(); i++) {
                String springBootVersion = testedList.get(i);
                DefaultArtifactVersion v1 = new DefaultArtifactVersion(springBootVersion);
                for (int j = 0; j < springBootRange.size(); j++) {
                    DefaultArtifactVersion v2 =
                        new DefaultArtifactVersion(springBootRange.get(j).split("<")[1]);
                    if (v1.compareTo(v2) == -1) {
                        root.put(springBootVersion, springCloud.get(j));
                        break;
                    }
                }
            }
            FileWriter fileWriter = new FileWriter("compatibility-version-management.json");
            fileWriter.write(root.toString());
            fileWriter.close();
            LOGGER.info(" update success ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
