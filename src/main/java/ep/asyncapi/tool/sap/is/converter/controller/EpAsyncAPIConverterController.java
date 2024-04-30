package ep.asyncapi.tool.sap.is.converter.controller;

import com.solace.cloud.ep.designer.ApiClient;
import ep.asyncapi.tool.sap.is.converter.commons.ApiClientUtil;
import ep.asyncapi.tool.sap.is.converter.models.ApplicationDTO;
import ep.asyncapi.tool.sap.is.converter.models.ApplicationDomainDTO;
import ep.asyncapi.tool.sap.is.converter.models.ApplicationVersionDTO;
import ep.asyncapi.tool.sap.is.converter.models.EpTokenModel;
import ep.asyncapi.tool.sap.is.converter.service.EpActionsService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

// import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

@Controller
@Slf4j
public class EpAsyncAPIConverterController {

    private final EpActionsService epActionsService;
    private final ApiClientUtil apiClientUtil;

    public EpAsyncAPIConverterController(final EpActionsService epActionsService, final ApiClientUtil apiClientUtil) {
        this.epActionsService = epActionsService;
        this.apiClientUtil = apiClientUtil;
    }

    @GetMapping
    public String homePage(final Model model) {
        log.debug("Loading homepage for user");
        EpTokenModel epTokenModel = EpTokenModel.builder().build();
        model.addAttribute("epTokenModel", epTokenModel);
        return "home";
    }

    @PostMapping("/validateUserEPToken")
    public String validateUserEPToken(final HttpSession httpSession, final Model model, EpTokenModel epTokenModel) {
        final String epToken = epTokenModel.getEpToken();
        
        log.debug("Validating user EP token input:{}", epToken);
        if (StringUtils.hasText(epToken)) {
            boolean isUserEpTokenValidated = epActionsService.validateUserEPToken(epToken);
            model.addAttribute("isUserEpTokenValidated", isUserEpTokenValidated);
            if (isUserEpTokenValidated) {
                httpSession.setAttribute("validatedEpToken", epToken);
                ApiClient apiClient = apiClientUtil.initializeApiClient(epToken);
                httpSession.setAttribute("apiClient", apiClient);
                List<ApplicationDomainDTO> applicationDomainDTOList = epActionsService.getAllApplicationDomains(apiClient);
                model.addAttribute("applicationDomainDTOList", applicationDomainDTOList);
            }
        } else {
            log.error("User EP Token input is empty");
            model.addAttribute("isUserEpTokenValidated", false);
        }
        model.addAttribute("epTokenModel", epTokenModel);
        return "home";
    }

    @GetMapping(path = "/{appDomainId}/applications", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ApplicationDTO> getApplicationsForAppDomain(@PathVariable String appDomainId, final HttpSession httpSession) {
        if (StringUtils.hasText(appDomainId)) {
            ApiClient apiClient = apiClientUtil.getApiClientFromSession(httpSession);
            return epActionsService.getAllApplicationsForAppDomain(apiClient, appDomainId);
        }
        return Collections.EMPTY_LIST;
    }


    @GetMapping(path = "/{appDomainId}/applications/{applicationId}/versions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ApplicationVersionDTO> getVersionsForApplication(@PathVariable final String appDomainId, @PathVariable final String applicationId, final HttpSession httpSession) {
        if (StringUtils.hasText(appDomainId) && StringUtils.hasText(applicationId)) {
            ApiClient apiClient = apiClientUtil.getApiClientFromSession(httpSession);
            return epActionsService.getApplicationVersionsForAppId(apiClient, applicationId);
        }
        return Collections.EMPTY_LIST;
    }

    @GetMapping("/{appDomainId}/applications/{appId}/versions/{appVersionId}/isArtefact")
    public ResponseEntity<byte[]> generateISArtefactDownload(@PathVariable final String appDomainId, @PathVariable final String appId, @PathVariable final String appVersionId, final HttpSession httpSession) {
        log.debug("appDomainId:{}", appDomainId);
        log.debug("appId:{}", appId);
        log.debug("appVersionId:{}", appVersionId);
        ApiClient apiClient = apiClientUtil.getApiClientFromSession(httpSession);
        byte[] appVersionIsWorkflowArtefact = epActionsService.generateISWorkflowArtefactForAppVersion(apiClient, appVersionId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "isArtefact.zip");
        return ResponseEntity.ok().headers(headers).body(appVersionIsWorkflowArtefact);
    }


}
