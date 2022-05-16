package com.dragon;

import com.dragon.tcp.annotation.RpcScan;

/**
 * @author xuejingbao
 * @create 2021-12-22 9:49
 */
@RpcScan("com.dragon")
public class Main {

    public static void main(String[] args) {
        new DragonRpc().start();
    }

}
