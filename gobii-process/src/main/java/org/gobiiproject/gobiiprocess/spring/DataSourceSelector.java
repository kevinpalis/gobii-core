package org.gobiiproject.gobiiprocess.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;


public class DataSourceSelector extends AbstractRoutingDataSource {

    Logger LOGGER = LoggerFactory.getLogger(DataSourceSelector.class);

    private String testGobiiCropType;

    public void setTestGobiiCropType(String gobiiCropType) {
        this.testGobiiCropType = gobiiCropType;
    }

    public String getTestGobiCropType() {
       return this.testGobiiCropType;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return getTestGobiCropType();
    }

}
