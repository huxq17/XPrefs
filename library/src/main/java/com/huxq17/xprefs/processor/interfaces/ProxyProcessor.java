package com.huxq17.xprefs.processor.interfaces;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by huxq17 on 2016/6/23.
 */
public interface ProxyProcessor {
    <T> T getObject(Class<T> cls, InvocationHandler proxyHandler);

    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
