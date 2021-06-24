package org.gobiiproject.gobiiprocess.spring;

import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContextLoaderSingleton {
    private static final SpringContextLoaderSingleton INSTANCE = new SpringContextLoaderSingleton();

    private ApplicationContext context;
    private static String cropType;
    private static ConfigSettings configSettings;


    private SpringContextLoaderSingleton() {}

    public static SpringContextLoaderSingleton getInstance() {
        return INSTANCE;
    }

    public static void init(String cropType, ConfigSettings configSettings) {
        SpringContextLoaderSingleton.cropType = cropType;
        SpringContextLoaderSingleton.configSettings = configSettings;
        SpringContextLoaderSingleton.INSTANCE.context = new ClassPathXmlApplicationContext(
                "classpath:/spring/application-config.xml");
    }

    public ApplicationContext getContext() {
        if(SpringContextLoaderSingleton.cropType == null
            || SpringContextLoaderSingleton.configSettings == null) {
            throw new GobiiException(
                "Initialize Context with crop type and config location");
        }
        if(context == null) {
            context = new ClassPathXmlApplicationContext(
                "classpath:/spring/application-config.xml");
        }
        return context;
    }

    public <T> T getBean(Class<T> requiredType) {
        return this.context.getBean(requiredType);
    }

    public static String getCropType() {
        return cropType;
    }

    public static ConfigSettings getConfigSettings() {
        return configSettings;
    }
}
