package org.gobiiproject.gobiidomain.services.impl;

import java.util.ArrayList;
import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.ContactService;
import org.gobiiproject.gobiidtomapping.entity.auditable.DtoMapContact;
import org.gobiiproject.gobiimodel.dto.auditable.ContactDTO;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Angel on 5/4/2016.
 */
public class ContactServiceImpl implements ContactService {

    Logger LOGGER = LoggerFactory.getLogger(ContactServiceImpl.class);

    @Autowired
    DtoMapContact dtoMapContact = null;

    @Override
    public List<ContactDTO> getContacts() throws GobiiDomainException {

        List<ContactDTO> returnVal;

        try {
            returnVal = dtoMapContact.getList();
            for(ContactDTO currentContactDTO : returnVal ) {
                currentContactDTO.getAllowedProcessTypes().add(GobiiProcessType.READ);
                currentContactDTO.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);
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
    public ContactDTO createContact(ContactDTO contactDTO) throws GobiiDomainException {

        ContactDTO returnVal;

        try {

            returnVal = dtoMapContact.create(contactDTO);

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
    public ContactDTO replaceContact(Integer contactId, ContactDTO contactDTO) throws GobiiDomainException {

        ContactDTO returnVal;

        try {

            if (null == contactDTO.getContactId() ||
                    contactDTO.getContactId().equals(contactId)) {


                ContactDTO existingContactDTO = dtoMapContact.get(contactId);
                if (null != existingContactDTO.getContactId() && existingContactDTO.getContactId().equals(contactId)) {


                    returnVal = dtoMapContact.replace(contactId, contactDTO);
                    returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
                    returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

                } else {

                    throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                            GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                            "The specified contactId ("
                                    + contactId
                                    + ") does not match an existing contact ");
                }

            } else {

                throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                        GobiiValidationStatusType.BAD_REQUEST,
                        "The contactId specified in the dto ("
                                + contactDTO.getContactId()
                                + ") does not match the contactId passed as a parameter "
                                + "("
                                + contactId
                                + ")");

            }


        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }


        return returnVal;
    }

    @Override
    public ContactDTO getContactById(Integer contactId) throws GobiiDomainException {

        ContactDTO returnVal;

        try {
            returnVal = dtoMapContact.get(contactId);
            if (null == returnVal) {
                throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                        GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                        "The specified contactId ("
                                + contactId
                                + ") does not match an existing contact ");
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
    public ContactDTO getContactByEmail(String email) {

        ContactDTO returnVal;
        try {
            returnVal = dtoMapContact.getByEmail(email);
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);

        }

        return returnVal;
    }

    @Override
    public ContactDTO getContactByUserName(String userName) throws GobiiDomainException {

        ContactDTO returnVal;

        try {
            returnVal = dtoMapContact.getByUserName(userName);
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);

        }

        return  returnVal;
    }

    @Override
    public ContactDTO getContactByLastName(String lastName) {
        return null;
    }

    @Override
    public ContactDTO getContactByFirstName(String email, String lastName, String firstName) {
        return null;
    }
}