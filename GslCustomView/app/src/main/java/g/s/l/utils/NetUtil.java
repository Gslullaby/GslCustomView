package g.s.l.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import g.s.l.app.AbsApplication;
import g.s.l.app.GslApplication;

/**
 * Created by Deemo on 16/9/7.
 * (～ o ～)~zZ
 */
public class NetUtil {

    public static boolean isWifi() {
        return netType().equals(NetState.WIFI);
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = getConnectivityManager();
        if (cm == null)
            return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = cm.getAllNetworks();
            if (networks == null || networks.length == 0) {
                return false;
            }
            for (Network network : networks) {
                NetworkInfo info = cm.getNetworkInfo(network);
                if (info != null && info.isConnectedOrConnecting()) {
                    return true;
                }
            }
        } else {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnectedOrConnecting())
                return true;
        }
        return false;
    }

    public static NetState netType() {
        NetState state = NetState.NO_NET;
        ConnectivityManager cm = getConnectivityManager();
        if (cm == null)
            return NetState.NO_NET;
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (!(info == null) && info.isConnectedOrConnecting()) {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    state = NetState.WIFI;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    state = mobileNetType(info.getSubtype());
                    break;
                case ConnectivityManager.TYPE_ETHERNET://有线网络，以太网 Kitkat开始支持
                    state = NetState.ETHERNET;
                    break;
                default:
                    state = NetState.UNKNOWN;
            }
        }
        return state;
    }

    /**
     * TelephoneManager中标注为hide
     * NETWORK_TYPE_GSM：全球移动通信系统 2017年关闭
     * NETWORK_TYPE_TD_SCDMA：中国提出的第三代移动通信标准，即为3G
     */
    public static final int NETWORK_TYPE_GSM = 16;
    public static final int NETWORK_TYPE_TD_SCDMA = 17;
    public static final int NETWORK_TYPE_IWLAN = 18;

    private static NetState mobileNetType(int subType) {
        switch (subType) {
            case TelephonyManager.NETWORK_TYPE_GPRS://联通2G ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA://电信2G ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE://移动2G ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_1xRTT://2.5G或2.75G ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_IDEN://~25 kbps
                return NetState.G2;
            case TelephonyManager.NETWORK_TYPE_EVDO_A://~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_UMTS://~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0://~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA://~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSUPA://1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA://~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B://~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_EHRPD://~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP://~ 10-20 Mbps
            case NETWORK_TYPE_TD_SCDMA:
                return NetState.G3;
            case TelephonyManager.NETWORK_TYPE_LTE://~ 10+ Mbps
            case NETWORK_TYPE_IWLAN:
                return NetState.G4;
            default:
                return NetState.UNKNOWN;
        }
    }

    private static ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static Context getContext() {
        return GslApplication.sInstance;
    }

    public enum NetState {
        NO_NET, G2, G3, G4, WIFI, ETHERNET, UNKNOWN;
    }
}
