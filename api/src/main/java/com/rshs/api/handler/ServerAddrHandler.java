package com.rshs.api.handler;

import com.rshs.api.message.ServerAddrRespMessage;
import com.rshs.api.service.RegCenterService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerAddrHandler extends SimpleChannelInboundHandler<ServerAddrRespMessage> {
    private RegCenterService regCenterService;
    public ServerAddrHandler(RegCenterService regCenterService){
        this.regCenterService=regCenterService;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ServerAddrRespMessage serverAddrRespMessage) {
        regCenterService.completeGetAddrFuture(serverAddrRespMessage);
    }
}
