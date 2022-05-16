package com.dragon.tcp.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xuejingbao
 * @create 2021-12-21 16:36
 */
public abstract class Message {

    public Message() {
    }

    public abstract int getMessageType();
    /**
     * 请求类型 byte 值
     */
    public static final byte RPC_MESSAGE_TYPE_REQUEST = 0x22;
    /**
     * 响应类型 byte 值
     */
    public static final byte RPC_MESSAGE_TYPE_RESPONSE = 0x33;

    private static final Map<Byte, Class<? extends Message>> messageClasses = new ConcurrentHashMap<>();

    static {
        messageClasses.put(RPC_MESSAGE_TYPE_REQUEST, RpcRequestMessage.class);
        messageClasses.put(RPC_MESSAGE_TYPE_RESPONSE, RpcResponseMessage.class);
    }

    /**
     * 根据消息类型字节，获得对应的消息 class
     * @param messageType 消息类型字节
     * @return 消息 class
     */
    public static Class<? extends Message> getMessageClass(byte messageType) {
        return messageClasses.get(messageType);
    }

}
