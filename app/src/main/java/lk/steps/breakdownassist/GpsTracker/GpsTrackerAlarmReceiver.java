package lk.steps.breakdownassist.GpsTracker;

/**
 * Created by JagathPrasanga on 2017-09-23.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

// make sure we use a WakefulBroadcastReceiver so that we acquire a partial wakelock
public class GpsTrackerAlarmReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = "GpsTrackerAlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");
        context.startService(new Intent(context, LocationService.class));
    }
}
