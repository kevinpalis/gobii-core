package org.gobiiproject.gobiimodel.dto.brapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.Experiment;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudiesDTO {

    @GobiiEntityMap(paramName = "experimentId",
            entity = Experiment.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(example = "2")
    private Integer studyDbId;

    @GobiiEntityMap(paramName = "experimentName",
            entity = Experiment.class)
    @ApiModelProperty(example = "foo study")
    private String studyName;

    private List<ContactDTO> contacts = new ArrayList<>();

    @GobiiEntityMap(paramName = "experimentCode",
            entity = Experiment.class)
    @ApiModelProperty(example = "bar code")
    private String studyCode;

    public Integer getStudyDbId() {
        return studyDbId;
    }

    public void setStudyDbId(Integer studyDbId) {
        this.studyDbId = studyDbId;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public String getStudyCode() {
        return studyCode;
    }

    public void setStudyCode(String studyCode) {
        this.studyCode = studyCode;
    }

    public List<ContactDTO> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactDTO> contacts) {
        this.contacts = contacts;
    }
}
