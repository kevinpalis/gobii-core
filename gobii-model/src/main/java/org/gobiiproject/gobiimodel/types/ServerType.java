package org.gobiiproject.gobiimodel.types;

/**
 * Created by Phil on 5/18/2016.
 */
public enum ServerType {
    // these must correspond to the jdbc driver name
    GOBII_PGSQL,
    GOBII_WEB,
    GOBII_COMPUTE,
    BRAPI,
    KDC,
    GENERIC,
    OWN_CLOUD,
    UNKNOWN,
    LDAP,
    KEY_CLOAK
}
