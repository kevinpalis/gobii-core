package org.gobiiproject.gobiiweb.security.filters;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.gobiiproject.gobiiapimodel.types.GobiiHttpHeaderNames;

public class AuthTokenConverterServletWrapper extends HttpServletRequestWrapper {

    public AuthTokenConverterServletWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getHeader(String name) {
        if (name.toLowerCase().equals("authorization")) return 
            String.format(
                "Bearer %s",
                super.getHeader(GobiiHttpHeaderNames.HEADER_NAME_TOKEN)
            );
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> headerNames = Collections.list(super.getHeaderNames());
        headerNames.add("Authorization");
        return Collections.enumeration(headerNames);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> values = Collections.list(super.getHeaders(name));
        if (name.toLowerCase().equals("authorization")) 
            values.add(
                String.format(
                    "Bearer %s",
                    super.getHeader(GobiiHttpHeaderNames.HEADER_NAME_TOKEN)
                )
            );
        return Collections.enumeration(values);
    }
    
} 