package org.gobiiproject.gobiimodel.types;

public enum GobiiJobStatus {
    PENDING("pending"),
    STARTED("started"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed"),
    FAILED("failed");

    private String jobStatusCvTerm;

    GobiiJobStatus(String jobStatusCvTerm) {
        this.jobStatusCvTerm = jobStatusCvTerm;
    }

    public String getCvTerm() {
        return this.jobStatusCvTerm;
    }

}
