package com.rshs.api.message;

import lombok.Data;

@Data
public class RegResponseMessage extends Message {
    String interfaceClassName;
    String address;
    String status;

    public RegResponseMessage(String interfaceClassName, String address, String status) {
        this.interfaceClassName = interfaceClassName;
        this.address = address;
        this.status = status;
    }

    @Override
    public int getMessageType() {
        return REG_RESP_MESSAGE_TYPE;
    }
}
