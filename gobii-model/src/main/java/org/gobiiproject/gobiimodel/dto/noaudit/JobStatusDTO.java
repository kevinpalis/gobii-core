package org.gobiiproject.gobiimodel.dto.noaudit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;


/**
 * Model for JobStatus for sample tracking endpoints.
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true, value={
        "id", "allowedProcessTypes", "entityNameType",
        "datasetIds"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobStatusDTO extends JobDTO {

    public JobStatusDTO() {}

    private String JobStatusLink;

    public String getJobStatusLink() {
        return JobStatusLink;
    }

    public void setJobStatusLink(String jobStatusLink) {
        JobStatusLink = jobStatusLink;
    }
}
