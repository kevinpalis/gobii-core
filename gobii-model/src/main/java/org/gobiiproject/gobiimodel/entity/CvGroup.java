package org.gobiiproject.gobiimodel.entity;

import com.fasterxml.jackson.databind.JsonNode;
import org.gobiiproject.gobiimodel.entity.JpaConverters.JsonbConverter;

import javax.persistence.*;

@Entity
@Table(name = "cvgroup")
public class CvGroup {

    @Id
    @Column(name = "cvgroup_id")
    private Integer cvGroupId;

    @Column(name = "name")
    private String cvGroupName;

    @Column(name = "type")
    private Integer cvGroupType;

    @Column(name="props", columnDefinition = "jsonb")
    @Convert(converter = JsonbConverter.class)
    private JsonNode properties;

    public Integer getCvGroupId() {
        return cvGroupId;
    }

    public void setCvGroupId(Integer cvGroupId) {
        this.cvGroupId = cvGroupId;
    }

    public String getCvGroupName() {
        return cvGroupName;
    }

    public void setCvGroupName(String cvGroupName) {
        this.cvGroupName = cvGroupName;
    }

    public Integer getCvGroupType() {
        return cvGroupType;
    }

    public void setCvGroupType(Integer cvGroupType) {
        this.cvGroupType = cvGroupType;
    }

    public JsonNode getProperties() {
        return properties;
    }

    public void setProperties(JsonNode properties) {
        this.properties = properties;
    }
}
