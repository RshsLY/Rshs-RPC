package com.rshs.api.message;

import lombok.Data;

@Data
public class ServerAddrReqMessage  extends Message {
    /**
     * 调用的接口全限定名，服务端根据它找到实现
     */
    private String interfaceName;

    public ServerAddrReqMessage(Integer id,String interfaceName) {
        super.setSequenceId(id);
        this.interfaceName = interfaceName;
    }

    @Override
    public int getMessageType() {
        return ADDR_REQ_MESSAGE_TYPE;
    }
}
