package org.gobiiproject.gobiiclient.gobii.Helpers;

import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiEnvelopeRestResource;
import org.gobiiproject.gobiimodel.dto.base.DTOBase;
import org.gobiiproject.gobiiapimodel.payload.HeaderStatusMessage;


/**
 * This class encapsulates common standard functionality that can be used for retreiving and
 * getting statistics about entities. It is used with the modern RestRequest client
 * infrastructure only. Note that getEnvelopeResultList() is implicitly testing that
 * if there are no items to be retrieved, the list has 0 items. There is no easy deterministic
 * way to do this -- this is the best we can do.
 * @param <T>
 */
public class DtoRestRequestUtils<T extends DTOBase> {

    private Class<T> dtoType;
    private RestResourceId restResourceId;

    /**
     * Constructs a DtoRestRequestUtils specic to a DTO type and a RestResourceId enum value.
     * @param dtoType
     * The class type of the DTO
     * @param restResourceId
     * The ServiceReuqestId for the corresponding URI
     */
    public DtoRestRequestUtils(Class<T> dtoType, RestResourceId restResourceId) {
        this.dtoType = dtoType;
        this.restResourceId = restResourceId;
    }

    /**
     * Returns a PaylaodEnvelope containing a list of the specified type.
     * If there is no data for the entity, the list should be empty.
     * @return A list of T
     * @throws Exception
     */
    public PayloadEnvelope<T> getEnvelopeResultList() throws Exception {

        PayloadEnvelope<T> returnVal;

        RestUri restUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(this.restResourceId);
        GobiiEnvelopeRestResource<T,T> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(restUri);

        returnVal = gobiiEnvelopeRestResource.get(dtoType);

        if (!returnVal.getHeader().getStatus().isSucceeded()) {
            String message = "Request for collection of "
                    + dtoType.getClass()
                    + " with request "
                    + restUri.makeUrlPath("")
                    + " did not succeded with URI "
                    + restUri.makeUrlPath("")
                    + ": ";

            for (HeaderStatusMessage headerStatusMessage : returnVal.getHeader().getStatus().getStatusMessages()) {
                message += headerStatusMessage.getMessage();
            }

            throw new Exception(message);
        }

        if (returnVal.getPayload().getData().size() > 0) {

            boolean gotNullResult = ((returnVal
                    .getPayload()
                    .getData()
                    .stream()
                    .filter(i -> i.getId() == null))
                    .count() > 0);

            if (gotNullResult) {

                String message = "When the collection "
                        + dtoType.getClass()
                        + " with request "
                        + restUri.makeUrlPath("")
                        + " has no results, it should return an empty list!!!";

                throw (new Exception(message));
            }
        }

        return returnVal;

    }


    /**
     * Gets the maximum PK value for the entity corresponding to the DTO type.
     * @return The Integer value of the maximum PK
     * @throws Exception
     */
    public Integer getMaxPkVal() throws Exception {

        Integer returnVal = 0;

        PayloadEnvelope<T> resultEnvelope = this.getEnvelopeResultList();

            T dto = resultEnvelope
                    .getPayload()
                    .getData()
                    .stream()
                    .max((p1, p2) -> Integer.compare(p1.getId(), p2.getId()))
                    .orElse(null);

            if (dto != null) {
                returnVal = dto.getId();
            }

        return returnVal;

    } //


    /**
     * Returns the PayloadEnvelope for a request to retrieve a single instance of the DTO type
     * with the PK value specified by the id.
     * @param id The PK value for the specified entity
     * @return The PayloadEnvelope containing the entity. The data collection should be 0 if there is
     * no corresponding entity for that PK.
     * @throws Exception
     */
    public PayloadEnvelope<T> getResponseEnvelopeForEntityId(String id) throws Exception {

        PayloadEnvelope<T> returnVal;

        RestUri restUriContact = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(this.restResourceId);

        restUriContact.setParamValue("id", id);
        GobiiEnvelopeRestResource<T,T> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(restUriContact);

        returnVal = gobiiEnvelopeRestResource
                .get(this.dtoType);

        return returnVal;

    }

}
