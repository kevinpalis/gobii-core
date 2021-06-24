package org.gobiiproject.gobiidao;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

/**
 * Created by Phil on 4/7/2016.
 */
@SuppressWarnings("serial")
public class GobiiDaoException extends GobiiException {

    public GobiiDaoException(Exception e) {
        super(e);
    }


    public GobiiDaoException(String message) {
        super(message);
    }

    public GobiiDaoException(String message, Exception e) {
        super(message, e);
    }


    public GobiiDaoException(GobiiStatusLevel gobiiStatusLevel,
                                GobiiValidationStatusType gobiiValidationStatusType,
                                String message) {

        super(gobiiStatusLevel, gobiiValidationStatusType, message);

    } //
}
