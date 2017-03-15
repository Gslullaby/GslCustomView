package g.s.l.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import g.s.l.app.AbsApplication;
import g.s.l.app.GslApplication;

/**
 * Created by Deemo on 16/9/6.
 * (～ o ～)~zZ
 */
public class DisplayUtil {

    public static float dp2px(int dp) {
        return getDisplayMetrics().density * dp;
    }

    public static float px2dp(int px) {
        return px / getDisplayMetrics().density;
    }

    public static float[] getScreenSize() {
        float[] size = new float[2];
        DisplayMetrics metrics = getDisplayMetrics();
        size[0] = metrics.widthPixels;
        size[1] = metrics.heightPixels;
        return size;
    }

    public static DisplayMetrics getDisplayMetrics() {
        return getResources().getDisplayMetrics();
    }

    public static Resources getResources() {
        return GslApplication.sInstance.getResources();
    }
}
