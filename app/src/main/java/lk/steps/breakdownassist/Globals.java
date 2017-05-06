package lk.steps.breakdownassist;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Chirantha on 4/9/2017.
 */

public class Globals
{
    public static String AreaCode1="";
    public static String AreaCode2="";
    public static String AreaCode3="";
    public static int NoOfAreaCodes=0;

    public static void initAreaCodes(Context context)
    {
        SharedPreferences sharedPrefs = PreferenceManager
            .getDefaultSharedPreferences(context);
        SharedPreferences sharedPrefsForSMSBroadcastReceiver = context.getSharedPreferences("MYPREF", Context.MODE_PRIVATE);

        NoOfAreaCodes=0;
        if (sharedPrefs.getString("areacode1","").length()==2) {
            AreaCode1 = sharedPrefs.getString("areacode1", "");
            sharedPrefsForSMSBroadcastReceiver.edit().putString("areacode1", AreaCode1).commit();
            NoOfAreaCodes++;
        }
        if (sharedPrefs.getString("areacode2","").length()==2) {
            AreaCode2 = sharedPrefs.getString("areacode2", "");
            sharedPrefsForSMSBroadcastReceiver.edit().putString("areacode2", AreaCode2).commit();
            NoOfAreaCodes++;
        }
        if (sharedPrefs.getString("areacode3","").length()==2){
            AreaCode3=sharedPrefs.getString("areacode3","");
            sharedPrefsForSMSBroadcastReceiver.edit().putString("areacode3", AreaCode3).commit();
            NoOfAreaCodes++;
        }

    }

    public static void LoadAreaCodesForSMSBroadCastReceiver(Context context)
    {
        SharedPreferences sharedPrefsForSMSBroadcastReceiver = context.getSharedPreferences("MYPREF", Context.MODE_PRIVATE);

        NoOfAreaCodes=0;
        if (sharedPrefsForSMSBroadcastReceiver.getString("areacode1","").length()==2) {
            AreaCode1 = sharedPrefsForSMSBroadcastReceiver.getString("areacode1", "");
            NoOfAreaCodes++;
        }
        if (sharedPrefsForSMSBroadcastReceiver.getString("areacode2","").length()==2) {
            AreaCode2 = sharedPrefsForSMSBroadcastReceiver.getString("areacode2", "");
            NoOfAreaCodes++;
        }
        if (sharedPrefsForSMSBroadcastReceiver.getString("areacode3","").length()==2){
            AreaCode3=sharedPrefsForSMSBroadcastReceiver.getString("areacode3","");
            NoOfAreaCodes++;
        }
    }

    public static String getNightMode(){
        String sReturn="1";
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.getAppContext());

        sReturn=sharedPrefs.getString("night_mode", "");
        return sReturn;
    }
}
