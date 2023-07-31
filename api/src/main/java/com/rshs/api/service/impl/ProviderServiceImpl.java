package com.rshs.api.service.impl;

import com.rshs.api.handler.ProviderHandler;
import com.rshs.api.protocol.MessageCodec;
import com.rshs.api.protocol.ProtocolFrameDecoder;
import com.rshs.api.service.ProviderService;
import com.rshs.api.service.RegCenterService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ProviderServiceImpl implements ProviderService {



    @Resource
    RegCenterService regCenterService;

    @Override
    public void startAndRegServer(Object provider,int port){
        String className = provider.getClass().getInterfaces()[0].getCanonicalName();
        try {
            String address= InetAddress.getLocalHost().getHostAddress()+"."+port;
            new Thread(()->{
                startAndRegServerInThread(provider,className,address,port);
            }).start();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

    }
    public void startAndRegServerInThread(Object provider,String className,String address,Integer port){
        // 启动服务端
        log.info("RPC 服务开始启动服务端");
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(10);
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodec MESSAGE_CODEC = new MessageCodec();

        ScheduledExecutorService regToRedisService = Executors.newSingleThreadScheduledExecutor();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast(new ProtocolFrameDecoder());
                            ch.pipeline().addLast(LOGGING_HANDLER);
                            ch.pipeline().addLast(MESSAGE_CODEC);
                            ch.pipeline().addLast(new ProviderHandler(provider));
                        }
                    })
                    // 全连接队列里，还未被accept的连接最大数量
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 这个参数只是过一段时间内客户端没有响应，服务端会发送一个 ack 包，以判断客户端是否还活着。
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 绑定端口，开始接收进来的链接
            ChannelFuture channelFuture = serverBootstrap.bind(port).syncUninterruptibly();
            log.info("RPC 服务端启动完成，监听【" + port + "】端口");
            //心跳保持，定时重新注册一次
            regToRedisService.scheduleAtFixedRate(()->{
               regCenterService.regServerToRegCenter(className,address);
            }, 0, 60, TimeUnit.SECONDS);

            channelFuture.channel().closeFuture().syncUninterruptibly();
            regToRedisService.shutdown();
            log.info("RPC 服务端关闭完成");
        } catch (Exception e) {
            log.error("RPC 服务异常", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            regToRedisService.shutdown();
        }
    }
}
