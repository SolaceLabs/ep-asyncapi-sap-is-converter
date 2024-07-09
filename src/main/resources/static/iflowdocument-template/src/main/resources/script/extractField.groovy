/* Developed with https://ide.contiva.com */
import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import groovy.json.JsonSlurper;

def Message processData(Message message) {
    final GP_TOPICEXP = 'gpath_topicexp_';
    final GP_TOPICVAL = 'gpath_topicval_';
    def properties = message.getProperties();
    def newProperties = new HashMap<String, String>();
  try {
    final js = new groovy.json.JsonSlurper();
    final request = js.parseText(message.getBody(java.lang.String) as String);
    
    properties.each { prop, texp ->
        if (prop.startsWith(GP_TOPICEXP)) {
            def varval = groovy.util.Eval.x(request, 'x.' + texp);
            def varvalAsString = varval.toString();
            newProperties.put(GP_TOPICVAL + prop.replace(GP_TOPICEXP, ""), varvalAsString);
        }
    }
    
    // Add Extracted values to message properties
    newProperties.each { key, value ->
        message.setProperty( key, value );
    }

  } catch (Exception ex) {
    message.setProperty('gpath_error', ex.getMessage());
  }
  return message;
}