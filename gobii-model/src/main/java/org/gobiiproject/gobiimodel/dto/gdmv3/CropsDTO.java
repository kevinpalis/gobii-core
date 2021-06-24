package org.gobiiproject.gobiimodel.dto.gdmv3;

public class CropsDTO {


    private String cropType;

    public boolean isUserAuthorized() {
        return isUserAuthorized;
    }

    public void setUserAuthorized(boolean userAuthorized) {
        isUserAuthorized = userAuthorized;
    }

    private boolean isUserAuthorized;



    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }
}
