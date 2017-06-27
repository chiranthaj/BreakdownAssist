package lk.steps.breakdownassist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Created by JagathPrasanga on 2017-06-27.
 */

public class WhatsNewScreen {
    private static final String LOG_TAG                 = "WhatsNewScreen";

    private static final String LAST_VERSION_CODE_KEY   = "last_version_code";

    private Activity mActivity;

    // Constructor memorize the calling Activity ("context")
    public WhatsNewScreen(Activity context) {
        mActivity = context;
    }

    // Show the dialog only if not already shown for this version of the application
    public void show() {
        try {
            // Get the versionCode of the Package, which must be different (incremented) in each release on the market in the AndroidManifest.xml
            final PackageInfo packageInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_ACTIVITIES);

            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
            final long lastVersionCode = prefs.getLong(LAST_VERSION_CODE_KEY, 0);

            if (packageInfo.versionCode != lastVersionCode) {
                Log.i(LOG_TAG, "versionCode " + packageInfo.versionCode + "is different from the last known version " + lastVersionCode);

                final String title = "What’s New in \n"+ mActivity.getString(R.string.app_name) + " v" + packageInfo.versionName;
                //TextView dialogTitle = new TextView(mActivity);
                //dialogTitle.setText(title);
                //dialogTitle.setPadding(30,20,20,20);
                //dialogTitle.setTextColor(Color.BLACK);
                //dialogTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);

                final String message = mActivity.getString(R.string.whats_new);

                // Show the News since last version
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                        //.setCustomTitle(dialogTitle)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Mark this version as read
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putLong(LAST_VERSION_CODE_KEY, packageInfo.versionCode);
                                editor.commit();
                                dialogInterface.dismiss();
                            }
                        });

                AlertDialog theDialog=builder.create();
                theDialog.setIcon(R.mipmap.ic_launcher);
                //TextView textView = (TextView) theDialog.findViewById(android.R.id.message);
                //textView.setTextSize(40);

                theDialog.show();

            } else {
                Log.i(LOG_TAG, "versionCode " + packageInfo.versionCode + "is already known");
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}
