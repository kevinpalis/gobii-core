package org.gobiiproject.gobiimodel.dto.children;

import java.util.Date;
import java.util.HashMap;

import lombok.NoArgsConstructor;
import org.gobiiproject.gobiimodel.dto.base.DTOBase;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

/**
 * Created by Phil on 4/8/2016.
 */
@NoArgsConstructor
public class NameIdDTO extends DTOBase implements Comparable<NameIdDTO>{

    private HashMap<String, Object> parameters = new HashMap<>();


    public NameIdDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    // entityLastModified is necessary because this class doe snot correspond to a
    // specific entity, and so it should not derive from DTOBaseAuditable
//    @JsonFormat
//            (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date entityLasetModified;

    private GobiiEntityNameType gobiiEntityNameType;
    private GobiiEntityNameType gobiiFkEntityNameType;
    private Integer id = 0;
    private Integer fkId = 0;
    private String name = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getEntityLasetModified() {
        return entityLasetModified;
    }

    public void setEntityLasetModified(Date entityLasetModified) {
        this.entityLasetModified = entityLasetModified;
    }

    public GobiiEntityNameType getGobiiEntityNameType() {
        return gobiiEntityNameType;
    }

    public void setGobiiEntityNameType(GobiiEntityNameType gobiiEntityNameType) {
        this.gobiiEntityNameType = gobiiEntityNameType;
    }

    public GobiiEntityNameType getGobiiFkEntityNameType() {
        return gobiiFkEntityNameType;
    }

    public void setGobiiFkEntityNameType(GobiiEntityNameType gobiiFkEntityNameType) {
        this.gobiiFkEntityNameType = gobiiFkEntityNameType;
    }

    public Integer getFkId() {
        return fkId;
    }

    public void setFkId(Integer fkId) {
        this.fkId = fkId;
    }


    public HashMap<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(HashMap<String, Object> parameters) {
        this.parameters = parameters;
    }

    public int compareTo(NameIdDTO compareNameIdDTO) {

        return name.compareTo(compareNameIdDTO.getName());

    }

}
