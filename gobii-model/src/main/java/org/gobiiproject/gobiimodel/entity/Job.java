package org.gobiiproject.gobiimodel.entity;


import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "job")
@NamedEntityGraph(
    name = "graph.job",
    attributeNodes = {
        @NamedAttributeNode(value = "payloadType"),
        @NamedAttributeNode(value = "submittedBy"),
        @NamedAttributeNode(value = "type"),
        @NamedAttributeNode(value = "status")
    }
)
public class Job {

    @Id
    @Column(name="job_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer jobId;

    @Column(name="name")
    private String jobName;

    @Column(name="message")
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", referencedColumnName = "cv_id")
    private Cv type = new Cv();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payload_type_id", referencedColumnName = "cv_id")
    private Cv payloadType = new Cv();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status", referencedColumnName = "cv_id")
    private Cv status = new Cv();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by", referencedColumnName = "contact_id")
    private Contact submittedBy;

    @Column(name="submitted_date")
    private Date submittedDate;

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Cv getType() {
        return type;
    }

    public void setType(Cv type) {
        this.type = type;
    }

    public Cv getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(Cv payloadType) {
        this.payloadType = payloadType;
    }

    public Cv getStatus() {
        return status;
    }

    public void setStatus(Cv status) {
        this.status = status;
    }

    public Contact getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(Contact submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }
}
