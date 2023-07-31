package com.rshs.center;

import com.rshs.api.message.RegRequestMessage;
import com.rshs.api.message.RegResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;


public class RegServerHandler extends SimpleChannelInboundHandler<RegRequestMessage> {

    private StringRedisTemplate stringRedisTemplate;
    public RegServerHandler(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate=stringRedisTemplate;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RegRequestMessage regRequestMessage) throws Exception {
        // 获取当前时间戳
        long currentTimeMillis = System.currentTimeMillis();
        // 加上30s作为服务注册的过期时间
        int secondsToAdd = 45;
        long futureTimeMillis = currentTimeMillis + (secondsToAdd * 1000);
        stringRedisTemplate.opsForHash().put(regRequestMessage.getInterfaceClassName(),regRequestMessage.getAddress(),String.valueOf(futureTimeMillis));
        channelHandlerContext.writeAndFlush(new RegResponseMessage(regRequestMessage.getInterfaceClassName(),
                regRequestMessage.getAddress(),"RegCenter 已经向Redis请求写入"));
    }
}
