package org.gobiiproject.gobiimodel.config;

import org.gobiiproject.gobiimodel.types.ServerType;
import org.gobiiproject.gobiimodel.utils.LineUtils;

/**
 * Created by Phil on 5/13/2016.
 */


public enum RestResourceId {

    GOBII_AUTH("auth", ServerType.GOBII_WEB),
    GOBII_PING("ping", ServerType.GOBII_WEB),
    GOBII_PROJECTS("projects", ServerType.GOBII_WEB),
    GOBII_GENOTYPE_CALLS("calls", ServerType.BRAPI),
    GOBII_NAME_ID_LIST_DEPRECATED("nameidlist", ServerType.GOBII_WEB),
    GOBII_NAMES("names", ServerType.GOBII_WEB),
    GOBII_FILE_LOAD("files/loader", ServerType.GOBII_WEB),
    GOBII_FILE_LOAD_INSTRUCTIONS("instructions/loader", ServerType.GOBII_WEB),
    GOBII_FILE_EXTRACTOR_INSTRUCTIONS("instructions/extractor", ServerType.GOBII_WEB),
    GOBII_FILE_EXTRACTOR_JOBS("instructions/extractor/jobs", ServerType.GOBII_WEB),
    GOBII_FILE_LOADER_JOBS("instructions/loader/jobs", ServerType.GOBII_WEB),
    GOBII_DISPLAY("displays", ServerType.GOBII_WEB),
    GOBII_CV("cvs", ServerType.GOBII_WEB),
    GOBII_CVGROUP("cvgroups", ServerType.GOBII_WEB),
    GOBII_CONTACTS("contacts", ServerType.GOBII_WEB),
    GOBII_CONTACT_SEARCH("contact-search", ServerType.GOBII_WEB),
    GOBII_ORGANIZATION_SEARCH("organization-search", ServerType.GOBII_WEB),
    GOBII_REFERENCE("references", ServerType.GOBII_WEB),
    GOBII_EXPERIMENTS("experiments", ServerType.GOBII_WEB),
    GOBII_DATASETS("datasets", ServerType.GOBII_WEB),
    GOBII_DATASETTYPES("datasets/types", ServerType.GOBII_WEB),
    GOBII_ANALYSIS("analyses", ServerType.GOBII_WEB),
    GOBII_MARKERGROUP("markergroups", ServerType.GOBII_WEB),
    GOBII_MANIFEST("manifests", ServerType.GOBII_WEB),
    GOBII_ORGANIZATION("organizations", ServerType.GOBII_WEB),
    GOBII_PLATFORM("platforms", ServerType.GOBII_WEB),
    GOBII_MAPSET("mapsets", ServerType.GOBII_WEB),
    GOBII_CONFIGSETTINGS("configsettings", ServerType.GOBII_WEB),
    GOBII_ROLES("roles", ServerType.GOBII_WEB),
    GOBII_MARKERS("markers", ServerType.GOBII_WEB),
    GOBII_MARKER_SEARCH("marker-search", ServerType.GOBII_WEB),
    GOBII_CALLS("calls", ServerType.GOBII_WEB),
    GOBII_GERMPLASM("germplasm", ServerType.GOBII_WEB),
    GOBII_OBSERVATION_VARIABLES("observationVariables", ServerType.GOBII_WEB),
    GOBII_PROTOCOL("protocols", ServerType.GOBII_WEB),
    GOBII_VENDORS("vendors", ServerType.GOBII_WEB),
    GOBII_FILES("files", ServerType.GOBII_WEB),
    GOBII_JOB("jobs", ServerType.GOBII_WEB),
    GOBII_ENTITIES("entities", ServerType.GOBII_WEB),
    GOBII_COUNT("count", ServerType.GOBII_WEB),
    GOBII_LAST_MODIFIED("lastmodified", ServerType.GOBII_WEB),
    GOBII_VERTICES("vertices", ServerType.GOBII_WEB),
    GOBII_VALUES("values", ServerType.GOBII_WEB),
    GOBII_REST_PROFILES("restprofiles", ServerType.GOBII_WEB),
    GOBII_DNASAMPLES("dnasamples", ServerType.GOBII_WEB),
    GOBII_DNARUN("dnarun", ServerType.GOBII_WEB),
    GOBII_LINKAGEGROUP("linkagegroup", ServerType.GOBII_WEB),
    GOBII_JOB_DNASAMPLE("jobs/dnasamples", ServerType.GOBII_WEB),
    BRAPI_ALLELE_MATRIX_SEARCH("allelematrix-search", ServerType.BRAPI),
    BRAPI_ALLELE_MATRIX_SEARCH_STATUS("allelematrix-search/status", ServerType.BRAPI),
    BRAPI_ALLELE_MATRICES("allelematrices", ServerType.BRAPI),
    KDC_START("qcStart", ServerType.KDC),
    KDC_STATUS("qcStatus", ServerType.KDC),
    KDC_DOWNLOAD("qcDownload", ServerType.KDC),
    KDC_PURGE("qcPurge", ServerType.KDC),
    BRAPI_LOGIN("token", ServerType.BRAPI),
    BRAPI_STUDIES_SEARCH("studies-search", ServerType.BRAPI),
    BRAPI_STUDIES("studies", ServerType.BRAPI),
    BRAPI_CALLS("calls", ServerType.BRAPI),


    BRAPI_SERVER_INFO("serverinfo", ServerType.BRAPI),

    BRAPI_MAPS_CALLS("maps", ServerType.BRAPI),

    BRAPI_MARKER_POSITIONS("markerpositions", ServerType.BRAPI),

    BRAPI_CALLSETS("callsets", ServerType.BRAPI),

    BRAPI_CALLSETS_BY_ID("callsets/{callSetDbId}", ServerType.BRAPI),

    BRAPI_VARIANTS("variants", ServerType.BRAPI),

    BRAPI_VARIANTS_BY_ID("variants/{variantDbId}", ServerType.BRAPI),

    BRAPI_VARIANTSETS("variantsets", ServerType.BRAPI),

    BRAPI_GENOTYPES_BY_VARIANTSET(
            "variantsets/{variantSetDbId}/calls",
            ServerType.BRAPI),

    BRAPI_DOWNLOAD_GENOTYPES_BY_VARIANTSET(
            "variantsets/{variantSetDbId}/calls/download",
            ServerType.BRAPI),

    BRAPI_VARIANTS_BY_VARIANTSET(
            "variantsets/{variantSetDbId}/variants",
            ServerType.BRAPI),

    BRAPI_CALLSETS_BY_VARIANTSET(
            "variantsets/{variantSetDbId}/callsets",
            ServerType.BRAPI),

    BRAPI_SEARCH_GENOTYPES(
            "search/calls",
            ServerType.BRAPI),

    BRAPI_GENOTYPES_BY_SEARCH_QUERY(
            "search/calls/{searchResultDbId}",
            ServerType.BRAPI);

    private String requestPath;
    private ServerType serverType;

    RestResourceId(String requestPath, ServerType serverType) {
        this.requestPath = requestPath;
        this.serverType = serverType;
    }

    public String getResourcePath() {
        return this.requestPath;
    }

    public void setResourcePath(String resourcePath) {
        this.requestPath = resourcePath;
    }

    public ServerType getServerType() {
        return this.serverType;
    }

    public String getRequestUrl(String contextRoot, String secondaryPath, String cropType) throws Exception {

        String returnVal = "";


        if (!LineUtils.isNullOrEmpty(contextRoot)) {
            returnVal = contextRoot;
        }

        if (!LineUtils.isNullOrEmpty(secondaryPath)) {
            returnVal += secondaryPath;
        }

        returnVal += this.getResourcePath();

        if(!LineUtils.isNullOrEmpty(cropType)) {
            returnVal = returnVal.replace("{cropType}", cropType);

        }

        return returnVal;
    }

}
