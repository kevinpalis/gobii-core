// these have to match what's on the server
export enum EntityType {
    UNKNOWN,
    ANALYSIS,
    CONTACT,
    DATASET,
    CV,
    CVGROUP,
    PROJECT,
    ORGANIZATION,
    PLATFORM,
    MANIFEST,
    MAPSET,
    MARKER_GROUP,
    EXPERIMENT,
    REFERENCE,
    ROLE,
    DISPLAY,
    MARKER,
    PROTOCOL,
    VENDORS_PROTOCOL,
    CROP
}

export const entityTypefromString = function (entityTypeName: String): EntityType {

    let returnVal: EntityType = EntityType.UNKNOWN;

    switch (entityTypeName) {

        case "ANALYSIS":
            returnVal = EntityType.ANALYSIS;
            break;

        case "CONTACT":
            returnVal = EntityType.CONTACT;
            break;

        case "DATASET":
            returnVal = EntityType.DATASET;
            break;

        case "CV":
            returnVal = EntityType.CV;
            break;

        case "CVGROUP":
            returnVal = EntityType.CVGROUP;
            break;

        case "PROJECT":
            returnVal = EntityType.PROJECT;
            break;

        case "ORGANIZATION":
            returnVal = EntityType.ORGANIZATION;
            break;

        case "PLATFORM":
            returnVal = EntityType.PLATFORM;
            break;

        case "MANIFEST":
            returnVal = EntityType.MANIFEST
            break;

        case "MAPSET":
            returnVal = EntityType.MAPSET;
            break;

        case "MARKER_GROUP":
            returnVal = EntityType.MARKER_GROUP;
            break;

        case "EXPERIMENT":
            returnVal = EntityType.EXPERIMENT;
            break;

        case "REFERENCE":
            returnVal = EntityType.REFERENCE;
            break;

        case "ROLE":
            returnVal = EntityType.ROLE;
            break;

        case "DISPLAY":
            returnVal = EntityType.DISPLAY;
            break;

        case "MARKER":
            returnVal = EntityType.MARKER;
            break;

        case "PROTOCOL":
            returnVal = EntityType.PROTOCOL;
            break;

        case "VENDOR_PROTOCOL":
            returnVal = EntityType.VENDORS_PROTOCOL;
            break;
        
        case "CROP":
            returnVal = EntityType.CROP;
            break;

    }

    return returnVal;
}

export enum EntitySubType {
    UNKNOWN,
    CONTACT_PRINCIPLE_INVESTIGATOR,
    CONTACT_SUBMITED_BY
}

