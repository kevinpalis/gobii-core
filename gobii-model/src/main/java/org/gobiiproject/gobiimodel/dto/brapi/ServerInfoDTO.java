package org.gobiiproject.gobiimodel.dto.brapi;

import java.util.List;

public class ServerInfoDTO {


    private String service;

    private List<String> dataTypes;

    private List<String> methods;

    private List<String> versions;


    public ServerInfoDTO(String service, List<String> methods,
                         List<String> dataTypes, List<String> versions) {

        this.service = service;
        this.dataTypes = dataTypes;
        this.methods = methods;
        this.versions = versions;

    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public List<String> getDataTypes() {
        return dataTypes;
    }

    public void setDataTypes(List<String> dataTypes) {
        this.dataTypes = dataTypes;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public List<String> getVersions() {
        return versions;
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }
}
