package org.gobiiproject.gobiidomain.services.gdmv3.exceptions;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

@SuppressWarnings("serial")
public class EntityAlreadyExistsException extends GobiiException {
    
    public EntityAlreadyExistsException(String entityType) {
        super(GobiiStatusLevel.ERROR,
              GobiiValidationStatusType.ENTITY_ALREADY_EXISTS,
              String.format("%s already exists", entityType)
        );
    }

    //subclasses

    public static class Mapset extends EntityAlreadyExistsException {
        public Mapset() { super("Mapset"); }
    }
}
