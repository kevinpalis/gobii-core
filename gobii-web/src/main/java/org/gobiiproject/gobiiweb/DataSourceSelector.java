package org.gobiiproject.gobiiweb;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Created by Phil on 5/25/2016.
 */
public class DataSourceSelector extends AbstractRoutingDataSource {

    Logger LOGGER = LoggerFactory.getLogger(DataSourceSelector.class);

    /*
    For reasons that are not clear, for the very first time that the determineCurrentLookupkey()
    method is hit, the ServletRequest is not available through the ServletRequestAttributes
    (this is the mechanism used by the CropRequestAnalyzer to figure out which crop
    to use based on the inbound url). This happens because in the TokenAuthenticationFilter
    we need to look up the user in the contact table as part of the authentication
    process. I gather that the nominal use case for setting up JDBC connections
    is that they are defined statically, such that a given applicaiton would just
    always be directed towards a specific connection through. In our case, we identify
    the connection dynamically. Apparently, it is ordinarily not until the delegatingProxyFilterChain
    has completed that the ServeltRequestAttributes are set up.
    In this scenario, the CroRequestAnalyzer was defaulting to provide the
    ID of the first active gobii server instance listed in the config file.
    For single crop configurations this doesn't matter. But when more than
    one crop is configured, it means that for the authentication request --
    and only for that request before the ServlettRequestAttributes are set up  --
    the user credentials were being retrieved from the wrong database. Accordingly
    I set up a oncePerRequest filter that sets the servlet request in a thread local that is
    then visible to this class, which can then use the method of CropRequestAnalyzer to
    which you provide the request object directly.
    One thing to note is that when you look at this problem in the debugger, it looks
    as if there is only one initial request in an entire series of requests in which
    the ServletRequestAttributes are not set. However, this is an illusion: once you've
    authenticated, the lookup in the contact table is not done because the you have a
    token. In other words, once you've authenticated and have a token, the branch
    of TokenAuthenticationFilter that looks up the user in the contact table does
    not execute. Thus, it is _only_ for the request in which the user's identity
    is established -- i.e., in which the user authenticates -- that the empty
    ServletRequestAttributes are a problem.
    Perhaps we should be doing static configuration instead to avoid this complication.
    However, this approach makes our server configurations much easier: it makes it
    unnecessary to manage static configuration for each specific crop type.
     */
    private ThreadLocal<HttpServletRequest> currentRequest;

    public ThreadLocal<HttpServletRequest> getCurrentRequest() {
        return currentRequest;
    }

    public void setCurrentRequest(ThreadLocal<HttpServletRequest> currentRequest) {
        this.currentRequest = currentRequest;
    }

    final String executorServiceThreadNameIdentifier = "gdm-executor-service-thread;";

    @Override
    protected Object determineCurrentLookupKey() {

        Object returnVal = null;

        try {

            String currentThreadName = Thread.currentThread().getName();

            if( currentRequest.get() != null ) {
                returnVal = CropRequestAnalyzer.getGobiiCropType(currentRequest.get());
            }
            else if(currentThreadName.startsWith(executorServiceThreadNameIdentifier)) {
                returnVal = getCropTypeFromThreadName(currentThreadName);
            }
            else {
                returnVal = CropRequestAnalyzer.getGobiiCropType();
            }
        } catch( Exception e) {
            LOGGER.error("Error looking up lookup key",e);
        }

        return returnVal;
    }

    private String getCropTypeFromThreadName(String currentThreadName) {
        String[] threadNameGroups = currentThreadName.split(";");
        if(threadNameGroups.length >= 2) {
            String[] cropTypeSection = threadNameGroups[1].split(":");
            if(cropTypeSection.length == 2 && cropTypeSection[0].equals("cropType")) {
                return cropTypeSection[1];
            }
        }

        return null;
    }
}
