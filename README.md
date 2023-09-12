[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=stliu_azure-sdk-for-java&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=stliu_azure-sdk-for-java)
# Overview

This repo hosts various tools for [Spring Cloud Azure](https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/spring) development.

## Sonar Analysis

This repo contains a [github actions](.github/workflows/sonar-spring-cloud-azure.yml) which runs daily to build the **main** branch of [Spring Cloud Azure](https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/spring) and submit it to [Sonar Cloud](https://sonarcloud.io/project/overview?id=stliu_azure-sdk-for-java) for analysis.

## Automated test spring boot and spring cloud compatibility

This tool set aims to automate the process of testing compatibility of Spring Cloud Azure with Spring Boot and Spring Cloud. The automation includes:
- [Defining the target versions of Spring Boot / Spring Cloud](https://github.com/Azure/spring-cloud-azure-tools/actions/workflows/update-spring-cloud-azure-support-file.yml), which will maintain a `spring-cloud-azure-supported-spring.json` file in [azure-sdk-for-java/sdk/spring](https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/spring/pipeline/spring-cloud-azure-supported-spring.json). See [this](./information-about-spring-cloud-azure-supported-spring-json-file.md) for more details about the json file definition.
- [Upgrade the dependency versions](https://github.com/Azure/spring-cloud-azure-tools/actions/workflows/update-spring-dependencies.yml) of Spring Cloud Azure that are managed by Spring Boot and Spring Cloud.
- Run compatibility tests in the `azure-sdk-for-java` repo, see the [job definition](https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/spring/compatibility-tests.yml).

See [this](./automate-test-spring-boot-and-spring-cloud-compatibility.md) for details about the automation process.

## Contributing

This project welcomes contributions and suggestions.  Most contributions require you to agree to a
Contributor License Agreement (CLA) declaring that you have the right to, and actually do, grant us
the rights to use your contribution. For details, visit https://cla.opensource.microsoft.com.

When you submit a pull request, a CLA bot will automatically determine whether you need to provide
a CLA and decorate the PR appropriately (e.g., status check, comment). Simply follow the instructions
provided by the bot. You will only need to do this once across all repos using our CLA.

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/).
For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or
contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## Trademarks

This project may contain trademarks or logos for projects, products, or services. Authorized use of Microsoft 
trademarks or logos is subject to and must follow 
[Microsoft's Trademark & Brand Guidelines](https://www.microsoft.com/en-us/legal/intellectualproperty/trademarks/usage/general).
Use of Microsoft trademarks or logos in modified versions of this project must not cause confusion or imply Microsoft sponsorship.
Any use of third-party trademarks or logos are subject to those third-party's policies.
