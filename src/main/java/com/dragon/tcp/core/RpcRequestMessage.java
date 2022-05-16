package com.dragon.tcp.core;

import java.util.Arrays;

/**
 * @author xuejingbao
 * @create 2021-12-21 16:39
 */
public class RpcRequestMessage extends Message{

    /**
     * 请求id（8个字节）
     */
    private long requestId;
    /**
     * 接口名
     */
    private String interfaceName;
    /**
     * 接口方法名
     */
    private String methodName;
    /**
     * 方法参数
     */
    private Object[] params;
    /**
     * 方法参数类型
     */
    private Class<?>[] parameterTypes;

    public RpcRequestMessage() {
    }

    public RpcRequestMessage(long requestId, String interfaceName, String methodName, Object[] params, Class<?>[] parameterTypes) {
        this.requestId = requestId;
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.params = params;
        this.parameterTypes = parameterTypes;
    }

    @Override
    public int getMessageType() {
        return Message.RPC_MESSAGE_TYPE_REQUEST;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    @Override
    public String toString() {
        return "RpcRequestMessage{" +
                "requestId=" + requestId +
                ", interfaceName='" + interfaceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", params=" + Arrays.toString(params) +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                '}';
    }
}
