package lk.steps.breakdownassistpluss.ServiceManager;

/**
 * Created by JagathPrasanga on 2017-09-23.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import lk.steps.breakdownassistpluss.GpsTracker.LocationService;
import lk.steps.breakdownassistpluss.Sync.SyncService;

// make sure we use a WakefulBroadcastReceiver so that we acquire a partial wakelock
public class AlarmReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = "GpsTracker";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "AlarmReceiver onReceive");
        context.startService(new Intent(context, LocationService.class));
        context.startService(new Intent(context, SyncService.class));
    }
}
