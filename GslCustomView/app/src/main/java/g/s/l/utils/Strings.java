package g.s.l.utils;

import android.support.annotation.Nullable;

/**
 * Created by Deemo on 16/9/6.
 * copy from guava
 * (～ o ～)~zZ
 */
public class Strings {

    public static String nullToEmpty(@Nullable String string) {
        return (string == null) ? "" : string;
    }

    @Nullable
    public static String emptyToNull(@Nullable String string) {
        return isNullOrEmpty(string) ? null : string;
    }


    public static boolean isNullOrEmpty(@Nullable String string) {
        return string == null || string.length() == 0; // string.isEmpty() in Java 6
    }

    public static String firstCharToUpperCase(String source) {
        if (isNullOrEmpty(source))
            return null;
        char[] chars = source.toCharArray();
        if (chars[0] < 'a' || chars[0] > 'z') {
            return source;
        }
        chars[0] -= 32;
        return String.valueOf(chars);
    }

}
