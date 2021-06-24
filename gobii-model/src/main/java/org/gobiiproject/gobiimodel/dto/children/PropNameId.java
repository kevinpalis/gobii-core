package org.gobiiproject.gobiimodel.dto.children;

/**
 * A singluar identifier used for unambiguously sepcifying a UI parameter used during this action.
 * Created by Phil on 9/1/2016.
 */
public class PropNameId {
    //Property ID - Internal unique identifier for this label

    public PropNameId() {};

    public PropNameId(Integer id, String name ) {
        this.id = id;
        this.name = name;
    }

    Integer id;
    //Property Name (User readable)
    String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
