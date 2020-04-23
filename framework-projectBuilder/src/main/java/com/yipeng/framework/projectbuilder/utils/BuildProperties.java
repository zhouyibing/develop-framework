package com.yipeng.framework.projectbuilder.utils;

import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * @author: yibingzhou
 */
public class BuildProperties {
    private static Properties properties;
    public static void load() {
        try {
            properties = PropertiesLoaderUtils.loadAllProperties("projectbuild.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getString(String key) {
        if(null == properties) load();
        if(null == properties) return null;
        return properties.getProperty(key);
    }
}
