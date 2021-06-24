package org.gobiiproject.gobiimodel.dto.gdmv3;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.Job;
import org.gobiiproject.gobiimodel.utils.customserializers.UtcDateSerializer;

import lombok.Data;

import java.util.Date;

@Data
public class JobDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    @GobiiEntityMap(paramName = "jobId", entity = Job.class)
    private Integer jobId;

    @GobiiEntityMap(paramName = "jobName", entity = Job.class)
    private String jobName;

    @GobiiEntityMap(paramName = "status.term", entity = Job.class, deep = true)
    private String status;

    @GobiiEntityMap(paramName = "message", entity = Job.class)
    private String jobMessage;

    @GobiiEntityMap(paramName = "payloadType.term", entity = Job.class, deep = true)
    private String payload;

    @GobiiEntityMap(paramName = "type.term", entity = Job.class, deep = true)
    private String jobType;

    @JsonSerialize(using= UtcDateSerializer.class)
    @GobiiEntityMap(paramName="submittedDate", base = true)
    private Date submittedDate;

    private ContactDTO submittedBy;

    private String jobFilesDownloadUrl;

}
