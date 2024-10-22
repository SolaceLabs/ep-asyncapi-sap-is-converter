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

    public static final String
            TOPIC_PARAMETERS_GROOVY_HEADER =
                "/**\n" + //
                " * topicParameters.groovy\n" + //
                " * ----------------------\n" + //
                " * EDIT this script to define sources for published topic variables\n" + //
                " * - One function is generated per event type published to Event Mesh\n" + //
                " * - Define entry for variable source as JSON Path (payload) or Set Value (explicit)\n" + //
                " * - The JSON Path will be evaluated first, the Set Value can be used as a default\n" + //
                " *   if the field specified in JSON Path is not found in the payload\n" + //
                " **/\n" + //
                "import com.sap.gateway.ip.core.customdev.util.Message;\n" + //
                "\n" + //
                "TV_JSON_PATH_PROPERTY = \"__topicVarsJsonPath__\";\n" + //
                "TV_SET_VALUE_PROPERTY = \"__topicVarsSetValue__\";\n" + //
                "\n",
            TOPIC_PARAMETERS_GROOVY_FX = 
                "/**\n" + //
                " * Define Topic Variables for Event:\n" + //
                " *     >>$$__EVENT_NAME__$$<<\n" + //
                " *\n" + //
                " * Topic Address Pattern:\n" + //
                " *     >>$$__TOPIC_ADDRESS_PATTERN__$$<<\n" + //
                " *\n" + //
                " * Topic Variables:\n" + //
                ">>$$__TOPIC_VARS_LIST__$$<<\n" + //
                " **/\n" + //
                "def Message defineTopicParams_>>$$__FX_INSTANCE__$$<<(Message message) {\n" + //
                "\n" + //
                "    def topicVarsJsonPath = [:];    // Map of topicVariables -> JSON path locations\n" + //
                "    def topicVarsSetValue = [:];    // Map of topicVariables -> Explicit values\n" + //
                "\n" + //
                "    // Uncomment property value to set JSON Path location in payload\n" + //
                "    // DO NOT use $. prefix for JSON Path specification\n" + //
                ">>$$__TOPIC_VARS_JSON_PATH__$$<<\n" + //
                "\n" + //
                "    // Uncomment property entry below to set a topic value directly\n" + //
                ">>$$__TOPIC_VARS_SET_VALUE__$$<<\n" + //
                "\n" + //
                "    message.setProperty(TV_JSON_PATH_PROPERTY, topicVarsJsonPath);\n" + //
                "    message.setProperty(TV_SET_VALUE_PROPERTY, topicVarsSetValue);\n" + //
                "\n" + //
                "    return message;\n" + //
                "}\n" + //
                "\n",
            TOPIC_PARAMETERS_JSON_PATH_PATTERN = "    // topicVarsJsonPath.%s = \"payload.field%d\"\n",
            TOPIC_PARAMETERS_SET_VALUE_PATTERN = "    // topicVarsSetValue.%s = \"VALUE%d\"\n",
            TOPIC_PARAMETERS_VAR_LIST_PATTERN  = " * - %s\n";

        public static String
            TP_TOKEN_EVENT_NAME = ">>$$__EVENT_NAME__$$<<",
            TP_TOKEN_TOPIC_ADDRESS_PATTERN = ">>$$__TOPIC_ADDRESS_PATTERN__$$<<",
            TP_TOKEN_TOPIC_VARS_LIST = ">>$$__TOPIC_VARS_LIST__$$<<\n",
            TP_TOKEN_FX_INSTANCE = ">>$$__FX_INSTANCE__$$<<",
            TP_TOKEN_VARS_JSON_PATH = ">>$$__TOPIC_VARS_JSON_PATH__$$<<\n",
            TP_TOKEN_VARS_SET_VALUE = ">>$$__TOPIC_VARS_SET_VALUE__$$<<\n";

            public static final String
            AEM_INPUT_EXC_GROOVY_HEADER = 
                "import com.sap.gateway.ip.core.customdev.util.Message;\n" + //
                "\n" + //
                "",

            AEM_INPUT_EXC_GROOVY_FUNCTION = 
                "def Message inputExceptionProcess_>>$$__FX_INSTANCE__$$<<(Message message) {\n" + //
                "\n" + //
                "\t// get a map of iflow properties\n" + //
                "\tdef map = message.getProperties()\n" + //
                "\tdef refernceID = map.get(\"ReferenceId\")\t\t// ReferenceID could be an Identifier\n" + //
                "\tdef logException = map.get(\"ExceptionLogging\")\t// Externalized parameter \"ExceptionLogging\" is set to \"True\" to log exceptions\n" + //
                "\tdef attachID = \"\"\n" + //
                "\tdef errordetails = \"\"\n" + //
                "\n" + //
                "\t// get an exception java class instance\n" + //
                "\tdef ex = map.get(\"CamelExceptionCaught\")\n" + //
                "\tif (ex != null) \n" + //
                "\t{\n" + //
                "\t\t// save the error response as a message attachment \n" + //
                "\t\tdef messageLog = messageLogFactory.getMessageLog(message);\n" + //
                "\t\tif (refernceID == null || refernceID == \"\" )\n" + //
                "\t\t{\n" + //
                "\t\t\terrordetails = \"The  replication failed because of the following error:  \" + ex.toString()\n" + //
                "\t\t\tattachID  = \"Error Details\"\n" + //
                "\t\t} else {\n" + //
                "\t\t\terrordetails = \"The replication  '\" + refernceID + \"' failed because of the following error:  \" + ex.toString()\n" + //
                "\t\t\tattachID  = \"Error Details'\" + refernceID + \"'\"\t\n" + //
                "\t\t}\n" + //
                "\n" + //
                "\t\tif (logException != null && logException.equalsIgnoreCase(\"TRUE\")) \n" + //
                "\t\t{\n" + //
                "\t\t\tmessageLog.addAttachmentAsString(attachID, errordetails, \"text/plain\");\n" + //
                "\t\t}\n" + //
                "\n" + //
                "\t\t// messageLog.addAttachmentAsString(\"Some Details\", \"These are some text details, how about that?\", \"text/plain\")\n" + //
                "\n" + //
                "\t\tmessageLog.setStringProperty(\"String Property 1\", \"This is some string data, which means text, doesn't it?\")\n" + //
                "\t\tmessageLog.setStringProperty(\"String Property 2\", \"This is some string data, which means text, doesn't it?\")\n" + //
                "\t\t\n" + //
                "\t\tmessageLog.addCustomHeaderProperty(\"Custom Header Property 1\", \"This is custom header property number 1\")\n" + //
                "\t\tmessageLog.addCustomHeaderProperty(\"Custom Header Property 2\", \"This is custom header property number 2\")\n" + //
                "\n" + //
                "\t\t// message.setProperty(\"http.ResponseBody\", errordetails.replaceAll(\"\\\\<\\\\?xml(.+?)\\\\?\\\\>\", \"\").trim());\n" + //
                "\t\t// message.setBody(message.getBody());\n" + //
                "\t\t// message.setProperty(\"http.StatusCode\", message.getHeaders().get(\"status\").toString()); \n" + //
                "\t} else {\n" + //
                "\t\tmessageLog.setProperty\n" + //
                "\t}\n" + //
                "\n" + //
                "\treturn message;\n" + //
                "}";

        public static final String 
            AEXC_TOKEN_FX_INSTANCE = ">>$$__FX_INSTANCE__$$<<";

        public static final String
            HTTP_INPUT_EXC_GROOVY_FILE_PATH = "/static/iflowdocument-template/src/main/resources/script/exceptionHandlingHttpIn.groovy";
}
