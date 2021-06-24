package org.gobiiproject.gobiimodel.entity;


import com.fasterxml.jackson.databind.JsonNode;
import org.gobiiproject.gobiimodel.entity.JpaConverters.JsonbConverter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Model for Germplasm Entity.
 * Represents the database table project.
 *
 * props - is a jsonb column. It is mapped to jackson.fasterxml JsonNode using a
 * user defined hibernate converter class.
 */
@Entity
@Table(name = "germplasm")
public class Germplasm extends BaseEntity {

    @Id
    @Column(name="germplasm_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    private Integer germplasmId;

    @Column(name="name")
    private String germplasmName;

    @Column(name="external_code")
    private String externalCode;

    @Column(name="species_id")
    private Integer germplasmSpecies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", referencedColumnName = "cv_id")
    private Cv germplasmType;

    @Column(name="code")
    private String code;

    @Column(name="props", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private JsonNode properties;

    public Integer getGermplasmId() {
        return germplasmId;
    }

    public void setGermplasmId(Integer germplasmId) {
        this.germplasmId = germplasmId;
    }

    public String getGermplasmName() {
        return germplasmName;
    }

    public void setGermplasmName(String germplasmName) {
        this.germplasmName = germplasmName;
    }

    public String getExternalCode() {
        return externalCode;
    }

    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

    public Integer getGermplasmSpecies() {
        return germplasmSpecies;
    }

    public void setGermplasmSpecies(Integer germplasmSpecies) {
        this.germplasmSpecies = germplasmSpecies;
    }

    public Cv getGermplasmType() {
        return germplasmType;
    }

    public void setGermplasmType(Cv germplasmType) {
        this.germplasmType = germplasmType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public JsonNode getProperties() {
        return properties;
    }

    public void setProperties(JsonNode properties) {
        this.properties = properties;
    }

    public Cv getStatus() {
        return status;
    }

    public void setStatus(Cv status) {
        this.status = status;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status", referencedColumnName = "cv_id")
    private Cv status = new Cv();


}
