package org.gobiiproject.gobiidomain.services.gdmv3.exceptions;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

public class InvalidTypeException extends GobiiException {

    /**
     *
     */
    private static final long serialVersionUID = 5282149398214726444L;

    public InvalidTypeException() {
        super(GobiiStatusLevel.ERROR, GobiiValidationStatusType.BAD_REQUEST, "Invalid type");
        // TODO Auto-generated constructor stub
    }
    
}