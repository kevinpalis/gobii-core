package org.gobiiproject.gobiiprocess.digester.utils.validation;

import org.apache.commons.lang.StringUtils;
import org.gobiiproject.gobiiapimodel.payload.HeaderStatusMessage;
import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.payload.Status;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiapimodel.restresources.gobii.GobiiUriFactory;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiEnvelopeRestResource;
import org.gobiiproject.gobiimodel.config.RestResourceId;

import org.gobiiproject.gobiimodel.config.ServerConfigItem;
import org.gobiiproject.gobiimodel.cvnames.CvGroup;
import org.gobiiproject.gobiimodel.dto.entity.auditable.ExperimentDTO;
import org.gobiiproject.gobiimodel.dto.entity.auditable.MapsetDTO;
import org.gobiiproject.gobiimodel.dto.entity.auditable.PlatformDTO;
import org.gobiiproject.gobiimodel.dto.entity.auditable.ProjectDTO;
import org.gobiiproject.gobiimodel.dto.entity.children.NameIdDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.gobiiproject.gobiimodel.types.GobiiFilterType;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.gobiiproject.gobiiprocess.digester.utils.validation.errorMessage.Failure;
import org.gobiiproject.gobiiprocess.digester.utils.validation.errorMessage.FailureTypes;

import java.net.URL;
import java.util.*;

public class ValidationWebServicesUtil {

    public static boolean loginIntoServer(String url, String username, String password, String crop, List<Failure> failures) {
        try {
            GobiiClientContext.getInstance(url, true).getCurrentClientCropType();
            String contextRoot = new URL(url).getPath();
            if ('/' != contextRoot.charAt(contextRoot.length() - 1))
                contextRoot = contextRoot + "/";
            List<String> crops = GobiiClientContext.getInstance(null, false).getCropTypeTypes();
            for (String currentCrop : crops) {
                ServerConfigItem currentServerConfig = GobiiClientContext.getInstance(null, false).getServerConfig(currentCrop);
                if (contextRoot.equals(currentServerConfig.getContextRoot())) {
                    // use the crop for this server config
                    crop = currentCrop;
                    break;
                }
            }
            if (crop == null || crop.isEmpty()) {
                Failure failure = new Failure();
                failure.reason = FailureTypes.LOGIN_FAILURE;
                failure.values.add("Undefined crop for server: " + url);
                failures.add(failure);
                return false;
            }

            boolean login = GobiiClientContext.getInstance(url, true).login(crop, username, password);
            if (!login) {
                String failureMessage = GobiiClientContext.getInstance(null, false).getLoginFailure();

                Failure failure = new Failure();
                failure.reason = FailureTypes.LOGIN_FAILURE;
                failure.values.add(failureMessage);
                failures.add(failure);
                return false;
            }
            return true;
        } catch (Exception e) {
            Failure failure = new Failure();
            failure.reason = FailureTypes.LOGIN_FAILURE;
            failure.values.add(e.getMessage());
            failures.add(failure);
            return false;
        }
    }

    /**
     * Gets the allowed values for foreign key
     */
    public static Map<String, String> getAllowedForeignKeyList(String foreignKey, List<Failure> failureList) throws MaximumErrorsValidationException {
        Map<String, String> mapsetDTOList = new HashMap<>();
        try {
            GobiiUriFactory uriFactory = GobiiClientContext.getInstance(null, false).getUriFactory();
            RestUri restUri;
            if (foreignKey.equalsIgnoreCase(ValidationConstants.LINKAGE_GROUP)) {
                restUri = uriFactory.resourceColl(RestResourceId.GOBII_MAPSET);
                GobiiEnvelopeRestResource<MapsetDTO, MapsetDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(restUri);
                PayloadEnvelope<MapsetDTO> resultEnvelope = gobiiEnvelopeRestResource.get(MapsetDTO.class);
                resultEnvelope.getPayload().getData().forEach(dto -> mapsetDTOList.put(dto.getMapsetId().toString(), dto.getName()));
                return mapsetDTOList;
            } else if (foreignKey.equalsIgnoreCase(ValidationConstants.DNARUN)) {
                restUri = uriFactory.resourceColl(RestResourceId.GOBII_EXPERIMENTS);
                GobiiEnvelopeRestResource<ExperimentDTO, ExperimentDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(restUri);
                PayloadEnvelope<ExperimentDTO> resultEnvelope = gobiiEnvelopeRestResource.get(ExperimentDTO.class);
                resultEnvelope.getPayload().getData().forEach(dto -> mapsetDTOList.put(dto.getExperimentId().toString(), dto.getExperimentName()));
                return mapsetDTOList;
            } else if (foreignKey.equalsIgnoreCase(ValidationConstants.DNASAMPLE_NAME)) {
                restUri = uriFactory.resourceColl(RestResourceId.GOBII_PROJECTS);
                GobiiEnvelopeRestResource<ProjectDTO, ProjectDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(restUri);
                PayloadEnvelope<ProjectDTO> resultEnvelope = gobiiEnvelopeRestResource.get(ProjectDTO.class);
                resultEnvelope.getPayload().getData().forEach(dto -> mapsetDTOList.put(dto.getProjectId().toString(), dto.getProjectName()));
                return mapsetDTOList;
            } else {
                ValidationUtil.createFailure(FailureTypes.UNDEFINED_FOREIGN_KEY, new ArrayList<>(), foreignKey, failureList);
                return mapsetDTOList;
            }
        } catch (Exception e) {
            ValidationUtil.createFailure(FailureTypes.EXCEPTION, new ArrayList<>(), e.getMessage(), failureList);
            return mapsetDTOList;
        }
    }

    /**
     * Verifies whether the Platform Id is valid or not
     */
    public static Map<String, String> validatePlatformId(String platformId, List<Failure> failureList) throws MaximumErrorsValidationException {
        Map<String, String> mapsetDTOList = new HashMap<>();
        try {
            RestUri restUri = GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .resourceByUriIdParam(RestResourceId.GOBII_PLATFORM);
            restUri.setParamValue("id", platformId);
            GobiiEnvelopeRestResource<PlatformDTO, PlatformDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(restUri);
            PayloadEnvelope<PlatformDTO> resultEnvelope = gobiiEnvelopeRestResource.get(PlatformDTO.class);
            if (resultEnvelope.getHeader().getStatus().isSucceeded())
                resultEnvelope.getPayload().getData().forEach(dto -> mapsetDTOList.put(dto.getPlatformId().toString(), dto.getPlatformName()));
            else
                ValidationUtil.createFailure(FailureTypes.UNDEFINED_PLATFORM_ID, new ArrayList<>(), resultEnvelope.getHeader().getStatus().messages(), failureList);
            return mapsetDTOList;
        } catch (Exception e) {
            ValidationUtil.createFailure(FailureTypes.EXCEPTION, new ArrayList<>(), e.getMessage(), failureList);
            return mapsetDTOList;
        }
    }

    /**
     * Web service call to validate CV and reference type
     *
     * @param nameIdDTOList       Items list
     * @param gobiiEntityNameType CV or Reference
     * @param filterValue         filter value
     * @param failureList         failure list
     * @return Items list with id
     * @throws MaximumErrorsValidationException exception
     */
    public static List<NameIdDTO> getNamesByNameList(List<NameIdDTO> nameIdDTOList, String gobiiEntityNameType, String filterValue, List<Failure> failureList) throws MaximumErrorsValidationException {
        List<NameIdDTO> nameIdDTOListResponse = new ArrayList<>();
        try {
            PayloadEnvelope<NameIdDTO> payloadEnvelope = new PayloadEnvelope<>();
            payloadEnvelope.getHeader().setGobiiProcessType(GobiiProcessType.CREATE);
            payloadEnvelope.getPayload().setData(nameIdDTOList);

            RestUri namesUri = GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .nameIdListByQueryParams();
            GobiiEnvelopeRestResource<NameIdDTO, NameIdDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(namesUri);
            namesUri.setParamValue("entity", gobiiEntityNameType.toLowerCase());

            namesUri.setParamValue("filterType", StringUtils.capitalize(GobiiFilterType.NAMES_BY_NAME_LIST.toString().toUpperCase()));
            if (gobiiEntityNameType.equalsIgnoreCase(GobiiEntityNameType.CV.toString())) {
                switch (filterValue) {
                    case "species_name":
                        namesUri.setParamValue("filterValue", CvGroup.CVGROUP_GERMPLASM_SPECIES.getCvGroupName());
                        break;
                    case "type_name":
                        namesUri.setParamValue("filterValue", CvGroup.CVGROUP_GERMPLASM_TYPE.getCvGroupName());
                        break;
                    case "strand_name":
                        namesUri.setParamValue("filterValue", CvGroup.CVGROUP_MARKER_STRAND.getCvGroupName());
                        break;
                    default:
                        ValidationUtil.createFailure(FailureTypes.UNDEFINED_CV, new ArrayList<>(), filterValue, failureList);
                        return nameIdDTOListResponse;
                }
            } else namesUri.setParamValue("filterValue", filterValue);

            PayloadEnvelope<NameIdDTO> responsePayloadEnvelope = gobiiEnvelopeRestResource.post(NameIdDTO.class, payloadEnvelope);
            Status status = responsePayloadEnvelope.getHeader().getStatus();
            if (!status.isSucceeded()) {
                ArrayList<HeaderStatusMessage> statusMessages = status.getStatusMessages();
                for (HeaderStatusMessage message : statusMessages)
                    ValidationUtil.createFailure(FailureTypes.DATABASE_ERROR, new ArrayList<>(), message.getMessage(), failureList);
                return nameIdDTOListResponse;
            }
            nameIdDTOListResponse.addAll(responsePayloadEnvelope.getPayload().getData());
        } catch (MaximumErrorsValidationException e) {
            throw e;
        } catch (Exception e) {
            ValidationUtil.createFailure(FailureTypes.EXCEPTION, new ArrayList<>(), e.getMessage(), failureList);
        }
        return nameIdDTOListResponse;
    }
}
