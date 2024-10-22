[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](CODE_OF_CONDUCT.md)

# SAP Advanced Event Mesh - Event Portal to SAP CPI IFlow Generator

## Overview

This utility tool automates the process of generating scaffolding for SAP CPI (Cloud Platform Integration) iFlow
artifacts based on applications defined in the SAP Advanced Event Mesh - Event Portal. \
By connecting to the Event Portal, the tool queries for various event-driven applications and retrieves their associated
AsyncAPI specifications.
Using this information, it generates a fully structured SAP CPI iFlow artifact.

### Key Features:

- **Event Portal Integration**: Seamlessly connects to SAP Advanced Event Mesh - Event Portal to fetch applications and
  their corresponding AsyncAPI specifications.
- **Automated iFlow Generation**: Automatically creates iFlow scaffolding based on the application’s event-driven
  architecture, significantly reducing manual coding efforts.
- **Accelerated Development**: Provides a starting point with ready-to-use scaffolding code, enabling developers to
  quickly
  implement business logic.
- **SAP Integration Suite Compatibility**: The generated iFlow artifact can be directly imported into SAP Integration
  Suite,
  allowing for further customization, implementation, and deployment.

### Usage:
This tool accelerates the integration development process by providing the foundation needed for event-driven
integrations in SAP Integration Suite, allowing developers to focus on business logic instead of creating boilerplate
code.

## Prerequisites
To build and run this application, you need to have the following tools locally available :
1. Java SDK 18
2. Git
3. Maven
4. OpenApi Generator CLI

## Deployment instructions

### On local machine
The tool can be build and deployed on an infrastructure
supporting Java applications including a local machine or server by following the below instructions.
1. Build required libraries:
   a. **solace-ep-codegen**
   Check out the solace-ep-codegen from https://github.com/SolaceLabs/solace-ep-codegen
   and build it using ```mvn clean install```
   b. OpenAPI client for Solace EP Designer API
    - Download the Open API spec for the EP Designer API and use the below command to generate the Open API client for
      this:
       ```java
          openapi-generator generate -i open-api-spec.json -g java -o ep-designer-client-2.4.1
          --additional-properties=invokerPackage=com.solace.cloud.ep.designer,apiPackage=com.solace.cloud.ep.designer.api,
          modelPackage=com.solace.cloud.ep.designer.model,groupId=com.solace.cloud.ep.designer,artifactId=ep-designer-client,
          openApiNullable=true,disallowAdditionalPropertiesIfNotPresent=false
       ```
        - Build it using the command ```mvn clean install```
2. Build ep-asyncapi-sap-is-converter tool
    - Checkout the application code from the Solace GitHub
      repository: https://github.com/SolaceLabs/ep-asyncapi-sap-is-converter
    - Build the project using the command `mvn clean install`
3. Run the ep-asyncapi-sap-is-converter tool
    - Run the target artefact as `java -jar ep-asyncapi-sap-is-converter.jar`

### SAP BTP CloudFoundry
For convenience's sake,
it is also possible to directly deploy the release artifact into SAP BTP CloudFoundry space
if your organization has one enabled by following the below instructions.
1. Make sure that you have the CloudFoundry CLI installed.
2. Download the release artifact over here: [Github-Releases](https://github.com/SolaceLabs/ep-asyncapi-sap-is-converter/releases)
3. Download the manifest file included as a part of the source code over here: [Manifest file](https://github.com/SolaceLabs/ep-asyncapi-sap-is-converter/blob/main/manifest.yml)
4. Make sure that the above two files are stored in the same directory/folder
5. Update the route in the below section as suitable for your organization space and settings :
    ```
       routes:
         - route: TARGET-ROUTE
    ```
6. Login to your organization's SAP BTP Cloudfoundry space.
7. Navigate to the location where the artifact release and manifest file are stored in a terminal/shell and run the following command :
    ```
    cf push -f manifest.yml
    ```
8. Monitor the deployment and startup of the application
9. Navigate with the application and click on the route defined in the application to access the tool.

## Resources
This is not an officially supported Solace product.

For more information, try these resources:

- Ask the [Solace Community](https://solace.community)
- The Solace Developer Portal website at: https://solace.dev

## Contributing

Contributions are encouraged! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the
process for submitting pull requests to us.

## Authors

See the list of [contributors](https://github.com/SolaceLabs/ep-asyncapi-sap-is-converter/graphs/contributors) who participated in
this project.

## License

See the [LICENSE](LICENSE) file for details.