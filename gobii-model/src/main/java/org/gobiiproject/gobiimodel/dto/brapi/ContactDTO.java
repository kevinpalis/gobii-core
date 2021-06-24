package org.gobiiproject.gobiimodel.dto.brapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.Contact;

@JsonIgnoreProperties(ignoreUnknown = true, value={
        "firstName", "lastName"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactDTO {

    @GobiiEntityMap(paramName = "contactId", entity = Contact.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer contactDbId;

    @GobiiEntityMap(paramName = "email", entity = Contact.class)
    private String email;

    @GobiiEntityMap(paramName = "organization.name", entity = Contact.class,
            deep = true)
    private String instituteName;

    @GobiiEntityMap(paramName = "firstName", entity = Contact.class)
    private String firstName;

    @GobiiEntityMap(paramName = "lastName", entity = Contact.class)
    private String lastName;


    private String type = "PI";

    public Integer getContactDbId() {
        return contactDbId;
    }

    public void setContactDbId(Integer contactDbId) {
        this.contactDbId = contactDbId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInstituteName() {
        return instituteName;
    }

    public void setInstituteName(String instituteName) {
        this.instituteName = instituteName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return lastName + ", " + firstName;
    }
}
