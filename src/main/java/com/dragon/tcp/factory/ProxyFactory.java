package com.dragon.tcp.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xuejingbao
 * @create 2021-12-22 15:58
 */
public class ProxyFactory {

    private volatile static ProxyFactory instance;

    private ProxyFactory(){}

    public static ProxyFactory getInstance(){
        if (instance == null){
            synchronized (ProxyFactory.class){
                if (instance == null){
                    instance = new ProxyFactory();
                }
            }
        }
        return instance;
    }

    private Map<String, Object> proxyFactory = new ConcurrentHashMap<>();

    public Map<String, Object> getServiceFactory() {
        return proxyFactory;
    }

}
