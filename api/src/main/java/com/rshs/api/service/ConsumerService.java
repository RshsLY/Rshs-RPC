package com.rshs.api.service;

import com.rshs.api.message.RpcRequestMessage;
import com.rshs.api.message.RpcResponseMessage;
import com.rshs.api.message.ServerAddrRespMessage;

import java.util.concurrent.ExecutionException;

public interface ConsumerService {
     Object doConsume(ServerAddrRespMessage serverAddr, RpcRequestMessage rpcRequestMessage) throws ExecutionException, InterruptedException;

     void completeRpcFuture(RpcResponseMessage rpcResponseMessage);
}
