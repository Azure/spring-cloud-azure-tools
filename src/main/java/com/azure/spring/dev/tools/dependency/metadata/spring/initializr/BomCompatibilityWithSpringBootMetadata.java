package com.azure.spring.dev.tools.dependency.metadata.spring.initializr;


import java.util.HashMap;

/**
 *     {
 *       "Hoxton.SR12": "Spring Boot >=2.2.0.RELEASE and <2.4.0.M1",
 *       "2020.0.5": "Spring Boot >=2.4.0.M1 and <2.6.0-M1",
 *       "2021.0.0-M1": "Spring Boot >=2.6.0-M1 and <2.6.0-M3",
 *       "2021.0.0-M3": "Spring Boot >=2.6.0-M3 and <2.6.0-RC1",
 *       "2021.0.0-RC1": "Spring Boot >=2.6.0-RC1 and <2.6.1",
 *       "2021.0.3": "Spring Boot >=2.6.1 and <3.0.0-M1",
 *       "2022.0.0-M1": "Spring Boot >=3.0.0-M1 and <3.0.0-M2",
 *       "2022.0.0-M2": "Spring Boot >=3.0.0-M2 and <3.1.0-M1"
 *     }
 */
public class BomCompatibilityWithSpringBootMetadata extends HashMap<String, String> {

}
