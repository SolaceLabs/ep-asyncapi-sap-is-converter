package ep.asyncapi.tool.sap.is.converter.service.clients;

import com.solace.cloud.ep.designer.ApiClient;
import com.solace.cloud.ep.designer.client.ApplicationDomainsApi;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.client.model.ApplicationDomain;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ApplicationDomainApiClientService {
    public List<ApplicationDomain> getAllApplicationDomains(final ApiClient apiClient) {
        ApplicationDomainsApi applicationDomainsApi = new ApplicationDomainsApi(apiClient);
        try {
            return applicationDomainsApi.getApplicationDomains(20, 1, null, null, null).getData();
        } catch (Exception exception) {
            log.error("Error encountered in ApplicationDomainApiClientService.getAllApplicationDomains, Exception:{}", exception);
            return Collections.EMPTY_LIST;
        }
    }
}
