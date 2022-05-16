package com.dragon.test;

import com.dragon.tcp.annotation.RpcInterFace;

/**
 * @author xuejingbao
 * @create 2021-12-23 13:58
 */
@RpcInterFace("center")
public interface TestMyMethord {

    String gotTest(String str);
}
