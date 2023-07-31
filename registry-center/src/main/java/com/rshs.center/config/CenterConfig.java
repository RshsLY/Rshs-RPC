package com.rshs.center.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class CenterConfig {
    static Properties properties;
    static {
        try (InputStream in = CenterConfig.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    public static int getCenterNoReqCloseChannelTime() {
        String value = properties.getProperty("rshs.center.no.req.close.channel.time");
        if(value == null) {
            return 30;
        } else {
            return Integer.parseInt(value);
        }
    }

}