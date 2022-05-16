package com.dragon;

import com.dragon.business.pojo.Address;
import com.dragon.business.server.NodeManagement;
import com.dragon.business.server.serverImpl.NodeManagementImpl;
import com.dragon.tcp.annotation.RpcClient;
import com.dragon.tcp.annotation.RpcInterFace;
import com.dragon.tcp.annotation.RpcScan;
import com.dragon.tcp.annotation.RpcServer;
import com.dragon.tcp.config.Config;
import com.dragon.tcp.constants.RpcConstant;
import com.dragon.tcp.core.RpcRequestMessage;
import com.dragon.tcp.factory.ProxyFactory;
import com.dragon.tcp.factory.ServiceFactory;
import com.dragon.tcp.manager.NettyClient;
import com.dragon.tcp.manager.NettyServer;
import com.dragon.tcp.util.PackageScanUtils;
import com.dragon.tcp.util.ThreadPoolUtil;
import com.dragon.test.TestMyMethord;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * @author xuejingbao
 * @create 2021-12-22 10:07
 */
@Slf4j
public class DragonRpc {

    @RpcClient
    public static NodeManagementImpl nodeManagement;

    @RpcClient
    public static TestMyMethord testMyMethord;

    private ServiceFactory serviceFactory;
    private ProxyFactory proxyFactory;

    public DragonRpc() {
        this.serviceFactory = ServiceFactory.getInstance();
        this.proxyFactory = ProxyFactory.getInstance();
        scanAnnotation();
    }

    private void scanAnnotation() {
        String mainClassPath = PackageScanUtils.getStackTrace();
        Class<?> mainClass;
        try {
            mainClass = Class.forName(mainClassPath);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("启动类未找到");
        }
        String annotationValue = mainClass.getAnnotation(RpcScan.class).value();
        //如果注解路径的值是空，则等于main父路径包下
        if ("".equals(annotationValue)) {
            annotationValue = mainClassPath.substring(0, mainClassPath.lastIndexOf("."));
        }
        Set<Class<?>> clazzs = PackageScanUtils.getClasses(annotationValue);
        for (Class<?> clazz : clazzs) {
            try {
                scanServerAnnotation(clazz);
            } catch (IllegalAccessException | InstantiationException e) {
                log.error(e.getMessage(),e);
            }finally {
                try {
                    scanClientAnnotation(clazz);
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                    continue;
                }
            }
        }

        for (Class<?> clazz : clazzs) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.isAnnotationPresent(RpcClient.class)) {
                    Class<?> type = declaredField.getType();
                    String key = type.getAnnotation(RpcInterFace.class).value() + "-" + type.getName();
                    Object value = proxyFactory.getServiceFactory().get(key);
                    declaredField.setAccessible(true);
                    try {
                        declaredField.set(clazz,value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private void scanServerAnnotation(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        if (clazz.isAnnotationPresent(RpcServer.class)) {
            String serviceName = clazz.getInterfaces()[0].getName();
            Object instance;
            instance = clazz.newInstance();
            serviceFactory.getServiceFactory().put(serviceName, instance);
            log.info("服务{}添加至工厂",serviceName);
        }
    }



    private void scanClientAnnotation(Class<?> clazz) throws Exception {
        if (clazz.isAnnotationPresent(RpcInterFace.class)) {
            String node = clazz.getAnnotation(RpcInterFace.class).value();
            String key = node + "-" + clazz.getName();
            Object proxyInstance = getProxyService(clazz, node);

            proxyFactory.getServiceFactory().put(key, proxyInstance);
            log.info("注释代理{}添加至工厂",node);
        }
    }

    public static <T> T getProxyService(Class<T> serviceClass, String node) {
        //创建代理对象
        Object o = Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass},(proxy, method, args) -> {
            long sequenceId = NettyClient.nextId();
            RpcRequestMessage msg = new RpcRequestMessage(sequenceId,
                    serviceClass.getName(),
                    method.getName(),
                    args,
                    method.getParameterTypes()
                    );
            // 2. 准备一个空 Promise 对象，来接收结果 存入集合            指定 promise 对象异步接收结果线程
            DefaultPromise<Object> promise = new DefaultPromise<Object>(NettyClient.eventLoopGroup.next());
            NettyClient.promiseMap.put(sequenceId,promise);
            // 3. 将消息对象发送出去
            NettyClient.getInstance().sendRequest(msg, node);
            // 4. 等待 promise 结果
            promise.await();
            if(promise.isSuccess()) {
                // 调用正常
                return promise.getNow();
            } else {
                // 调用失败
                throw new RuntimeException(promise.cause());
            }
        });
        return (T) o;
    }


    public void start(){
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ThreadPoolUtil.execute(()->{
            try {
                Thread.currentThread().setName("rpc服务线程");
                Address myAddress = Config.getMyAddress();
                new NettyServer(myAddress.getIp(),myAddress.getPort(),countDownLatch).startNettyServer();
            } catch (Exception e) {
                log.error("start Netty Server Occur Exception,",e);
                e.printStackTrace();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage(),e);
        }
        log.info("netty服务启动后的事情");

        try {
            NodeManagement.nodeAddressRelevance.put(RpcConstant.CENTER,Config.getCenter());

            //NodeManagementImpl nodeManagement = (NodeManagementImpl)proxyFactory.getServiceFactory().get(Config.getNodeName());
            log.info("远程获得注册者们：{}", nodeManagement.getAllRegisterNodes());

            //Boolean register = nodeManagement.register(Config.getNodeName(), Config.getMyAddress());
            Boolean register = nodeManagement.register(Config.getNodeName(), Config.getMyAddress());
            log.info("注册是否成功：{}",register);

            log.info(testMyMethord.gotTest("bulabula"));

            //nodeManagement.register(Config.getNodeName(),Config.getMyAddress());
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

    }

}
