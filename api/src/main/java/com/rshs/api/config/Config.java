package com.rshs.api.config;

import com.rshs.api.protocol.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class Config {
    static Properties properties;
    static {
        try (InputStream in = Config.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    public static String getRegCenterHost() {
        String value = properties.getProperty("rshs.center.host");
        if(value == null) {
            return "localhost";
        } else {
            return value;
        }
    }
    public static int getRegCenterPort() {
        String value = properties.getProperty("rshs.center.port");
        if(value == null) {
            return 8080;
        } else {
            return Integer.parseInt(value);
        }
    }
    public static Serializer.Algorithm getSerializerAlgorithm() {
        String value = properties.getProperty("rshs.serializer.algorithm");
        if(value == null) {
            return Serializer.Algorithm.Json;
        } else {
            return Serializer.Algorithm.valueOf(value);
        }
    }
}