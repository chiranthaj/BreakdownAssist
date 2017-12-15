package lk.steps.breakdownassistpluss;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by JagathPrasanga on 12/14/2017.
 */

public  class Common {
    public static boolean ReadBooleanPreferences(Context context, String key, boolean defaultValue){
        SharedPreferences prfs = context.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        //SharedPreferences prfs = getPreferences(Context.MODE_PRIVATE);
        return prfs.getBoolean(key, defaultValue);
    }

    public static void WriteBooleanPreferences(Context context, String key, boolean value){
        SharedPreferences prfs = context.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prfs.edit();
        editor.putBoolean(key,value).apply();
    }
}
