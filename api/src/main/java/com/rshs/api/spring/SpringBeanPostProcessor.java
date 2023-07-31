package com.rshs.api.spring;

import com.rshs.api.annotation.RshsConsumer;
import com.rshs.api.annotation.RshsProvider;
import com.rshs.api.proxy.RpcConsumerProxy;
import com.rshs.api.service.ConsumerService;
import com.rshs.api.service.ProviderService;
import com.rshs.api.service.RegCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;

@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    @Resource
    private ProviderService providerService;
    @Resource
    private RegCenterService regCenterService;

    @Resource
    private ConsumerService consumerService;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RshsProvider.class)) {
            log.info("[{}] is annotated with  [{}]", bean.getClass().getName(), RshsProvider.class.getCanonicalName());
            // get RpcService annotation
            RshsProvider annotation = bean.getClass().getAnnotation(RshsProvider.class);
            providerService.startAndRegServer(bean,annotation.port());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            RshsConsumer rshsConsumer = declaredField.getAnnotation(RshsConsumer.class);
            if (rshsConsumer != null) {
                RpcConsumerProxy rpcClientProxy = new RpcConsumerProxy(regCenterService,consumerService,declaredField.getType().getName());
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
        return bean;
    }
}
