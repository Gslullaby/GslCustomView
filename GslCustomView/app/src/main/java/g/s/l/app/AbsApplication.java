package g.s.l.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Deemo on 2017/3/15.
 */

public class AbsApplication extends Application implements Application.ActivityLifecycleCallbacks{
    private List<Activity> mActStack = new LinkedList<>();
    private boolean isRunningForeground;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public boolean isRunningForeground() {
        return isRunningForeground;
    }

    /*
     * ActivityLifecycleCallbacks
     */
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mActStack.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        isRunningForeground = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        isRunningForeground = false;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mActStack.remove(activity);
    }
}
