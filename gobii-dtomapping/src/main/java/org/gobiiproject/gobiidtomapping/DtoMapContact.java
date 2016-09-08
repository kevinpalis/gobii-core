package org.gobiiproject.gobiidtomapping;

import org.gobiiproject.gobiimodel.dto.container.ContactDTO;
import org.gobiiproject.gobiimodel.dto.response.RequestEnvelope;
import org.gobiiproject.gobiimodel.dto.response.ResultEnvelope;

/**
 * Created by Anggel on 5/4/2016.
 */
public interface DtoMapContact {

    ContactDTO getContactDetails(Integer contactId) throws Exception;
    ResultEnvelope<ContactDTO> createContact(RequestEnvelope<ContactDTO> requestEnvelope) throws GobiiDtoMappingException;
    ResultEnvelope<ContactDTO> updateContact(RequestEnvelope<ContactDTO> requestEnvelope) throws GobiiDtoMappingException;

}
