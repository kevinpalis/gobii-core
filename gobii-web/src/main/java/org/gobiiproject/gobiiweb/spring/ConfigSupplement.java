package org.gobiiproject.gobiiweb.spring;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiCropConfig;
import org.gobiiproject.gobiimodel.config.KeycloakConfig;
import org.gobiiproject.gobiimodel.config.ServerConfig;
import org.gobiiproject.gobiimodel.types.GobiiFileProcessDir;
import org.gobiiproject.gobiimodel.types.ServerType;
import org.gobiiproject.gobiimodel.utils.HelperFunctions;
import org.gobiiproject.gobiiweb.DataSourceSelector;
import org.gobiiproject.gobiiweb.Hdf5ProcessPathSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Created by Phil on 8/16/2016.
 */
@Configuration
public class ConfigSupplement {

    @Autowired
    private ThreadLocal<HttpServletRequest> currentRequest;

    private static String CONFIG_FILE_LOCATION_PROP = "cfgFqpn";

    private ConfigSettings configSettings;

    public ConfigSupplement() {
        String configFileLocation = System.getProperty(CONFIG_FILE_LOCATION_PROP);
        this.configSettings = new ConfigSettings(configFileLocation);
    }

    @Bean(name="dataSourceMulti")
    public DataSourceSelector dataSourceMulti() throws Exception {

        DataSourceSelector returnVal = new DataSourceSelector();

        returnVal.setCurrentRequest(currentRequest);


        Map<Object,Object> targetDataSources = new HashMap<>();

        for (GobiiCropConfig currentGobiiCropConfig : this.configSettings.getActiveCropConfigs()) {

            //Sets Postgres settings
            ServerConfig currentPostGresConfig = currentGobiiCropConfig.getServer(ServerType.GOBII_PGSQL);

            DriverManagerDataSource currentDataSource = new DriverManagerDataSource();

            currentDataSource.setDriverClassName("org.postgresql.Driver");

            String url = HelperFunctions.getJdbcConnectionString(currentPostGresConfig);

            currentDataSource.setUrl(url);
            currentDataSource.setUsername(currentPostGresConfig.getUserName());
            currentDataSource.setPassword(currentPostGresConfig.getPassword());

            targetDataSources.put(currentGobiiCropConfig.getGobiiCropType(),currentDataSource);


        } // iterate crop configs

        returnVal.setTargetDataSources(targetDataSources);

        return returnVal;

    }

    @Bean(name="pathToHdf5Exe")
    public String PathToHdf5Executables() {
        return this.configSettings.getHdf5ExePath();
    }

    @Bean(name="hdf5ProcessPathSelector")
    public Hdf5ProcessPathSelector hdf5ProcessPaths() throws Exception {

        Hdf5ProcessPathSelector returnVal = new Hdf5ProcessPathSelector();

        returnVal.setCurrentRequest(this.currentRequest);

        Map<String, Object> hdf5ProcessPathsByCrop = new HashMap<>();

        for (GobiiCropConfig currentGobiiCropConfig : this.configSettings.getActiveCropConfigs()) {
           Map<String, String> hdf5ProcessPaths = new HashMap<>();

           String gobiiCropType = currentGobiiCropConfig.getGobiiCropType();

           hdf5ProcessPaths.put("outputDir",
                   this.configSettings.getProcessingPath(gobiiCropType, GobiiFileProcessDir.EXTRACTOR_DONE));

           hdf5ProcessPaths.put("dataFiles",
                   this.configSettings.getProcessingPath(gobiiCropType, GobiiFileProcessDir.HDF5_FILES));

           hdf5ProcessPathsByCrop.put(gobiiCropType, hdf5ProcessPaths);

        }

        returnVal.setHdf5ProcessingPathByCrop(hdf5ProcessPathsByCrop);

        return returnVal;
    }

    @Bean(name="keycloakConfig")
    public KeycloakConfig keycloakConfig() {
        return this.configSettings.getKeycloakConfig();

        
    }

    @Bean(name="configSettings")
    public ConfigSettings configSettings() {
        return this.configSettings;
    }


}
