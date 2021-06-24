package org.gobiiproject.gobiiweb;

import org.gobiiproject.gobiisampletrackingdao.hdf5.AbstractHdf5ProcessPathSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Phil on 5/25/2016.
 */
public class Hdf5ProcessPathSelector extends AbstractHdf5ProcessPathSelector {

    Logger LOGGER = LoggerFactory.getLogger(Hdf5ProcessPathSelector.class);


    private ThreadLocal<HttpServletRequest> currentRequest;

    public ThreadLocal<HttpServletRequest> getCurrentRequest() {
        return currentRequest;
    }

    public void setCurrentRequest(ThreadLocal<HttpServletRequest> currentRequest) {
        this.currentRequest = currentRequest;
    }

    final String executorServiceThreadNameIdentifier = "gdm-executor-service-thread;";

    @Override
    protected String determineCurrentLookupKey() {

        String returnVal = null;

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
