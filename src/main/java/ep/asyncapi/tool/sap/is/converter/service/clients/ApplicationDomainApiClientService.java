package ep.asyncapi.tool.sap.is.converter.service.clients;

import com.solace.cloud.ep.designer.ApiClient;
import com.solace.cloud.ep.designer.api.ApplicationDomainsApi;
import com.solace.cloud.ep.designer.model.ApplicationDomainsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApplicationDomainApiClientService {

    public ApplicationDomainsResponse getPaginatedApplicationDomains(final ApiClient apiClient, final int pageNumber) {
        ApplicationDomainsApi applicationDomainsApi = new ApplicationDomainsApi(apiClient);
        try {
            return applicationDomainsApi.getApplicationDomains(10, pageNumber, null, null, null);
        } catch (Exception exception) {
            log.error("Error encountered in ApplicationDomainApiClientService.getAllApplicationDomains, Exception:{}", exception);
            return null;
        }

    }
}
