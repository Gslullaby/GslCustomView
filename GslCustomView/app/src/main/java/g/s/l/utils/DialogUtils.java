package g.s.l.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Dialog
 * Created by opticalix@gmail.com on 16/9/28.
 */
public class DialogUtils {
    public static AlertDialog createDialog(Context context, String title, String tip, CharSequence pMsg, CharSequence nMsg,
                                           DialogInterface.OnClickListener positive, DialogInterface.OnClickListener negative) {
        return new AlertDialog.Builder(context).setTitle(title).setMessage(tip).setNegativeButton(nMsg, negative)
                .setPositiveButton(pMsg, positive).create();
    }

}
