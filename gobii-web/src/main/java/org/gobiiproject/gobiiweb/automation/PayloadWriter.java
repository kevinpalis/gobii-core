package org.gobiiproject.gobiiweb.automation;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.gobiiproject.gobiiapimodel.hateos.Link;
import org.gobiiproject.gobiiapimodel.payload.HeaderAuth;
import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.common.ResourceParam;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiapimodel.types.GobiiHttpHeaderNames;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiimodel.dto.base.DTOBase;
import org.gobiiproject.gobiimodel.dto.system.PagedList;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiimodel.types.RestMethodType;
import org.gobiiproject.gobiimodel.utils.LineUtils;

/**
 * Created by Phil on 9/25/2016.
 */
@SuppressWarnings("unused")
public class PayloadWriter<T extends DTOBase> {

    private final Class<T> dtoType;
    private HttpServletRequest httpServletRequest; //TODO: what is this for?
    private HttpServletResponse httpServletResponse;
    private String gobiiWebVersion;

    public PayloadWriter(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         Class<T> dtoType) throws Exception {

        this.dtoType = dtoType;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;

        this.gobiiWebVersion = GobiiVersionInfo.getVersion();
    }

    /***
     * For most DTOs, in the response we want to include HATEOS  links that are
     * in line with the DTOs permissions. the generic type for this class extends DTOBase, so we are
     * theoretically guaranteed it must have the getId() methd and the set of allowable actions.
     * That being said, there are DTOs in which the getId() is not meaningful. That it is sometimes
     * meaningful and sometimes not breaks inheritance and is at best clumsy OO design. However, the
     * overall shape of the generalization is sound. In the case where we the ID is not HATEOSable,
     * the id parameter can be set to null, in which case the HATEOS links are not created.
     * @param payloadEnvelope
     * @param restUri
     * @param itemToWrite
     * @param id
     * @throws Exception
     */
    public void writeSingleItemForId(PayloadEnvelope<T> payloadEnvelope,
                                     RestUri restUri,
                                     T itemToWrite,
                                     String id,
                                     String cropType) throws Exception {

        if ((null != itemToWrite) &&
                !LineUtils.isNullOrEmpty(id) &&
                (itemToWrite.getId() != null) &&
                (itemToWrite.getId() > 0)) {

            if (itemToWrite.getClass() == this.dtoType) {

                payloadEnvelope.getPayload().getData().add(itemToWrite);

                if (restUri != null) {
                    restUri.setParamValue("id", id);
                    //And hence we can create the link here

                    String uri = restUri.makeUrlPath(cropType);
                    Link link = new Link(uri, "Link to " + dtoType + ", id " + id);

                    for (GobiiProcessType currentProcessType : itemToWrite.getAllowedProcessTypes()) {

                        switch (currentProcessType) {

                            case CREATE:
                                link.getMethods().add(RestMethodType.POST);
                                break;
                            case READ:
                                link.getMethods().add(RestMethodType.GET);
                                break;
                            case UPDATE:
                                link.getMethods().add(RestMethodType.PUT);
                                // add PATCH when we support that
                                break;
                            case DELETE:
                                link.getMethods().add(RestMethodType.DELETE);
                            default:
                                break;
                        }
                    }
                    payloadEnvelope.getPayload().getLinkCollection().getLinksPerDataItem().add(link);
                }


                payloadEnvelope.getHeader().setGobiiVersion(this.gobiiWebVersion);
                setAuthHeader(payloadEnvelope.getHeader().getDtoHeaderAuth(), this.httpServletResponse);
                payloadEnvelope.getHeader().setCropType(payloadEnvelope.getHeader().getDtoHeaderAuth().getGobiiCropType());

            } else {
                throw new GobiiWebException(GobiiStatusLevel.VALIDATION,
                        GobiiValidationStatusType.BAD_REQUEST,
                        "The  data item type ("
                                + itemToWrite.getClass()
                                + ") does not match the intended type("
                                + this.dtoType
                                + ")");
            }
        }
    }

    public void writeSingleItemForDefaultId(PayloadEnvelope<T> payloadEnvelope,
                                            RestUri restUri,
                                            T itemToWrite,
                                            String cropType) throws GobiiWebException, Exception {

        if ((null != itemToWrite) && (itemToWrite.getId() != null)) {
            String id = itemToWrite.getId().toString();

            this.writeSingleItemForId(payloadEnvelope,
                    restUri,
                    itemToWrite,
                    id, cropType);
        }


        payloadEnvelope.getHeader().setGobiiVersion(this.gobiiWebVersion);

    }

    public void writeList(PayloadEnvelope<T> payloadEnvelope,
                          RestUri restUri,
                          List<T> itemsToWrite,
                          String cropType) throws GobiiWebException, Exception {

        for (T currentItem : itemsToWrite) {
            this.writeSingleItemForDefaultId(payloadEnvelope, restUri, currentItem, cropType);
        }


        payloadEnvelope.getHeader().setGobiiVersion(this.gobiiWebVersion);
    }

    public void writeListFromPagedQuery(PayloadEnvelope<T> payloadEnvelope,
                                        RestUri restUri,
                                        PagedList<T> pagedListToWrite,
                                        String cropType) throws GobiiWebException, Exception {

        for (T currentItem : pagedListToWrite.getDtoList()) {
            this.writeSingleItemForDefaultId(payloadEnvelope, restUri, currentItem, cropType);
        }

        payloadEnvelope.getHeader().setPagination(pagedListToWrite.getPagingQueryId(),
                pagedListToWrite.getQueryTime(),
                pagedListToWrite.getPageSize(),
                pagedListToWrite.getTotalPages(),
                pagedListToWrite.getCurrentPageNo());

        payloadEnvelope.getHeader().setGobiiVersion(this.gobiiWebVersion);
    }


    public void setAuthHeader(HeaderAuth headerAuth, HttpServletResponse response) {

        String userName = response.getHeader(GobiiHttpHeaderNames.HEADER_NAME_USERNAME);
        String token = response.getHeader(GobiiHttpHeaderNames.HEADER_NAME_TOKEN);
        String gobiiCropType = response.getHeader(GobiiHttpHeaderNames.HEADER_NAME_GOBII_CROP);

        headerAuth.setToken(token);
        headerAuth.setGobiiCropType(gobiiCropType);
        headerAuth.setUserName(userName);

    }


    public void setCallLimitToHeader(PayloadEnvelope<T> payloadEnvelope,
                                     RestUri restUri) throws Exception {
        if (restUri != null) {
            RestResourceId restResourceId = restUri.getRestResourceId();
            if (restResourceId != null) {

                Integer callLimitGet = null;
                Integer callLimitPost = null;
                Integer callLimitPut = null;

                // If there are any template parameters in the uri, we have to just grab
                // all of them and interrogate the call profiles as to whether any parameter
                // in particular returns a limit
                if (restUri.getTemplateParams().size() <= 0) {

                    callLimitGet = RestResourceLimits.getResourceLimit(restResourceId, RestMethodType.GET);
                    callLimitPost = RestResourceLimits.getResourceLimit(restResourceId, RestMethodType.POST);
                    callLimitPut = RestResourceLimits.getResourceLimit(restResourceId, RestMethodType.PUT);

                } else {

                    for (ResourceParam currentResourceParam : restUri.getTemplateParams()) {

                        String currentTemplateParam = currentResourceParam.getValue().toUpperCase();
                        callLimitGet = RestResourceLimits.getResourceLimit(restResourceId, RestMethodType.GET, currentTemplateParam);
                        callLimitPost = RestResourceLimits.getResourceLimit(restResourceId, RestMethodType.POST, currentTemplateParam);
                        callLimitPut = RestResourceLimits.getResourceLimit(restResourceId, RestMethodType.PUT, currentTemplateParam);

                        //if any one got a valid limit, it means that, if there were any others for the current
                        //template parameter, they would have been set; so here we want to call it quits
                        //the limitation to this approach is of course that you can only have only one template
                        //parameter per method type
                        if( callLimitGet != null || callLimitPost != null || callLimitPut != null ) {
                            break;
                        }

                    }

                }

                payloadEnvelope.getHeader().setMaxGet(callLimitGet);
                payloadEnvelope.getHeader().setMaxPost(callLimitPost);
                payloadEnvelope.getHeader().setMaxPut(callLimitPut);

            }
        }
    } // setCallLimitToHeader()
}
