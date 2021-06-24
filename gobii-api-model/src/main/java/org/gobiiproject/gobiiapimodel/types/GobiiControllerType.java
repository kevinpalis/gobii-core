package org.gobiiproject.gobiiapimodel.types;

/**
 * Created by Phil on 5/13/2016.
 */
public enum GobiiControllerType {
    CROPS(GobiiControllerType.SERVICE_PATH_CROPS),
    GOBII(GobiiControllerType.SERVICE_PATH_GOBII),
    GOBII_V3(GobiiControllerType.SERVICE_PATH_GOBII_V3),
    BRAPI(GobiiControllerType.SERVICE_PATH_BRAPI),
    BRAPI_V2(GobiiControllerType.SERVICE_PATH_BRAPI_V2);

    // we need these to be static final so that they can also be used for the root @RequestMapping
    // annotation in the Controllers themselves.
    public static final String SERVICE_PATH_CROPS = "crops";
    public static final String SERVICE_PATH_GOBII = "crops/{cropType}/gobii/v1/";
    public static final String SERVICE_PATH_BRAPI = "crops/{cropType}/brapi/v1/";
    public static final String SERVICE_PATH_BRAPI_V2 = "crops/{cropType}/brapi/v2/";
    public static final String SERVICE_PATH_GOBII_V3 = "crops/{cropType}/gobii/v3";

    private String controllerPath;

    GobiiControllerType(String controllerPath) {
        this.controllerPath = controllerPath;
    }

    public String getControllerPath() {
        return this.controllerPath;
    }

}


