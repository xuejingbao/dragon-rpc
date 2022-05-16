package com.dragon.business.server;

import com.dragon.business.pojo.Address;
import com.dragon.business.server.serverImpl.NodeManagementImpl;
import com.dragon.tcp.annotation.RpcServer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xuejingbao
 * @create 2021-12-22 10:09
 */
@RpcServer
public class NodeManagement implements NodeManagementImpl {

    public static Map<String, Address> nodeAddressRelevance = new ConcurrentHashMap<>();

    @Override
    public Boolean register(String nodeName, Address address) {
        if (nodeAddressRelevance.containsKey(nodeName)) {
            return false;
        }
        nodeAddressRelevance.put(nodeName,address);
        return true;
    }

    @Override
    public Map<String, Address> getAllRegisterNodes() {
        return nodeAddressRelevance;
    }

}
