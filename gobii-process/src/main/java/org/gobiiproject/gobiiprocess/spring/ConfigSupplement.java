package org.gobiiproject.gobiiprocess.spring;

import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiCropConfig;
import org.gobiiproject.gobiimodel.config.ServerConfig;
import org.gobiiproject.gobiimodel.types.ServerType;
import org.gobiiproject.gobiimodel.utils.HelperFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class ConfigSupplement {

    private static ConfigSettings configSettings;

    /**
     * Gets the environment variables for gobii-web.xml location and test crop type.
     * Reads the test configurations and sets the testExecConfig.
     * @throws Exception
     */
    public void readConfigFile() throws Exception{
        configSettings = SpringContextLoaderSingleton.getConfigSettings();
        String cropType = SpringContextLoaderSingleton.getCropType();
        if (configSettings == null || cropType == null) {
            String message = "The the environment does not define the FQPN of " +
                    "configuration in environment variable";
            throw new Exception(message);
        }
        configSettings.setCurrentGobiiCropType(cropType);
    }

    /**
     * Dependency Injection Bean for the testing DataSource.
     * Postgres database connection configuration will be read from the gobii-web.xml file
     * from the location given as environment variable cfgFqpn.
     * @return Implementation of AbstractRoutingDatasource abstract class for database connection lookup.
     * @throws Exception
     */
    @Bean(name="dataSourceMulti")
    public DataSourceSelector dataSourceMulti() throws Exception {

        DataSourceSelector returnVal = new DataSourceSelector();
        readConfigFile();

        GobiiCropConfig currentGobiiCropConfig = configSettings.getCurrentCropConfig();

        returnVal.setTestGobiiCropType(
            currentGobiiCropConfig.getGobiiCropType());

        Map<Object,Object> targetDataSources = new HashMap<>();

        ServerConfig currentPostGresConfig =
            currentGobiiCropConfig.getServer(ServerType.GOBII_PGSQL);

        DriverManagerDataSource currentDataSource = new DriverManagerDataSource();

        currentDataSource.setDriverClassName("org.postgresql.Driver");

        String url =
            HelperFunctions.getJdbcConnectionString(currentPostGresConfig);

        currentDataSource.setUrl(url);
        currentDataSource.setUsername(currentPostGresConfig.getUserName());
        currentDataSource.setPassword(currentPostGresConfig.getPassword());

        targetDataSources.put(
            currentGobiiCropConfig.getGobiiCropType(),
            currentDataSource);

        returnVal.setTargetDataSources(targetDataSources);

        return returnVal;
    }


}
