package com.rshs.api.handler;

import com.rshs.api.message.RegResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegToCenterHandler extends SimpleChannelInboundHandler<RegResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RegResponseMessage regResponseMessage) {
        log.info("注册服务，interface：{}，addr：{}，status：{}",
                regResponseMessage.getInterfaceClassName(),regResponseMessage.getAddress(),regResponseMessage.getStatus());

    }
}
