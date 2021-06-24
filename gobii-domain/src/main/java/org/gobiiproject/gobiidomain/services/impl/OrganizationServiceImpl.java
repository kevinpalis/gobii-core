package org.gobiiproject.gobiidomain.services.impl;

import java.util.ArrayList;
import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.OrganizationService;
import org.gobiiproject.gobiidtomapping.entity.auditable.DtoMapOrganization;
import org.gobiiproject.gobiimodel.dto.auditable.OrganizationDTO;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Phil on 4/27/2016.
 */
public class OrganizationServiceImpl implements OrganizationService {

    Logger LOGGER = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    @Autowired
    DtoMapOrganization dtoMapOrganization;



    @Override
    public List<OrganizationDTO> getOrganizations() throws GobiiDomainException {

        List<OrganizationDTO> returnVal;

        try {
            returnVal = dtoMapOrganization.getList();
            for(OrganizationDTO currentOrganizationDTO : returnVal ) {
                currentOrganizationDTO.getAllowedProcessTypes().add(GobiiProcessType.READ);
                currentOrganizationDTO.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);
            }


            if (null == returnVal) {
                returnVal = new ArrayList<>();
            }

        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);

        }

        return returnVal;
    }

    @Override
    public OrganizationDTO getOrganizationById(Integer organizationId) throws GobiiDomainException{

        OrganizationDTO returnVal;

        try {
            returnVal = dtoMapOrganization.get(organizationId);
            if (null == returnVal) {
                throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                        GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                        "The specified organizationId ("
                                + organizationId
                                + ") does not match an existing organization ");
            }
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);        

        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);

        }

        return returnVal;
    }

    @Override
    public OrganizationDTO createOrganization(OrganizationDTO organizationDTO) throws GobiiDomainException {
        OrganizationDTO returnVal;

        try {

            returnVal = dtoMapOrganization.create(organizationDTO);

            // When we have roles and permissions, this will be set programmatically
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }
        return returnVal;
    }

    @Override
    public OrganizationDTO replaceOrganization(Integer organizationId, OrganizationDTO organizationDTO) throws GobiiDomainException {
        OrganizationDTO returnVal;

        try {

            if (null == organizationDTO.getOrganizationId() ||
                    organizationDTO.getOrganizationId().equals(organizationId)) {


                OrganizationDTO existingOrganizationDTO = dtoMapOrganization.get(organizationId);
                if (null != existingOrganizationDTO.getOrganizationId() && existingOrganizationDTO.getOrganizationId().equals(organizationId)) {


                    returnVal = dtoMapOrganization.replace(organizationId, organizationDTO);
                    returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
                    returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

                } else {

                    throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                            GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                            "The specified organizationId ("
                                    + organizationId
                                    + ") does not match an existing organization ");
                }

            } else {

                throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                        GobiiValidationStatusType.BAD_REQUEST,
                        "The organizationId specified in the dto ("
                                + organizationDTO.getOrganizationId()
                                + ") does not match the organizationId passed as a parameter "
                                + "("
                                + organizationId
                                + ")");

            }


        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }


        return returnVal;
    }
}
