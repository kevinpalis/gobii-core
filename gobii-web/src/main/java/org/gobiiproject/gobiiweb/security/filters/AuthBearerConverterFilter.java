package org.gobiiproject.gobiiweb.security.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.gobiiproject.gobiiapimodel.types.GobiiHttpHeaderNames;
import org.springframework.web.filter.GenericFilterBean;

public class AuthBearerConverterFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if (httpServletRequest.getHeader(GobiiHttpHeaderNames.HEADER_NAME_TOKEN) == null) {
            chain.doFilter(request, response);
            return;
        }

        //if X-Auth-Token exists
        AuthTokenConverterServletWrapper servletRequestWrapper = new AuthTokenConverterServletWrapper(httpServletRequest);
        chain.doFilter(servletRequestWrapper, response);

    }
    
}