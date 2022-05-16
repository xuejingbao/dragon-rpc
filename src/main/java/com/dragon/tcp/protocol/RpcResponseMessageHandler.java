package com.dragon.tcp.protocol;

import com.dragon.tcp.core.RpcResponseMessage;
import com.dragon.tcp.manager.NettyClient;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xuejingbao
 * @create 2021-12-22 15:04
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        try {
            Promise<Object> promise = NettyClient.promiseMap.remove(msg.getRequestId());
            if (promise != null) {
                Object returnValue = msg.getReturnValue();
                Exception exceptionValue = msg.getExceptionValue();
                if (exceptionValue != null) {
                    promise.setFailure(exceptionValue);
                } else {
                    promise.setSuccess(returnValue);
                }
            } else {
                promise.setFailure(new Exception("服务端返回了一个客户端未调用的方法"));
            }
        } finally {

        }
    }
}
