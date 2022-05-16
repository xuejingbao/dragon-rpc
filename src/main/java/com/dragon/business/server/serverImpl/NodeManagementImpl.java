package com.dragon.business.server.serverImpl;

import com.dragon.business.pojo.Address;
import com.dragon.tcp.annotation.RpcInterFace;

import java.util.Map;

/**
 * @author xuejingbao
 * @create 2021-12-22 10:35
 */
@RpcInterFace("center")
public interface NodeManagementImpl {

    Boolean register(String nodeName, Address address);

    Map<String, Address> getAllRegisterNodes();

}
