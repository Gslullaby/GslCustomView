package g.s.l.utils;

import java.lang.reflect.Method;

/**
 * Created by Deemo on 16/9/10.
 * (～ o ～)~zZ
 */
public class ReflectUtil {

    public static <T> Method getMethod(Class<T> clazz, String methodName, Class<?>... params) {
        Method method = null;
        try {
            method = clazz.getMethod(methodName, params);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return method;
    }
}
