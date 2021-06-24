package org.gobiiproject.gobiimodel.dto.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * To map template field to Aspect fields
 * <p>
 * To map dto field to aspect field. Will be ignored when defined alone.
 * Had to be defined as array value in {@link GobiiAspectMaps}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface GobiiAspectMap {

    /**
     * Name of aspect field to which the value needs to be mapped if it is different from
     * dto field name.
     * If not given, will be mapped to aspect field with same name as dto field.
     */
    String paramName() default "";

    /**
     * Aspect table to which the field should be mapped.
     */
    Class<?> aspectTable() default void.class;
}
