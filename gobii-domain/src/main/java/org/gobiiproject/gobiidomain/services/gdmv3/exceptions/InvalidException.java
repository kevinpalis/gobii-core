package org.gobiiproject.gobiidomain.services.gdmv3.exceptions;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

@SuppressWarnings("serial")
public class InvalidException extends GobiiException {

    public InvalidException(String itemType) {
        super(GobiiStatusLevel.ERROR,
              GobiiValidationStatusType.BAD_REQUEST, 
              String.format("Invalid %s", itemType)
        );
    }
    
}