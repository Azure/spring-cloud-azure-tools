package com.azure.spring.dev.tools.dependency;

import com.azure.spring.dev.tools.dependency.metadata.SupportStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

@Data
@Component
public class SpringCloudAzureSupportedMetadataReader {

    ObjectMapper mapper = new ObjectMapper();


    public Map<String, SupportStatus> getAzureSupportStatus() {
        Map<String, SupportStatus> azureSupportStatus = new HashMap<>();
        try {
            JsonNode jsonData = mapper.readTree(new URL("https://raw.githubusercontent"
                + ".com/Azure/azure-sdk-for-java/main/sdk/spring/spring-cloud-azure-supported-spring.json"));
            jsonData.forEach(version -> {
                azureSupportStatus.put(version.get("spring-boot-version").toString().
                        replaceAll("\"",Matcher.quoteReplacement("")),
                    SupportStatus.valueOf(version.get("supportStatus").toString().
                        replaceAll("\"", Matcher.quoteReplacement(""))));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return azureSupportStatus;
    }

}
