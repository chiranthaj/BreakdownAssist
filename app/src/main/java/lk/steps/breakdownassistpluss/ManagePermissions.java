package lk.steps.breakdownassistpluss;

import android.app.Activity;

import android.content.Context;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by Chirantha on 12/11/2016.
 */

public class ManagePermissions extends AppCompatActivity {
    /**
     * Id to identify a RECEIVE_SMS permission request, i.e. requestCode used in onRequestPermissionsResult
     */
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    public static void CheckAndRequestAllRuntimePermissions(Context context, final Activity currentActivity)
    {
        if ((ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(context,
                        android.Manifest.permission.RECEIVE_SMS)
                        != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(context,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(currentActivity,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                    /*TODO Address the Never Ask again issue*/
            //shouldShowRequestPermissionRationale(permission)
        }
        else
        {
           // Toast.makeText(context, "Permission Granted for all WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION and RECEIVE_SMS" , Toast.LENGTH_SHORT).show();
        }

    }

    @Override  /*TODO Test for Permissions Models before running a task*/
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        Context context = getApplicationContext();
        switch (requestCode)
        {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                String message="";
                for (int i=0;i<grantResults.length;i++)
                {
                    if (grantResults.length > 0
                            && grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    {
                        message+= permissions[i] + " - Permission Granted\n";
                    }
                    else
                    {
                        message+= permissions[i] + " - Permission not Granted\n";
                    }
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
