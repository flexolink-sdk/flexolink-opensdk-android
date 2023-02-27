package com.flexolink.example;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * SharedPrefUtils
 * create by qyg 2018.10.16
 */
public class SharedPrefUtils {

    private static SharedPreferences getSP(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp;
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences spf = getSP(context);
        Editor edit = spf.edit();
        edit.putInt(key, value);
        edit.apply();
    }

    public static void putFloat(Context context, String key, float value) {
        SharedPreferences spf = getSP(context);
        Editor edit = spf.edit();
        edit.putFloat(key, value);
        edit.apply();
    }

    public static float getFloat(Context context, String key, int defaultValue) {
        SharedPreferences spf = getSP(context);
        return spf.getFloat(key, defaultValue);
    }

    public static float getFloat(Context context, String key) {
        return getFloat(context, key, 0);
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences spf = getSP(context);
        return spf.getInt(key, defaultValue);
    }

    public static int getInt(Context context, String key) {
        return getInt(context, key, 0);
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences spf = getSP(context);
        Editor edit = spf.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences spf = getSP(context);
        return spf.getString(key, defaultValue);
    }

    public static String getString(Context context, String key) {
        return getString(context, key, "");
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences spf = getSP(context);
        Editor edit = spf.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences spf = getSP(context);
        return spf.getBoolean(key, defaultValue);
    }

    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    public static void remove(Context context, String key) {
        SharedPreferences spf = getSP(context);
        Editor edit = spf.edit();
        edit.remove(key);
        edit.apply();
    }

    public static void clear(Context context) {
        SharedPreferences spf = getSP(context);
        Editor edit = spf.edit();
        edit.clear();
        edit.commit();
    }

    public static void registerOnSharedPreferenceChangeListener(Context context, OnSharedPreferenceChangeListener listener) {
        getSP(context).registerOnSharedPreferenceChangeListener(listener);
    }

    private static void unregisterOnSharedPreferenceChangeListener(Context context, OnSharedPreferenceChangeListener listener) {
        getSP(context).unregisterOnSharedPreferenceChangeListener(listener);
    }


    public static <T> List<Field> getPublicFields(Class<?> clazz) {
        if (clazz.equals(Object.class)) {
            return null;
        }
        //用来存储clazz中用public修饰的属性的list
        List<Field> list = new ArrayList<Field>();
        //获得clazz中所有用public修饰的属性
        Field[] fields = clazz.getFields();
        //将fields加入到list中
        for (int i = 0; i < fields.length; i++) {
            list.add(fields[i]);
        }
        return list;
    }




    /**
     * 根据key和预期的value类型获取value的值
     *
     * @param key
     * @param clazz
     * @return
     */
    public static <T> T getValue(Context context, String key, Class<T> clazz) {
        if (context == null) {
            throw new RuntimeException("请先调用带有context，name参数的构造！");
        }
        SharedPreferences sp = getSP(context);
        return getValue(key, clazz, sp);
    }

    /**
     * 针对复杂类型存储<对象>
     *
     * @param key
     */
    public static void putObject(Context context, String key, Object object) {
        SharedPreferences sp = getSP(context);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {

            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            Editor editor = sp.edit();
            editor.putString(key, objectVal);
            editor.commit();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> T getObject(Context context, String key, Class<T> clazz) {
        SharedPreferences sp = getSP(context);
        if (sp.contains(key)) {
            String objectVal = sp.getString(key, null);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 对于外部不可见的过渡方法
     *
     * @param key
     * @param clazz
     * @param sp
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T> T getValue(String key, Class<T> clazz, SharedPreferences sp) {
        T t;
        try {
            t = clazz.newInstance();
            if (t instanceof Integer) {
                return (T) Integer.valueOf(sp.getInt(key, 0));
            } else if (t instanceof String) {
                return (T) sp.getString(key, "");
            } else if (t instanceof Boolean) {
                return (T) Boolean.valueOf(sp.getBoolean(key, false));
            } else if (t instanceof Long) {
                return (T) Long.valueOf(sp.getLong(key, 0L));
            } else if (t instanceof Float) {
                return (T) Float.valueOf(sp.getFloat(key, 0L));
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
            Log.e("system", "类型输入错误或者复杂类型无法解析[" + e.getMessage() + "]");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.e("system", "类型输入错误或者复杂类型无法解析[" + e.getMessage() + "]");
        }
        Log.e("system", "无法找到" + key + "对应的值");
        return null;
    }

}
