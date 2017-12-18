package lk.steps.breakdownassistpluss.ServiceManager;

/**
 * Created by JagathPrasanga on 2017-09-23.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.Sync.SignalRService;
import lk.steps.breakdownassistpluss.Sync.SyncService;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            //Intent signalRService = new Intent(context, SignalRService.class);
            //context.startService(signalRService);

            Intent signalRIntent = new Intent(context, SignalRService.class);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) signalRIntent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            context.startActivity(signalRIntent);

            Intent syncService = new Intent(context, SyncService.class);
            context.startService(syncService);

            //Repeating service to collect GPS location
            Intent gpsTrackerIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    Globals.GpsIntervalInMinutes, // 60000 = 1 minute,
                    pendingIntent);
        }
    }
}
