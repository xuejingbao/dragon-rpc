package com.dragon.tcp.factory;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xuejingbao
 * @create 2021-12-22 10:52
 */
@Slf4j
public class ServiceFactory {

    private volatile static ServiceFactory instance;

    private ServiceFactory() {
    }

    public static ServiceFactory getInstance(){
        if (instance == null){
            synchronized (ServiceFactory.class){
                if (instance == null){
                    instance = new ServiceFactory();
                }
            }
        }
        return instance;
    }

    private Map<String, Object> serviceFactory = new ConcurrentHashMap<>();

    public Map<String, Object> getServiceFactory() {
        return serviceFactory;
    }






}
