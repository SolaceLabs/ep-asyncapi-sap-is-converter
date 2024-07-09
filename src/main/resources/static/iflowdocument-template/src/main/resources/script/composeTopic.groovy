import com.sap.gateway.ip.core.customdev.util.Message

def Message processData(Message message) {
    final GP_TOPICVAL = "gpath_topicval_";
    final GP_COMPOSED_PATTERN = "gpath_composedTopicPattern";
    final COMPOSED_TOPIC = "composedTopic";
    try {
        final properties = message.getProperties();
        
        def composedTopicString = properties.get(GP_COMPOSED_PATTERN);
        if (composedTopicString == null || composedTopicString.length() == 0) {
            // Log Error
            return;
        }
        if (!composedTopicString.contains("{")) {
            // Debug log, static topic detected
            message.setProperty(COMPOSED_TOPIC, composedTopicString);
            return;
        }

        properties.each { key, val ->
            if (key.startsWith(GP_TOPICVAL)) {
                def varName = "{" + key.replace(GP_TOPICVAL, "") + "}";
                def varVal = val;
                if (varVal == null) {
                    varVal = "NULL";
                }
                if (varVal.toString().isEmpty()) {
                    varVal = "EMPTY";
                }
                composedTopicString = composedTopicString.replace(varName, varVal);
            }
        }
  
        // Log composed topic      
        message.setProperty(COMPOSED_TOPIC, composedTopicString);

    } catch (Exception ex) {
        message.setProperty('gpath_error', ex.getMessage());
        // Log Error
    }
    return message;
}