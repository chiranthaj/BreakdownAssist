package lk.steps.breakdownassist;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import lk.steps.breakdownassist.HtmlTextView.HtmlTextView;
import lk.steps.breakdownassist.Sync.SignalRService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class AboutActivity extends Activity {
    private SignalRService mService;
    private boolean mBound = false;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        Intent intent = new Intent();
        intent.setClass(this, SignalRService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
/*
        PackageInfo info;
        String vName ="";
        int vCode =0;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
            vName = info.versionName;
            vCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/

        String version = "0";
        int versionCode = 0;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView textview = (TextView) findViewById(R.id.vName);
        textview.setText("Version : "+ version);
      //  textview.setText("Version : "+ Global.VersionName.substring(0, Math.min(Global.VersionName.length(), 5)));

        textview = (TextView) findViewById(R.id.vCode);
        textview.setText("Build number : " + Integer.toString(versionCode));

        HtmlTextView text = (HtmlTextView) findViewById(R.id.html_text);
        text.setHtmlFromString(readEula(this), true);
       // Typeface font = Typeface.createFromAsset(this.getApplicationContext().getAssets(), "fonts/Roboto-Light.ttf");
      //  text.setTypeface(font);

    }

    private static String readEula(Activity activity) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(activity.getAssets().open("eula.txt")));
            String line;
            String eula="";
            while ((line = in.readLine()) != null) eula=eula+line;
            Log.d("eula ", eula);
            return eula;
        } catch (IOException e) {
            return "";
        }
    }
    @Override
    protected void onStop() {
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }

    public void test(View view){
        Log.e("SIGNALR","001");
        sendMessage("gamage","prasanga");
    }
       public void sendMessage(String receiver,String message) {
        if (mBound) {
            // Call a method from the SignalRService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            Log.e("SIGNALR","002");
            mService.sendMessage(message);
        }
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
