package ep.asyncapi.tool.sap.is.converter.service.converter;

import org.springframework.stereotype.Component;

@Component
public class SapIflorConverterConstants {


    public static final String
            PROJECT_FILE_PATH = "/static/iflowdocument-template/.project",
            META_INFO_PROP_FILE_PATH = "/static/iflowdocument-template/metainfo.prop",
            README_PATH = "/static/iflowdocument-template/README.md",
            META_INF_FILE_PATH = "/static/iflowdocument-template/META-INF/MANIFEST.MF",
            PARAMETERS_PROP_FILE_PATH = "/static/iflowdocument-template/src/main/resources/parameters.prop",
            PARAMETERS_PROP_DEF_FILE_PATH = "/static/iflowdocument-template/src/main/resources/parameters.propdef",
            RESOURCES_JSON_SCHEMA_TEMPLATE = "/static/iflowdocument-template/src/main/resources/json/schema_api.json",
            RESOURCES_MAPPING_SCHEMA_TEMPLATE = "/static/iflowdocument-template/src/main/resources/mapping/validateSchema.mmap",
            RESOURCES_MAPPING_TRANSFORMATION_BRICK_TEMPLATE = "/static/iflowdocument-template/src/main/resources/mapping/transformationBrickTemplate.xml",
            RESOURCES_MAPPING_TO_DESTINATIONFORMAT_TEMPLATE = "/static/iflowdocument-template/src/main/resources/mapping/toDestinationFormatMmap.xml",
            RESOURCES_MAPPING_SOURCE_TO_DESTINATIONFORMAT_TEMPLATE = "/static/iflowdocument-template/src/main/resources/mapping/sourceToDestinationFormatMmap.xml",
            RESOURCES_SCRIPT_COMPOSE_TOPIC_FILE_PATH = "/static/iflowdocument-template/src/main/resources/script/composeTopic.groovy",
            RESOURCES_SCRIPT_EXTRACT_FIELD_FILE_PATH = "/static/iflowdocument-template/src/main/resources/script/extractField.groovy";
}
