package org.gobiiproject.gobiibrapi.core.common;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Phil on 12/15/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrapiMetaData {

    public BrapiMetaData() {

    }

    public BrapiMetaData(
            BrapiPagination pagination,
            List<BrapiStatus> status,
            List<String> datafiles,
            BrapiAsynchStatus asynchStatus) {
        this.pagination = pagination;
        this.status = status;
        this.datafiles = datafiles;
        this.asynchStatus = asynchStatus;

    }

    private BrapiAsynchStatus asynchStatus;

    BrapiPagination pagination = new BrapiPagination(0,0,0,0);

    List<BrapiStatus> status = new ArrayList<>();

    List<String> datafiles = new ArrayList<>();

    public BrapiPagination getPagination() {
        return pagination;
    }

    public void setPagination(BrapiPagination pagination) {
        this.pagination = pagination;
    }


    // [{code: <code>, message: <message>}]
    public void addStatusMessage(String code, String message) {
        if( this.status == null ) {
            this.status = new ArrayList<>();
        }

        BrapiStatus brapiStatus = new BrapiStatus(code,message);

        this.status.add(brapiStatus);
    }

    public List<BrapiStatus> getStatus() {
        return status;
    }

    public void setStatus(List<BrapiStatus> status) {
        this.status = status;
    }

    public List<String> getDatafiles() {
        return datafiles;
    }

    public void setDatafiles(List<String> datafiles) {
        this.datafiles = datafiles;
    }

    public BrapiAsynchStatus getAsynchStatus() {
        return asynchStatus;
    }

    public void setAsynchStatus(BrapiAsynchStatus asynchStatus) {
        this.asynchStatus = asynchStatus;
    }
}
