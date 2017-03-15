package g.s.l.storage.sp;

/**
 * Created by Deemo on 16/9/7.
 * (～ o ～)~zZ
 */
public class SpFactory {

    private static SpFactory sInstance;

    private UserSp mUserSp;
    private PathSp mPathSp;

    private SpFactory() {
        mUserSp = new UserSp();
        mPathSp = new PathSp();
    }

    public static SpFactory instance() {
        if (sInstance == null) {
            synchronized (SpFactory.class) {
                if (sInstance == null) {
                    sInstance = new SpFactory();
                }
            }
        }
        return sInstance;
    }

    public UserSp getUserSp() {
        return mUserSp;
    }

    public PathSp getPathSp() {
        return mPathSp;
    }
}
