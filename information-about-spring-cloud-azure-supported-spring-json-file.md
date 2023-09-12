[spring-cloud-azure-supported-spring.json](https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/spring/pipeline/spring-cloud-azure-supported-spring.json) file is to record **Spring Cloud Azure**'s support status of **Spring Boot** and **Spring Cloud** versions.
Also provide information for `spring - compatibility - tests`.

Following is a metadata from the file:
```json
  {
    "current" : true,
    "releaseStatus" : "GENERAL_AVAILABILITY",
    "snapshot" : false,
    "supportStatus" : "SUPPORTED",
    "spring-boot-version" : "3.0.1",
    "spring-cloud-version" : "2022.0.0"
  }
```
- `current`: Current status follows Spring Boot. Only one is true.
- `releaseStatus`: Release status of Spring Boot version. Could be `GENERAL_AVAILABILITY`, `PRERELEASE` and `SNAPSHOT`.
- `snapshot`: Snapshot status of Spring Boot version.
- `supportStatus`: Spring Cloud Azure's support status for Spring Boot. Could be `END_OF_LIFE`, `SUPPORTED` and `TODO`. `spring - compatibility - tests` only tests the `SUPPORTED` ones.
- `spring-boot-version`: Spring Boot version.
- `spring-cloud-version`: The most suitable and the latest Spring Cloud version for this Spring Boot version.