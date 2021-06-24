package org.gobiiproject.gobiiweb.automation;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.ErrorPayload;
import org.springframework.http.MediaType;

public class ResponseUtils {
    
    private static String UNAUTHORIZED_PAYLOAD;

    static{
        ErrorPayload payload = new ErrorPayload();
        payload.setError("Unauthorized");
        ObjectMapper mapper = new ObjectMapper();
        try {
            UNAUTHORIZED_PAYLOAD = mapper.writeValueAsString(payload);
        } catch (JsonProcessingException jpe) {
            UNAUTHORIZED_PAYLOAD = "Unauthorized";
        }
    }

    public static void sendUnauthorizedResponse(ServletResponse response) throws IOException {
        //throw unauthorized
        HttpServletResponse hsResponse = (HttpServletResponse) response;
        hsResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        hsResponse.setContentType(MediaType.APPLICATION_JSON.toString());
        PrintWriter out = hsResponse.getWriter();
        out.println(UNAUTHORIZED_PAYLOAD);   
    }
    
}