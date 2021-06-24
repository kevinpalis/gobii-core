package org.gobiiproject.gobiimodel.validators;


import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Set;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * reference : "https://stackoverflow.com/questions/12211734/
 *              hibernate-validation-annotation-validate-that-at-
 *              least-one-field-is-not-null"
 *
 */
@Target( {TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy =
    CheckAtLeastOneNotNullOrEmpty.CheckAtLeastOneNotNullOrEmptyValidator.class)
@Documented
public @interface CheckAtLeastOneNotNullOrEmpty {

    String message() default "Empty Search Query or Invalid arguments";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] fieldNames();

    class CheckAtLeastOneNotNullOrEmptyValidator implements
        ConstraintValidator<CheckAtLeastOneNotNullOrEmpty, Object> {

        private String[] fieldNames;

        public void initialize(CheckAtLeastOneNotNullOrEmpty constraintAnnotation) {
            this.fieldNames = constraintAnnotation.fieldNames();
        }

        @SuppressWarnings("unchecked")
        public boolean isValid(Object object, ConstraintValidatorContext constraintContext) {


            if (object == null)
                return true;

            try {
                for (String fieldName:fieldNames) {
                    Object property = PropertyUtils.getProperty(object, fieldName);

                    if ((property instanceof Set<?> && ((Set<Object>) property).size() > 0) ||
                        (property != null && !(property instanceof Set<?>))) {
                        return true;
                    }
                }
                return false;

            } catch (Exception e) {
                return false;
            }
        }

    }


}
