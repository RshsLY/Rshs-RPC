package com.rshs.api.handler;

import com.rshs.api.message.RpcResponseMessage;
import com.rshs.api.service.ConsumerService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsumerHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    private ConsumerService consumerService;
    public ConsumerHandler(ConsumerService consumerService){
        this.consumerService=consumerService;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponseMessage rpcResponseMessage) {
        consumerService.completeRpcFuture(rpcResponseMessage);
    }
}
