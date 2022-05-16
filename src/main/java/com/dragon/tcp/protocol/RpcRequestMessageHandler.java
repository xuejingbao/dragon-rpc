package com.dragon.tcp.protocol;


import com.dragon.tcp.core.RpcRequestMessage;
import com.dragon.tcp.core.RpcResponseMessage;
import com.dragon.tcp.factory.ServiceFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author xuejingbao
 * @create 2021-12-21 17:09
 */
@ChannelHandler.Sharable
@Slf4j
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) throws Exception {
        RpcResponseMessage responseMessage=new RpcResponseMessage();
        responseMessage.setRequestId(msg.getRequestId());
        Object result;
        try {
            Object serviceInstance = ServiceFactory.getInstance().getServiceFactory().get(msg.getInterfaceName());
            Method method = serviceInstance.getClass().getMethod(msg.getMethodName(),msg.getParameterTypes());
            result = method.invoke(serviceInstance, msg.getParams());
            responseMessage.setReturnValue(result);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            log.error(e.getMessage(),e);
            responseMessage.setExceptionValue(new Exception("远程方法出错:"+e.getMessage()));
        } finally {
            ctx.writeAndFlush(responseMessage);
            ReferenceCountUtil.release(msg);
        }
    }

}
