package com.rshs.api.service.impl;


import com.rshs.api.handler.ConsumerHandler;
import com.rshs.api.handler.RegToCenterHandler;
import com.rshs.api.handler.ServerAddrHandler;
import com.rshs.api.protocol.MessageCodec;
import com.rshs.api.protocol.ProtocolFrameDecoder;
import com.rshs.api.service.ChannelService;
import com.rshs.api.service.ConsumerService;
import com.rshs.api.service.RegCenterService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ChannelServiceImpl implements ChannelService {

    @Resource
    RegCenterService regCenterService;

    @Resource
    ConsumerService consumerService;

    private final Object lock=new Object();
    Bootstrap bootstrap;

    public ChannelServiceImpl(){
        NioEventLoopGroup group= new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodec MESSAGE_CODEC = new MessageCodec();
        bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProtocolFrameDecoder());
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(new RegToCenterHandler());
                ch.pipeline().addLast(new ServerAddrHandler(regCenterService));
                ch.pipeline().addLast(new ConsumerHandler(consumerService));
            }
        });
    }
    ConcurrentHashMap<String,Channel> channelMap=new ConcurrentHashMap<>();

    @Override
    public  Channel getChannel(String host,int port){
        Channel channel = channelMap.get(host + port);
        if(channel!=null&&channel.isOpen()){
            return channel;
        }
        //lock 起来，防止map里的channel不是真正的channel，（被覆盖）
        synchronized (lock){
            channel = channelMap.get(host + port);
            if(channel==null||!channel.isOpen()) {
                channelMap.remove(host+port);
                ChannelFuture channelFuture = bootstrap.connect(host,port).syncUninterruptibly();
                channelMap.put(host+port,channelFuture.channel());
            }
        }
        return channelMap.get(host + port);
    }


}
