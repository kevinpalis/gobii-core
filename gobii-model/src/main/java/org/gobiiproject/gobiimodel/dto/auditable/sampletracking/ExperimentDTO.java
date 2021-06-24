package org.gobiiproject.gobiimodel.dto.auditable.sampletracking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.Experiment;
import org.gobiiproject.gobiimodel.entity.Project;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown = true, value={
        "id", "allowedProcessTypes", "entityNameType",
        "dataFilePath"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ExperimentDTO extends DTOBaseAuditable {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @GobiiEntityMap(paramName="experimentId", entity = Experiment.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private int experimentId;

    @GobiiEntityMap(paramName="experimentName", entity= Experiment.class)
    private String experimentName;

    @GobiiEntityMap(paramName="experimentCode", entity= Experiment.class)
    private String experimentCode;

    @GobiiEntityMap(paramName="dataFile", entity= Experiment.class)
    private String dataFilePath;

    @GobiiEntityMap(paramName="project.projectId", entity= Experiment.class, deep=true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer projectId;

    @GobiiEntityMap(paramName = "vendorProtocol.vendorProtocolId", entity = Experiment.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer vendorProtocolId;

    @GobiiEntityMap(paramName="manifestId", entity= Experiment.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer manifestId;

    //Mapped by cvStatus
    @GobiiEntityMap(paramName="status.term", entity = Project.class, deep=true)
    private String experimentStatus;

    private String dataFileUrl;

    public ExperimentDTO() { super(GobiiEntityNameType.EXPERIMENT); }

    @Override
    public Integer getId() { return this.experimentId; }

    @Override
    public void setId(Integer id) { this.experimentId = id; }

}
