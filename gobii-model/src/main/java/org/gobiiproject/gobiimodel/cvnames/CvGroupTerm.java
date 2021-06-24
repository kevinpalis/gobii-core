package org.gobiiproject.gobiimodel.cvnames;

/**
 * Created by Phil on 5/13/2016.
 */


public enum CvGroupTerm {

    CVGROUP_JOBTYPE("job_type"),
    CVGROUP_PAYLOADTYPE("payload_type"),
    CVGROUP_JOBSTATUS("job_status"),
    CVGROUP_ANALYSIS_TYPE("analysis_type"),
    CVGROUP_DATASET_TYPE("dataset_type"),
    CVGROUP_DNARUN_PROP("dnarun_prop"),
    CVGROUP_DNASAMPLE_PROP("dnasample_prop"),
    CVGROUP_GERMPLASM_PROP("germplasm_prop"),
    CVGROUP_GERMPLASM_SPECIES("germplasm_species"),
    CVGROUP_GERMPLASM_TYPE("germplasm_type"),
    CVGROUP_GOBII_DATAWAREHOUSE("gobii_datawarehouse"),
    CVGROUP_MAPSET_TYPE("mapset_type"),
    CVGROUP_MARKER_PROP("marker_prop"),
    CVGROUP_MARKER_STRAND("marker_strand"),
    CVGROUP_PLATFORM_TYPE("platform_type"),
    CVGROUP_PROJECT_PROP("project_prop"),
    CVGROUP_STATUS("status");


    private String cvGroupName;

    CvGroupTerm(String cvGroupName) {
        this.cvGroupName = cvGroupName;
    }

    public String getCvGroupName() {
        return this.cvGroupName;
    }

}
