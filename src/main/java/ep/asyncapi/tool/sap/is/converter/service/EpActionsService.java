package ep.asyncapi.tool.sap.is.converter.service;

import com.solace.cloud.ep.designer.ApiClient;
import com.solace.ep.muleflow.mapper.asyncapi.AsyncApiToMuleDocMapper;
import com.solace.ep.muleflow.mapper.model.MapMuleDoc;
import ep.asyncapi.tool.sap.is.converter.models.ApplicationDTO;
import ep.asyncapi.tool.sap.is.converter.models.ApplicationDomainDTO;
import ep.asyncapi.tool.sap.is.converter.models.ApplicationVersionDTO;
import ep.asyncapi.tool.sap.is.converter.service.apis.SolaceCloudV0APIClient;
import ep.asyncapi.tool.sap.is.converter.service.clients.ApplicationApiClientService;
import ep.asyncapi.tool.sap.is.converter.service.clients.ApplicationDomainApiClientService;
import ep.asyncapi.tool.sap.is.converter.service.converter.SapIFlowConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openapitools.client.model.Application;
import org.openapitools.client.model.ApplicationDomain;
import org.openapitools.client.model.ApplicationVersion;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
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

    public EpActionsService(final SolaceCloudV0APIClient solaceCloudV0APIClient,
                            final ApplicationDomainApiClientService applicationDomainApiClientService,
                            final ApplicationApiClientService applicationApiClientService,
                            final SapIFlowConverter sapIFlowConverter) {
        this.solaceCloudV0APIClient = solaceCloudV0APIClient;
        this.applicationDomainApiClientService = applicationDomainApiClientService;
        this.applicationApiClientService = applicationApiClientService;
        this.sapIFlowConverter = sapIFlowConverter;
    }

    public boolean validateUserEPToken(final String userEpToken) {
        if (StringUtils.hasText(userEpToken)) {
            HttpStatus epTokenValidationStatusCode = solaceCloudV0APIClient.validateUserEPToken(userEpToken);
            if (epTokenValidationStatusCode == HttpStatus.OK) {
                log.info("User EP Token input validated");
                return true;
            } else {
                log.error("User EP token input could not be validated");
            }
        }
        return false;
    }

    public List<ApplicationDomainDTO> getAllApplicationDomains(final ApiClient apiClient) {
        final List<ApplicationDomain> applicationDomains = applicationDomainApiClientService.getAllApplicationDomains(apiClient);
        return applicationDomains.stream().map(applicationDomain -> {
            ApplicationDomainDTO dto = new ApplicationDomainDTO();
            dto.setId(applicationDomain.getId());
            dto.setName(applicationDomain.getName());
            dto.setDescription(applicationDomain.getDescription());
            return dto;
        }).collect(Collectors.toList());
    }

    public List<ApplicationDTO> getAllApplicationsForAppDomain(final ApiClient apiClient, final String appDomainId) {
        final List<Application> applications = applicationApiClientService.getApplicationsForAppDomainId(apiClient, appDomainId);
        return applications.stream().map(application -> {
            ApplicationDTO dto = new ApplicationDTO();
            dto.setId(application.getId());
            dto.setName(application.getName());
            dto.setNumberOfVersions(application.getNumberOfVersions());
            return dto;
        }).collect(Collectors.toList());
    }


    public List<ApplicationVersionDTO> getApplicationVersionsForAppId(final ApiClient apiClient, final String applicationId) {
        final List<ApplicationVersion> applicationVersions = applicationApiClientService.getApplicationsVersionsForAppId(apiClient, applicationId);
        return applicationVersions.stream().map(applicationVersion -> {
            ApplicationVersionDTO dto = new ApplicationVersionDTO();
            dto.setId(applicationVersion.getId());
            dto.setDescription(applicationVersion.getDescription());
            dto.setVersion(applicationVersion.getVersion());
            dto.setState("1".equals(applicationVersion.getStateId()) ? "Draft" : "2".equals(applicationVersion.getStateId()) ? "Released" : "Unknown");

            return dto;
        }).collect(Collectors.toList());
    }


    public byte[] generateISWorkflowArtefactForAppVersion(final ApiClient apiClient, final String appVersionId) {
        try {

            //Get AsyncAPI spec
            final String asyncApiSpecForAppVersion = applicationApiClientService.getAsycnApiSpecForAppVersion(apiClient, appVersionId);

            //Handle AsyncAPI spec

            MapMuleDoc mapMuleDoc = AsyncApiToMuleDocMapper.mapMuleDocFromAsyncApi(asyncApiSpecForAppVersion);
            final String appVersionTitle = mapMuleDoc.getGlobalProperties().get("epApplicationVersionTitle");
            final String appSemanticVersion = mapMuleDoc.getGlobalProperties().get("epApplicationVersion");

            //create temp directory
            final File mainDirectory = new File(appVersionTitle);
            FileUtils.forceMkdir(mainDirectory);

            final File resourcesSubDirectory = new File(mainDirectory, "src/main/resources");
            FileUtils.forceMkdir(resourcesSubDirectory);

            final File jsonSubDirectory = new File(resourcesSubDirectory, "json");
            FileUtils.forceMkdir(jsonSubDirectory);

            final File mappingSubDirectory = new File(resourcesSubDirectory, "mapping");
            FileUtils.forceMkdir(mappingSubDirectory);

            final File integrationFlowSubDirectory = new File(resourcesSubDirectory, "scenarioflows/integrationflow");
            FileUtils.forceMkdir(integrationFlowSubDirectory);

            //project file:
            sapIFlowConverter.createProjectFile(appVersionTitle, mainDirectory);
            //metaInfo.prop
            sapIFlowConverter.createMetaInfoPropFile(appVersionId, mainDirectory);
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
            zip(directory, directory.getName(), zos);
        }
        return zipFile;
    }

    private void zip(File directory, String baseName, ZipOutputStream zos) throws IOException {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                zip(file, baseName + File.separator + file.getName(), zos);
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
