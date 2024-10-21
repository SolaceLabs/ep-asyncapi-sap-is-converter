package ep.asyncapi.tool.sap.is.converter.service;

import com.solace.cloud.ep.designer.ApiClient;
import com.solace.cloud.ep.designer.model.ApplicationDomainsResponse;
import com.solace.cloud.ep.designer.model.ApplicationVersionsResponse;
import com.solace.cloud.ep.designer.model.ApplicationsResponse;
import com.solace.ep.codegen.asyncapi.mapper.AsyncApiToMuleDocMapper;
import com.solace.ep.codegen.internal.model.MapMuleDoc;
import ep.asyncapi.tool.sap.is.converter.models.*;
import ep.asyncapi.tool.sap.is.converter.service.apis.SolaceCloudV0APIClient;
import ep.asyncapi.tool.sap.is.converter.service.clients.ApplicationApiClientService;
import ep.asyncapi.tool.sap.is.converter.service.clients.ApplicationDomainApiClientService;
import ep.asyncapi.tool.sap.is.converter.service.converter.SapIFlowConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class EpActionsService {

    private final SolaceCloudV0APIClient solaceCloudV0APIClient;
    private final ApplicationDomainApiClientService applicationDomainApiClientService;
    private final ApplicationApiClientService applicationApiClientService;
    private final SapIFlowConverter sapIFlowConverter;
    private int runCount = 0;

    public EpActionsService(final SolaceCloudV0APIClient solaceCloudV0APIClient, final ApplicationDomainApiClientService applicationDomainApiClientService, final ApplicationApiClientService applicationApiClientService, final SapIFlowConverter sapIFlowConverter) {
        this.solaceCloudV0APIClient = solaceCloudV0APIClient;
        this.applicationDomainApiClientService = applicationDomainApiClientService;
        this.applicationApiClientService = applicationApiClientService;
        this.sapIFlowConverter = sapIFlowConverter;
    }

    public boolean validateUserEPToken(final String userEpToken) {
        if (StringUtils.hasText(userEpToken)) {
            HttpStatusCode epTokenValidationStatusCode = solaceCloudV0APIClient.validateUserEPToken(userEpToken);
            if (epTokenValidationStatusCode == HttpStatus.OK) {
                log.info("User EP Token input validated");
                return true;
            } else {
                log.error("User EP token input could not be validated");
            }
        }
        return false;
    }

    public PaginatedApplicationDomainDTO getPaginatedApplicationDomains(final ApiClient apiClient, final int pageNumber) {
        final ApplicationDomainsResponse applicationDomainsResponse = applicationDomainApiClientService.getPaginatedApplicationDomains(apiClient, pageNumber);
        final PaginatedApplicationDomainDTO paginatedApplicationDomainDTO = new PaginatedApplicationDomainDTO();
        paginatedApplicationDomainDTO.setApplicationDomainDTOList(applicationDomainsResponse.getData().stream().map(applicationDomain -> {
            ApplicationDomainDTO dto = new ApplicationDomainDTO();
            dto.setId(applicationDomain.getId());
            dto.setName(applicationDomain.getName());
            dto.setDescription(applicationDomain.getDescription());
            return dto;
        }).collect(Collectors.toList()));
        paginatedApplicationDomainDTO.setTotalCount(applicationDomainsResponse.getMeta().getPagination().getCount());
        paginatedApplicationDomainDTO.setTotalPages(applicationDomainsResponse.getMeta().getPagination().getTotalPages());
        paginatedApplicationDomainDTO.setCurrentPage(applicationDomainsResponse.getMeta().getPagination().getPageNumber());
        paginatedApplicationDomainDTO.setPageSize(applicationDomainsResponse.getMeta().getPagination().getPageSize());
        return paginatedApplicationDomainDTO;
    }


    public PaginatedApplicationDTO getAllApplicationsForAppDomain(final ApiClient apiClient, final String appDomainId, final int pageNumber) {
        final ApplicationsResponse applicationResponse = applicationApiClientService.getApplicationsForAppDomainId(apiClient, appDomainId, pageNumber);
        final PaginatedApplicationDTO paginatedApplicationDTO = new PaginatedApplicationDTO();
        paginatedApplicationDTO.setApplicationDTOList(applicationResponse.getData().stream().map(application -> {
                    ApplicationDTO dto = new ApplicationDTO();
                    dto.setId(application.getId());
                    dto.setName(application.getName());
                    dto.setNumberOfVersions(application.getNumberOfVersions());
                    return dto;
                }).collect(Collectors.toList())

        );
        paginatedApplicationDTO.setTotalCount(applicationResponse.getMeta().getPagination().getCount());
        paginatedApplicationDTO.setTotalPages(applicationResponse.getMeta().getPagination().getTotalPages());
        paginatedApplicationDTO.setCurrentPage(applicationResponse.getMeta().getPagination().getPageNumber());
        paginatedApplicationDTO.setPageSize(applicationResponse.getMeta().getPagination().getPageSize());
        return paginatedApplicationDTO;
    }


    public PaginatedApplicationVersionDTO getApplicationVersionsForAppId(final ApiClient apiClient, final String applicationId, final int pageNumber) {
        final ApplicationVersionsResponse applicationVersionsResponse = applicationApiClientService.getApplicationsVersionsForAppId(apiClient, applicationId, pageNumber);
        final PaginatedApplicationVersionDTO paginatedVersionDTO = new PaginatedApplicationVersionDTO();
        paginatedVersionDTO.setApplicationVersionDTOList(applicationVersionsResponse.getData().stream().map(applicationVersion -> {
            ApplicationVersionDTO dto = new ApplicationVersionDTO();
            dto.setId(applicationVersion.getId());
            dto.setDescription(applicationVersion.getDescription());
            dto.setVersion(applicationVersion.getVersion());
            dto.setState("1".equals(applicationVersion.getStateId()) ? "Draft" : "2".equals(applicationVersion.getStateId()) ? "Released" : "Unknown");

            return dto;
        }).collect(Collectors.toList()));
        paginatedVersionDTO.setTotalCount(applicationVersionsResponse.getMeta().getPagination().getCount());
        paginatedVersionDTO.setTotalPages(applicationVersionsResponse.getMeta().getPagination().getTotalPages());
        paginatedVersionDTO.setCurrentPage(applicationVersionsResponse.getMeta().getPagination().getPageNumber());
        paginatedVersionDTO.setPageSize(applicationVersionsResponse.getMeta().getPagination().getPageSize());
        return paginatedVersionDTO;
    }


    public byte[] generateISWorkflowArtefactForAppVersion(final ApiClient apiClient, final String appVersionId) {
        try {
            //Get AsyncAPI spec
            final String asyncApiSpecForAppVersion = applicationApiClientService.getAsyncApiSpecForAppVersion(apiClient, appVersionId);

            //Handle AsyncAPI spec
            MapMuleDoc mapMuleDoc = AsyncApiToMuleDocMapper.mapMuleDocFromAsyncApi(asyncApiSpecForAppVersion);
            final String appVersionTitle = mapMuleDoc.getGlobalProperties().get("epApplicationVersionTitle");
            final String appSemanticVersion = mapMuleDoc.getGlobalProperties().get("epApplicationVersion");
            final String appDescription = mapMuleDoc.getGlobalProperties().get("epApplicationVersionDescription");

            final File tmpDirectory = FileUtils.getTempDirectory();

            final File mainDirectory = new File(tmpDirectory, appVersionTitle + "_" + runCount++);
            FileUtils.forceMkdir(mainDirectory);

            log.debug("Creating project structure in system temp directory: {}", mainDirectory.getAbsolutePath());

            final File resourcesSubDirectory = new File(mainDirectory, "src/main/resources");
            FileUtils.forceMkdir(resourcesSubDirectory);

            final File jsonSubDirectory = new File(resourcesSubDirectory, "json");
            FileUtils.forceMkdir(jsonSubDirectory);

            final File mappingSubDirectory = new File(resourcesSubDirectory, "mapping");
            FileUtils.forceMkdir(mappingSubDirectory);

            final File integrationFlowSubDirectory = new File(resourcesSubDirectory, "scenarioflows/integrationflow");
            FileUtils.forceMkdir(integrationFlowSubDirectory);

            final File scriptSubDirectory = new File(resourcesSubDirectory, "script");
            FileUtils.forceMkdir(scriptSubDirectory);

            //project file:
            sapIFlowConverter.createProjectFile(appVersionTitle, mainDirectory);
            //metaInfo.prop
            sapIFlowConverter.createMetaInfoPropFile(appVersionId, mainDirectory);
            // README.md
            sapIFlowConverter.createReadmeFile(appVersionTitle, appSemanticVersion, appDescription, mainDirectory);
            //meta-inf folder
            sapIFlowConverter.createMetaInfFolderAndFiles(appVersionTitle, appSemanticVersion, mainDirectory);
            //resources/parameters prop
            sapIFlowConverter.createParametersProp(resourcesSubDirectory);
            //resources/parameters-propdef
            sapIFlowConverter.createParametersPropDef(resourcesSubDirectory);
            //resources/jsons
            sapIFlowConverter.createSchemaJsonsFolderAndFiles(jsonSubDirectory, mapMuleDoc);
            //resources/mappings/validateMmapFiles
            sapIFlowConverter.createValidationMmapFiles(mappingSubDirectory, mapMuleDoc);
            //resources/mappings/toDestinationFormatMmapFiles
            sapIFlowConverter.createToDestinationFormatMmapFiles(mappingSubDirectory, mapMuleDoc);
            //resources/mappings/sourceToDestinationFormatMmapFiles
            sapIFlowConverter.createSourceToDestinationFormatMmapFiles(mappingSubDirectory, mapMuleDoc);
            //resources/scenarios/integrationflow
            sapIFlowConverter.createIntegrationFlowFiles(integrationFlowSubDirectory, mapMuleDoc);
            //resources/script/
            sapIFlowConverter.createDynamicTopicScriptFiles(scriptSubDirectory, mapMuleDoc);

            //create zip file
            final File zipFile = createZipFile(mainDirectory);

            return FileUtils.readFileToByteArray(zipFile);
        } catch (Exception exception) {
            log.error("Error encountered while generating the IS Workflow Artefact", exception);
        }
        return null;
    }

    private File createZipFile(File directory) throws IOException {
        File zipFile = new File(directory.getParent(), directory.getName() + ".zip");
        try (FileOutputStream fos = new FileOutputStream(zipFile); ZipOutputStream zos = new ZipOutputStream(fos)) {
            zip(directory, "", zos);
        }
        return zipFile;
    }

    private void zip(File directory, String baseName, ZipOutputStream zos) throws IOException {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                zip(file, (baseName.isEmpty() ? (baseName + File.separator) : "") + file.getName(), zos);
            } else {
                byte[] buffer = new byte[1024];
                try (FileInputStream fis = new FileInputStream(file)) {
                    zos.putNextEntry(new ZipEntry(baseName + File.separator + file.getName()));
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }
            }
        }
    }

}
