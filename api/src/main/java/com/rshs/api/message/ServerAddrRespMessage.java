package com.rshs.api.message;

import lombok.Data;

@Data
public class ServerAddrRespMessage  extends Message {
    private String host;
    private int port;

    public ServerAddrRespMessage(Integer id,String host, int port) {
        super.setSequenceId(id);
        this.host = host;
        this.port = port;
    }

    @Override
    public int getMessageType() {
        return ADDR_RESP_MESSAGE_TYPE;
    }
}
