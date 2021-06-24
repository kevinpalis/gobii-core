package org.gobiiproject.gobiimodel.entity;


import com.fasterxml.jackson.databind.JsonNode;
import org.gobiiproject.gobiimodel.entity.JpaConverters.JsonbConverter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Model for Dnasample Entity.
 * Represents the database table project.
 *
 * props - is a jsonb column. It is converted to jackson.fasterxml JsonNode using a
 * user defined hibernate converter class.
 */
@Entity
@Table(name = "dnasample")
public class DnaSample extends BaseEntity {

    @Id
    @Column(name="dnasample_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    private Integer dnaSampleId;

    @Column(name="name")
    private String dnaSampleName;


    @Column(name="uuid")
    private String dnaSampleUuid;

    @Column(name="code")
    private Integer dnaSampleCode;

    @Column(name="platename")
    private String plateName;

    @Column(name="num")
    private String dnaSampleNum;

    @Column(name="well_row")
    private String wellRow;

    @Column(name="well_col")
    private String wellCol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "germplasm_id")
    private Germplasm germplasm;

    @Column(name="props", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private JsonNode properties;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status", referencedColumnName = "cv_id")
    private Cv status;

    public Integer getDnaSampleId() {
        return dnaSampleId;
    }

    public void setDnaSampleId(Integer dnaSampleId) {
        this.dnaSampleId = dnaSampleId;
    }

    public String getDnaSampleName() {
        return dnaSampleName;
    }

    public void setDnaSampleName(String dnaSampleName) {
        this.dnaSampleName = dnaSampleName;
    }

    public Integer getDnaSampleCode() {
        return dnaSampleCode;
    }

    public void setDnaSampleCode(Integer dnaSampleCode) {
        this.dnaSampleCode = dnaSampleCode;
    }

    public String getPlateName() {
        return plateName;
    }

    public void setPlateName(String plateName) {
        this.plateName = plateName;
    }

    public String getDnaSampleNum() {
        return dnaSampleNum;
    }

    public void setDnaSampleNum(String dnaSampleNum) {
        this.dnaSampleNum = dnaSampleNum;
    }

    public String getWellRow() {
        return wellRow;
    }

    public void setWellRow(String wellRow) {
        this.wellRow = wellRow;
    }

    public String getWellCol() {
        return wellCol;
    }

    public void setWellCol(String wellCol) {
        this.wellCol = wellCol;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Germplasm getGermplasm() {
        return germplasm;
    }

    public void setGermplasm(Germplasm germplasm) {
        this.germplasm = germplasm;
    }

    public JsonNode getProperties() {
        return properties;
    }

    public void setProperties(JsonNode properties) {
        this.properties = properties;
    }

    public String getDnaSampleUuid() {
        return dnaSampleUuid;
    }

    public void setDnaSampleUuid(String dnaSampleUuid) {
        this.dnaSampleUuid = dnaSampleUuid;
    }

    public Cv getStatus() {
        return status;
    }

    public void setStatus(Cv status) {
        this.status = status;
    }

}
