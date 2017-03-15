package g.s.l.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import g.s.l.app.AbsApplication;
import g.s.l.app.GslApplication;

/**
 * Created by Deemo on 16/12/29".
 * *_*
 */
public class DeviceUtil {

    /**
     * 获取设备系统版本号
     *
     * @return 设备系统版本号
     */
    public static int getSDKVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获取设备AndroidID
     *
     * @return AndroidID
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidID() {
        return Settings.Secure.getString(GslApplication.sInstance.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    /**
     * 获取设备id
     *
     * @return deviceId
     */
    public static String getDeviceId(){
        TelephonyManager tm = (TelephonyManager) GslApplication.sInstance.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

}
