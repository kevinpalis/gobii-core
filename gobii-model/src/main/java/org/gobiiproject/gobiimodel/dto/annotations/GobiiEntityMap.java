package org.gobiiproject.gobiimodel.dto.annotations;

import java.lang.annotation.*;

/**
 * Annotation to Map DTO Object to Database Entity.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@SuppressWarnings("rawtypes")
public @interface GobiiEntityMap {
    String paramName();
    Class entity() default void.class;
    boolean deep() default false;
    boolean base() default false;
    boolean ignoreOnDtoToEntity() default false;
}
