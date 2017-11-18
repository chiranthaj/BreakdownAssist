package lk.steps.breakdownassistpluss;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import lk.steps.breakdownassistpluss.Sync.Token;

/**
 * Created by JagathPrasanga on 11/18/2017.
 */

public class StartUpTasks {

    public static void InitVariables(Context context){
        SetVersion(context);
        Globals.mToken = ReadToken(context);
        Globals.dbHandler = new DBHandler(context, null, null, 1);
        Globals.initAreaCodes(context);
    }

    private static void SetVersion(Context context){
        try{
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            Globals.VERSION_CODE = info.versionCode;
            Log.e("VERSION_CODE","="+info.versionCode);
        }catch(Exception e){
            Log.e("VERSION_CODE","="+e.getMessage());
        }
    }

    private static Token ReadToken(Context context){
        SharedPreferences prfs = context.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        Token token = new Token(){};
        token.user_id=prfs.getString("user_id", "");
        token.area_id=prfs.getString("area_id", "");
        token.team_id=prfs.getString("team_id", "");
        token.area_name=prfs.getString("area_name", "");
        token.access_token=prfs.getString("access_token", "");
        token.expires_in= prfs.getLong("expires_in", 0);
        return token;
    }


}
