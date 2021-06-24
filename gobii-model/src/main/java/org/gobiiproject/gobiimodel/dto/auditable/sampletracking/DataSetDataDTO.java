package org.gobiiproject.gobiimodel.dto.auditable.sampletracking;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.gobiiproject.gobiimodel.dto.base.DTOBase;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true, value={
        "id", "allowedProcessTypes", "entityNameType"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataSetDataDTO extends DTOBase {

    private Integer dataSetId;
    private Map<String, String> vendorFileUrls;

    @Override
    public Integer getId() {
        return this.dataSetId;
    }

    @Override
    public void setId(Integer id) {
        this.dataSetId = id;
    }


    public Integer getDataSetId() {
        return this.dataSetId;
    }

    public void setDatasetId(Integer datasetId) {
        this.dataSetId = datasetId;
    }

}
