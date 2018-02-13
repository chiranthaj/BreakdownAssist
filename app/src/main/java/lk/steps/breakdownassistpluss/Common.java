package lk.steps.breakdownassistpluss;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.URL;

import lk.steps.breakdownassistpluss.Sync.SyncRESTService;
import lk.steps.breakdownassistpluss.Sync.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by JagathPrasanga on 12/14/2017.
 */

public class Common {
    public static boolean ReadBooleanPreferences(Context context, String key, boolean defaultValue) {
        //  SharedPreferences prfs = context.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(context);
        return prfs.getBoolean(key, defaultValue);
    }

    public static void WriteBooleanPreferences(Context context, String key, boolean value) {
        // SharedPreferences prfs = context.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prfs.edit();
        editor.putBoolean(key, value).apply();
    }

    public static long ReadLongPreferences(Context context, String key, long defaultValue) {
        //SharedPreferences prfs = context.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(context);
        return prfs.getLong(key, defaultValue);
    }

    public static void WriteLongPreferences(Context context, String key, long value) {
        //SharedPreferences prfs = context.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prfs.edit();
        editor.putLong(key, value).apply();
    }

    public static String ReadStringPreferences(Context context, String key, String defaultValue) {
        //SharedPreferences prfs = context.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(context);
        return prfs.getString(key, defaultValue);
    }

    public static void WriteStringPreferences(Context context, String key, String value) {
        //SharedPreferences prfs = context.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prfs.edit();
        editor.putString(key, value).apply();
    }


    public static void RemoteLoginWithLastCredentials(final Context context, int location) {
        Log.e("GetAuthToken", "location="+location);

        String lastUsername = Common.ReadStringPreferences(context, "last_username", "");
        String lastPassword = Common.ReadStringPreferences(context, "last_password", "");
        String area = Common.ReadStringPreferences(context, "area_id", "");
        String team = Common.ReadStringPreferences(context, "team_id", "");
        boolean deviceEligible = Common.ReadBooleanPreferences(context,"device_eligible", false);
        if(!deviceEligible){
            Globals.ServerConnected = false;
            return;
        }

        final SyncRESTService syncAuthService = new SyncRESTService(2);
        Call<Token> call = syncAuthService.getService().GetJwt(lastUsername, lastPassword,
                Common.GetDeviceId(context), Globals.APPID,Common.GetVersionCode(context), area, team);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    Log.e("GetAuthToken", "Authorized3");
                    Token token = response.body();
                    Globals.mToken = token;
                    //SaveToken(token);
                    Common.WriteStringPreferences(context, "user_id", token.user_id);
                    Common.WriteStringPreferences(context, "area_id", token.area_id);
                    Common.WriteStringPreferences(context, "area_name", token.area_name);
                    Common.WriteStringPreferences(context, "team_id", token.team_id);
                    Common.WriteLongPreferences(context, "expires_in", token.expires_in);
                    Common.WriteStringPreferences(context, "access_token", token.access_token);
                    Common.WriteStringPreferences(context, "group_token", token.group_token);
                    Common.WriteBooleanPreferences(context,"device_eligible", true);
                    Common.WriteBooleanPreferences(context,"login_status",true);
                    Globals.ServerConnected = true;
                    //Common.WriteBooleanPreferences(context, "restart_due_to_authentication_fail", false);
                    Toast.makeText(context, "Remote login successful..\n" + Globals.serverUrl, Toast.LENGTH_LONG).show();
                } else if (response.errorBody() != null) {
                    Log.d("GetAuthToken", "Fail" + response.errorBody());
                   // Toast.makeText(context, "Network failure..\nSwitch to local login" + response.errorBody(), Toast.LENGTH_LONG).show();
                    Globals.ServerConnected = false;
                }
                syncAuthService.CloseAllConnections();
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.e("Login", "Remote login onFailure" + t.getMessage());//Remote login
                syncAuthService.CloseAllConnections();
                Globals.ServerConnected = false;
            }

        });
    }

    public static String GetVersionCode(Context context){
        try{

          return String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);

        }catch(Exception e){}

        return "0";
    }

    public static String GetDeviceId2(Context context) {
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(context);
        Log.e("TelephonyInfo","="+telephonyInfo.getImeiSIM1());
        return telephonyInfo.getImeiSIM1();
    }

    public static String GetDeviceId(Context context) {
        String deviceUniqueIdentifier = null;
        try{
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (null != tm) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    deviceUniqueIdentifier=tm.getDeviceId();
                }
            }
        }catch(Exception e){}


        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        Common.WriteStringPreferences(context,"device_id",deviceUniqueIdentifier);
        return deviceUniqueIdentifier;
    }

    public static  void GetIpAddress(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //String url = "http://meterasist.hopto.org";
                    String url = "http://breakdownassist.ddns.net";
                    //String url = "http://cebkandy.ddns.net";
                    InetAddress address = InetAddress.getByName(new URL(url).getHost());
                    String ip = address.getHostAddress();
                    Globals.serverUrl="http://"+ip+"";
                    Log.e("IP","="+ip);
                    Log.e("SERVER",Globals.serverUrl);
                }catch(Exception e){
                    Log.e("IP","="+e.getMessage());
                }
            }
        });
        thread.start();
    }

}
