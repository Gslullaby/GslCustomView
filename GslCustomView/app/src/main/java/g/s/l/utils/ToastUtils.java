package g.s.l.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * ToastUtils
 * Created by Felix on 2015/4/21.
 */
public class ToastUtils
{

    private ToastUtils()
    {
		/* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isShow = true;
    private static Toast sLastToast;

    private static void showInner(Context context, CharSequence message, int duration) {
        if (isShow) {
            if(sLastToast != null){
                sLastToast.cancel();
            }
            Toast toast = Toast.makeText(context, message, duration);
            toast.show();
            sLastToast = toast;
        }
    }
    private static void showInner(Context context, int resId, int duration) {
        if (isShow) {
            if(sLastToast != null){
                sLastToast.cancel();
            }
            Toast toast = Toast.makeText(context, resId, duration);
            toast.show();
            sLastToast = toast;
        }
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message)
    {
        showInner(context, message, Toast.LENGTH_SHORT);
    }



    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, int message)
    {
        showInner(context, message, Toast.LENGTH_SHORT);
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message)
    {
        showInner(context, message, Toast.LENGTH_LONG);
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, int message)
    {
        showInner(context, message, Toast.LENGTH_LONG);
    }

}
