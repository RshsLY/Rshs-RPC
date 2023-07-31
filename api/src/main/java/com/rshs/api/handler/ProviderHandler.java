package com.rshs.api.handler;

import com.rshs.api.message.RpcRequestMessage;
import com.rshs.api.message.RpcResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

public class ProviderHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    private Object o;

    private final Object lock=new Object();

    public ProviderHandler(Object provider) {
        this.o=provider;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequestMessage rpcRequestMessage) throws Exception {
        Method method = o.getClass().getMethod(rpcRequestMessage.getMethodName(), rpcRequestMessage.getParameterTypes());
        Object invoke = method.invoke(o, rpcRequestMessage.getParameterValue());
        channelHandlerContext.writeAndFlush(new RpcResponseMessage(rpcRequestMessage.getSequenceId(),invoke,null,invoke.getClass()));
    }
}
