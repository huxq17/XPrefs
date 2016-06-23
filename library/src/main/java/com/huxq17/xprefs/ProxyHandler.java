package com.huxq17.xprefs;

import com.huxq17.xprefs.processor.interfaces.ProxyProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by huxq17 on 2016/6/22.
 */
public class ProxyHandler implements InvocationHandler {
    private ProxyProcessor processor;

    public static <T> T getObject(Class<T> cls, ProxyProcessor processor) {
        ProxyHandler proxyHandler = new ProxyHandler();
        proxyHandler.processor = processor;
        return processor.getObject(cls, proxyHandler);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return processor.invoke(proxy, method, args);
    }


}
