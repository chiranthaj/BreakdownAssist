package lk.steps.breakdownassistpluss;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import lk.steps.breakdownassistpluss.Sync.SyncRESTService;
import lk.steps.breakdownassistpluss.Sync.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by JagathPrasanga on 12/14/2017.
 */

public  class Common {
    public static boolean ReadBooleanPreferences(Context context, String key, boolean defaultValue){
      //  SharedPreferences prfs = context.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(context);
        return prfs.getBoolean(key, defaultValue);
    }

    public static void WriteBooleanPreferences(Context context, String key, boolean value){
       // SharedPreferences prfs = context.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prfs.edit();
        editor.putBoolean(key,value).apply();
    }

    public static long ReadLongPreferences(Context context, String key, long defaultValue){
        //SharedPreferences prfs = context.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(context);
        return prfs.getLong(key, defaultValue);
    }

    public static void WriteLongPreferences(Context context, String key, long value){
        //SharedPreferences prfs = context.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prfs.edit();
        editor.putLong(key,value).apply();
    }

    public static String ReadStringPreferences(Context context, String key, String defaultValue){
        //SharedPreferences prfs = context.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(context);
        return prfs.getString(key, defaultValue);
    }

    public static void WriteStringPreferences(Context context, String key, String value){
        //SharedPreferences prfs = context.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prfs.edit();
        editor.putString(key,value).apply();
    }



    public static void RemoteLoginWithLastCredentials(final Context context){
        String lastUsername = Common.ReadStringPreferences(context,"last_username", "");
        String lastPassword = Common.ReadStringPreferences(context,"last_password", "");
        final SyncRESTService syncAuthService = new SyncRESTService(2);
        Call<Token> call = syncAuthService.getService().GetJwt(lastUsername,lastPassword);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    Log.e("GetAuthToken","Authorized");
                    Token token = response.body();
                    Globals.mToken = token;
                    //SaveToken(token);
                    Common.WriteStringPreferences(context,"user_id",token.user_id);
                    Common.WriteStringPreferences(context,"area_id",token.area_id);
                    Common.WriteStringPreferences(context,"area_name",token.area_name);
                    Common.WriteStringPreferences(context,"team_id",token.team_id);
                    Common.WriteLongPreferences(context,"expires_in",token.expires_in);
                    Common.WriteStringPreferences(context,"access_token",token.access_token);
                    Common.WriteStringPreferences(context,"group_token",token.group_token);
                    Common.WriteBooleanPreferences(context,"restart_due_to_authentication_fail",false);
                    Toast.makeText(context,"Remote login successful.. \n"+Globals.serverUrl, Toast.LENGTH_LONG).show();
                } else if (response.errorBody() != null) {
                    Log.d("GetAuthToken","Fail"+response.errorBody());
                    Toast.makeText(context,"Network failure..\nSwitch to local login"+response.errorBody(), Toast.LENGTH_LONG).show();

                }
                syncAuthService.CloseAllConnections();
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.e("Login","Remote login onFailure"+t.getMessage());//Remote login
                syncAuthService.CloseAllConnections();
            }

        });
    }
}
