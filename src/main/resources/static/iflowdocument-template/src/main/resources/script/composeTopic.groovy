/*
This script will build a topic string with variable elements by extracting
values from a JSON message body
- If there are no variable elements in the topic string then then the set value will be passed on
- If there is no composedTopic pattern then the script will do nothing
*/
import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import groovy.json.JsonSlurper;

def Message processData(Message message) {
    final GP_TOPICEXP = 'gpath_topicexp_';
    final GP_TOPICVAL = 'gpath_topicval_';
    final GP_COMPOSED_PATTERN = "gpath_composedTopicPattern";
    final COMPOSED_TOPIC = "composedTopic";
    
    def properties = message.getProperties();
    def topicValProperties = new HashMap<String, String>();
    
    try {
        def composedTopicString = properties.get(GP_COMPOSED_PATTERN);
        if (composedTopicString == null || composedTopicString.length() == 0) {
            // This could be an error, or the topic may be set directly on the publisher
            // (bypass composed topic logic)
            return message;
        }
        if (!composedTopicString.contains("{")) {
            // Debug log, static topic detected
            message.setProperty(COMPOSED_TOPIC, composedTopicString);
            return message;
        }

        // Extract dynamic topic values from message body        
        final js = new groovy.json.JsonSlurper();
        final request = js.parseText(message.getBody(java.lang.String) as String);
        properties.each { prop, texp ->
            if (prop.startsWith(GP_TOPICEXP)) {
                def varval = groovy.util.Eval.x(request, 'x.' + texp);
                topicValProperties.put(GP_TOPICVAL + prop.replace(GP_TOPICEXP, ""), varval.toString());
            } else if (prop.startsWith(GP_TOPICVAL)) {
                topicValProperties.put(prop, texp.toString());
            }
        }
        
        // Add Extracted values to message properties
        topicValProperties.each { key, value ->
            def varName = "{" + key.replace(GP_TOPICVAL, "") + "}";
            def varVal = value;
            if (varVal == null || varVal.isEmpty()) {
                varVal = "NULL";
            }
            composedTopicString = composedTopicString.replace(varName, varVal);
        }
        
        // Set the composed topic to expected property
        message.setProperty(COMPOSED_TOPIC, composedTopicString);

    } catch (Exception ex) {
        message.setProperty('gpath_error', ex.getMessage());
    }
    return message;
}