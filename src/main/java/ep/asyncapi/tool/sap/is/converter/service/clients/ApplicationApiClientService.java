package ep.asyncapi.tool.sap.is.converter.service.clients;

import com.solace.cloud.ep.designer.ApiClient;

import com.solace.cloud.ep.designer.api.ApplicationsApi;
import com.solace.cloud.ep.designer.model.Application;
import com.solace.cloud.ep.designer.model.ApplicationVersion;
import com.solace.cloud.ep.designer.model.ApplicationVersionsResponse;
import com.solace.cloud.ep.designer.model.ApplicationsResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class ApplicationApiClientService {
    private ApplicationsApi applicationsApi;

    public List<Application> getApplicationsForAppDomainId(final ApiClient apiClient, final String appDomainId) {
        applicationsApi = new ApplicationsApi(apiClient);
        try {
            ApplicationsResponse response = applicationsApi.getApplications(20, 1, null, appDomainId, null, null, null, null);
            return response.getData();
        } catch (Exception exception) {
            log.error("Error encountered in ApplicationApiClientService.getApplicationsForAppDomainId, Exception:", exception);
            return Collections.EMPTY_LIST;
        }
    }


    public List<ApplicationVersion> getApplicationsVersionsForAppId(final ApiClient apiClient, final String applicationId) {
        applicationsApi = new ApplicationsApi(apiClient);
        try {
            ApplicationVersionsResponse applicationVersionsResponse = applicationsApi.getApplicationVersions(20, 1, new HashSet<>(Set.of(applicationId)), null, null, new HashSet<>(Set.of("1", "2")));
            return applicationVersionsResponse.getData();
        } catch (Exception exception) {
            log.error("Error encountered in ApplicationApiClientService.getApplicationsVersionsForAppId, Exception:", exception);
            return Collections.EMPTY_LIST;
        }
    }

    public String getAsycnApiSpecForAppVersion(final ApiClient apiClient, final String applicationVersionId) {
        applicationsApi = new ApplicationsApi(apiClient);
        try {
            return applicationsApi.getAsyncApiForApplicationVersion(applicationVersionId, "json", null, null, null, null, null);
        } catch (Exception exception) {
            log.error("Error encountered in ApplicationApiClientService.getAsycnApiSpecForAppVersion, Exception:", exception);
            return "";
        }
    }


}
