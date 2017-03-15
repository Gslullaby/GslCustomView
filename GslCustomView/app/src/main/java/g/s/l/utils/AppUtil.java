package g.s.l.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import g.s.l.app.AbsApplication;
import g.s.l.app.GslApplication;

import java.io.File;
import java.util.List;

/**
 * Created by Deemo on 16/9/6.
 * (～ o ～)~zZ
 */
public class AppUtil {

    public static final int STATE_UNKNOW = 0;
    public static final int STATE_FOREGROUND = 1;
    public static final int STATE_BACKGROUND = 2;

    private static int getAppRunningStateBeforeLollipop() {
        ActivityManager am = (ActivityManager) GslApplication.sInstance.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(20);
        if (CollectionUtil.isNullOrEmptyList(tasks)) {
            return STATE_UNKNOW;
        }
        ActivityManager.RunningTaskInfo taskInfo = tasks.get(0);
        if (taskInfo.topActivity.getPackageName().equals(packageName())) {
            return STATE_FOREGROUND;
        } else {
            for (ActivityManager.RunningTaskInfo info : tasks) {
                if (info.topActivity.getPackageName().equals(packageName())) {
                    return STATE_BACKGROUND;
                }
            }
        }
        return STATE_UNKNOW;
    }

    private static int getAppRunningStateLollipop() {
        return GslApplication.sInstance.isRunningForeground() ? STATE_FOREGROUND : STATE_BACKGROUND;
    }

    public static int getAppRunningState() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? getAppRunningStateLollipop() :
                getAppRunningStateBeforeLollipop();
    }

    public static void installApk(Context context, String path) {
        if (Strings.isNullOrEmpty(path)) {
            return;
        }
        installApk(context, new File(path));
    }

    public static void installApk(Context context, File file) {
        if (file == null || !file.exists())
            return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static String packageName() {
        return GslApplication.sInstance.getPackageName();
    }
}
