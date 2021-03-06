package lk.steps.breakdownassist;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Chirantha on 4/9/2017.
 */

public class Globals
{
    public static String AreaCode1="";
    public static String AreaCode2="";
    public static String AreaCode3="";
    public static int NoOfAreaCodes=0;

    public static SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" , Locale.US);//This format is used to store in SqLite db
    private static SimpleDateFormat timeFormat2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a" , Locale.US); // This format is used to easily identify to user
    public static long AverageTime = 0;

    public static void initAreaCodes(Context context)
    {
        SharedPreferences sharedPrefs = PreferenceManager
            .getDefaultSharedPreferences(context);
        SharedPreferences sharedPrefsForSMSBroadcastReceiver = context.getSharedPreferences("MYPREF", Context.MODE_PRIVATE);

        NoOfAreaCodes=0;
        if (sharedPrefs.getString("areacode1","41").length()==2) {
            AreaCode1 = sharedPrefs.getString("areacode1", "41");
            sharedPrefsForSMSBroadcastReceiver.edit().putString("areacode1", AreaCode1).apply();
            NoOfAreaCodes++;
        }
        if (sharedPrefs.getString("areacode2","42").length()==2) {
            AreaCode2 = sharedPrefs.getString("areacode2", "42");
            sharedPrefsForSMSBroadcastReceiver.edit().putString("areacode2", AreaCode2).apply();
            NoOfAreaCodes++;
        }
        if (sharedPrefs.getString("areacode3","71").length()==2){
            AreaCode3=sharedPrefs.getString("areacode3","71");
            sharedPrefsForSMSBroadcastReceiver.edit().putString("areacode3", AreaCode3).apply();
            NoOfAreaCodes++;
        }
        Log.d("AreaCodes1",AreaCode1+","+AreaCode2+","+AreaCode3);
    }

    public static void LoadAreaCodesForSMSBroadCastReceiver(Context context)
    {
        SharedPreferences sharedPrefsForSMSBroadcastReceiver = context.getSharedPreferences("MYPREF", Context.MODE_PRIVATE);

        NoOfAreaCodes=0;
        if (sharedPrefsForSMSBroadcastReceiver.getString("areacode1","41").length()==2) {
            AreaCode1 = sharedPrefsForSMSBroadcastReceiver.getString("areacode1", "41");
            NoOfAreaCodes++;
        }
        if (sharedPrefsForSMSBroadcastReceiver.getString("areacode2","42").length()==2) {
            AreaCode2 = sharedPrefsForSMSBroadcastReceiver.getString("areacode2", "42");
            NoOfAreaCodes++;
        }
        if (sharedPrefsForSMSBroadcastReceiver.getString("areacode3","71").length()==2){
            AreaCode3=sharedPrefsForSMSBroadcastReceiver.getString("areacode3","71");
            NoOfAreaCodes++;
        }
        Log.d("AreaCodes2",AreaCode1+","+AreaCode2+","+AreaCode3);
    }

    public static String getNightMode()
    {
        String sReturn="1";
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.getAppContext());

        sReturn=sharedPrefs.getString("night_mode", "1");
        return sReturn;
    }

    public static String parseDate(String time) {
        String str = "-";
        try {
            Date date = Globals.timeFormat.parse(time);
            str = Globals.timeFormat2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
}
