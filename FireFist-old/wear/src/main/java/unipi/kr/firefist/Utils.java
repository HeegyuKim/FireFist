package unipi.kr.firefist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by KimHeekue on 2014-09-20.
 */
public class Utils {

    public static void alert(Context ctx, String message,
                             AlertDialog.OnClickListener lis)
    {
        new AlertDialog.Builder(ctx)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, lis)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void alert(Context ctx, String message, String title,
        AlertDialog.OnClickListener lis)
    {
        new AlertDialog.Builder(ctx)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, lis)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
