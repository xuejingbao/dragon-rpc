package com.dragon.tcp.core;

/**
 * @author xuejingbao
 * @create 2021-12-21 16:40
 */
public class RpcResponseMessage extends Message{

    /**
     * 请求id（8个字节）
     */
    private long requestId;
    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 异常值
     */
    private Exception exceptionValue;

    public RpcResponseMessage() {
    }

    public RpcResponseMessage(long requestId, Object returnValue, Exception exceptionValue) {
        this.requestId = requestId;
        this.returnValue = returnValue;
        this.exceptionValue = exceptionValue;
    }

    @Override
    public int getMessageType() {
        return Message.RPC_MESSAGE_TYPE_RESPONSE;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public Exception getExceptionValue() {
        return exceptionValue;
    }

    public void setExceptionValue(Exception exceptionValue) {
        this.exceptionValue = exceptionValue;
    }

    @Override
    public String toString() {
        return "RpcResponseMessage{" +
                "requestId=" + requestId +
                ", returnValue=" + returnValue +
                ", exceptionValue=" + exceptionValue +
                '}';
    }
}
