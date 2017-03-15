package g.s.l.storage.sp;

import android.content.Context;

import g.s.l.utils.SpUtil;


/**
 * Created by Deemo on 16/9/6.
 * (～ o ～)~zZ
 */
public class UserSp implements ISp {
    private static final String KEY_LOGIN_NAME = "KEY_LOGIN_NAME";

    public String getLastLoginName() {
        return (String) SpUtil.get(spName(), KEY_LOGIN_NAME, "");
    }

    @Override
    public String spName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int spMode() {
        return Context.MODE_PRIVATE;
    }

    public void putLastLoginName(String loginName) {
        SpUtil.put(spName(), KEY_LOGIN_NAME, loginName);
    }
}
