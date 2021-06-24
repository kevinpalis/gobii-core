package org.gobiiproject.gobiiweb.controllers;

import org.gobiiproject.gobiimodel.dto.brapi.envelope.ErrorPayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController {

    @RequestMapping(value = "error", method = RequestMethod.GET)
    public ResponseEntity<ErrorPayload> error(HttpServletRequest request) {

        ErrorPayload errorPayload = new ErrorPayload();

        try {
            Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
            HttpStatus httpStatus = HttpStatus.valueOf(status);
            errorPayload.setError(httpStatus.getReasonPhrase());
            return ResponseEntity.status(httpStatus).body(errorPayload);
        }
        catch (Exception e) {
            e.printStackTrace();
            errorPayload.setError("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorPayload);
        }
    }

}
