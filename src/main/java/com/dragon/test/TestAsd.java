package com.dragon.test;

import com.dragon.tcp.annotation.RpcServer;

/**
 * @author xuejingbao
 * @create 2021-12-23 13:59
 */
@RpcServer
public class TestAsd implements TestMyMethord{
    @Override
    public String gotTest(String str) {
        return "null:"+str;
    }
}
