package com.dragon.tcp.config;

import com.dragon.business.pojo.Address;
import com.dragon.tcp.serial.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author xuejingbao
 * @create 2021-12-22 10:24
 */
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

    public static String getNodeName() throws Exception {
        String value = properties.getProperty("rpc.device.name");
        if (value == null){
            throw new Exception("没有rpc.device.name配置");
        }
        return value;
    }

    public static Address getMyAddress() throws Exception {
        String value = properties.getProperty("rpc.my.server");
        if (value == null){
            throw new Exception("没有rpc.my.server配置");
        }
        String[] split = value.split(":");
        return new Address(split[0],Integer.parseInt(split[1]));
    }

    public static Address getCenter() throws Exception {
        String value = properties.getProperty("rpc.center.server");
        if (value == null){
            throw new Exception("没有rpc.center.server配置");
        }
        String[] split = value.split(":");
        return new Address(split[0],Integer.parseInt(split[1]));
    }

    public static Serializer.Algorithm getSerializerAlgorithm() {
        String value = properties.getProperty("rpc.serializer");
        if(value == null) {
            return Serializer.Algorithm.Protostuff;
        } else {
            return Serializer.Algorithm.valueOf(value);
        }
    }
    
}
