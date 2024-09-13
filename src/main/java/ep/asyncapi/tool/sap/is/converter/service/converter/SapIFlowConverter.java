package ep.asyncapi.tool.sap.is.converter.service.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solace.ep.codegen.internal.model.MapMuleDoc;
import com.solace.ep.codegen.internal.model.MapSubFlowEgress;
import com.solace.ep.codegen.internal.model.SchemaInstance;
import com.solace.ep.codegen.sap.iflow.SapIFlowGenerator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;

@Component
@Slf4j
public class SapIFlowConverter {

    final ObjectMapper objectMapper = new ObjectMapper();

    public void createIntegrationFlowFiles(final File integrationFlowSubDirectory, final MapMuleDoc mapMuleDoc) {
        try {
            createAndAddIntegrationFlowFiles(integrationFlowSubDirectory, mapMuleDoc);
        } catch (Exception exception) {
            log.error("Error encountered in SapIFlowConverter.createIntegrationFlowFiles", exception);
        }
    }

    private void createAndAddIntegrationFlowFiles(File integrationFlowSubDirectory, MapMuleDoc mapMuleDoc) {
        try {
            final String integrationFlowFileContent = generateIntegrationFlowFileContent(mapMuleDoc);
            String title = mapMuleDoc.getGlobalProperties().getOrDefault("epApplicationVersionTitle", "");
            final String iFlowFilename = ( title.isBlank() ? "NAME_NOT_FOUND" : title ) + ".iflw";
            final File integarationFlowFile = new File(integrationFlowSubDirectory, iFlowFilename);
            FileUtils.writeStringToFile(integarationFlowFile, integrationFlowFileContent, StandardCharsets.UTF_8);
        } catch (final IOException ioException) {
            log.error("Error encountered in SapIFlowConverter.createAndAddIntegrationFlowFiles", ioException);
        }
    }

    private String generateIntegrationFlowFileContent(final MapMuleDoc mapMuleDoc) {
        try {
            return SapIFlowGenerator.getSapIflowFromMapDoc(mapMuleDoc);
        } catch (Exception exception) {
            log.error("Error encountered in SapIFlowConverter.generateIntegrationFlowFileContent", exception);
            return StringUtils.EMPTY;
        }
    }


    public void createSourceToDestinationFormatMmapFiles(final File mappingSubDirectory, final MapMuleDoc mapMuleDoc) {
        try {
            mapMuleDoc.getMapEgressSubFlows().forEach(mapSubFlowEgress -> {
                final SchemaInstance schemaInstance = mapMuleDoc.getSchemaMap().get(mapSubFlowEgress.getJsonSchemaReference());
                final String messageName = mapSubFlowEgress.getMessageName();
                createAndAddSourceToDestinationFormatMmapFile(mappingSubDirectory, messageName, schemaInstance);
            });
        } catch (Exception exception) {
            log.error("Error encountered in SapIFlowConverter.createSourceToDestinationFormatMmapFiles", exception);
        }
    }

    private void createAndAddSourceToDestinationFormatMmapFile(final File mappingSubDirectory, final String messageName, final SchemaInstance schemaInstance) {
        try {
            final String sourceToDestinationFormatMmapFileContent = generateSourceToDestinationFormatMmapFileContent(schemaInstance);
            // final File sourceToDestinationFormatMmapFile = new File(mappingSubDirectory, messageName + "SourceToDestinationFormat.mmap");
            final File sourceToDestinationFormatMmapFile = new File(mappingSubDirectory, String.format("SourceFormatTo%s.mmap", schemaInstance.getName()));
            FileUtils.writeStringToFile(sourceToDestinationFormatMmapFile, sourceToDestinationFormatMmapFileContent, StandardCharsets.UTF_8);
        } catch (final IOException ioException) {
            log.error("Error encountered in SapIFlowConverter.createAndAddSourceToDestinationFormatMmapFile", ioException);
        }
    }

    private String generateSourceToDestinationFormatMmapFileContent(final SchemaInstance schemaInstance) {
        try {
            final InputStream inputStream = SapIFlowConverter.class.getResourceAsStream(SapIflorConverterConstants.RESOURCES_MAPPING_SOURCE_TO_DESTINATIONFORMAT_TEMPLATE);
            String templateContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            templateContent = templateContent.replaceAll("<NAME_OF_JSON_FILE>", schemaInstance.getName() + "_api.json");
            return templateContent;
        } catch (Exception exception) {
            log.error("Error encountered in SapIFlowConverter.generateSourceToDestinationFormatMmapFileContent", exception);
            return StringUtils.EMPTY;
        }
    }

    public void createToDestinationFormatMmapFiles(final File mappingSubDirectory, final MapMuleDoc mapMuleDoc) {
        try {
            mapMuleDoc.getMapFlows().forEach(mapFlow -> {
                final SchemaInstance schemaInstance = mapMuleDoc.getSchemaMap().get(mapFlow.getJsonSchemaReference());
                createAndAddToDestinationFormatMmapFile(mappingSubDirectory, schemaInstance);
            });
        } catch (Exception exception) {
            log.error("Error encountered in SapIFlowConverter.createToDestinationFormatMmapFiles", exception);
        }
    }

    private void createAndAddToDestinationFormatMmapFile(final File mappingSubDirectory, final SchemaInstance schemaInstance) {
        try {
            final String toDestinationFormatMmapFileContent = generateToDestinationFormatMmapFileContent(schemaInstance);
            final File toDestinationFormatMmapFile = new File(mappingSubDirectory, schemaInstance.getName() + "ToDestinationFormat.mmap");
            FileUtils.writeStringToFile(toDestinationFormatMmapFile, toDestinationFormatMmapFileContent, StandardCharsets.UTF_8);
        } catch (final IOException ioException) {
            log.error("Error encountered in SapIFlowConverter.createAndAddToDestinationFormatMmapFile", ioException);
        }
    }

    private String generateToDestinationFormatMmapFileContent(final SchemaInstance schemaInstance) {
        try {
            final InputStream inputStream = SapIFlowConverter.class.getResourceAsStream(SapIflorConverterConstants.RESOURCES_MAPPING_TO_DESTINATIONFORMAT_TEMPLATE);
            String templateContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            templateContent = templateContent.replaceAll("<NAME_OF_JSON_FILE>", schemaInstance.getName() + "_api.json");
            return templateContent;
        } catch (Exception exception) {
            log.error("Error encountered in SapIFlowConverter.generateToDestinationFormatMmapFileContent", exception);
            return StringUtils.EMPTY;
        }
    }

    public void createValidationMmapFiles(final File mappingSubDirectory, final MapMuleDoc mapMuleDoc) {
        try {
            mapMuleDoc.getSchemaMap().values().forEach(schemaInstance -> createAndAddValidateSchemaMmapFile(mappingSubDirectory, schemaInstance));
        } catch (Exception exception) {
            log.error("Error encountered in SapIFlowConverter.createValidationMmapFiles", exception);
        }
    }

    private void createAndAddValidateSchemaMmapFile(final File mappingSubDirectory, final SchemaInstance schemaInstance) {
        try {
            final String validateSchemaMmapFileContent = generateValidateSchemaMmapFileContent(schemaInstance);
            final File validateSchemaMmapFile = new File(mappingSubDirectory, "Validate" + schemaInstance.getName() + ".mmap");
            FileUtils.writeStringToFile(validateSchemaMmapFile, validateSchemaMmapFileContent, StandardCharsets.UTF_8);
        } catch (final IOException ioException) {
            log.error("Error encountered in SapIFlowConverter.createAndAddValidateSchemaMmapFile", ioException);
        }
    }

    private String generateValidateSchemaMmapFileContent(final SchemaInstance schemaInstance) {
        try {
            final InputStream inputStream = SapIFlowConverter.class.getResourceAsStream(SapIflorConverterConstants.RESOURCES_MAPPING_SCHEMA_TEMPLATE);
            String templateContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            templateContent = templateContent.replaceAll("<NAME_OF_JSON_FILE>", schemaInstance.getName() + "_api.json");

            final InputStream transformationBrickTemplateIs = SapIFlowConverter.class.getResourceAsStream(SapIflorConverterConstants.RESOURCES_MAPPING_TRANSFORMATION_BRICK_TEMPLATE);
            String transformationBrickTemplateContent = IOUtils.toString(transformationBrickTemplateIs, StandardCharsets.UTF_8);

            final JsonNode jsonNode = objectMapper.readTree(schemaInstance.getPayload());
            final JsonNode propertiesNode = jsonNode.get("properties");
            String propertiesString = objectMapper.writeValueAsString(propertiesNode);
            Map<String, Object> propertiesMap = objectMapper.readValue(propertiesString, Map.class);
            StringBuilder transformationInnerBrickBlockContent = new StringBuilder();
            propertiesMap.keySet().forEach(propertyKey -> transformationInnerBrickBlockContent.append(transformationBrickTemplateContent.replaceAll("<NAME_OF_PROPERTY>", propertyKey)));
            String escapedTransformationInnerBrickBlockContent = Matcher.quoteReplacement(transformationInnerBrickBlockContent.toString());
            templateContent = templateContent.replaceAll("<TRANSFORMATION_INNER_BRICK_BLOCK>", escapedTransformationInnerBrickBlockContent);
            return templateContent;
        } catch (Exception exception) {
            log.error("Error encountered in SapIFlowConverter.generateValidateSchemaMmapFileContent", exception);
            return StringUtils.EMPTY;
        }
    }

    public void createSchemaJsonsFolderAndFiles(final File jsonSubDirectory, final MapMuleDoc mapMuleDoc) {
        try {
            mapMuleDoc.getSchemaMap().values().forEach(schemaInstance -> createAndAddSchemaApiJsonFile(jsonSubDirectory, schemaInstance));
        } catch (Exception exception) {
            log.error("Error encountered in SapIFlowConverter.createSchemaJsonsFolderAndFiles", exception);
        }
    }

    private void createAndAddSchemaApiJsonFile(final File resourcesJsonSubDirectory, final SchemaInstance schemaInstance) {
        try {
            final String schemaApiJsonFileContent = generateSchemaApiJsonFileContent(schemaInstance);
            final File schemaApiJsonFile = new File(resourcesJsonSubDirectory, schemaInstance.getName() + "_api.json");
            FileUtils.writeStringToFile(schemaApiJsonFile, schemaApiJsonFileContent, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            log.error("Error encountered in SapIFlowConverter.createAndAddSchemaApiJsonFile", ioException);
        }
    }

    private String generateSchemaApiJsonFileContent(final SchemaInstance schemaInstance) {
        try {
            final InputStream inputStream = SapIFlowConverter.class.getResourceAsStream(SapIflorConverterConstants.RESOURCES_JSON_SCHEMA_TEMPLATE);
            String templateContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            templateContent = templateContent.replaceAll("<SCHEMA_NAME>", schemaInstance.getName());
            JsonNode jsonNode = objectMapper.readTree(schemaInstance.getPayload());
            JsonNode propertiesNode = jsonNode.get("properties");
            String propertiesString = objectMapper.writeValueAsString(propertiesNode);
            String escapedPropertiesString = Matcher.quoteReplacement(propertiesString);
            templateContent = templateContent.replaceAll("<SCHEMA_PROPERTIES>", escapedPropertiesString);

            JsonNode definitionsNode = jsonNode.get("definitions");
            String escapedDefinitionsString = "{ }";
            if ( definitionsNode != null ) {
                String definitionsString = objectMapper.writeValueAsString(definitionsNode);
                escapedDefinitionsString = Matcher.quoteReplacement(definitionsString);
            }
            templateContent = templateContent.replaceAll("<SCHEMA_DEFINITIONS>", escapedDefinitionsString);

            return templateContent;
        } catch (Exception exception) {
            log.error("Error encountered in SapIFlowConverter.generateSchemaApiJsonFileContent", exception);
            return StringUtils.EMPTY;
        }

    }


    public void createParametersPropDef(final File resourcesSubDirectory) {
        try {
            final String parametersPropDefFileContent = generateParametersPropDefFileContent();
            File parametersPropDefFile = new File(resourcesSubDirectory, "parameters.propdef");
            FileUtils.writeStringToFile(parametersPropDefFile, parametersPropDefFileContent, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            log.error("Error encountered in SapIFlowConverter.createParametersPropDef", ioException);
        }
    }

    private String generateParametersPropDefFileContent() {
        try {
            final InputStream inputStream = SapIFlowConverter.class.getResourceAsStream(SapIflorConverterConstants.PARAMETERS_PROP_DEF_FILE_PATH);
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (Exception exception) {
            log.error("Error encountered in SapIFlowConverter.generateParametersPropDefFileContent", exception);
            return StringUtils.EMPTY;
        }
    }


    public void createParametersProp(final File resourcesSubDirectory) {
        try {
            final String parametersPropFileContent = generateParametersPropFileContent();
            File parametersPropFile = new File(resourcesSubDirectory, "parameters.prop");
            FileUtils.writeStringToFile(parametersPropFile, parametersPropFileContent, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            log.error("Error encountered in SapIFlowConverter.createParametersProp", ioException);
        }
    }

    private String generateParametersPropFileContent() {
        try {
            final InputStream inputStream = SapIFlowConverter.class.getResourceAsStream(SapIflorConverterConstants.PARAMETERS_PROP_FILE_PATH);
            String templateContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            templateContent = templateContent.replace("<DATE_TIME_STAMP>", generateDateTimeStamp());
            return templateContent;
        } catch (Exception exception) {
            log.error("Error encountered in SapIFlowConverter.generateParametersPropFileContent", exception);
            return StringUtils.EMPTY;
        }
    }

    public void createMetaInfFolderAndFiles(final String appName, final String appSemanticVersion, final File mainDirectory) {
        try {
            File metaInfSubDirectory = new File(mainDirectory, "META-INF");
            FileUtils.forceMkdir(metaInfSubDirectory);

            final String metaInfFileContent = generateMetaInfFileContent(appName, appSemanticVersion);
            final File metaInfFile = new File(metaInfSubDirectory, "MANIFEST.MF");
            FileUtils.writeStringToFile(metaInfFile, metaInfFileContent, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            log.error("Error encountered in SapIFlowConverter.createMetaInfFolderAndFiles", ioException);
        }
    }

    private String generateMetaInfFileContent(final String appName, final String appSemanticVersion) {
        try {
            final InputStream inputStream = SapIFlowConverter.class.getResourceAsStream(SapIflorConverterConstants.META_INF_FILE_PATH);
            String templateContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            templateContent = templateContent.replace("<APP_NAME>", appName);
            templateContent = templateContent.replace("<APP_SYMBOLIC_NAME>", appName.replace(" ", "_"));
            templateContent = templateContent.replace("<APP_SEMANTIC_VERSION_ID>", appSemanticVersion);
            return templateContent;
        } catch (Exception exception) {
            log.error("Error encountered in SapIFlowConverter.generateMetaInfFileContent", exception);
            return StringUtils.EMPTY;
        }
    }

    public void createMetaInfoPropFile(final String appVersionId, final File mainDirectory) {
        try {
            final String metaInfoPropFileContent = generateMetaInfoPropFileContent(appVersionId);
            File projectFile = new File(mainDirectory, "metainfo.prop");
            FileUtils.writeStringToFile(projectFile, metaInfoPropFileContent, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            log.error("Error encountered in SapIFlowConverter.createMetaInfoPropFile", ioException);
        }
    }

    private String generateMetaInfoPropFileContent(final String appVersionId) {
        try {
            final InputStream inputStream = SapIFlowConverter.class.getResourceAsStream(SapIflorConverterConstants.META_INFO_PROP_FILE_PATH);
            String templateContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            templateContent = templateContent.replace("<APP_VERSION_ID>", appVersionId);
            templateContent = templateContent.replace("<DATE_TIME_STAMP>", generateDateTimeStamp());
            return templateContent;
        } catch (Exception exception) {
            log.error("Error encountered in SapIFlowConverter.generateMetaInfoPropFileContent", exception);
            return StringUtils.EMPTY;
        }
    }

    public void createReadmeFile(final String appName, final String appSemanticVersion, final String appDescription, final File mainDirectory) {
      try {
          final String readme = generateReadmeFileContent(appName, appSemanticVersion, appDescription);
          File readmeFile = new File(mainDirectory, "README.md");
          FileUtils.writeStringToFile(readmeFile, readme, StandardCharsets.UTF_8);
      } catch (IOException ioException) {
          log.error("Error encountered in SapIFlowConverter.createReadmeFile", ioException);
      }
  }

    private String generateReadmeFileContent(final String appName, final String appSemanticVersion, final String appDescription) {
      try {
          final InputStream inputStream = SapIFlowConverter.class.getResourceAsStream(SapIflorConverterConstants.README_PATH);
          String templateContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
          templateContent = templateContent.replace("<APP_NAME>", appName);
          templateContent = templateContent.replace("<APP_SEMANTIC_VERSION_ID>", appSemanticVersion);
          templateContent = templateContent.replace("<APP_DESCRIPTION>", appDescription);
          return templateContent;
      } catch (Exception exception) {
          log.error("Error encountered in SapIFlowConverter.generateReadmeFileContent", exception);
          return StringUtils.EMPTY;
      }
  }

    public void createProjectFile(final String projectName, final File mainDirectory) {
        try {
            final String projectFileContent = generateProjectFileContent(projectName);
            File projectFile = new File(mainDirectory, ".project");
            FileUtils.writeStringToFile(projectFile, projectFileContent, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            log.error("Error encountered in SapIFlowConverter.createProjectFile", ioException);
        }
    }

    private String generateProjectFileContent(final String asyncAPITitle) {
        try {
            final InputStream inputStream = SapIFlowConverter.class.getResourceAsStream(SapIflorConverterConstants.PROJECT_FILE_PATH);
            String templateContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            templateContent = templateContent.replace("<NAME_PLACEHOLDER>", asyncAPITitle);
            return templateContent;
        } catch (Exception exception) {
            log.error("Error encountered in SapIFlowConverter.generateProjectFileContent", exception);
            return StringUtils.EMPTY;
        }
    }

    /**
     * This method will create script files:
     * - composeTopic.groovy (static)
     * - topicParameters.groovy -- generated dynamically with topic parameters in code, one script function per event
     * @param scriptSubDirectory
     * @param mapMuleDoc
     */
    public void createDynamicTopicScriptFiles(final File scriptSubDirectory, MapMuleDoc mapMuleDoc) {
        try {
            final InputStream composeTopicScriptInputStream = SapIFlowConverter.class.getResourceAsStream(SapIflorConverterConstants.RESOURCES_SCRIPT_COMPOSE_TOPIC_FILE_PATH);
            File composeTopicScriptFile = new File(scriptSubDirectory, "composeTopic.groovy");
            FileUtils.copyInputStreamToFile(composeTopicScriptInputStream, composeTopicScriptFile);
            
            if (mapMuleDoc.getMapEgressSubFlows().isEmpty()) {
                return;
            } else {
                int count = 0;
                for (MapSubFlowEgress pub : mapMuleDoc.getMapEgressSubFlows() ) {
                    if (pub.getSetVariables().size() > 0) {
                        count++;
                    }
                }
                if (count == 0) {
                    return;
                }
            }

            File topicParametersScriptFile = new File(scriptSubDirectory, "topicParameters.groovy");
            FileUtils.writeStringToFile(topicParametersScriptFile, SapIflorConverterConstants.TOPIC_PARAMETERS_GROOVY_HEADER, "UTF-8", false );
            int outputChannel = 0;  // , functionCount = 0;
            for ( MapSubFlowEgress pub : mapMuleDoc.getMapEgressSubFlows() ) {
                if ( pub.getSetVariables().size() == 0 ) {
                    outputChannel++;
                    continue;
                }
                if ( pub.isPublishToQueue() ) {
                    outputChannel++;
                    continue;
                }
                String topicParametersGroovyFx = SapIflorConverterConstants.TOPIC_PARAMETERS_GROOVY_FX;
                topicParametersGroovyFx = topicParametersGroovyFx.replace(
                    SapIflorConverterConstants.TP_TOKEN_EVENT_NAME, 
                    pub.getMessageName() != null ? pub.getMessageName() : "UNKNOWN"
                );
                topicParametersGroovyFx = topicParametersGroovyFx.replace(
                    SapIflorConverterConstants.TP_TOKEN_TOPIC_ADDRESS_PATTERN, 
                    pub.getPublishAddress() != null ? pub.getPublishAddress() : "UNKNOWN"
                );
                topicParametersGroovyFx = topicParametersGroovyFx.replace(
                    SapIflorConverterConstants.TP_TOKEN_FX_INSTANCE, 
                    Integer.toString(outputChannel++)
                );
                StringBuilder varsList = new StringBuilder();
                StringBuilder jsonPath = new StringBuilder();
                StringBuilder setValue = new StringBuilder();
                int varIndex = 1;
                for( Map.Entry<String, String> var : pub.getSetVariables().entrySet() ) {
                    final String topicVar = var.getKey();
                    varsList.append(String.format(SapIflorConverterConstants.TOPIC_PARAMETERS_VAR_LIST_PATTERN, topicVar));
                    jsonPath.append(String.format(SapIflorConverterConstants.TOPIC_PARAMETERS_JSON_PATH_PATTERN, topicVar, varIndex));
                    setValue.append(String.format(SapIflorConverterConstants.TOPIC_PARAMETERS_SET_VALUE_PATTERN, topicVar, varIndex));
                    varIndex++;
                }
                topicParametersGroovyFx = topicParametersGroovyFx.replace(SapIflorConverterConstants.TP_TOKEN_TOPIC_VARS_LIST, varsList.toString());
                topicParametersGroovyFx = topicParametersGroovyFx.replace(SapIflorConverterConstants.TP_TOKEN_VARS_JSON_PATH, jsonPath.toString());
                topicParametersGroovyFx = topicParametersGroovyFx.replace(SapIflorConverterConstants.TP_TOKEN_VARS_SET_VALUE, setValue.toString());
                FileUtils.writeStringToFile(topicParametersScriptFile, topicParametersGroovyFx, "UTF-8", true);
            };
        } catch (IOException ioException) {
            log.error("Error encountered in SapIFlowConverter.createDynamicTopicScriptFiles", ioException);
        }
    }

    private String generateDateTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date currentDate = new Date();
        return sdf.format(currentDate);
    }
}
