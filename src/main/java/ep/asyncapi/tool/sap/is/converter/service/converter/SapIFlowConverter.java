package ep.asyncapi.tool.sap.is.converter.service.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solace.ep.muleflow.mapper.model.MapMuleDoc;
import com.solace.ep.muleflow.mapper.model.SchemaInstance;
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
            final File integarationFlowFile = new File(integrationFlowSubDirectory, "DUMMY_IFLW_FILE_NAME.iflw");
            FileUtils.writeStringToFile(integarationFlowFile, integrationFlowFileContent, StandardCharsets.UTF_8);
        } catch (final IOException ioException) {
            log.error("Error encountered in SapIFlowConverter.createAndAddIntegrationFlowFiles", ioException);
        }
    }

    private String generateIntegrationFlowFileContent(final MapMuleDoc mapMuleDoc) {
        try {
            // Todo: Call the actual method for generating the iflow document content
            return "DUMMY_IFLOW_CONTENT";
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
            final File sourceToDestinationFormatMmapFile = new File(mappingSubDirectory, messageName + "SourceToDestinationFormat.mmap");
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

    public void createMetaInfFolderAndFiles(final String appName, final String appVersionId, final File mainDirectory) {
        try {
            File metaInfSubDirectory = new File(mainDirectory, "META-INF");
            FileUtils.forceMkdir(metaInfSubDirectory);

            final String metaInfFileContent = generateMetaInfFileContent(appName, appVersionId);
            final File metaInfFile = new File(metaInfSubDirectory, "MANIFEST.MF");
            FileUtils.writeStringToFile(metaInfFile, metaInfFileContent, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            log.error("Error encountered in SapIFlowConverter.createMetaInfFolderAndFiles", ioException);
        }
    }

    private String generateMetaInfFileContent(final String appName, final String appVersionId) {
        try {
            final InputStream inputStream = SapIFlowConverter.class.getResourceAsStream(SapIflorConverterConstants.META_INF_FILE_PATH);
            String templateContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            templateContent = templateContent.replace("<APP_NAME>", appName);
            templateContent = templateContent.replace("<APP_VERSION_ID>", appVersionId);
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
            templateContent = templateContent.replace("<APP_VERSION_ID>", "1.0.1");
            templateContent = templateContent.replace("<DATE_TIME_STAMP>", generateDateTimeStamp());
            return templateContent;
        } catch (Exception exception) {
            log.error("Error encountered in SapIFlowConverter.generateMetaInfoPropFileContent", exception);
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

    private String generateDateTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date currentDate = new Date();
        return sdf.format(currentDate);
    }
}
