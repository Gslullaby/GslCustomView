package g.s.l.storage.sp;

import android.content.Context;

import g.s.l.utils.SpUtil;

/**
 * Created by Deemo on 16/9/7.
 * (～ o ～)~zZ
 */
public class PathSp implements ISp {

    private final String KEY_APP_ROOT_PATH = "storage_settings";

    @Override
    public String spName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int spMode() {
        return Context.MODE_PRIVATE;
    }

    public void setAppRootPath(String path) {
        SpUtil.put(spName(), KEY_APP_ROOT_PATH, path);
    }

    public String getAppRootPath() {
        return (String) SpUtil.get(spName(), KEY_APP_ROOT_PATH, "");
    }
}
