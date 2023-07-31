package com.rshs.api.message;

import lombok.Data;
import lombok.ToString;


@Data
@ToString(callSuper = true)
public class RpcResponseMessage extends Message {
    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 方法返回类型
     */
    private Class returnType;
    /**
     * 异常值
     */
    private Exception exceptionValue;

    public RpcResponseMessage(Integer id,Object returnValue, Exception exceptionValue,Class clazz) {
        super.setSequenceId(id);
        this.returnValue = returnValue;
        this.exceptionValue = exceptionValue;
        this.returnType =clazz;
    }

    @Override
    public int getMessageType() {
        return RPC_RESP_MESSAGE_TYPE;
    }
}
