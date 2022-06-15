package com.azure.spring.dev.tools.poc;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CompareDependencies {
    public static void main(String[] args) throws Exception {
        String springBootV2 = "/Users/stliu/projects/microsoft/azure-sdk-for-java/sdk/spring/scripts/spring_boot_2.6.5_managed_external_dependencies.txt";
        String springBootV3 = "/Users/stliu/a.txt";
        List<String> v2 = Files.readAllLines(Paths.get(springBootV2));
        List<String> v3 = Files.readAllLines(Paths.get(springBootV3));

        List<String> v22 = new ArrayList<>();
        for(String d : v2){
            if(v3.contains(d)){
                v3.remove(d);
            }else{
                v22.add(d);
            }
        }

        System.out.println("v2: " + v22.size());
        System.out.println("v3: " + v3.size());

    }
}
