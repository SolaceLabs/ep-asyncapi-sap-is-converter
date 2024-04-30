package ep.asyncapi.tool.sap.is.converter.commons;

import com.solace.cloud.ep.designer.ApiClient;
import com.solace.cloud.ep.designer.Configuration;
import com.solace.cloud.ep.designer.auth.HttpBearerAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

//import javax.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSession;

@Component
@Slf4j
public class ApiClientUtil {

    @Value("${solace.api.ep.authTokenType}")
    String authTokenType;

    @Value("${solace.api.ep.basePath}")
    String basePath;


    public ApiClient initializeApiClient(final String epToken) {
        if (StringUtils.hasText(epToken)) {
            ApiClient apiClient = Configuration.getDefaultApiClient();
            apiClient.setBasePath(basePath);
            HttpBearerAuth APIToken = (HttpBearerAuth) apiClient.getAuthentication("APIToken");
            APIToken.setBearerToken(epToken);

            return apiClient;
        } else {
            log.error("Failed to initialize ApiClient: AuthToken is empty");
        }
        return null;
    }


    public ApiClient getApiClientFromSession(final HttpSession httpSession) {
        ApiClient apiClient = (ApiClient) httpSession.getAttribute("apiClient");
        if (apiClient == null) {
            log.warn("ApiClient is not coming in the session. Identify and fix!!");
            final String epTokenFromSession = (String) httpSession.getAttribute("validatedEpToken");
            apiClient = initializeApiClient(epTokenFromSession);
            httpSession.setAttribute("apiClient", apiClient);
        }
        return apiClient;
    }


}
