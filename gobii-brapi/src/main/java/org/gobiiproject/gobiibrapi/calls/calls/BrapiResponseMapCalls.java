package org.gobiiproject.gobiibrapi.calls.calls;

import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiibrapi.types.BrapiDataTypes;
import org.gobiiproject.gobiimodel.dto.brapi.ServerInfoDTO;
import org.gobiiproject.gobiimodel.types.RestMethodType;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Phil on 12/15/2016.
 */
public class BrapiResponseMapCalls {

    String contextRoot = null;

    public BrapiResponseMapCalls(HttpServletRequest request) {

        this.contextRoot = request.getContextPath();
    }

    private List<BrapiResponseCallsItem> getBrapiResponseCallsItems()
            throws Exception {

        List<BrapiResponseCallsItem> returnVal = new ArrayList<>();

        //returnVal.add(new BrapiResponseCallsItem(new RestUri(this.contextRoot,
        //        GobiiControllerType.BRAPI.getControllerPath(),
        //        RestResourceId.BRAPI_LOGIN.getResourcePath()),
        //        Arrays.asList(RestMethodType.POST),
        //        Arrays.asList(BrapiDataTypes.JSON)));

        returnVal.add(new BrapiResponseCallsItem(new RestUri(this.contextRoot,
                GobiiControllerType.BRAPI.getControllerPath(),
                RestResourceId.BRAPI_CALLS.getResourcePath()),
                Arrays.asList(RestMethodType.GET),
                Arrays.asList(BrapiDataTypes.JSON)));

        returnVal.add(new BrapiResponseCallsItem(new RestUri(this.contextRoot,
                GobiiControllerType.BRAPI.getControllerPath(),
                RestResourceId.BRAPI_STUDIES_SEARCH.getResourcePath()),
                Arrays.asList(RestMethodType.POST),
                Arrays.asList(BrapiDataTypes.JSON)));

        returnVal.add(new BrapiResponseCallsItem(new RestUri(this.contextRoot,
                GobiiControllerType.BRAPI.getControllerPath(),
                RestResourceId.BRAPI_ALLELE_MATRICES.getResourcePath()).addQueryParam("studyDbId"),
                Arrays.asList(RestMethodType.GET),
                Arrays.asList(BrapiDataTypes.JSON)));

        returnVal.add(new BrapiResponseCallsItem(new RestUri(this.contextRoot,
                GobiiControllerType.BRAPI.getControllerPath(),
                RestResourceId.BRAPI_ALLELE_MATRIX_SEARCH.getResourcePath()),
                Arrays.asList(RestMethodType.GET, RestMethodType.POST),
                Arrays.asList(BrapiDataTypes.FLAPJACK)));


        return returnVal;
    }

    public List<ServerInfoDTO> getBrapi2ServerInfos() {

        List<ServerInfoDTO> returnVal = new ArrayList<>();

        returnVal.add(
                new ServerInfoDTO(
                        RestResourceId.BRAPI_LOGIN.getResourcePath(),
                        Arrays.asList(HttpMethod.POST),
                        Arrays.asList(MediaType.APPLICATION_JSON),
                        Arrays.asList("1.0", "2.0")));

        returnVal.add(
                new ServerInfoDTO(
                        RestResourceId.BRAPI_STUDIES_SEARCH.getResourcePath(),
                        Arrays.asList(HttpMethod.GET),
                        Arrays.asList(MediaType.APPLICATION_JSON),
                        Arrays.asList("1.0")));

        returnVal.add(
                new ServerInfoDTO(
                        RestResourceId.BRAPI_ALLELE_MATRICES.getResourcePath(),
                        Arrays.asList(HttpMethod.GET),
                        Arrays.asList(MediaType.APPLICATION_JSON),
                        Arrays.asList("1.0")));

        returnVal.add(
                new ServerInfoDTO(
                        RestResourceId.BRAPI_ALLELE_MATRIX_SEARCH
                                .getResourcePath(),
                        Arrays.asList(HttpMethod.GET),
                        Arrays.asList(MediaType.APPLICATION_JSON),
                        Arrays.asList("1.0")));

        returnVal.add(new ServerInfoDTO(
                RestResourceId.BRAPI_SERVER_INFO.getResourcePath(),
                Arrays.asList(HttpMethod.GET),
                Arrays.asList(MediaType.APPLICATION_JSON),
                Arrays.asList("2.0")));

        returnVal.add(new ServerInfoDTO(
                RestResourceId.BRAPI_STUDIES.getResourcePath(),
                Arrays.asList(HttpMethod.GET),
                Arrays.asList(MediaType.APPLICATION_JSON),
                Arrays.asList("2.0")));

        returnVal.add(new ServerInfoDTO(
                RestResourceId.BRAPI_MAPS_CALLS.getResourcePath(),
                Arrays.asList(HttpMethod.GET),
                Arrays.asList(MediaType.APPLICATION_JSON),
                Arrays.asList("2.0")));

        returnVal.add(new ServerInfoDTO(
                RestResourceId.BRAPI_MARKER_POSITIONS.getResourcePath(),
                Arrays.asList(HttpMethod.GET),
                Arrays.asList(MediaType.APPLICATION_JSON),
                Arrays.asList("2.0")));

        returnVal.add(new ServerInfoDTO(
                RestResourceId.BRAPI_CALLSETS.getResourcePath(),
                Arrays.asList(HttpMethod.GET),
                Arrays.asList(MediaType.APPLICATION_JSON),
                Arrays.asList("2.0")));

        returnVal.add(new ServerInfoDTO(
                RestResourceId.BRAPI_CALLSETS_BY_ID.getResourcePath(),
                Arrays.asList(HttpMethod.GET),
                Arrays.asList(MediaType.APPLICATION_JSON),
                Arrays.asList("2.0")));

        returnVal.add(new ServerInfoDTO(
                RestResourceId.BRAPI_VARIANTS.getResourcePath(),
                Arrays.asList(HttpMethod.GET),
                Arrays.asList(MediaType.APPLICATION_JSON),
                Arrays.asList("2.0")));

        returnVal.add(new ServerInfoDTO(
                RestResourceId.BRAPI_VARIANTS_BY_ID.getResourcePath(),
                Arrays.asList(HttpMethod.GET),
                Arrays.asList(MediaType.APPLICATION_JSON),
                Arrays.asList("2.0")));

        returnVal.add(new ServerInfoDTO(
                RestResourceId.BRAPI_VARIANTSETS.getResourcePath(),
                Arrays.asList(HttpMethod.GET),
                Arrays.asList(MediaType.APPLICATION_JSON),
                Arrays.asList("2.0")));

        returnVal.add(new ServerInfoDTO(
                RestResourceId.BRAPI_GENOTYPES_BY_VARIANTSET.getResourcePath(),
                Arrays.asList(HttpMethod.GET),
                Arrays.asList(MediaType.APPLICATION_JSON),
                Arrays.asList("2.0")));

        returnVal.add(new ServerInfoDTO(
                RestResourceId.BRAPI_DOWNLOAD_GENOTYPES_BY_VARIANTSET
                        .getResourcePath(),
                Arrays.asList(HttpMethod.GET),
                Arrays.asList(MediaType.APPLICATION_JSON),
                Arrays.asList("2.0")));

        returnVal.add(new ServerInfoDTO(
                RestResourceId.BRAPI_CALLSETS_BY_VARIANTSET.getResourcePath(),
                Arrays.asList(HttpMethod.GET),
                Arrays.asList(MediaType.APPLICATION_JSON),
                Arrays.asList("2.0")));

        returnVal.add(new ServerInfoDTO(
                RestResourceId.BRAPI_VARIANTS_BY_VARIANTSET.getResourcePath(),
                Arrays.asList(HttpMethod.GET),
                Arrays.asList(MediaType.APPLICATION_JSON),
                Arrays.asList("2.0")));

        returnVal.add(new ServerInfoDTO(
                RestResourceId.BRAPI_SEARCH_GENOTYPES.getResourcePath(),
                Arrays.asList(HttpMethod.GET),
                Arrays.asList(MediaType.APPLICATION_JSON),
                Arrays.asList("2.0")));

        returnVal.add(new ServerInfoDTO(
                RestResourceId.BRAPI_GENOTYPES_BY_SEARCH_QUERY.getResourcePath(),
                Arrays.asList(HttpMethod.GET),
                Arrays.asList(MediaType.APPLICATION_JSON),
                Arrays.asList("2.0")));

        return returnVal;
    }

    public BrapiResponseCalls getBrapiResponseCalls() throws Exception {

        BrapiResponseCalls returnVal = new BrapiResponseCalls();
        List<BrapiResponseCallsItem> brapiResponseCallsItems = this.getBrapiResponseCallsItems();
        returnVal.setData(brapiResponseCallsItems);

        return returnVal;

    }
}
