package com.huxq17.xprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class SPUtils {
    private static final String SP_FILE_NAME = "XPrefs";
    public static final ThreadLocal<String> fileNameLocal = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return SP_FILE_NAME;
        }
    };
    public static final ThreadLocal<Integer> fileModeLocal = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return Context.MODE_PRIVATE;
        }
    };

    public static void setFileName(String name) {
        if (!TextUtils.isEmpty(name)) {
            fileNameLocal.set(name);
            fileModeLocal.set(Context.MODE_PRIVATE);
        }
    }

    public static void setFileMode(int fileMode) {
        if (isInvalidFileMode(fileMode)) {
            fileMode = Context.MODE_PRIVATE;
        }
        fileModeLocal.set(fileMode);
    }

    public static String getFileName() {
        return fileNameLocal.get();
    }

    public static int getFileMode() {
        return fileModeLocal.get();
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param value
     */
    public static void put(Context context, String key, Object value) {
        SharedPreferences sp = context.getSharedPreferences(getFileName(), getFileMode());
        SharedPreferences.Editor editor = sp.edit();
        add(editor, key, value);
        SharedPreferencesCompat.apply(editor);
    }

    protected static SharedPreferences.Editor add(SharedPreferences.Editor editor, String key, Object value) {
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
            editor.putString(key, value.toString());
        }
        return editor;
    }

    protected static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences sp = context.getSharedPreferences(getFileName(), getFileMode());
        return sp.edit();
    }

    protected static SharedPreferences getSharedPrefs(Context context, String fileName, int fileMode) {
        if (TextUtils.isEmpty(fileName)) {
            fileName = SP_FILE_NAME;
            fileMode = Context.MODE_PRIVATE;
        } else if (isInvalidFileMode(fileMode)) {
            fileMode = Context.MODE_PRIVATE;
        }
        return context.getSharedPreferences(fileName, fileMode);
    }

    private static boolean isInvalidFileMode(int fileMode) {
        if (fileMode != Context.MODE_PRIVATE && fileMode != Context.MODE_APPEND && fileMode != Context.MODE_ENABLE_WRITE_AHEAD_LOGGING && fileMode != Context.MODE_MULTI_PROCESS
                && fileMode != Context.MODE_WORLD_READABLE && fileMode != Context.MODE_WORLD_WRITEABLE) {
            LogUtils.e("filemode " + fileMode + " is wrong, so MODE_PRIVATE become effective");
            return true;
        }
        return false;
    }

    protected static void apply(SharedPreferences.Editor editor) {
        SharedPreferencesCompat.apply(editor);
    }

    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(getFileName(), getFileMode());
        return sp.getString(key, "");
    }

    public static int getInt(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(getFileName(), getFileMode());
        return sp.getInt(key, -1);
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(getFileName(), getFileMode());
        return sp.getBoolean(key, false);
    }

    public static long getLong(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(getFileName(), getFileMode());
        return sp.getLong(key, -1L);
    }

    public static float getFloat(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(getFileName(), getFileMode());
        return sp.getFloat(key, -1f);
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param context
     * @param key
     */
    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(getFileName(), getFileMode());
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     *
     * @param context
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(getFileName(), getFileMode());
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(getFileName(), getFileMode());
        return sp.contains(key);
    }

    public static boolean contains(Context context, String key,String fileName,int fileMode) {
        SharedPreferences sp = getSharedPrefs(context,fileName,fileMode);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(getFileName(), getFileMode());
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }

}