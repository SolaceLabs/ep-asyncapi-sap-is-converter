import com.sap.gateway.ip.core.customdev.util.Message;

/**********************************************************************
	Auto-Generated Exception Script
	This is a base-line script to log exceptions. See Control Properties
	below to understand how they impact script behavior.
	
	Enhance the script as required to suit your use case!!!
**********************************************************************/

def Message processData(Message message) {

    /**
    *   Control Properties:
    *   - logException  - Set by Externalized Parameter "ExceptionLogging"
    *                     If True, activates enhanced logging
    *
    *   - logStackTrace - Set by Externalized Parameter "LogStackTrace"
    *                     If True, (and ExceptionLogging is True),
    *                     Then include java stack trace in Exception Logs
    *
    *   - referenceID   - Set from message property "ReferenceID"
    *                     A user property for reference in logs to make them
    *                     more informative. "ReferenceID" property must be assigned
    *                     from message payload or another source by a developer.
    *                     e.g. Set to PO Number, Shipment ID, Customer ID, Message ID etc.
    */

	// get a map of iflow properties
	def map = message.getProperties()
	def referenceID = map.get("ReferenceId")		// ReferenceID could be an Identifier
	def logException = map.get("ExceptionLogging")	// Externalized parameter "ExceptionLogging" is set to "True" to log exceptions
	def logStackTrace = map.get("LogStackTrace")	// Externalized parameter "LogStackTrace" is set to "True" to include stack trace in exception logs
	def attachID = ""
	def errordetails = ""

	// get an exception java class instance
	def ex = map.get("CamelExceptionCaught")
	if (ex != null) 
	{
		// save the error response as a message attachment 
		def messageLog = messageLogFactory.getMessageLog(message);
		def logMessage = new StringBuilder()
		def stackTrace = ex.getStackTrace()
		
		logMessage.append("Error Message: ").append(ex.getMessage())

		// IF logStackTrace == TRUE, THEN Check for stack trace, add to logMessage
		if (logStackTrace != null && logStackTrace.equalsIgnoreCase("TRUE") && stackTrace != null && stackTrace.length > 0)
		{
			logMessage.append("Stack Trace:\n")
			stackTrace.each { element ->
				logMessage.append(element.toString()).append("\n")
			}
		}

		// Check if referenceID is null
		if (referenceID == null || referenceID == "" )
		{
			errordetails = "Processing failed because of the following error:  " + logMessage.toString()
			attachID  = "Error Details"
		}
		else {
			errordetails = "Processing '" + referenceID + "' failed because of the following error:  " + logMessage.toString()
			attachID  = "Error Details '" + referenceID + "'"	
		}

		//Check if the ExceptionLogging is configured 
		if (logException != null && logException.equalsIgnoreCase("TRUE")) 
		{
			messageLog.addAttachmentAsString(attachID, errordetails, "text/plain");
		}

		message.setProperty("http.ResponseBody", errordetails.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim());
		message.setBody(message.getBody());
		message.setProperty("http.StatusCode", message.getHeaders().get("status").toString()); 
	} else {
		messageLog.setProperty
	}

	return message;
}