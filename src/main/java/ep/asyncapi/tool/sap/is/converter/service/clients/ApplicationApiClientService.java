package ep.asyncapi.tool.sap.is.converter.service.clients;

import com.solace.cloud.ep.designer.ApiClient;
import com.solace.cloud.ep.designer.api.ApplicationsApi;
import com.solace.cloud.ep.designer.model.ApplicationVersionsResponse;
import com.solace.cloud.ep.designer.model.ApplicationsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class ApplicationApiClientService {
    private ApplicationsApi applicationsApi;

    public ApplicationsResponse getApplicationsForAppDomainId(final ApiClient apiClient, final String appDomainId, final int pageNumber) {
        applicationsApi = new ApplicationsApi(apiClient);
        try {
            return applicationsApi.getApplications(10, pageNumber, null, appDomainId, null, null, null, null);
        } catch (Exception exception) {
            log.error("Error encountered in ApplicationApiClientService.getApplicationsForAppDomainId, Exception:", exception);
            return null;
        }
    }


    public ApplicationVersionsResponse getApplicationsVersionsForAppId(final ApiClient apiClient, final String applicationId, final int pageNumber) {
        applicationsApi = new ApplicationsApi(apiClient);
        try {
            return applicationsApi.getApplicationVersions(10, pageNumber, new HashSet<>(Set.of(applicationId)), null, null, new HashSet<>(Set.of("1", "2")));
        } catch (Exception exception) {
            log.error("Error encountered in ApplicationApiClientService.getApplicationsVersionsForAppId, Exception:", exception);
            return null;
        }
    }

    public String getAsyncApiSpecForAppVersion(final ApiClient apiClient, final String applicationVersionId) {
        applicationsApi = new ApplicationsApi(apiClient);
        try {
            return applicationsApi.getAsyncApiForApplicationVersion(applicationVersionId, "json", null, null, null, null, null);
        } catch (Exception exception) {
            log.error("Error encountered in ApplicationApiClientService.getAsycnApiSpecForAppVersion, Exception:", exception);
            return "";
        }
    }
}
