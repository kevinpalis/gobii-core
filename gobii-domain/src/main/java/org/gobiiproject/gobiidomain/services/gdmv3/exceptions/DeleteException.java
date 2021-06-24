package org.gobiiproject.gobiidomain.services.gdmv3.exceptions;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

@SuppressWarnings("serial")
public class DeleteException extends GobiiException {

    public DeleteException() {
        super(
            GobiiStatusLevel.ERROR,
            GobiiValidationStatusType.FOREIGN_KEY_VIOLATION,
            "Associated resources found. Cannot complete the action unless they are deleted."
        );
    }
    
}