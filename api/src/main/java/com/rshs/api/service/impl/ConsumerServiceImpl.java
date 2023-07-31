package com.rshs.api.service.impl;


import com.rshs.api.message.RpcRequestMessage;
import com.rshs.api.message.RpcResponseMessage;
import com.rshs.api.message.ServerAddrRespMessage;
import com.rshs.api.service.ChannelService;
import com.rshs.api.service.ConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class ConsumerServiceImpl implements ConsumerService {

    @Resource
    ChannelService channelService;

    private ConcurrentHashMap<Integer, CompletableFuture<RpcResponseMessage>> rpcCompletableFutureMap=new ConcurrentHashMap<>();

    @Override
    public void completeRpcFuture(RpcResponseMessage rpcResponseMessage){
         rpcCompletableFutureMap.get(rpcResponseMessage.getSequenceId()).complete(rpcResponseMessage);
    }


    @Override
    public Object doConsume(ServerAddrRespMessage serverAddr, RpcRequestMessage rpcRequestMessage) throws ExecutionException, InterruptedException {
        CompletableFuture<RpcResponseMessage> completableFuture = new CompletableFuture<>();
        rpcCompletableFutureMap.put(rpcRequestMessage.getSequenceId(),completableFuture);
        channelService.getChannel(serverAddr.getHost(),serverAddr.getPort()).
                writeAndFlush(rpcRequestMessage)
                .addListener(promise -> {
                    if (!promise.isSuccess()) {
                        Throwable cause = promise.cause();
                        log.error("rpc服务调用失败，原因：", cause);
                    }
                });

        return completableFuture.get().getReturnValue();
    }
}
