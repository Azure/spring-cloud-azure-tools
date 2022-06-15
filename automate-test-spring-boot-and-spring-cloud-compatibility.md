# Problem

Spring Cloud Azure is used within Spring Boot application, and probably with Spring Cloud as well.
Both Spring Boot and Spring Cloud have lots of active version.

For example, Spring Boot versions can be found [here](https://spring.io/projects/spring-boot#support), and Spring Cloud versions can be found [here](https://spring.io/projects/spring-cloud#support).

Spring Cloud [maintains a compatibility matrix](https://spring.io/projects/spring-cloud), which is a list of all the supported Spring Boot versions and the supported Spring Cloud versions.


It is very complicated and error-prone to check if a Spring Boot version is compatible with a Spring Cloud version.

Same applies to Spring Cloud Azure project.

We see lots of bug reports / questions about this. Like when a customer upgrade its spring boot version, and the application fails to start due to some class not found or other incompatible issue.

Provide a compatibility matrix for Spring Boot and Spring Cloud, so customer would know which Spring Boot version is compatible with which Spring Cloud Azure version.

Spring Cloud Azure's support policy currently is supporting `N-1` Spring Boot versions, where `N` is the **latest** Spring Boot release.

So, we need to test our code as soon as a newer Spring Boot version is released, idealy we should start testing with the milestone release.

Currently, the whole process is purely manual, as described [here](https://github.com/Azure/azure-sdk-for-java/wiki/Spring-Cloud-Azure-tasks-for-a-new-Spring-Boot-release).

Basically:

1. Human monitors the new spring release.
2. Manually create github issue for the new spring boot release.
3. Manually create PR for the new spring boot release.
   1. Using handwritten python script to parse the dependencies of the new spring boot release.
   2. more details can be found [here](https://github.com/Azure/azure-sdk-for-java/blob/aec4c6247ba7ba4de57dd866e3f5511ca4fbd387/sdk/spring/scripts/README.md)
4. Manually trigger pipeline to test the new spring boot release.

# Goal

## Automation

Automatically create github issue when a new spring boot release is released.

We focus on:

* Milestone release
* GA release

We don't do anything for spring boot's **SNAPSHOT** release.

(this requires the automation system remembers which version of spring boot has been tested.)

# Design

## Get supported Spring Boot/Cloud versions

[Spring Initializr](https://spring.io/initializr) has an API `GET https://start.spring.io/metadata/config` provides all the information about supported Spring Boot versions.

for example 

```json
{
  "bootVersions": {
    "id": "bootVersion",
    "type": "SINGLE_SELECT",
    "title": "Spring Boot Version",
    "description": "spring boot version",
    "content": [
      {
        "name": "3.0.0 (SNAPSHOT)",
        "id": "3.0.0-SNAPSHOT",
        "default": false
      },
      {
        "name": "3.0.0 (M2)",
        "id": "3.0.0-M2",
        "default": false
      },
      {
        "name": "2.7.0 (SNAPSHOT)",
        "id": "2.7.0-SNAPSHOT",
        "default": false
      },
      {
        "name": "2.7.0 (M3)",
        "id": "2.7.0-M3",
        "default": false
      },
      {
        "name": "2.6.7 (SNAPSHOT)",
        "id": "2.6.7-SNAPSHOT",
        "default": false
      },
      {
        "name": "2.6.6",
        "id": "2.6.6",
        "default": true
      },
      {
        "name": "2.5.13 (SNAPSHOT)",
        "id": "2.5.13-SNAPSHOT",
        "default": false
      },
      {
        "name": "2.5.12",
        "id": "2.5.12",
        "default": false
      }
    ]
  }
}
```

From the above output, we can know, there are two GAed Spring Boot versions:

* 2.6.6 (default)
* 2.5.12

and there are two milestone releases:

* 3.0.0-M2
* 2.7.0-M3

So, based on this information, we can do the following things:

1. Align dependencies of Spring Cloud Azure's development branch with the ones of the default spring boot version, in this case, it is 2.6.6.
2. Ideally we should know which version of Spring Cloud Azure is compatible with Spring Boot 2.5.12.
3. Test Spring Cloud Azure development branch with Spring Boot
   1. 3.0.0-M2
   2. 2.7.0-M3

And we still need to find out which version of Spring Cloud is compatible with which Spring Boot version.
Although there is a matrix table in https://spring.io/projects/spring-cloud, but we can't use it in our automation process.

We can do this by creating a pom from Spring Initializr and parsing the pom file.

```shell
curl -G https://start.spring.io/pom.xml -d dependencies=web,cloud-stream -d bootVersion=3.0.0-M2 -o pom.xml
```

## How to parse pom file with maven API

We need to parse the generated pom file with maven API, and get the dependencies (including the transitive ones) of the pom file.
So we can compare the ones used in Spring Cloud Azure with the ones used in the pom file, and upgrade dependencies versions in the Spring Cloud Azure project.



```shell