/**
 * GobiiPostgresSQLDialect.java
 * 
 * Custom dialect class set in config to enable jsonb columns get/set for use in
 * Hibernate EntityManager 
 * 
 * @author Rodolfo N. Duldulao, Jr.
 * @since 2020-03-13
 */
package org.gobiiproject.gobiimodel.entity.pgsql;

import java.sql.Types;

public class GobiiPostgresSQLDialect extends org.hibernate.dialect.PostgreSQL9Dialect {
    public GobiiPostgresSQLDialect() {
        //this.registerHibernateType(Types.ARRAY, "");
        this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
        this.registerColumnType(Types.ARRAY, "integer[]");
    }
}