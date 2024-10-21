package ep.asyncapi.tool.sap.is.converter.controller;

import com.solace.cloud.ep.designer.ApiClient;
import ep.asyncapi.tool.sap.is.converter.commons.ApiClientUtil;
import ep.asyncapi.tool.sap.is.converter.models.EpTokenModel;
import ep.asyncapi.tool.sap.is.converter.models.PaginatedApplicationDTO;
import ep.asyncapi.tool.sap.is.converter.models.PaginatedApplicationDomainDTO;
import ep.asyncapi.tool.sap.is.converter.models.PaginatedApplicationVersionDTO;
import ep.asyncapi.tool.sap.is.converter.service.EpActionsService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
    public String homePage(@RequestParam(required = false, defaultValue = "") String error, final Model model) {
        log.debug("Loading homepage for user");
        EpTokenModel epTokenModel = EpTokenModel.builder().build();
        model.addAttribute("epTokenModel", epTokenModel);
        if (StringUtils.hasText(error) && "SESSION_EXPIRED".equals(error)) {
            model.addAttribute("errorMessageFlag", error);
        }
        return "home";
    }

    @PostMapping("/validateUserEPToken")
    public String validateUserEPToken(final HttpSession httpSession, final Model model, EpTokenModel epTokenModel) {
        final String epToken = epTokenModel.getEpToken();
        log.info("Validating user EP token");
        if (StringUtils.hasText(epToken)) {
            boolean isUserEpTokenValidated = epActionsService.validateUserEPToken(epToken);
            model.addAttribute("isUserEpTokenValidated", isUserEpTokenValidated);
            log.info("Is User's EP Token validated: {}", isUserEpTokenValidated);
            if (isUserEpTokenValidated) {
                httpSession.setAttribute("validatedEpToken", epToken);
                ApiClient apiClient = apiClientUtil.initializeApiClient(epToken);
                httpSession.setAttribute("apiClient", apiClient);
                log.info("Retrieving application domains for user token");
                final PaginatedApplicationDomainDTO paginatedApplicationDomainDTO = epActionsService.getPaginatedApplicationDomains(apiClient, 1);
                model.addAttribute("paginatedApplicationDomainDTO", paginatedApplicationDomainDTO);
            }
        } else {
            log.error("User EP Token input is empty");
            model.addAttribute("isUserEpTokenValidated", false);
        }
        model.addAttribute("epTokenModel", epTokenModel);
        return "home";
    }

    @GetMapping(path = "/applicationDomains", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object getPaginatedApplicationDomains(final HttpSession httpSession, @RequestParam(defaultValue = "1") int pageNumber) {
        log.info("Retrieving paginated application domains for user");
        final ApiClient apiClient = apiClientUtil.getApiClientFromSession(httpSession);
        if (ObjectUtils.isEmpty(apiClient)) {
            return "redirect:/?error=" + URLEncoder.encode("SESSION_EXPIRED", StandardCharsets.UTF_8);
        }
        return epActionsService.getPaginatedApplicationDomains(apiClient, pageNumber);
    }

    @GetMapping(path = "/{appDomainId}/applications", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object getApplicationsForAppDomain(@PathVariable String appDomainId, final HttpSession httpSession, @RequestParam(defaultValue = "1") int pageNumber) {
        if (StringUtils.hasText(appDomainId)) {
            log.info("Retrieving applications for user app domain id");
            final ApiClient apiClient = apiClientUtil.getApiClientFromSession(httpSession);
            if (ObjectUtils.isEmpty(apiClient)) {
                return "redirect:/?error=" + URLEncoder.encode("SESSION_EXPIRED", StandardCharsets.UTF_8);
            }
            return epActionsService.getAllApplicationsForAppDomain(apiClient, appDomainId, pageNumber);
        }
        return new PaginatedApplicationDTO();
    }


    @GetMapping(path = "/{appDomainId}/applications/{applicationId}/versions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object getVersionsForApplication(@PathVariable final String appDomainId, @PathVariable final String applicationId,
                                            final HttpSession httpSession, @RequestParam(defaultValue = "1") int pageNumber) {
        if (StringUtils.hasText(appDomainId) && StringUtils.hasText(applicationId)) {
            log.info("Retrieving applications versions for user app id");
            ApiClient apiClient = apiClientUtil.getApiClientFromSession(httpSession);
            if (ObjectUtils.isEmpty(apiClient)) {
                return "redirect:/?error=" + URLEncoder.encode("SESSION_EXPIRED", StandardCharsets.UTF_8);
            }
            return epActionsService.getApplicationVersionsForAppId(apiClient, applicationId, pageNumber);
        }
        return new PaginatedApplicationVersionDTO();
    }

    @GetMapping("/{appDomainId}/applications/{appId}/versions/{appVersionId}/isArtefact")
    public ResponseEntity<byte[]> generateISArtefactDownload(@PathVariable final String appDomainId, @PathVariable final String appId, @PathVariable final String appVersionId, final HttpSession httpSession) {
        log.info("Generating IS artefact for user app version");
        ApiClient apiClient = apiClientUtil.getApiClientFromSession(httpSession);
        byte[] appVersionIsWorkflowArtefact = epActionsService.generateISWorkflowArtefactForAppVersion(apiClient, appVersionId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "isArtefact.zip");
        return ResponseEntity.ok().headers(headers).body(appVersionIsWorkflowArtefact);
    }


}
