
/**
 * ContactDTO.java
 * 
 * GDM V3 Version
 * 
 * @author Rodolfo N. Duldulao, Jr.
 * @since 2020-03-26
 */
package org.gobiiproject.gobiimodel.dto.gdmv3;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.AccessLevel;

@JsonIgnoreProperties(ignoreUnknown = false, value={
    "id", "allowedProcessTypes", "entityNameType", "status"
})
@JsonInclude(JsonInclude.Include.ALWAYS)
@Data
@EqualsAndHashCode(callSuper=false)
public class ContactDTO extends DTOBaseAuditable {

    public ContactDTO() {
        super(GobiiEntityNameType.CONTACT);
    }

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public void setId(Integer id) {
        //this.piContactId = id;
    }

    @GobiiEntityMap(paramName = "contactId", entity = Contact.class)
    @JsonIgnore
    private Integer contactId;

    @GobiiEntityMap(paramName = "lastName", entity = Contact.class)
    private String piContactLastName;

    @GobiiEntityMap(paramName = "firstName", entity = Contact.class)
    private String piContactFirstName;

    @GobiiEntityMap(paramName = "organization.organizationId", entity = Contact.class, deep=true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer organizationId;

    @GobiiEntityMap(paramName = "organization.name", entity = Contact.class, deep = true)
    private String organizationName;

    @GobiiEntityMap(paramName = "username", entity = Contact.class)
    private String username;

    @GobiiEntityMap(paramName = "email", entity = Contact.class)
    //@JsonIgnore
    private String email;

    @Getter(AccessLevel.NONE)
    private String piContactId;

    @JsonProperty
    public String getFullName() {
        return String.format("%s, %s", this.getPiContactLastName(), this.getPiContactFirstName());
    }


    public String getPiContactId() {
        return Optional.ofNullable(username)
                    .map(v -> v.toString())
                    .orElse(piContactId);
    }

}