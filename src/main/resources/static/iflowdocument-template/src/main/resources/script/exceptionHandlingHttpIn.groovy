import com.sap.gateway.ip.core.customdev.util.Message;

def Message inputExceptionProcess_http(Message message) {
	
	// get a map of iflow properties
	def map = message.getProperties()
	def refernceID = map.get("ReferenceId")		// ReferenceID could be an Identifier
	def logException = map.get("ExceptionLogging")	// Externalized parameter "ExceptionLogging" is set to "True" to log exceptions
	def attachID = ""
	def errordetails = ""
	
	// get an exception java class instance
	def ex = map.get("CamelExceptionCaught")
	if (ex!=null) 
	{
		// save the error response as a message attachment 
		def messageLog = messageLogFactory.getMessageLog(message);
		if (refernceID == null || refernceID == "" )
		{
			errordetails = "The  replication failed because of the following error:  " + ex.toString()
			attachID  = "Error Details"
		} else {
			errordetails = "The replication  '" + refernceID + "' failed because of the following error:  " + ex.toString()
			attachID  = "Error Details'" + refernceID + "'"	
		}
		if (logException != null && logException.equalsIgnoreCase("TRUE")) 
		{
			messageLog.addAttachmentAsString(attachID, errordetails, "text/plain");
		}
	
		message.setProperty("http.ResponseBody", errordetails.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim());
		message.setBody(message.getBody());
		message.setProperty("http.StatusCode", message.getHeaders().get("status").toString()); 
		message.setProperty("ExceptionMessage", map.get("ExceptionMessage").replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim());
	}
	return message;
}