package lk.steps.breakdownassist;

import android.app.Application;
import android.content.Context;

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
}