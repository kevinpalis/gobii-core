package org.gobiiproject.gobiimodel.dto.auditable;

import java.util.ArrayList;
import java.util.List;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityColumn;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityParam;
import org.gobiiproject.gobiimodel.dto.noaudit.DataSetDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

/**
 * Created by Angel on 4/13/2016.
 */
public class ExperimentDTO extends DTOBaseAuditable {

    public ExperimentDTO() {
        super(GobiiEntityNameType.EXPERIMENT);
    }


    private Integer experimentId = 0;
    private String experimentName = null;
    private String experimentCode = null;
    private String experimentDataFile = null;
    private Integer projectId;
    private Integer vendorProtocolId = null;
    private Integer manifestId;
    private Integer statusId;
    private List<DataSetDTO> datasets = new ArrayList<>();


    @Override
    public Integer getId() {
        return this.experimentId;
    }

    @Override
    public void setId(Integer id) {
        this.experimentId = id;
    }

    @GobiiEntityParam(paramName = "experimentId")
    public Integer getExperimentId() {
        return experimentId;
    }

    @GobiiEntityColumn(columnName = "experiment_id")
    public void setExperimentId(Integer experimentId) {
        this.experimentId = experimentId;
    }

    @GobiiEntityParam(paramName = "experimentName")
    public String getExperimentName() {
        return experimentName;
    }

    @GobiiEntityColumn(columnName = "name")
    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    @GobiiEntityParam(paramName = "experimentCode")
    public String getExperimentCode() {
        return experimentCode;
    }

    @GobiiEntityColumn(columnName = "code")
    public void setExperimentCode(String experimentCode) {
        this.experimentCode = experimentCode;
    }

    @GobiiEntityParam(paramName = "experimentDataFile")
    public String getExperimentDataFile() {
        return experimentDataFile;
    }

    @GobiiEntityColumn(columnName = "data_file")
    public void setExperimentDataFile(String dataFile) {
        this.experimentDataFile = dataFile;
    }

    @GobiiEntityParam(paramName = "projectId")
    public Integer getProjectId() {
        return projectId;
    }

    @GobiiEntityColumn(columnName = "project_id")
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }


    @GobiiEntityParam(paramName = "vendorProtocolId")
    public Integer getVendorProtocolId() {
        return vendorProtocolId;
    }

    @GobiiEntityColumn(columnName = "vendor_protocol_id")
    public void setVendorProtocolId(Integer vendorProtocolId) {
        this.vendorProtocolId = vendorProtocolId;
    }

    @GobiiEntityParam(paramName = "manifestId")
    public Integer getManifestId() {
        return manifestId;
    }

    @GobiiEntityColumn(columnName = "manifest_id")
    public void setManifestId(Integer manifestId) {
        this.manifestId = manifestId;
    }

    @GobiiEntityParam(paramName = "status")
    public Integer getStatusId() {
        return statusId;
    }

    @GobiiEntityColumn(columnName = "status")
    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public List<DataSetDTO> getDatasets() { return datasets; }

    public void setDatasets(List<DataSetDTO> datasets) { this.datasets = datasets;}


}
