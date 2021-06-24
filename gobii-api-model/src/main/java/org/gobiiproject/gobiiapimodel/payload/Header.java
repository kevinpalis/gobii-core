// ************************************************************************
// (c) 2016 GOBii Project
// Initial Version: Phil Glaser
// Create Date:   2016-03-24
// ************************************************************************
package org.gobiiproject.gobiiapimodel.payload;


import org.gobiiproject.gobiimodel.types.GobiiProcessType;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Phil on 3/24/2016.
 */
@SuppressWarnings("serial")
public class Header implements Serializable {

    public Header() {
    }

    public Header(GobiiProcessType gobiiProcessType) {
        this.gobiiProcessType = gobiiProcessType;
    }

    private GobiiProcessType gobiiProcessType = GobiiProcessType.READ;
    private HeaderAuth dtoHeaderAuth = new HeaderAuth();
    private Status status = new Status();
    private Pagination pagination = null;

    private Integer maxGet = null; // add additional verbs as needed
    private Integer maxPost = null;
    private Integer maxPut = null;


    private String gobiiVersion;

    // we also have String in HeaderAuth; we need it in both cases,
    // because HeaderAuth is returned in the body of an authentication response,
    // and we need that to remain as light weight as possible. Here we need crop type
    // because of the application.
    private String cropType = null;

    // in order for the private properties to be serialized into the JSON,
    // we must use the proper bean naming convention for these getters --
    // this is how the Jackson serializer knows that it should serialize what
    // would other wise be private propeties.
    public HeaderAuth getDtoHeaderAuth() {
        return dtoHeaderAuth;
    }

    public Status getStatus() {
        if (null == this.status) {
            this.status = new Status();
        }
        return this.status;
    }

    public GobiiProcessType getGobiiProcessType() {
        return gobiiProcessType;
    }

    public void setGobiiProcessType(GobiiProcessType gobiiProcessType) {
        this.gobiiProcessType = gobiiProcessType;
    }


    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }

    public void setPagination(String queryId,
                              Date queryTime,
                              Integer pageSize,
                              Integer totalPages,
                              Integer currentPage) {

        this.pagination = new Pagination(queryId,
                queryTime,
                pageSize,
                totalPages,
                currentPage);

    } // setPagination()

    public Pagination getPagination() {
        return pagination;
    }

    public String getGobiiVersion() {
        return gobiiVersion;
    }

    public void setGobiiVersion(String gobiiVersion) {
        this.gobiiVersion = gobiiVersion;
    }

    public Integer getMaxGet() {
        return maxGet;
    }

    public void setMaxGet(Integer maxGet) {
        this.maxGet = maxGet;
    }

    public Integer getMaxPost() {
        return maxPost;
    }

    public void setMaxPost(Integer maxPost) {
        this.maxPost = maxPost;
    }

    public Integer getMaxPut() {
        return maxPut;
    }

    public void setMaxPut(Integer maxPut) {
        this.maxPut = maxPut;
    }
} // Header
