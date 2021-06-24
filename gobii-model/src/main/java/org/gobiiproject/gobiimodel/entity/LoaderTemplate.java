package org.gobiiproject.gobiimodel.entity;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="loader_template")
@NamedEntityGraph(
    name = "graph.loader_template",
    attributeNodes = {
        @NamedAttributeNode(value = "templateType"),
        @NamedAttributeNode(value = "createdBy"),
        @NamedAttributeNode(value = "modifiedBy")
    }
)
public class LoaderTemplate {

    @Id
    @Column(name="template_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer templateId;

    @Column(name="name")
    private String templateName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_type", referencedColumnName = "cv_id")
    private Cv templateType;

    @Type(type = "jsonb")
    private JsonNode template;

    @Column(name="created_at")
    private Date createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "contact_id")
    private Contact createdBy;

    @Column(name="modified_at")
    private Date modifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modified_by", referencedColumnName = "contact_id")
    private Contact modifiedBy;

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Cv getTemplateType() {
        return templateType;
    }

    public void setTemplateType(Cv templateType) {
        this.templateType = templateType;
    }

    public JsonNode getTemplate() {
        return template;
    }

    public void setTemplate(JsonNode template) {
        this.template = template;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Contact getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Contact createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Contact getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Contact modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
