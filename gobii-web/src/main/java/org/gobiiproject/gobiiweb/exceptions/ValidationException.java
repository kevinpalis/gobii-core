/**
 * ValidationException.java
 * 
 * @author Rodolfo N. Duldulao, Jr.
 * @since 2020-03-13
 */

 package org.gobiiproject.gobiiweb.exceptions;

 public class ValidationException  extends Exception {

     /**
     *
     */
     private static final long serialVersionUID = 8672372128301106323L;

     public ValidationException(String message) {
         super(message);
     }
     
 }