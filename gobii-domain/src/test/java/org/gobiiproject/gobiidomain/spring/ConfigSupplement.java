package org.gobiiproject.gobiidomain.spring;

import java.util.HashMap;
import java.util.Map;

import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiCropConfig;
import org.gobiiproject.gobiimodel.config.ServerConfig;
import org.gobiiproject.gobiimodel.types.ServerType;
import org.gobiiproject.gobiimodel.utils.HelperFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


@Configuration
public class ConfigSupplement {

    private static ConfigSettings testConfig;

    private static String CONFIG_FILE_LOCATION_PROP = "cfgFqpn";
    private static String TEST_CROP_PROP = "cropType";



    /**
     * Gets the environment variables for gobii-web.xml location and test crop type.
     * Reads the test configurations and sets the testExecConfig.
     * @throws Exception
     */
    public void readTestConfig() throws Exception{

        String configFileLocation = System.getProperty(CONFIG_FILE_LOCATION_PROP);

        String testCropType = System.getProperty(TEST_CROP_PROP);

        if (configFileLocation == null) {
            String message = "The the environment does not define the FQPN of " +
                    "configuration in environment variable: " + CONFIG_FILE_LOCATION_PROP;
            throw new Exception(message);
        }

        if(testCropType == null) {
            String message = "The the environment does not define cropType " +
                    "in environment variable: " + TEST_CROP_PROP;
            throw new Exception(message);
        }

        testConfig = new ConfigSettings(configFileLocation);

        testConfig.setCurrentGobiiCropType(testCropType);

    }

    /**
     * Dependency Injection Bean for the testing DataSource.
     * Postgres database connection configuration will be read from the gobii-web.xml file
     * from the location given as environment variable cfgFqpn.
     * Test crop type will also be read from environment variable cropType.
     * @return Implementation of AbstractRoutingDatasource abstract class for database connection lookup.
     * @throws Exception
     */
    @Bean(name="dataSourceMulti")
    public DataSourceSelector dataSourceMulti() throws Exception {

        DataSourceSelector returnVal = new DataSourceSelector();

        readTestConfig();

        GobiiCropConfig currentGobiiCropConfig = testConfig.getCurrentCropConfig();

        returnVal.setTestGobiiCropType(currentGobiiCropConfig.getGobiiCropType());

        Map<Object,Object> targetDataSources = new HashMap<>();

        ServerConfig currentPostGresConfig = currentGobiiCropConfig.getServer(ServerType.GOBII_PGSQL);

        DriverManagerDataSource currentDataSource = new DriverManagerDataSource();

        currentDataSource.setDriverClassName("org.postgresql.Driver");

        String url = HelperFunctions.getJdbcConnectionString(currentPostGresConfig);

        currentDataSource.setUrl(url);
        currentDataSource.setUsername(currentPostGresConfig.getUserName());
        currentDataSource.setPassword(currentPostGresConfig.getPassword());

        targetDataSources.put(currentGobiiCropConfig.getGobiiCropType(),currentDataSource);

        returnVal.setTargetDataSources(targetDataSources);

        return returnVal;
    }


}
