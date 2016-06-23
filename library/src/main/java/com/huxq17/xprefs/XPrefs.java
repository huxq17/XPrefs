package com.huxq17.xprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.huxq17.xprefs.annotations.XIgnore;
import com.huxq17.xprefs.processor.PrefsProxyProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Created by huxq17 on 2016/6/20.
 */
public class XPrefs {
    private static Context mContext;
    private static final PrefsProxyProcessor prefsProxyProcessor = new PrefsProxyProcessor();

    /**
     * 使用前需先绑定contenxt
     *
     * @param context
     */
    public static void bind(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 切换当前操作的sharedpreferences的文件名
     *
     * @param fileName
     */
    public static void changeFileName(String fileName) {
        SPUtils.setFileName(fileName);
    }

    /**
     * 切换当前操作的sharedpreferences的文件操作模式
     *
     * @param mode
     */
    public static void changeFileMode(int mode) {
        SPUtils.setFileMode(mode);
    }

    /**
     * 切换当前操作的sharedpreferences的文件名和操作模式
     *
     * @param fileName
     * @param mode
     */
    public static void changeFileNameAndMode(String fileName, int mode) {
        if (!TextUtils.isEmpty(fileName)) {
            SPUtils.setFileName(fileName);
            SPUtils.setFileMode(mode);
        }
    }

    public static <T> T getObject(Class<T> cls) {
        return ProxyHandler.getObject(cls, prefsProxyProcessor);
    }

    public static SharedPreferences getSharedPrefs(String fileName, int fileMode) {
        return SPUtils.getSharedPrefs(mContext, fileName, fileMode);
    }

    /**
     * 保存javabean中的单个字段
     *
     * @param javabean
     * @param key      字段的名称
     */
    public static void save(Object javabean, String key) {
        SharedPreferences.Editor editor = getEditor();
        add(editor, javabean, key);
        apply(editor);
    }

    public static boolean contains(String key) {
        return SPUtils.contains(mContext, key);
    }

    public static boolean contains(String key, String fileName, int fileMode) {
        return SPUtils.contains(mContext, key, fileName, fileMode);
    }

    public static Map<String, ?> getAll() {
        return SPUtils.getAll(mContext);
    }

    public static void remove(String key) {
        SPUtils.remove(mContext, key);
    }

    public static void saveAll(Object javabean) {
        if (javabean == null) {
            return;
        }
        Class cls = javabean.getClass();
        Field[] fields = cls.getDeclaredFields();
        SharedPreferences.Editor editor = getEditor();
        for (Field field : fields) {
            add(editor, javabean, field.getName());
        }
        apply(editor);
    }

    public static SharedPreferences.Editor add(SharedPreferences.Editor editor, Object javabean, String key) {
        if (javabean == null || TextUtils.isEmpty(key) || editor == null) {
            return editor;
        }
        Class cls = javabean.getClass();
        try {
            Field field = javabean.getClass().getDeclaredField(key);
            if (isInvalidField(field)) {
                return editor;
            }
            setFieldAccessible(field);
            Object value = field.get(javabean);
            if (value != null) {
                SPUtils.add(editor, getKeyFromFieldName(cls, key), value);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return editor;
    }

    public static SharedPreferences.Editor getEditor() {
        return SPUtils.getEditor(mContext);
    }

    public static void apply(SharedPreferences.Editor editor) {
        SPUtils.apply(editor);
    }

    public static <T> T get(Class<T> cls) {
        assertClassNotNull(cls);
        T result = getInstanceFromClass(cls);
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            String type = field.getType().getCanonicalName();
            String name = field.getName();
            Object value = null;
            if (isInvalidField(field)) {
                continue;
            }
            if (type.contains("String")) {
                value = getString(cls, name);
            } else if (type.contains("int")) {
                value = getInt(cls, name);
            } else if (type.contains("float")) {
                value = getFloat(cls, name);
            } else if (type.contains("long")) {
                value = getLong(cls, name);
            } else if (type.contains("boolean")) {
                value = getBoolean(cls, name);
            }
            try {
                setFieldAccessible(field);
                field.set(result, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void clear() {
        SPUtils.getEditor(mContext).clear().commit();
    }

    public static void remove(Class cls) {
        assertClassNotNull(cls);
        Field[] fields = cls.getDeclaredFields();
        SharedPreferences.Editor editor = SPUtils.getEditor(mContext);
        for (Field field : fields) {
            setFieldAccessible(field);
            if (isInvalidField(field)) {
                continue;
            }
            editor.remove(getKeyFromFieldName(cls, field.getName()));
        }
        editor.commit();
    }

    public static String getString(Class cls, String key) {
        assertClassNotNull(cls);
        String result = null;
        try {
            Field field = cls.getDeclaredField(key);
            setFieldAccessible(field);
            String type = field.getType().getCanonicalName();
            if (type.contains("String")) {
                result = SPUtils.getString(mContext, getKeyFromFieldName(cls, key));
            } else {
                throw new RuntimeException("the " + key + "'s value is not a String");
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("can't find field  " + key + " in " + cls.getName());
        }
        return result;
    }

    public static int getInt(Class cls, String key) {
        assertClassNotNull(cls);
        int result = -1;
        try {
            Field field = cls.getDeclaredField(key);
            setFieldAccessible(field);
            String type = field.getType().getCanonicalName();
            if (type.contains("int")) {
                result = SPUtils.getInt(mContext, getKeyFromFieldName(cls, key));
            } else {
                throw new RuntimeException("the " + key + "'s value is not a int");
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("can't find field  " + key + " in " + cls.getName());
        }
        return result;
    }

    public static float getFloat(Class cls, String key) {
        assertClassNotNull(cls);
        float result = -1f;
        try {
            Field field = cls.getDeclaredField(key);
            setFieldAccessible(field);
            String type = field.getType().getCanonicalName();
            if (type.contains("float")) {
                result = SPUtils.getFloat(mContext, getKeyFromFieldName(cls, key));
            } else {
                throw new RuntimeException("the " + key + "'s value is not a float");
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("can't find field  " + key + " in " + cls.getName());
        }
        return result;
    }

    public static long getLong(Class cls, String key) {
        assertClassNotNull(cls);
        long result = -1L;
        try {
            Field field = cls.getDeclaredField(key);
            setFieldAccessible(field);
            String type = field.getType().getCanonicalName();
            if (type.contains("long")) {
                result = SPUtils.getLong(mContext, getKeyFromFieldName(cls, key));
            } else {
                throw new RuntimeException("the " + key + "'s value is not a long");
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("can't find field  " + key + " in " + cls.getName());
        }
        return result;
    }

    public static boolean getBoolean(Class cls, String key) {
        if (cls == null) {
            throw new RuntimeException("class can not be null");
        }
        boolean result = false;
        try {
            Field field = cls.getDeclaredField(key);
            setFieldAccessible(field);

            String type = field.getType().getCanonicalName();
            if (type.contains("boolean")) {
                result = SPUtils.getBoolean(mContext, getKeyFromFieldName(cls, key));
            } else {
                throw new RuntimeException("the " + key + "'s value is not a boolean");
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("can't find field  " + key + " in " + cls.getName());
        }
        return result;
    }

    private static void assertClassNotNull(Class cls) {
        if (cls == null) {
            throw new RuntimeException("class can not be null");
        }
    }

    private static boolean isInvalidField(Field field) {
        return Modifier.isFinal(field.getModifiers()) || field.isAnnotationPresent(XIgnore.class);
    }

    private static <T> T getInstanceFromClass(Class<T> cls) {
        try {
            return cls.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static String getKeyFromFieldName(Class cls, String fieldName) {
//        StringBuffer sb = new StringBuffer();
//        sb.append(cls.getSimpleName()).append("$_$").append(fieldName);
//        return sb.toString();
        return fieldName;
    }

    private static void setFieldAccessible(Field field) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
    }
}
