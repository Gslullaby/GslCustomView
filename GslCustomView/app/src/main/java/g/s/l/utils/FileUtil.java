package g.s.l.utils;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;

/**
 * Created by Deemo on 16/9/6.
 * (～ o ～)~zZ
 */
public class FileUtil {
    public final static String TAG = "FileUtil";

    public static boolean isWritable(String path) {
        if (Strings.isNullOrEmpty(path))
            return false;
        return isWritable(new File(path));
    }

    public static boolean createDirectory(@NonNull String path) {
        if (!path.endsWith(File.separator))
            path += File.separator;
        File file = new File(path);
        if (file.exists() && file.isDirectory())
            return true;
        return file.mkdirs();
    }

    public static boolean isWritable(File file) {
        if (file == null)
            return false;
        if (!file.exists())
            return file.mkdirs() && writeTempFile(file.getPath());
        if (file.isFile()) {
            return writeTempFile(file.getParent());
        } else
            return writeTempFile(file.getPath());
    }

    private static boolean writeTempFile(@NonNull String path) {
        File file = new File(path, "test.tmp");
        if (file.exists()) {
            if (!file.delete())
                return false;
        }
        try {
            if (!file.createNewFile())
                return false;
            file.delete();
        } catch (IOException e) {
            LogUtils.e(TAG, "file path can non-writable\n" + e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean isNullOrNotExist(File f){
        return f == null || !f.exists();
    }
}
