package org.gobiiproject.gobiiweb.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * HTTP Handler methods containing this annotation will require Admin/Curator roles OR 
 * the user role attribute values indicated in the annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CropAuth {
    public String[] value();
}