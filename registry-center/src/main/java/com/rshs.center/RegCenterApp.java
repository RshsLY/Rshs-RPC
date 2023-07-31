package com.rshs.center;

import com.rshs.api.config.Config;
import com.rshs.api.protocol.MessageCodec;
import com.rshs.api.protocol.ProtocolFrameDecoder;
import com.rshs.center.config.CenterConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
public class RegCenterApp {


    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(RegCenterApp.class, args);
//        String[] names = run.getBeanDefinitionNames();
//        for (String name : names) {
//            System.out.println("spring bean名称>>>>>>" + name);
//        }
//        System.out.println("------Bean 总计:" + run.getBeanDefinitionCount());
        StringRedisTemplate stringRedisTemplate = run.getBean(StringRedisTemplate.class);

        // 启动服务端
        log.info("RPC Reg Center 服务开始启动");
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(10);
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodec MESSAGE_CODEC = new MessageCodec();
        try {
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            // 固定时间之内没有收到客户端请求的话就关闭连接
                            ch.pipeline().addLast(new IdleStateHandler(CenterConfig.getCenterNoReqCloseChannelTime(), 0, 0, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new ProtocolFrameDecoder());
                            ch.pipeline().addLast(LOGGING_HANDLER);
                            ch.pipeline().addLast(MESSAGE_CODEC);
                            ch.pipeline().addLast(new RegServerHandler(stringRedisTemplate));
                            ch.pipeline().addLast(new ServerAddrHandler(stringRedisTemplate));
                        }
                    })
                    // 全连接队列里，还未被accept的连接最大数量
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //  是否开启 TCP 底层心跳机制 这个参数只是过一段时间内客户端没有响应 ，服务端会发送一个 ack 包，以判断客户端是否还活着。
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // 绑定端口，开始接收进来的链接
            ChannelFuture channelFuture = serverBootstrap.bind(Config.getRegCenterPort()).syncUninterruptibly();
            log.info("RPC Reg Center 启动完成，监听【" + Config.getRegCenterPort() + "】端口");
            channelFuture.channel().closeFuture().syncUninterruptibly();
            log.info("RPC Reg Center 关闭完成");
        }catch (Exception e) {
            log.error("RPC Reg Center 异常", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
