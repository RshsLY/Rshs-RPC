package com.rshs.api.message;

import lombok.Data;

@Data
public class RegRequestMessage extends Message {
    String interfaceClassName;
    String address;

    public RegRequestMessage(String interfaceClassName, String address) {
        this.interfaceClassName = interfaceClassName;
        this.address = address;
    }

    @Override
    public int getMessageType() {
        return REG_REQ_MESSAGE_TYPE;
    }
}
