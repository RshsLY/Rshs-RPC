package com.rshs.api.message;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class Message implements Serializable {

    /**
     * 根据消息类型字节，获得对应的消息 class
     * @param messageType 消息类型字节
     * @return 消息 class
     */
    public static Class<? extends Message> getMessageClass(int messageType) {
        return messageClasses.get(messageType);
    }

    private int sequenceId;

    private int messageType;

    public abstract int getMessageType();


    /**
     * 注册服务请求消息， byte值
     */
    public static final int REG_REQ_MESSAGE_TYPE = 1;
    /**
     * 注册服务相应消息， byte值
     */
    public static final int REG_RESP_MESSAGE_TYPE = 2;
    
    
    /**
    /**
     * 请求类型 byte 值
     */
    public static final int RPC_REQ_MESSAGE_TYPE = 3;
    /**
     * 响应类型 byte 值
     */
    public static final int  RPC_RESP_MESSAGE_TYPE = 4;

    /**
     * 请求server地址
     */
    public static final int ADDR_REQ_MESSAGE_TYPE = 5;
    /**
     * 响应server地址
     */
    public static final int ADDR_RESP_MESSAGE_TYPE = 6;

    private static final Map<Integer, Class<? extends Message>> messageClasses = new HashMap<>();

    static {

        messageClasses.put(RPC_REQ_MESSAGE_TYPE, RpcRequestMessage.class);
        messageClasses.put(RPC_RESP_MESSAGE_TYPE, RpcResponseMessage.class);
        messageClasses.put(REG_REQ_MESSAGE_TYPE, RegRequestMessage.class);
        messageClasses.put(REG_RESP_MESSAGE_TYPE, RegResponseMessage.class);
        messageClasses.put(ADDR_REQ_MESSAGE_TYPE,ServerAddrReqMessage.class);
        messageClasses.put(ADDR_RESP_MESSAGE_TYPE,ServerAddrRespMessage.class);
    }

}
