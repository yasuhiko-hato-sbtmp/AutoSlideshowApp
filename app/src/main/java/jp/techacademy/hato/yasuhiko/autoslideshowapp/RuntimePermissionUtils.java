package jp.techacademy.hato.yasuhiko.autoslideshowapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

/**
 * Created by hatoy37 on 2/4/17.
 */

public class RuntimePermissionUtils {
    private Context mContext;
    private Resources mResources;

    public void showSettingsDialog(Context context){
        mContext = context;
        mResources = mContext.getResources();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle(mResources.getString(R.string.permission_dialog_titile));
        alertDialogBuilder.setMessage(mResources.getString(R.string.permission_dialog_message) + mResources.getString(R.string.app_name));

        alertDialogBuilder.setPositiveButton(mResources.getString(R.string.permission_dialog_appinfo),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Dialog", "App info");
                        Intent intent = new Intent(
                                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + mContext.getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                });


        alertDialogBuilder.setNegativeButton(mResources.getString(R.string.permission_dialog_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Dialog", "Cancel");
                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
