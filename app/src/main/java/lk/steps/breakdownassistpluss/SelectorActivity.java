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
        if(!ReadBooleanPreferences("server",false))GetIpAddress();
        Log.e("SERVER",Globals.serverUrl);
        if(ReadBooleanPreferences2("keep_sign_in",false)){
            performAutoLogin();
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
    private void GetIpAddress(){
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
    private void performAutoLogin(){
        String lastUsername = ReadStringPreferences("last_username", "");
        String lastPassword = ReadStringPreferences("last_password", "");
        SyncRESTService syncAuthService = new SyncRESTService(2);
        Call<Token> call = syncAuthService.getService().GetJwt(lastUsername,lastPassword);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    Log.e("GetAuthToken","Authorized");
                    Token token = response.body();
                    //SaveToken(token);
                    WriteStringPreferences("user_id",token.user_id);
                    WriteStringPreferences("area_id",token.area_id);
                    WriteStringPreferences("area_name",token.area_name);
                    WriteStringPreferences("team_id",token.team_id);
                    WriteLongPreferences("expires_in",token.expires_in);
                    WriteStringPreferences("access_token",token.access_token);
                    WriteStringPreferences("group_token",token.group_token);

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            Intent myIntent = new Intent(SelectorActivity.this, MainActivity.class);
                            SelectorActivity.this.startActivity(myIntent);
                        }
                    });
                    Globals.serverConnected = true;
                    WriteLongPreferences("restart_due_to_authentication_fail",false);
                    Toast.makeText(getApplicationContext(),"Remote login successful.. \n"+Globals.serverUrl, Toast.LENGTH_LONG).show();
                    WriteLongPreferences("keep_sign_in",true);
                    finish();
                } else if (response.errorBody() != null) {
                    Log.d("GetAuthToken","Fail"+response.errorBody());
                    Toast.makeText(getApplicationContext(),"Network failure..\nSwitch to local login"+response.errorBody(), Toast.LENGTH_LONG).show();
                    Globals.serverConnected = false;
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            Intent myIntent = new Intent(SelectorActivity.this, LoginActivity.class);
                            SelectorActivity.this.startActivity(myIntent);
                        }
                    });
                }
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
            }

        });
    }

    private void WriteLongPreferences(String key, long value){
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prfs.edit();
        editor.putLong(key,value).apply();
    }
    private void WriteStringPreferences(String key, String value){
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prfs.edit();
        editor.putString(key,value).apply();
    }
    private void WriteLongPreferences(String key, boolean value){
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
    }
}
