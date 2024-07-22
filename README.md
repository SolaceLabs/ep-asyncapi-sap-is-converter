[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](CODE_OF_CONDUCT.md)

# Project Title

## Overview
This project is ...

## Getting started quickly
1. Step 1
1. Step 2
1. Step 3
2. Step 4
   Download the solace-ep-codegen from https://github.com/SolaceLabs/solace-ep-codegen and build it using `mvn clean install`
3. Step 5
    - Download the Open API spec for the EP Designer API and use the below command to generate the Open API client for this:
    `openapi-generator generate -i open-api-spec.json -g java -o ep-designer-client-2.4.1 
    --additional-properties=invokerPackage=com.solace.cloud.ep.designer,apiPackage=com.solace.cloud.ep.designer.api,
    modelPackage=com.solace.cloud.ep.designer.model,groupId=com.solace.cloud.ep.designer,artifactId=ep-designer-client,
    openApiNullable=true,disallowAdditionalPropertiesIfNotPresent=false`
    - Build it using the command `mvn clean install`
4. Build the project ep-asyncapi-sap-is-converter using the command `mvn clean install`
5. Run the target artefact as `java -jar ep-asyncapi-sap-is-converter-1.0.X.jar`

## Documentation
Details about the what why and how of this project. Either refer to external documentations or document in this repo

## Resources
This is not an officially supported Solace product.

For more information try these resources:
- Ask the [Solace Community](https://solace.community)
- The Solace Developer Portal website at: https://solace.dev


## Contributing
Contributions are encouraged! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Authors
See the list of [contributors](https://github.com/solacecommunity/<github-repo>/graphs/contributors) who participated in this project.

## License
See the [LICENSE](LICENSE) file for details.
