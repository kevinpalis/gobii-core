package org.gobiiproject.gobiiweb.automation;

import javax.servlet.http.HttpServletResponse;
import org.gobiiproject.gobiiapimodel.payload.Header;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * Created by Phil on 9/23/2016.
 */
public class ControllerUtils {

    public static void setHeaderResponse(Header header,
                                         HttpServletResponse response,
                                         HttpStatus successResponseCode,
                                         HttpStatus failureResponseCode) {

        if (header.getStatus().isSucceeded()) {
            response.setStatus(successResponseCode.value());
        } else {
            response.setStatus(failureResponseCode.value());
        }

    }

    public static void writeRawResponse(HttpServletResponse httpResponse,
                                        int httpServletResponse,
                                        String message) throws Exception {

        httpResponse.setStatus(httpServletResponse);
        httpResponse.setContentType(MediaType.APPLICATION_JSON.toString());
        httpResponse.getOutputStream().print(message);
        httpResponse.getOutputStream().flush();
        httpResponse.getOutputStream().close();

    }

}
