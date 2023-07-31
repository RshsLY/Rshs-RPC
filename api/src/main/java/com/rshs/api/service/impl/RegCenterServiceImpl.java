package com.rshs.api.service.impl;


import com.rshs.api.config.Config;
import com.rshs.api.message.RegRequestMessage;
import com.rshs.api.message.ServerAddrReqMessage;
import com.rshs.api.message.ServerAddrRespMessage;
import com.rshs.api.protocol.SequenceIdGenerator;
import com.rshs.api.service.ChannelService;
import com.rshs.api.service.RegCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class RegCenterServiceImpl implements RegCenterService {
    @Resource
    ChannelService channelService;

    @Override
    public void  regServerToRegCenter(String className, String address){
        channelService.getChannel(Config.getRegCenterHost(),Config.getRegCenterPort()).writeAndFlush(new RegRequestMessage(className,address))
                .addListener(promise -> {
                    if (!promise.isSuccess()) {
                        Throwable cause = promise.cause();
                        log.error("注册服务失败，原因：", cause);
                    }
                });
    }

    private  ConcurrentHashMap<Integer,CompletableFuture<ServerAddrRespMessage>>addrCompletableFutureMap=new ConcurrentHashMap<>();
    @Override
    public void completeGetAddrFuture(ServerAddrRespMessage serverAddrRespMessage){
        addrCompletableFutureMap.get(serverAddrRespMessage.getSequenceId()).complete(serverAddrRespMessage);
    }

    @Override
    public ServerAddrRespMessage getServerAddr(String interfaceClassName) throws ExecutionException, InterruptedException {
        CompletableFuture<ServerAddrRespMessage> completableFuture = new CompletableFuture<>();
        ServerAddrReqMessage serverAddrReqMessage = new ServerAddrReqMessage(SequenceIdGenerator.nextId(), interfaceClassName);
        addrCompletableFutureMap.put(serverAddrReqMessage.getSequenceId(),completableFuture);
        channelService.getChannel(Config.getRegCenterHost(),Config.getRegCenterPort()).
                writeAndFlush(serverAddrReqMessage)
                .addListener(promise -> {
                    if (!promise.isSuccess()) {
                        Throwable cause = promise.cause();
                        log.error("注册服务失败，原因：", cause);
                    }
                });

        return completableFuture.get();
    }

}
