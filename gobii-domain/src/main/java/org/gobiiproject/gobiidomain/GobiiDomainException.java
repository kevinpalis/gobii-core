package org.gobiiproject.gobiidomain;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

/**
 * Created by Phil on 4/7/2016.
 */
public class GobiiDomainException extends GobiiException {

    /**
     *
     */
    private static final long serialVersionUID = 5508543101388167841L;

    public GobiiDomainException(Exception e) {
        super(e);
    }


    public GobiiDomainException(String message) {
        super(message);
    }

    public GobiiDomainException(GobiiStatusLevel gobiiStatusLevel,
                                GobiiValidationStatusType gobiiValidationStatusType,
                                String message) {

        super(gobiiStatusLevel, gobiiValidationStatusType, message);

    } //

}
