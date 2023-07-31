package com.rshs.api.service;

import com.rshs.api.message.ServerAddrRespMessage;

import java.util.concurrent.ExecutionException;

public interface RegCenterService {
    public void  regServerToRegCenter(String className, String address);

    void completeGetAddrFuture(ServerAddrRespMessage serverAddrRespMessage);

    ServerAddrRespMessage getServerAddr(String interfaceClassName) throws ExecutionException, InterruptedException;
}
