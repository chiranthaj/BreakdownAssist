package lk.steps.breakdownassistpluss.GpsTracker;

/**
 * Created by JagathPrasanga on 2017-09-23.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.Sync.SignalRService;
import lk.steps.breakdownassistpluss.Sync.SyncService;

public class GpsTrackerBootReceiver extends BroadcastReceiver {
    private static final String TAG = "GpsTrackerBootReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);

       // SharedPreferences sharedPreferences = context.getSharedPreferences("GPSTRACKER", Context.MODE_PRIVATE);
      //  int intervalInMinutes = sharedPreferences.getInt("intervalInMinutes", 1);
        /*Boolean currentlyTracking = sharedPreferences.getBoolean("currentlyTracking", false);

        if (currentlyTracking) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    intervalInMinutes * 10000, // 60000 = 1 minute,
                    pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
        }*/
       /* alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                60000, // 60000 = 1 minute,
                pendingIntent);*/

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            Intent signalRService = new Intent(context, SignalRService.class);
            context.startService(signalRService);

            Intent syncService = new Intent(context, SyncService.class);
            context.startService(syncService);

            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    Globals.GpsIntervalInMinutes, // 60000 = 1 minute,
                    pendingIntent);
        }
    }
}
