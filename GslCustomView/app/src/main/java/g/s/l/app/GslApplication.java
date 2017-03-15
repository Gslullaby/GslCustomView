package g.s.l.app;

import android.os.Environment;

import g.s.l.utils.LogUtils;

/**
 * Created by Deemo on 2017/3/15.
 */

public class GslApplication extends AbsApplication {
    public static final boolean LOG = true;
    public static final boolean LOCAL_LOG = true;

    public static GslApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        LogUtils.initWithFullDir(LOG, LOCAL_LOG, null, Environment.getExternalStorageDirectory().getPath());
    }
}
