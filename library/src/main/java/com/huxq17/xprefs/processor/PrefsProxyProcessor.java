package com.huxq17.xprefs.processor;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.huxq17.xprefs.LogUtils;
import com.huxq17.xprefs.XPrefs;
import com.huxq17.xprefs.annotations.XGet;
import com.huxq17.xprefs.annotations.XSPFile;
import com.huxq17.xprefs.annotations.XSet;
import com.huxq17.xprefs.processor.interfaces.ProxyProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

/**
 * Created by huxq17 on 2016/6/23.
 */
public class PrefsProxyProcessor implements ProxyProcessor {
    private static final String MSG_ERROR_RETURN_TYPE = "the value of %s is not a %s type,please checkout it out and modify mothed %s's return type.";

    @Override
    public <T> T getObject(Class<T> cls, InvocationHandler proxyHandler) {
        return (T) Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, proxyHandler);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?>[] classes = proxy.getClass().getInterfaces();
        String fileName = null;
        int fileMode = Context.MODE_PRIVATE;
        if (null == classes || classes.length <= 0) {
            LogUtils.e("这不是正经的接口");
            return null;
        } else {
            Class<?> cls = classes[0];
            if (cls.isAnnotationPresent(XSPFile.class)) {
                fileName = cls.getAnnotation(XSPFile.class).fileName();
                fileMode = cls.getAnnotation(XSPFile.class).fileMode();
            }
            if (method.isAnnotationPresent(XGet.class) && method.isAnnotationPresent(XSet.class)) {
                throw new RuntimeException("@XSet and @XGet can not exist together!");
            }
            if (method.isAnnotationPresent(XGet.class)) {
                return doGet(method, fileName, fileMode);
            } else if (method.isAnnotationPresent(XSet.class)) {
                doSet(method, args, fileName, fileMode);
            } else {
                LogUtils.e("Method " + method.getName() + " have neither @XGet nor @XSet, so return null");
            }
        }
        return null;
    }

    private void doSet(Method method, Object[] args, String fileName, int fileMode) {
        if (!TextUtils.isEmpty(method.getAnnotation(XSet.class).fileName())) {
            fileName = method.getAnnotation(XSet.class).fileName();
            fileMode = method.getAnnotation(XSet.class).fileMode();
        }
        String key = method.getAnnotation(XSet.class).key();
        if (TextUtils.isEmpty(key)) {
            LogUtils.e("SharedPreferences's key " + key + " in @XSet is empty, so return null");
            return;
        }
        if (args == null || (args.length == 0) || args.length > 1 || args[0] == null) {
            throw new RuntimeException("method " + method.getName() + " can have one and only one parameter,and paramter can not be null");
        }
        Object value = args[0];
        SharedPreferences.Editor editor = XPrefs.getSharedPrefs(fileName, fileMode).edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else {
            LogUtils.e("can not support save value " + value.getClass().getName() + ",please contact author for help");
        }
        XPrefs.apply(editor);
    }

    private Object doGet(Method method, String fileName, int fileMode) {
        if (!TextUtils.isEmpty(method.getAnnotation(XGet.class).fileName())) {
            fileName = method.getAnnotation(XGet.class).fileName();
            fileMode = method.getAnnotation(XGet.class).fileMode();
        }
        String key = method.getAnnotation(XGet.class).key();
        if (TextUtils.isEmpty(key)) {
            LogUtils.e("SharedPreferences's key " + key + " in @XGet is empty, so return null");
            return null;
        }
        if (!XPrefs.contains(key, fileName, fileMode)) {
            LogUtils.e("SharedPreferences's file " + fileName + " do not contain key=" + key + ", so return null");
            return null;
        }
        Type returnType = method.getGenericReturnType();
        SharedPreferences sp = XPrefs.getSharedPrefs(fileName, fileMode);
        if (returnType == Integer.TYPE) {
            try {
                sp.getInt(key, -1);
            } catch (ClassCastException e) {
                throw new RuntimeException(String.format(MSG_ERROR_RETURN_TYPE, key, returnType, method.getName()));
            }
            return sp.getInt(key, -1);
        } else if (returnType == Float.TYPE) {
            try {
                sp.getFloat(key, -1);
            } catch (ClassCastException e) {
                throw new RuntimeException(String.format(MSG_ERROR_RETURN_TYPE, key, returnType, method.getName()));
            }
            return sp.getFloat(key, -1);
        } else if (returnType == Long.TYPE) {
            try {
                sp.getLong(key, -1);
            } catch (ClassCastException e) {
                throw new RuntimeException(String.format(MSG_ERROR_RETURN_TYPE, key, returnType, method.getName()));
            }
            return sp.getLong(key, -1);
        } else if (returnType == Boolean.TYPE) {
            try {
                sp.getBoolean(key, false);
            } catch (ClassCastException e) {
                throw new RuntimeException(String.format(MSG_ERROR_RETURN_TYPE, key, returnType, method.getName()));
            }
            return sp.getBoolean(key, false);
        } else if (returnType == String.class) {
            try {
                sp.getString(key, "");
            } catch (ClassCastException e) {
                throw new RuntimeException(String.format(MSG_ERROR_RETURN_TYPE, key, returnType, method.getName()));
            }
            return sp.getString(key, "");
        } else {
            LogUtils.e("can not support type " + returnType + ",please contact author for help");
        }
        return null;
    }
}
