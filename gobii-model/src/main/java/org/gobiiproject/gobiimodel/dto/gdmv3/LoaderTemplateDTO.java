package org.gobiiproject.gobiimodel.dto.gdmv3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.LoaderTemplate;
import org.gobiiproject.gobiimodel.utils.customserializers.UtcDateSerializer;

import lombok.Data;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = false, value={
    "id", "allowedProcessTypes", "entityNameType", "status"
})
@Data
public class LoaderTemplateDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    @GobiiEntityMap(paramName = "templateId", entity = LoaderTemplate.class)
    private Integer templateId;

    @GobiiEntityMap(paramName = "templateName", entity = LoaderTemplate.class)
    private String templateName;

    @GobiiEntityMap(paramName = "templateType.term", entity = LoaderTemplate.class, deep = true)
    private String templateType;

    @GobiiEntityMap(paramName = "template", entity = LoaderTemplate.class)
    private JsonNode template;

    @GobiiEntityMap(paramName = "createdBy.username", entity = LoaderTemplate.class, deep = true)
    private String createdBy = null;

    @JsonSerialize(using= UtcDateSerializer.class)
    @GobiiEntityMap(paramName="createdDate", base=true)
    private Date createdDate = null;

    @GobiiEntityMap(paramName = "modifiedBy.username", entity = LoaderTemplate.class, deep = true)
    private String modifiedBy = null;

    @JsonSerialize(using=UtcDateSerializer.class)
    @GobiiEntityMap(paramName="modifiedDate", base=true)
    private Date modifiedDate = null;

}
