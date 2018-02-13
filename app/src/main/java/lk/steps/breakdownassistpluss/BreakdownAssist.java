package lk.steps.breakdownassistpluss;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by Chirantha on 4/9/2017. 
 * BreakdownAssist
 */

public class BreakdownAssist extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        BreakdownAssist.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return BreakdownAssist.context;
    }


    // This is added on 2018/02/01 to fix "java.lang.ClassNotFoundException BaseDexClassLoader" error
    // By jps
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this); // this is the key code
    }
}