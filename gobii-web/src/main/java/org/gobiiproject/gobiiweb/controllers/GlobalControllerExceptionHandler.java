package org.gobiiproject.gobiiweb.controllers;

import java.util.List;

import javax.persistence.PersistenceException;

import com.fasterxml.jackson.databind.JsonMappingException;

import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.InvalidMarkersException;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.MarkerStatus;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.ErrorPayload;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiiweb.controllers.brapi.BRAPIIControllerV1;
import org.gobiiproject.gobiiweb.exceptions.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


/**
 * Handles the exceptions thrown in all the Controllers
 * in this project and returns appropriate HTTP response.
 */
@ControllerAdvice(assignableTypes = {
        BRAPIIControllerV1.class
}, basePackages = {"org.gobiiproject.gobiiweb.controllers.brapi.v2", "org.gobiiproject.gobiiweb.controllers.gdm.v3"})
public class GlobalControllerExceptionHandler {

    Logger LOGGER = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    /**
     * Handles Gobii exceptions.
     * Based on the ValidationStatusType in GobiiException, handler returns HTTP response
     * with appropriate HTTP status code and error message.
     *
     * @param gEx - GobiiException thrown by the controller.
     * @return ResponseEntity - Http Response with HTTP status code and body defined.
     */
    @ExceptionHandler(GobiiException.class)
    public ResponseEntity<ErrorPayload> GobiiExceptionHandler(GobiiException gEx) {
        gEx.printStackTrace();
        ErrorPayload errorPayload = new ErrorPayload();
        errorPayload.setError(gEx.getMessage());
        HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        switch (gEx.getGobiiValidationStatusType()) {

            case ENTITY_DOES_NOT_EXIST: {
                errorStatus = HttpStatus.NOT_FOUND;
                break;
            }
            case FOREIGN_KEY_VIOLATION:
            case ENTITY_ALREADY_EXISTS: {
                errorStatus = HttpStatus.CONFLICT;
                break;
            }
            case BAD_REQUEST: {
                errorStatus = HttpStatus.BAD_REQUEST;
                break;
            }
            default: {
                errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                errorPayload.setError("System error. Please contact the web service administrator.");
            }

        }

        LOGGER.error(gEx.getMessage());

        return ResponseEntity.status(errorStatus).body(errorPayload);
    }

    /**
     * Handles exceptions from request body not meeting the API endpoint specifications.
     * This handler exclusively serves json request body exceptions.
     *
     * @param httpEx - Exception with json request body.
     * @return ResponseEntity with Http Status code and Error payload with appropriate error message.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorPayload> NotReadableRequestBodyException(HttpMessageNotReadableException httpEx) {
        JsonMappingException jmEx = (JsonMappingException) httpEx.getCause();
        ErrorPayload errorPayload = new ErrorPayload();
        errorPayload.setError("Request does not comply with Input specification. " +
                "Please refer to API document to make sure request body complies to API specification");
        LOGGER.error(jmEx.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorPayload);
    }

    /**
     * HTTP request method not supported exception handler.
     *
     * @param e - exception object.
     * @return ResponseEntity with Internal server error as status code and Server error as message
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorPayload> HttpMethodNotSupportedExceptionHandler(Exception e) {

        ErrorPayload errorPayload = new ErrorPayload();
        errorPayload.setError("Request method not supported!");
        LOGGER.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorPayload);
    }


    /**
     * Handles all the other exceptions not handled by the other handlers.
     *
     * @param e - exception object.
     * @return ResponseEntity with Internal server error as status code and Server error as message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorPayload> GeneralExceptionHandler(Exception e) {
        e.printStackTrace();
        ErrorPayload errorPayload = new ErrorPayload();
        errorPayload.setError("Server Error");
        LOGGER.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorPayload);
    }

    /**
     * Handles exceptions when the Request Parameters and Path Variables
     * are of type different than the expected.
     * For example, String argument is passed to a pageNumber argument when Integer is expected.
     *
     * @param e - exception object.
     * @return ResponseEntity with "Bad Request" as status code and as message
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorPayload>
    InvalidParameterTypeExceptionHandler(MethodArgumentTypeMismatchException e) {
        ErrorPayload errorPayload = new ErrorPayload();
        errorPayload.setError("Invalid Request Arguments");
        LOGGER.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorPayload);
    }

    /**
     * Handles exceptions when the Request Parameters are invalid.
     *
     * @param e - exception object.
     * @return ResponseEntity with "Bad Request" as status code and as message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorPayload>
    InvalidMethodArgumentsExceptionHandler(MethodArgumentNotValidException e) {
        ErrorPayload errorPayload = new ErrorPayload();
        String errorMessage = "";
        for(ObjectError error : e.getBindingResult().getAllErrors()) {
            errorMessage += error.getDefaultMessage() + " ";
        }
        errorPayload.setError(errorMessage);
        LOGGER.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorPayload);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorPayload>
    ValidationExceptionHandler(ValidationException ve) {
        ErrorPayload errorPayload = new ErrorPayload();
        errorPayload.setError(ve.getMessage());
        LOGGER.error(ve.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorPayload);
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ErrorPayload> PersistenceExceptionHandler(PersistenceException pe) {
        ErrorPayload errorPayload = new ErrorPayload();
        if (pe.getMessage().contains("ConstraintViolation")) {
            errorPayload.setError("Entity already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorPayload);
        }

        errorPayload.setError(pe.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorPayload);

    }

    @ExceptionHandler({NullPointerException.class,ResourceDoesNotExistException.class})
    public ResponseEntity<ErrorPayload> NullPointerExceptionHandler(NullPointerException e) {
        ErrorPayload errorPayload = new ErrorPayload();
        errorPayload.setError("Resource not found");
        LOGGER.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorPayload);
    }


    @ExceptionHandler(InvalidMarkersException.class)
    public ResponseEntity<List<MarkerStatus>> InvalidMarkersExceptionHandler(InvalidMarkersException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getStatusList());
    }
    


}
