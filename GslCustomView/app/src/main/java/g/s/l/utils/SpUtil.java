package g.s.l.utils;

import android.content.Context;
import android.content.SharedPreferences;

import g.s.l.app.AbsApplication;
import g.s.l.app.GslApplication;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class SpUtil {

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param spName sp名称
     * @param key    键名
     * @param object 默认值
     */
    public static void put(String spName, String key, Object object) {
        put(Context.MODE_PRIVATE, spName, key, object);
    }

    public static void put(int mode, String spName, String key, Object object) {

        SharedPreferences sp = getSpWithMode(spName, mode);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param spName        sp名称
     * @param key           键名
     * @param defaultObject 默认值
     * @return
     */
    public static Object get(String spName, String key, Object defaultObject) {
        return get(spName, Context.MODE_PRIVATE, key, defaultObject);
    }

    public static Object get(String spName, int mode, String key, Object defaultObject) {
        SharedPreferences sp = getSpWithMode(spName, mode);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param spName sp名称
     * @param key    键
     */
    public static void remove(String spName, String key) {
        remove(spName, Context.MODE_PRIVATE, key);
    }

    public static void remove(String spName, int mode, String key) {
        SharedPreferences sp = getSpWithMode(spName, mode);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     *
     * @param spName sp名称
     */
    public static void clear(String spName) {
        clear(spName, Context.MODE_PRIVATE);
    }

    public static void clear(String spName, int mode) {
        SharedPreferences sp = getSpWithMode(spName, mode);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param spName sp名称
     * @param key    键
     * @return
     */
    public static boolean contains(String spName, String key) {
        SharedPreferences sp = getSp(spName);
        return sp.contains(key);
    }

    public static boolean contains(String spName, int mode, String key) {
        SharedPreferences sp = getSpWithMode(spName, mode);
        return sp.contains(key);
    }

    /**
     * 获取SharedPreferences
     *
     * @param spName sp名称
     * @return
     */
    private static SharedPreferences getSp(String spName) {
        return getSpWithMode(spName, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getSpWithMode(String spName, int mode) {
        return GslApplication.sInstance.getSharedPreferences(spName, mode);
    }

    /**
     * 返回所有的键值对
     *
     * @param spName sp名称
     * @return
     */
    public static Map<String, ?> getAll(String spName) {
        SharedPreferences sp = getSp(spName);
        return sp.getAll();
    }

    public static Map<String, ?> getAll(String spName, int mode) {
        SharedPreferences sp = getSpWithMode(spName, mode);
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
         * @return apply方法
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
         * @param editor sp editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            editor.commit();
        }
    }
}
