package g.s.l.utils;

import android.util.Log;

import g.s.l.utils.local_log.LocalLog;


/**
 * Log统一入口
 * 需要init
 * 需要权限
 * Created by opticalix@gmail.com on 16/9/6.
 */
public class LogUtils {
    private static boolean sEnableLog;

    /**
     * @param log
     * @param localLog
     * @param localTag
     * @param fullDirPath 全路径
     */
    public static void initWithFullDir(boolean log, boolean localLog, String localTag, String fullDirPath) {
        sEnableLog = log;
        LocalLog.initWithFullDir(localTag, fullDirPath, localLog);
    }

    /**
     *
     * @param log
     * @param localLog
     * @param localTag
     * @param localDir local文件夹name, like：/local_log
     */
    public static void init(boolean log, boolean localLog, String localTag, String localDir) {
        sEnableLog = log;
        LocalLog.init(localTag, localDir, localLog);
    }

    public static void d(Object tag, String msg) {
        d(tag.getClass().getSimpleName(), msg);
    }

    public static void d(String tag, String msg) {
        if (!sEnableLog) return;
        Log.d(tag, msg);
    }

    public static void i(Object tag, String msg) {
        i(tag.getClass().getSimpleName(), msg);
    }

    public static void i(String tag, String msg) {
        if (!sEnableLog) return;
        Log.i(tag, msg);
    }

    public static void w(Object tag, String msg) {
        w(tag.getClass().getSimpleName(), msg);
    }

    public static void w(String tag, String msg) {
        if (!sEnableLog) return;
        Log.w(tag, msg);
    }

    public static void e(Object tag, String msg) {
        e(tag.getClass().getSimpleName(), msg);
    }

    public static void e(String tag, String msg) {
        if (!sEnableLog) return;
        Log.e(tag, msg);
    }

    public static void e(Object tag, String msg, Throwable e) {
        e(tag.getClass().getSimpleName(), msg, e);
    }

    public static void e(String tag, String msg, Throwable e) {
        if (!sEnableLog) return;
        Log.e(tag, msg, e);
    }


    /*
    输出到本地指定dir中, 下同
     */

    public static void ld(Object tag, String msg) {
        ld(tag.getClass().getSimpleName(), msg);
    }

    public static void ld(String tag, String msg) {
        if (!sEnableLog) return;
        LocalLog.d(tag, msg);
    }

    public static void li(Object tag, String msg) {
        li(tag.getClass().getSimpleName(), msg);
    }

    public static void li(String tag, String msg) {
        if (!sEnableLog) return;
        LocalLog.i(tag, msg);
    }

    public static void lw(Object tag, String msg) {
        lw(tag.getClass().getSimpleName(), msg);
    }

    public static void lw(String tag, String msg) {
        if (!sEnableLog) return;
        LocalLog.w(tag, msg);
    }

    public static void le(Object tag, String msg) {
        le(tag.getClass().getSimpleName(), msg);
    }

    public static void le(String tag, String msg) {
        if (!sEnableLog) return;
        LocalLog.e(tag, msg);
    }

    public static void le(Object tag, String msg, Throwable e) {
        le(tag.getClass().getSimpleName(), msg, e);
    }

    public static void le(String tag, String msg, Throwable e) {
        if (!sEnableLog) return;
        LocalLog.e(tag, msg, e);
    }

}
