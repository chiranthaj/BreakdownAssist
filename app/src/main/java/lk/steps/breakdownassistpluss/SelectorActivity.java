package lk.steps.breakdownassistpluss;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
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
 * Created by JagathPrasanga on 10/11/2017.
 */

public class SelectorActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(this, "I'm alive", Toast.LENGTH_LONG).show();
        if(!Common.ReadBooleanPreferences(this,"server",false))Common.GetIpAddress();
        Log.e("SERVER",Globals.serverUrl);
        if(Common.ReadBooleanPreferences(this,"keep_sign_in",false)){
            performAutoLogin(this);
        }else{
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    Intent myIntent = new Intent(SelectorActivity.this, LoginActivity.class);
                    SelectorActivity.this.startActivity(myIntent);
                }
            });
        }
        finish();
    }


    private void performAutoLogin(final Context context){
        Log.e("GetAuthToken","Authorized1");
        boolean deviceEligible = Common.ReadBooleanPreferences(context,"device_eligible", false);
        if(!deviceEligible){
            Globals.ServerConnected = false;
            Intent myIntent = new Intent(SelectorActivity.this, LoginActivity.class);
            SelectorActivity.this.startActivity(myIntent);
        }


        String lastUsername = Common.ReadStringPreferences(context,"last_username", "");
        String lastPassword = Common.ReadStringPreferences(context,"last_password", "");
        String area = Common.ReadStringPreferences(context, "area_id", "");
        String team = Common.ReadStringPreferences(context, "team_id", "");
        final SyncRESTService syncAuthService = new SyncRESTService(2);
        Call<Token> call = syncAuthService.getService().GetJwt(lastUsername,lastPassword,
                Common.GetDeviceId(context), Globals.APPID,Common.GetVersionCode(context), area, team);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    Log.e("GetAuthToken","Authorized5");
                    Token token = response.body();
                    //SaveToken(token);
                    Common.WriteStringPreferences(context,"user_id",token.user_id);
                    Common.WriteStringPreferences(context,"area_id",token.area_id);
                    Common.WriteStringPreferences(context,"area_name",token.area_name);
                    Common.WriteStringPreferences(context,"team_id",token.team_id);
                    Common.WriteLongPreferences(context,"expires_in",token.expires_in);
                    Common.WriteStringPreferences(context,"access_token",token.access_token);
                    Common.WriteStringPreferences(context,"group_token",token.group_token);
                    Common.WriteBooleanPreferences(context,"device_eligible", true);
                    //Common.WriteBooleanPreferences(context,"restart_due_to_authentication_fail",false);
                    Toast.makeText(getApplicationContext(),"Remote login successful..\n"+Globals.serverUrl, Toast.LENGTH_LONG).show();
                    //WriteBooleanPreferences("keep_sign_in",true);
                    Common.WriteBooleanPreferences(context,"login_status",true);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            Intent myIntent = new Intent(SelectorActivity.this, MainActivity.class);
                            SelectorActivity.this.startActivity(myIntent);
                        }
                    });
                    finish();
                } else if (response.errorBody() != null) {
                    Log.d("GetAuthToken","Fail"+response.errorBody());
                    Toast.makeText(getApplicationContext(),"Network failure..\nSwitch to local login"+response.errorBody(), Toast.LENGTH_LONG).show();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            Intent myIntent = new Intent(SelectorActivity.this, LoginActivity.class);
                            SelectorActivity.this.startActivity(myIntent);
                        }
                    });
                }
                syncAuthService.CloseAllConnections();
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.e("Login","Remote login onFailure"+t.getMessage());//Remote login
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Intent myIntent = new Intent(SelectorActivity.this, LoginActivity.class);
                        SelectorActivity.this.startActivity(myIntent);
                    }
                });
                syncAuthService.CloseAllConnections();
            }
        });
    }

   /* private void WriteLongPreferences(String key, long value){
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prfs.edit();
        editor.putLong(key,value).apply();
    }
    private void WriteStringPreferences(String key, String value){
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prfs.edit();
        editor.putString(key,value).apply();
    }
    private void WriteBooleanPreferences(String key, boolean value){
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prfs.edit();
        editor.putBoolean(key,value).apply();
    }
    private String ReadStringPreferences(String key, String defaultValue){
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        return prfs.getString(key, defaultValue);
    }

    private boolean ReadBooleanPreferences2(String key, boolean defaultValue){
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        //SharedPreferences prfs = getPreferences(Context.MODE_PRIVATE);
        return prfs.getBoolean(key, defaultValue);
    }
    private boolean ReadBooleanPreferences(String key, boolean defaultValue){
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences prfs = getPreferences(Context.MODE_PRIVATE);
        return prfs.getBoolean(key, defaultValue);
    }*/
}
