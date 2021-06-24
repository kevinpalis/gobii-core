package org.gobiiproject.gobiimodel.dto.noaudit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.gobiiproject.gobiimodel.dto.base.DTOBase;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityColumn;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityParam;

/**
 * Created by VCalaminos on 8/25/2017.
 */
public class JobDTO extends DTOBase {

    public JobDTO() {}

    @Override
    public Integer getId() { return this.jobId; }

    @Override
    public void setId(Integer id) { this.jobId = id; }

    private Integer jobId;
    private String jobName;
    private String type;
    private String payloadType;
    private String status;
    private String message;
    private Integer submittedBy;
    private Date submittedDate;
    private List<Integer> datasetIds = new ArrayList<>();

    @GobiiEntityParam(paramName = "jobId")
    public Integer getJobId() { return jobId; }

    @GobiiEntityColumn(columnName = "job_id")
    public void setJobId(Integer jobId) { this.jobId = jobId; }

    @GobiiEntityParam(paramName = "jobName")
    public String getJobName() { return jobName; }

    @GobiiEntityColumn(columnName = "name")
    public void setJobName(String jobName) { this.jobName = jobName; }

    @GobiiEntityParam(paramName = "type")
    public String getType() { return type; }

    @GobiiEntityColumn(columnName = "type")
    public void setType(String type) { this.type = type;}

    @GobiiEntityParam(paramName = "payloadType")
    public String getPayloadType() { return payloadType; }

    @GobiiEntityColumn(columnName = "payload_type")
    public void setPayloadType(String payloadType) { this.payloadType = payloadType; }

    @GobiiEntityParam(paramName = "status")
    public String getStatus() { return status; }

    @GobiiEntityColumn(columnName = "status")
    public void setStatus(String status) { this.status = status; }

    @GobiiEntityParam(paramName = "message")
    public String getMessage(){ return message; }

    @GobiiEntityColumn(columnName = "message")
    public void setMessage(String message) { this.message = message; }

    @GobiiEntityParam(paramName = "submittedBy")
    public Integer getSubmittedBy() { return submittedBy; }

    @GobiiEntityColumn(columnName = "submitted_by")
    public void setSubmittedBy(Integer submittedBy) { this.submittedBy = submittedBy; }

    @GobiiEntityParam(paramName = "submittedDate")
    public Date getSubmittedDate() { return submittedDate; }

    @GobiiEntityColumn(columnName = "submitted_date")
    public void setSubmittedDate(Date submittedDate) { this.submittedDate = submittedDate; }

    @GobiiEntityParam(paramName = "datasetIds")
    public List<Integer> getDatasetIds() { return datasetIds; }

    @GobiiEntityColumn(columnName = "datasetids")
    public void setDatasetIds(List<Integer> datasetIds) { this.datasetIds = datasetIds; }

}
