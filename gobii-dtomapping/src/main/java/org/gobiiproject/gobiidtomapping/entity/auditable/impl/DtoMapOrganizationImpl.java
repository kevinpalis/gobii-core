package org.gobiiproject.gobiidtomapping.entity.auditable.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.gobiiproject.gobiidao.resultset.access.RsOrganizationDao;
import org.gobiiproject.gobiidao.resultset.core.ParamExtractor;
import org.gobiiproject.gobiidao.resultset.core.ResultColumnApplicator;
import org.gobiiproject.gobiidao.resultset.core.listquery.DtoListQueryColl;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiidtomapping.entity.auditable.DtoMapOrganization;
import org.gobiiproject.gobiidtomapping.entity.auditable.DtoMapProtocol;
import org.gobiiproject.gobiimodel.dto.auditable.OrganizationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Angel on 5/4/2016.
 */
public class DtoMapOrganizationImpl implements DtoMapOrganization {

    Logger LOGGER = LoggerFactory.getLogger(DtoMapOrganizationImpl.class);

    @Autowired
    private DtoListQueryColl dtoListQueryColl;

    @Autowired
    private RsOrganizationDao rsOrganizationDao;

    @Autowired
    private DtoMapProtocol dtoMapProtocol;

    @SuppressWarnings("unchecked")
    @Override
    public List<OrganizationDTO> getList() throws GobiiDtoMappingException {

        List<OrganizationDTO> returnVal = new ArrayList<OrganizationDTO>();

        try {

            returnVal = (List<OrganizationDTO>) dtoListQueryColl.getList(ListSqlId.QUERY_ID_ORGANIZATION_ALL);

            for( OrganizationDTO currentOrganizationDto : returnVal ) {
                this.dtoMapProtocol.addVendorProtocolsToOrganization(currentOrganizationDto);
            }

        } catch (Exception e) {
            LOGGER.error("Gobii Maping Error", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;
    }

    @Override
    public OrganizationDTO get(Integer organizationId) throws GobiiDtoMappingException {

        OrganizationDTO returnVal = new OrganizationDTO();

        try {

            ResultSet resultSet = rsOrganizationDao.getOrganizationDetailsByOrganizationId(organizationId);

            if (resultSet.next()) {

                // apply organization values
                ResultColumnApplicator.applyColumnValues(resultSet, returnVal);
                this.dtoMapProtocol.addVendorProtocolsToOrganization(returnVal);
            } // if result set has a row

        } catch (Exception e) {
            LOGGER.error("Gobii Maping Error", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;

    } // get()

    @Override
    public OrganizationDTO create(OrganizationDTO organizationDTO) throws GobiiDtoMappingException {
        OrganizationDTO returnVal = organizationDTO;

        try {

            Map<String, Object> parameters = ParamExtractor.makeParamVals(organizationDTO);
            Integer organizationId = rsOrganizationDao.createOrganization(parameters);
            returnVal.setOrganizationId(organizationId);

        } catch (Exception e) {

            LOGGER.error("Gobii Maping Error", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;
    }


    @Override
    public OrganizationDTO replace(Integer organizationId, OrganizationDTO organizationDTO) throws GobiiDtoMappingException {

        OrganizationDTO returnVal = organizationDTO;

        try {

            Map<String, Object> parameters = ParamExtractor.makeParamVals(returnVal);
            parameters.put("organizationId", organizationId);
            rsOrganizationDao.updateOrganization(parameters);

        } catch (Exception e) {

            LOGGER.error("Gobii Maping Error", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;
    }
}
