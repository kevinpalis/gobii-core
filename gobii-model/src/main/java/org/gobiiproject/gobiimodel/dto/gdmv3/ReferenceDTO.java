/**
 * ReferenceDTO.java
 * 
 * DTO for reference data GDM v3.
 * 
 * @author Rodolfo N. Duldulao, Jr.
 */
package org.gobiiproject.gobiimodel.dto.gdmv3;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.entity.Reference;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@JsonIgnoreProperties(ignoreUnknown = false, value={
    "id", "allowedProcessTypes", "entityNameType"
})
//@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ReferenceDTO extends DTOBaseAuditable {

    public static interface Create{}
    public static interface Update{}

    public ReferenceDTO() {
        super(GobiiEntityNameType.REFERENCE);
    }

    @Override
    public Integer getId() {
        return referenceId;
    }

    @Override
    public void setId(Integer id) {
        this.referenceId = id;
    }

    @GobiiEntityMap(paramName = "referenceId", entity = Reference.class )
    @JsonSerialize(using = ToStringSerializer.class)
    @Null(groups={ReferenceDTO.Create.class, ReferenceDTO.Update.class})
    private Integer referenceId;

    @GobiiEntityMap(paramName = "referenceName", entity = Reference.class)
    @NotBlank(groups={ReferenceDTO.Create.class})
    private String referenceName;

    @GobiiEntityMap(paramName = "version", entity = Reference.class)
    private String version;

    @GobiiEntityMap(paramName = "link", entity = Reference.class)
    @JsonIgnore
    @Null(groups={ReferenceDTO.Create.class, ReferenceDTO.Update.class})
    private String referenceLink;

	

}