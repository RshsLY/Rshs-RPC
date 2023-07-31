package com.rshs.api.proxy;
import com.rshs.api.message.RpcRequestMessage;
import com.rshs.api.message.ServerAddrRespMessage;
import com.rshs.api.protocol.SequenceIdGenerator;
import com.rshs.api.service.ConsumerService;
import com.rshs.api.service.RegCenterService;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Dynamic proxy class.
 * When a dynamic proxy object calls a method, it actually calls the following invoke method.
 * It is precisely because of the dynamic proxy that the remote method called by the client is like calling the local method (the intermediate process is shielded)
 *
 * @author shuang.kou
 * @createTime 2020年05月10日 19:01:00
 */
@Slf4j
public class RpcConsumerProxy implements InvocationHandler {

    private RegCenterService regCenterService;
    private ConsumerService consumerService;
    private String interfaceName;

    public RpcConsumerProxy(RegCenterService regCenterService, ConsumerService consumerService, String interfaceName) {
        this.regCenterService = regCenterService;
        this.consumerService = consumerService;
        this.interfaceName = interfaceName;
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        log.info("invoked method: [{}]", method.getName());
        ServerAddrRespMessage serverAddr = null;
        try {
            serverAddr = regCenterService.getServerAddr(interfaceName);
            Object o=consumerService.doConsume(serverAddr,
                    new RpcRequestMessage(SequenceIdGenerator.nextId(),interfaceName,method.getName(),method.getReturnType(),method.getParameterTypes(),args));
            log.info(serverAddr.toString());
            return o;
        } catch (Exception e) {
            throw new RuntimeException("rpc服务调用失败:",e);
        }
    }
}
