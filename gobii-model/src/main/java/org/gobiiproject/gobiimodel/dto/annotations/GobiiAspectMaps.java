package org.gobiiproject.gobiimodel.dto.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A wrapper to allow list of multiple {@link GobiiAspectMap} objects
 * <p>
 * When you need to describe only one map {@link GobiiAspectMap}, you still must use
 * this annotation to add add the map in an array
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GobiiAspectMaps {
    /**
     * List of {@link GobiiAspectMap} for mapping Gobii process aspects
     *
     */
    GobiiAspectMap[] maps();
}
