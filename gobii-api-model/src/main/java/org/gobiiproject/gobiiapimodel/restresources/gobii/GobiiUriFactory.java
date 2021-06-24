package org.gobiiproject.gobiiapimodel.restresources.gobii;


import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.gobiiproject.gobiimodel.types.GobiiFileProcessDir;

/**
 * Created by Phil on 9/7/2016.
 */
public class GobiiUriFactory {

    private GobiiControllerType gobiiControllerType;
    private String cropContextRoot;
    private String domain = null;
    private Integer port = null;
    private String cropType = null;

    public GobiiUriFactory(String domain,
                           Integer port,
                           String cropContextRoot,
                           String cropType,
                           GobiiControllerType gobiiControllerType) {

        this(cropContextRoot, gobiiControllerType, cropType);
        this.domain = domain;
        this.port = port;
    }

    public GobiiUriFactory(String cropContextRoot,
                           GobiiControllerType gobiiControllerType,
                           String cropType) {

        this.gobiiControllerType = gobiiControllerType;
        this.cropContextRoot = cropContextRoot;
        this.cropType = cropType;
    }

    public GobiiUriFactory(String cropContextRoot, String cropType) {

        this.gobiiControllerType = GobiiControllerType.GOBII;
        this.cropContextRoot = cropContextRoot;
        this.cropType = cropType;
    }

    public String getControllerPath() {
        String controllerPath = this.gobiiControllerType.getControllerPath();
        if(controllerPath.contains("{cropType}") && this.cropType != null) {
            controllerPath = controllerPath.replace("{cropType}", cropType);
        }
        return controllerPath;
    }

    public RestUri RestUriFromUri(String uri) {
        return new RestUri(uri);
    }

    public RestUri resourceColl(RestResourceId restResourceId) throws Exception {

        RestUri returnVal;

        returnVal = new RestUri(this.domain,
                this.port,
                this.cropContextRoot,
                this.getControllerPath(),
                restResourceId.getResourcePath());

        return returnVal;

    } //

    public static RestUri resourceColl(String contextRoot, RestResourceId restResourceId) throws Exception {

        RestUri returnVal;

        returnVal = new RestUri(contextRoot,
                GobiiControllerType.GOBII.getControllerPath(),
                restResourceId.getResourcePath());

        return returnVal;

    } //

    public RestUri resourceByUriIdParam(RestResourceId restResourceId) throws Exception {

        String paramName = "id";
        return new RestUri(this.domain,
                this.port,
                this.cropContextRoot,
                this.getControllerPath(),
                restResourceId.getResourcePath())
                .addUriParam(paramName);
    } //


    public RestUri resourceByUriIdParam(String resourcePath) throws Exception {

        String paramName = "id";
        return new RestUri(this.domain,
            this.port,
            this.cropContextRoot,
            this.getControllerPath(),
            resourcePath)
            .addUriParam(paramName);
    } //

    public RestUri resourceByUriIdParamName(String paramName, RestResourceId restResourceId) throws Exception {

        return new RestUri(this.domain,
                this.port,
                this.cropContextRoot,
                this.getControllerPath(),
                restResourceId.getResourcePath())
                .addUriParam(paramName);
    } //

    public static RestUri resourceByUriIdParam(String contextRoot, RestResourceId restResourceId) throws Exception {

        String paramName = "id";
        return new RestUri(contextRoot,
                GobiiControllerType.GOBII.getControllerPath(),
                restResourceId.getResourcePath())
                .addUriParam(paramName);
    } //


    public RestUri childResourceByUriIdParam(RestResourceId parentRestResourceId, RestResourceId childRestResourceId) throws Exception {

        String paramName = "id";
        RestUri returnVal = new RestUri(this.domain,
                this.port,
                this.cropContextRoot,
                this.getControllerPath(),
                parentRestResourceId.getResourcePath())
                .addUriParam(paramName)
                .appendSegment(childRestResourceId);

        return returnVal;

    } //

    public RestUri contactsByQueryParams() throws Exception {

        RestUri returnVal = new RestUri(this.domain,
                this.port,
                this.cropContextRoot,
                this.getControllerPath(),
                RestResourceId.GOBII_CONTACT_SEARCH.getResourcePath())
                .addQueryParam("email")
                .addQueryParam("lastName")
                .addQueryParam("firstName")
                .addQueryParam("userName");

        return returnVal;

    } //

    public RestUri cvGroupByQueryParams() throws Exception {

        RestUri returnVal = new RestUri(this.domain,
                this.port,
                this.cropContextRoot,
                this.getControllerPath(),
                RestResourceId.GOBII_CVGROUP.getResourcePath())
                .addUriParam("groupName")
                .addQueryParam("cvGroupTypeId");

        return returnVal;
    }

    public RestUri markerssByQueryParams() throws Exception {

        RestUri returnVal = new RestUri(this.domain,
                this.port,
                this.cropContextRoot,
                this.getControllerPath(),
                RestResourceId.GOBII_MARKER_SEARCH.getResourcePath())
                .addQueryParam("name");

        return returnVal;

    }

    public RestUri nameIdListByQueryParams() throws Exception {

        RestUri returnVal = new RestUri(this.domain,
                this.port,
                this.cropContextRoot,
                this.getControllerPath(),
                RestResourceId.GOBII_NAMES.getResourcePath())
                .addUriParam("entity")
                .addQueryParam("filterType")
                .addQueryParam("filterValue");

        return returnVal;

    } //

    public RestUri fileLoaderPreview() throws Exception {

        RestUri returnVal = new RestUri(this.domain,
                this.port,
                this.cropContextRoot,
                this.getControllerPath(),
                RestResourceId.GOBII_FILE_LOAD.getResourcePath())
                .addUriParam("directoryName")
                .addQueryParam("fileFormat");

        return returnVal;

    } //

    public RestUri fileForJob(String jobId,
                              GobiiFileProcessDir gobiiFileProcessDir,
                              String fileName) throws Exception {

        RestUri returnVal = new RestUri(this.domain,
                this.port,
                this.cropContextRoot,
                this.getControllerPath(),
                RestResourceId.GOBII_FILES.getResourcePath())
                .addUriParam("gobiiJobId", jobId)
                .addUriParam("destinationType", gobiiFileProcessDir.toString())
                .addQueryParam("fileName", fileName);

        return returnVal;
    }

    public RestUri file(GobiiFileProcessDir gobiiFileProcessDir,
                        String fileName) throws Exception {

        RestUri returnVal = new RestUri(this.domain,
                this.port,
                this.cropContextRoot,
                this.getControllerPath(),
                RestResourceId.GOBII_FILES.getResourcePath())
                .addUriParam("destinationType", gobiiFileProcessDir.toString())
                .addQueryParam("fileName", fileName);

        return returnVal;
    }

    public RestUri entityCount(GobiiEntityNameType gobiiEntityNameType) throws Exception {

        RestUri returnVal = new RestUri(this.domain,
                this.port,
                this.cropContextRoot,
                this.getControllerPath(),
                RestResourceId.GOBII_ENTITIES.getResourcePath())
                .addUriParam("entityName", gobiiEntityNameType.toString().toLowerCase())
                .appendSegment(RestResourceId.GOBII_COUNT);

        return returnVal;
    }

    public RestUri entityChildCount(GobiiEntityNameType gobiiEntityNameTypeParent,
                                    GobiiEntityNameType gobiiEntityNameTypeChild,
                                    Integer parentId) throws Exception {

        RestUri returnVal = new RestUri(this.domain,
                this.port,
                this.cropContextRoot,
                this.getControllerPath(),
                RestResourceId.GOBII_ENTITIES.getResourcePath())
                .addUriParam("entityNameParent", gobiiEntityNameTypeParent.toString().toLowerCase())
                .addUriParam("parentId", parentId.toString())
                .addUriParam("entityNameChild", gobiiEntityNameTypeChild.toString())
                .appendSegment(RestResourceId.GOBII_COUNT);

        return returnVal;
    }

    public RestUri entityLastModified(GobiiEntityNameType gobiiEntityNameType) throws Exception {

        RestUri returnVal = new RestUri(this.domain,
                this.port,
                this.cropContextRoot,
                this.getControllerPath(),
                RestResourceId.GOBII_ENTITIES.getResourcePath())
                .addUriParam("entityName", gobiiEntityNameType.toString().toLowerCase())
                .appendSegment(RestResourceId.GOBII_LAST_MODIFIED);

        return returnVal;
    }

    public RestUri pagedList(RestResourceId restResourceId,
                             Integer pageSize,
                             Integer pageNo,
                             String queryId) throws Exception {

        RestUri returnVal = this.resourceColl(restResourceId)
                .addQueryParam("pageSize", pageSize.toString())
                .addQueryParam("pageNo", pageNo.toString())
                .addQueryParam("queryId", queryId);

        return returnVal;
    }

}


