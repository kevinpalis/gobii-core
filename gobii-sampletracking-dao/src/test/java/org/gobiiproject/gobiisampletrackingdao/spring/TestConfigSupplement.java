package org.gobiiproject.gobiisampletrackingdao.spring;

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
public class TestConfigSupplement {

    private static ConfigSettings testConfig;

    private static String CONFIG_FILE_LOCATION_PROP = "cfgFqpn";

    /**
     * Gets the environment variables for gobii-web.xml location and test crop type.
     * Reads the test configurations and sets the testExecConfig.
     * @throws Exception
     */
    public void readTestConfig() throws Exception{

        String configFileLocation = System.getProperty(CONFIG_FILE_LOCATION_PROP);

        if (configFileLocation == null) {
            String message = "The the environment does not define the FQPN of " +
                    "configuration in environment variable: " + CONFIG_FILE_LOCATION_PROP;
            throw new Exception(message);
        }

        testConfig = new ConfigSettings(configFileLocation);

        String testCropType = testConfig.getTestExecConfig().getTestCrop();

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
    public TestDataSourceSelector dataSourceMulti() throws Exception {

        TestDataSourceSelector returnVal = new TestDataSourceSelector();

        readTestConfig();

        GobiiCropConfig currentGobiiCropConfig =
            testConfig.getCurrentCropConfig();

        returnVal.setTestGobiiCropType(
            currentGobiiCropConfig.getGobiiCropType());

        Map<Object,Object> targetDataSources = new HashMap<>();

        ServerConfig currentPostGresConfig =
            currentGobiiCropConfig.getServer(ServerType.GOBII_PGSQL);

        DriverManagerDataSource currentDataSource =
            new DriverManagerDataSource();

        currentDataSource.setDriverClassName("org.postgresql.Driver");

        String url =
            HelperFunctions.getJdbcConnectionString(currentPostGresConfig);

        currentDataSource.setUrl(url);
        currentDataSource.setUsername(currentPostGresConfig.getUserName());
        currentDataSource.setPassword(currentPostGresConfig.getPassword());

        targetDataSources.put(
            currentGobiiCropConfig.getGobiiCropType(), currentDataSource);

        returnVal.setTargetDataSources(targetDataSources);

        return returnVal;
    }


}
