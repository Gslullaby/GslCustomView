package g.s.l.utils;

import android.content.Context;
import android.media.AudioManager;

public class VolumeManager {
    private static VolumeManager mInstance;
    private AudioManager mAudioManager;
    private int mMaxVolume;

    private VolumeManager(Context context) {
        mAudioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = getMaxVolume();
    }

    public static VolumeManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolumeManager(context.getApplicationContext());
        }
        return mInstance;
    }

    public int getCurrentVolume() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public int getMaxVolume() {
        return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public void setVolume(int value) {
        if (value > mMaxVolume) {
            value = mMaxVolume;
        }

        if (value < 0) {
            value = 0;
        }
        if (getCurrentVolume() != value) {
            int direction = value > getCurrentVolume() ? AudioManager.ADJUST_RAISE
                    : AudioManager.ADJUST_LOWER;
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    direction, AudioManager.FX_FOCUS_NAVIGATION_DOWN);
        }
    }

}
