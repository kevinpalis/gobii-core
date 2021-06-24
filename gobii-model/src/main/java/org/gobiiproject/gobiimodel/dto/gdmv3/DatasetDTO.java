package org.gobiiproject.gobiimodel.dto.gdmv3;

import static org.gobiiproject.gobiimodel.utils.LineUtils.isNullOrEmpty;
import java.util.List;

import javax.validation.constraints.Digits;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.entity.Dataset;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonIgnoreProperties(ignoreUnknown = false, value={
    "id", "allowedProcessTypes", "entityNameType", "status"
})
@Data
@EqualsAndHashCode(callSuper = false)
public class DatasetDTO extends DTOBaseAuditable {
 
    public DatasetDTO() {
        super(GobiiEntityNameType.DATASET);
    }

	@Override
	public Integer getId() {
		return datasetId;
	}

	@Override
	public void setId(Integer id) {
		setDatasetId(id);
    }
    
    @GobiiEntityMap(paramName = "datasetId", entity = Dataset.class )
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer datasetId;


    @GobiiEntityMap(paramName = "datasetName", entity = Dataset.class)
    private String datasetName;

    @GobiiEntityMap(paramName = "experiment.experimentId", entity = Dataset.class, deep = true )
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer experimentId;


    @GobiiEntityMap(paramName = "experiment.experimentName", entity = Dataset.class, deep = true )
    private String experimentName;

    
    @GobiiEntityMap(paramName = "experiment.project.projectId", entity = Dataset.class, deep = true )
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer projectId;


    @GobiiEntityMap(paramName = "experiment.project.projectName", entity = Dataset.class, deep = true )
    private String projectName;

    
    @GobiiEntityMap(paramName = "experiment.project.contact.contactId", entity = Dataset.class, deep = true )
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer piContactId;


    @GobiiEntityMap(paramName="experiment.project.contact.lastName", entity = Dataset.class, deep=true)
    @JsonIgnore
    private String piContactLastName;

    @GobiiEntityMap(paramName="experiment.project.contact.firstName", entity = Dataset.class, deep=true)
    @JsonIgnore
    private String piContactFirstName;

    @GobiiEntityMap(paramName = "type.cvId", entity = Dataset.class, deep = true )
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer datasetTypeId;

    @GobiiEntityMap(paramName = "type.term", entity = Dataset.class, deep = true)
    private String datasetTypeName;


    @GobiiEntityMap(paramName = "analyses", entity = Dataset.class)
    @JsonIgnore
    private Integer[] analysisIds;


    @GobiiEntityMap(paramName = "callingAnalysis.analysisId", entity = Dataset.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    @Digits(integer = 10, fraction = 0, groups = DatasetRequestDTO.Create.class)
    private Integer callingAnalysisId;

    @GobiiEntityMap(paramName = "callingAnalysis.analysisName", entity = Dataset.class, deep = true)
    private String callingAnalysisName;

    //manually set this before transporting
    private List<AnalysisDTO> analyses;

    @JsonProperty("piContactName")
    public String getPiContactName() {
        if (!isNullOrEmpty(piContactFirstName) &&
            !isNullOrEmpty(piContactLastName)) {
            return String.format("%s, %s", piContactLastName, piContactFirstName);
        }
        if (!isNullOrEmpty(piContactFirstName)) {
            return piContactFirstName; //covers one-name persons
        }
        if (!isNullOrEmpty(piContactLastName)) {
            return piContactLastName;
        }
        return null;
    }


    @GobiiEntityMap(paramName="datasetStats.markerCount", entity=Dataset.class, deep=true)
    private Integer markerCount = 0;

    @GobiiEntityMap(paramName="datasetStats.dnarunCount", entity=Dataset.class, deep=true)
    private Integer dnaRunCount = 0;
}