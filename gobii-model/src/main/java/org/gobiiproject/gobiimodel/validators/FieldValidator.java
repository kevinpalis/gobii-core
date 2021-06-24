package org.gobiiproject.gobiimodel.validators;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class FieldValidator {
    private static  ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static Validator validator = factory.getValidator();

    public static void validate (Object validationObject) throws GobiiException {
        Set<ConstraintViolation<Object>> violations = validator.validate(validationObject);
        if(violations.size() > 0) {
            String violationMessage = "";
            for(ConstraintViolation<?> violation : violations) {
                violationMessage += violation.getMessage() + "; ";
            }
            throw new GobiiException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.BAD_REQUEST,
                violationMessage);
        }
    }

}
