package lk.steps.breakdownassist;

import android.app.Application;
import android.content.Context;

/**
 * Created by Chirantha on 4/9/2017.
 */

public class BreakDownMate extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        BreakDownMate.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return BreakDownMate.context;
    }
}