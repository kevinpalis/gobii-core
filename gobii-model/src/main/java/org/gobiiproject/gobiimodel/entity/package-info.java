@org.hibernate.annotations.TypeDefs({
        @org.hibernate.annotations.TypeDef(name = "CvPropertiesType", typeClass = org.gobiiproject.gobiimodel.entity.pgsql.CvPropertiesType.class),
        @org.hibernate.annotations.TypeDef(name = "JsonNodeType", typeClass = org.gobiiproject.gobiimodel.entity.pgsql.JsonNodeType.class),
        @org.hibernate.annotations.TypeDef(name = "IntArrayType", typeClass = com.vladmihalcea.hibernate.type.array.IntArrayType.class) 
})
package org.gobiiproject.gobiimodel.entity;


