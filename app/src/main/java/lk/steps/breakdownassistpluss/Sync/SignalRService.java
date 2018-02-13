package lk.steps.breakdownassistpluss.Sync;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.Common;
import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.GpsTracker.TrackerObject;
import lk.steps.breakdownassistpluss.Models.JobChangeStatus;
import lk.steps.breakdownassistpluss.Models.JobCompletion;
import lk.steps.breakdownassistpluss.MainActivity;
import lk.steps.breakdownassistpluss.R;
import lk.steps.breakdownassistpluss.SelectorActivity;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.MessageReceivedHandler;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignalRService extends Service {
    private static HubConnection mHubConnection;
    private static HubProxy mHubProxy;
    private Handler mHandler; // to display Toast message
    private final IBinder mBinder = new LocalBinder(); // Binder given to clients
    Timer timer;
    ReconnectTimer mReconnectTimer;
    private static MediaPlayer mMediaPlayer;
    private boolean RetryTimerStarted = false;
    private boolean mBound = false;
    private boolean mSignalRServerConnected = false;
    SyncRESTService syncRESTService;
    private static String area_id, team_id,device_id;
    protected PowerManager.WakeLock mWakeLock;

    private static String TAG = "BGService";

    public SignalRService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());
        try {
            registerReceiver(broadcastReceiver, new IntentFilter("lk.steps.breakdownassistpluss.stopmediaplayer"));
            if (!Common.ReadBooleanPreferences(getApplicationContext(),"server", false))
                Common.GetIpAddress();
        } catch (Exception e) {
        }
        Log.e(TAG, "SignalR->onCreate");
        //IgnoringBatteryOptimizations();
        final PowerManager pm = (PowerManager) getSystemService(getApplicationContext().POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BreakdownAssistSignalR");
        if(!this.mWakeLock.isHeld()){
            this.mWakeLock.acquire(60*60*60*6); // 6 hours
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //int result = super.onStartCommand(intent, flags, startId);
        Log.e(TAG, "SignalR->onStartCommand");

        syncRESTService = new SyncRESTService(10);
        //Toast.makeText(this, "SignalR Service Started", Toast.LENGTH_SHORT).show();
        IgnoringBatteryOptimizations();
        //Log.e(TAG, "SignalR->onStartCommand2");
        StartRetryTimer();
        //Log.e(TAG, "SignalR->onStartCommand3");
        StopMediaPlayer();

        final PowerManager pm = (PowerManager) getSystemService(getApplicationContext().POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BreakdownAssistSignalR");
        if(!this.mWakeLock.isHeld()){
            this.mWakeLock.acquire(60*60*60*6); // 6 hours
        }

        // return result;
        // return START_STICKY;
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        if(mHubConnection != null)mHubConnection.stop();

        //Log.e("SignalRService","501");
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        if (this.mWakeLock.isHeld()) this.mWakeLock.release();
        unregisterReceiver(broadcastReceiver);
        StopMediaPlayer();

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
        Log.e(TAG, "SignalR->onBind");
        startSignalR();
        return mBinder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        Globals.ServerConnected = false;
        //Restart the service once it has been killed android
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI);
        IgnoringBatteryOptimizations();
    }

    private void StartRetryTimer() {
        Log.e(TAG, "StartRetryTimer0");
        /*if (!Globals.ServerConnected){
            Log.e(TAG, "Server not Connected");
            return;
        }*/
        if (RetryTimerStarted) {
            Log.e(TAG, "RetryTimerStarted started");
            return;
        }
        timer = new Timer();
        mReconnectTimer = new ReconnectTimer();
        timer.schedule(mReconnectTimer, 1000, 20000);
        RetryTimerStarted = true;
       // Log.e(TAG, "StartRetryTimer1");
    }

    private void StopRetryTimer() {
       // Log.e(TAG, "StopRetryTimer0");
        if (!RetryTimerStarted) return;
        timer.cancel();
        RetryTimerStarted = false;
       // Log.e(TAG, "StopRetryTimer1");
    }

    private class ReconnectTimer extends TimerTask {
        @Override
        public void run() {
            Log.e(TAG, "*NOT*Connected*");
            //PlayTone(getApplicationContext(), R.raw.hone, false);
            if (!Common.ReadBooleanPreferences(getApplicationContext(),"server", false))
                Common.GetIpAddress();
            SendOnlineStatusChanged();

            startSignalR();
        }
    }

    private void IgnoringBatteryOptimizations(){
        try{
           // Log.e(TAG, "SignalR->IgnoringBatteryOptimizations 1");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // >=API 23
              //  Log.e(TAG, "SignalR->IgnoringBatteryOptimizations 2");
                Intent intent = new Intent();
                String packageName = this.getPackageName();
                PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
                if (pm.isIgnoringBatteryOptimizations(packageName)){
                   // Log.e(TAG, "SignalR->IgnoringBatteryOptimizations 3");
                    intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                }
                else {
                   // Log.e(TAG, "SignalR->IgnoringBatteryOptimizations 4");
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                }
                this.startService(intent);
               // Log.e(TAG, "SignalR->IgnoringBatteryOptimizations 5");
            }
        }catch(Exception e){
        }
    }
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public SignalRService getService() {
            // Return this instance of SignalRService so clients can call public methods
            return SignalRService.this;
        }
    }

    /**
     * method for clients (activities)
     */
   /* public void sendMessage(String message) {
        String SERVER_METHOD_SEND = "send";
        mHubProxy.invoke(SERVER_METHOD_SEND, message);
    }*/

    /**
     * method for clients (activities)
     */
    /*public void sendMessage_To(String receiverName, String message) {
        String SERVER_METHOD_SEND_TO = "send";
        mHubProxy.invoke(SERVER_METHOD_SEND_TO, receiverName, message);

    }*/
    public static void PostGpsLocation(String Timestamp, String lat, String lon, String accu) {

        if(Globals.mToken == null) return;

       // String SERVER_METHOD_SEND_TO = "PostGpsLocation";
        String SERVER_METHOD_SEND_TO = "PostLocation";
        //String groupId = area_id + "_" + team_id;


        TrackerObject obj = new TrackerObject();
        obj.DeviceId=device_id;
        obj.group_id=area_id + "_" + team_id;
        obj.UserId=Globals.mToken.user_id;
        obj.timestamp=Timestamp;
        obj.lat=lat;
        obj.lon=lon;
        obj.accuracy=accu;

        if (!Globals.ServerConnected) return;
        try {
            mHubProxy.invoke(SERVER_METHOD_SEND_TO, mHubConnection.getConnectionId(), obj);
        } catch (Exception e) {
        }
    }


    private void startSignalR() {
        if (!Common.ReadBooleanPreferences(getApplicationContext(),"login_status", false)) {
            StopRetryTimer();
            Log.e(TAG, "SignalR->User not logged-in");
            return;
        }
        /*if (!Globals.ServerConnected){
            Log.e(TAG, "SignalR->Server not Connected");
            return;
        }*/

        Log.e(TAG, "SignalR->startSignalR()");
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        String user_id = Common.ReadStringPreferences(getApplicationContext(), "user_id", "non");
        String CONNECTION_QUERYSTRING = "userId=" + user_id;
        mHubConnection = new HubConnection(Globals.serverUrl, CONNECTION_QUERYSTRING, true, new Logger() {
            @Override
            public void log(String message, LogLevel level) {
                System.out.println(message);
            }
        });

        //  mHubConnection = new HubConnection(Globals.serverUrl);
        mHubProxy = mHubConnection.createHubProxy("BreakdownAssistAndroidHub");


        ClientTransport clientTransport = new ServerSentEventsTransport(mHubConnection.getLogger());
        SignalRFuture<Void> signalRFuture = mHubConnection.start(clientTransport);

        //final String group_token = ReadStringPreferences("group_token","");
        area_id = Common.ReadStringPreferences(getApplicationContext(),"area_id", "");
        team_id = Common.ReadStringPreferences(getApplicationContext(),"team_id", "");
        device_id = Common.ReadStringPreferences(getApplicationContext(),"device_id", "");
        //Log.e("SignalR ", "SignalR->*groupid*"+area_id+"_"+team_id);
        mHubConnection.setGroupsToken(area_id + "_" + team_id);

        try {
            Log.d("JAGATH1", "1");
            signalRFuture.get();//Log.d("JAGATH1", "2");
            StopRetryTimer();//Log.d("JAGATH1", "3");
            Globals.ServerConnected = true;

            SendOnlineStatusChanged();

            Log.e(TAG, "SignalR->*Connected*");
            Log.e(TAG, "SignalR->ID=" + mHubConnection.getConnectionId());
        } catch (InterruptedException | ExecutionException e) {
           // Globals.ServerConnected = false;
            Log.e(TAG, "SignalR->" + e.toString());
            StartRetryTimer();
            return;
        }
        // sendMessage("Hello from BNK!");



        // Subscribe to the received event
        mHubConnection.received(new MessageReceivedHandler() {

            @Override
            public void onMessageReceived(final JsonElement json) {

                Log.e(TAG,"onMessageReceived "+ json.toString());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(json.toString().contains("EditedBreakdown")){
                            SignalRBreakdownObject msg = new Gson().fromJson(json, SignalRBreakdownObject.class);
                            // Log.e("SignalR ", "TEST3");
                            if(msg.M!=null) {
                                //  Log.e("SignalR ", "TEST4");
                                if(msg.M.equals("EditedBreakdown")){
                                     Log.e(TAG, "EditedBreakdown");
                                    CreateNotification(getApplicationContext(), "Breakdown "+msg.A.get(0).get_Job_No() + " has edited." );

                                    Globals.dbHandler.UpdateEditBreakdownByJobNo(msg.A.get(0));

                                    List<FeedbackObject> feedbacks = new ArrayList<FeedbackObject>();
                                    FeedbackObject feedback = new FeedbackObject();
                                    feedback.FeedbackType = "EDITED_BD";
                                    feedback.BreakdownId = msg.A.get(0).get_Job_No();
                                    feedback.UserId = Globals.mToken.user_id;
                                    feedback.AreaId = Globals.mToken.area_id;
                                    feedback.EcscId = Globals.mToken.team_id;
                                    feedbacks.add(feedback);

                                    PostFeedbackNew(feedbacks);
                                    Intent intent = new Intent();
                                    intent.setAction("lk.steps.breakdownassistpluss.MainActivityBroadcastReceiver");
                                    intent.putExtra("breakdown_edited", "refresh_list");
                                    intent.putExtra("edited_breakdown", new Gson().toJson(msg.A.get(0)));
                                    getApplicationContext().sendBroadcast(intent);

                                    if (msg.A.get(0).get_Priority() == 4){
                                        PlayTone(getApplicationContext(), R.raw.buzzer, false);
                                    }else{
                                        PlayTone(getApplicationContext(), R.raw.ding_ling, false);
                                    }

                                }
                            }
                        }else{
                            SignalRObject msg = new Gson().fromJson(json, SignalRObject.class);
                            if (msg.M != null) {
                                if (!msg.M.equals("PostFeedback") & !msg.M.equals("PostHeartBeat")) {//Post feedback and Heartbeat not required running activity
                                    StartMainActivityIfRequired(getApplicationContext(), msg);
                                }
                                HandleMsg(getApplicationContext(), msg.M, msg.A);
                            }
                        }
                    }
                });
            }
        });

        mHubConnection.error(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                Globals.ServerConnected = false;
                Log.e(TAG, "SignalR->ERROR");
                error.printStackTrace();
                StartRetryTimer();
            }
        });
        mHubConnection.connected(new Runnable() {
            @Override
            public void run() {
                Globals.ServerConnected = true;
                Log.e(TAG, "SignalR->CONNECTED");
                SendOnlineStatusChanged();
            }
        });

        mHubConnection.reconnected(new Runnable() {
            @Override
            public void run() {
                Globals.ServerConnected = true;
                Log.e(TAG, "SignalR->RECONNECTED");
                SendOnlineStatusChanged();
            }
        });
        // Subscribe to the closed event
        mHubConnection.closed(new Runnable() {
            @Override
            public void run() {
                Globals.ServerConnected = false;
                Log.e(TAG, "SignalR->DISCONNECTED");
                SendOnlineStatusChanged();
                StartRetryTimer();
            }
        });
    }

    public static void HandleMsg(Context context, String method, List<String> data) {

        if (method.equals("GetNewBreakdowns")) {
            Log.e(TAG, "SignalR->GetNewBreakdowns");
            //Toast.makeText(context, "SignalR NewBreakdowns request received", Toast.LENGTH_SHORT).show();
            String breakdownId = data.get(1);
            GetBreakdowns(context, breakdownId);
        } else if (method.equals("GetBreakdownStatusUpdate")) {
            Log.e(TAG, "SignalR->GetBreakdownStatusUpdate");
           // Toast.makeText(context, "SignalR BreakdownStatusUpdate request received", Toast.LENGTH_SHORT).show();
            String breakdownId = data.get(1);
            GetBreakdownsStatusChange(context, breakdownId);
        } else if (method.equals("GetBreakdownGroups")) {
            Log.e(TAG, "SignalR->GetBreakdownGroups");
           // Toast.makeText(context, "SignalR BreakdownGroups request received", Toast.LENGTH_SHORT).show();
            String breakdownId = data.get(1);
            GetBreakdownGroups(context, breakdownId);
        } else if (method.equals("GetBreakdownUngroups")) {
            Log.e(TAG, "SignalR->GetBreakdownUngroups");
            // Toast.makeText(context, "SignalR BreakdownGroups request received", Toast.LENGTH_SHORT).show();
            String breakdownId = data.get(1);
            Globals.dbHandler.UpdateUngroups(breakdownId,"1");
            Intent intent = new Intent();
            intent.setAction("lk.steps.breakdownassistpluss.MainActivityBroadcastReceiver");
            intent.putExtra("group_breakdowns", "group_breakdowns");
            context.sendBroadcast(intent);
            CreateNotification(context, "Breakdowns ungrouped.");

            PlayTone(context, R.raw.ching, false);

            List<FeedbackObject> feedbacks = new ArrayList<FeedbackObject>();
            FeedbackObject feedback = new FeedbackObject();
            feedback.FeedbackType = "UNGROUP";
            feedback.BreakdownId = breakdownId;
            //feedback.StatusId = String.valueOf(breakdown.get_Status());
            //feedback.StatusTime = breakdown.get_Received_Time();
            feedback.UserId = Globals.mToken.user_id;
            feedback.AreaId = Globals.mToken.area_id;
            feedback.EcscId = Globals.mToken.team_id;
            feedbacks.add(feedback);

            PostFeedbackNew(feedbacks);


        }  else if (method.equals("PostFeedback")) {
            //Log.e("SignalR", "PostFeedback");
            //Toast.makeText(context,"SignalR PostFeedback received", Toast.LENGTH_SHORT).show();
            String fbType = data.get(1);
            if (fbType.equals("GpsPoint")) {
                Log.e(TAG, "SignalR->PostFeedback GpsPoint");
               // Toast.makeText(context, "SignalR PostFeedback received GpsPoint", Toast.LENGTH_SHORT).show();
                String timestamp = data.get(2);
                Globals.dbHandler.UpdateTrackingDataByTimeStamp(timestamp);//Successfully done
            }
        } else if (method.equals("PostHeartBeat")) {
            Log.e(TAG, "SignalR->HeartBeatReceived");
            //Toast.makeText(context, "SignalR HeartBeatReceived", Toast.LENGTH_SHORT).show();
            TurnOnScreen(context);
            //PlayTone(context, R.raw.heartbeat2, false);
        }
    }

    private void SendOnlineStatusChanged(){
        Intent intent = new Intent();
        intent.setAction("lk.steps.breakdownassistpluss.MainActivityBroadcastReceiver");
        intent.putExtra("online_status_changed", "online_status_changed");
        getApplicationContext().sendBroadcast(intent);
    }

    private void StartMainActivityIfRequired(Context context, final SignalRObject msg) {
        if (!isForeground()) {
            Toast.makeText(context, "SignalR starting MainActivity", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    Intent myIntent = new Intent(SignalRService.this, MainActivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myIntent.putExtra("SignalR-msg.M", msg.M);
                    // myIntent.putExtra("SignalR-msg.A",msg.A.get(1));
                    myIntent.putStringArrayListExtra("SignalR-msg.A", msg.A);
                    SignalRService.this.startActivity(myIntent);
                }
            });
        }
    }

    public boolean isForeground() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals("lk.steps.breakdownassistpluss");
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            //  mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };




    private static void PlayTone(Context context, int resourceId, boolean looping) {
        mMediaPlayer = MediaPlayer.create(context, resourceId);
        mMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setLooping(looping);
        mMediaPlayer.setVolume(1.0f, 1.0f);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                mp.release();
                mMediaPlayer = null;
                Log.e(TAG,"MediaPlayer onCompletion");
            }
        });
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                Log.e(TAG,"MediaPlayer onPrepared");
            }
        });
        //mMediaPlayer.prepareAsync();
        //mMediaPlayer.start();
    }

    private void StopMediaPlayer(){
        try{
            if(mMediaPlayer != null){
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }catch(Exception e){

        }
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mMediaPlayer != null) {
                //if (mMediaPlayer.isPlaying())
                // mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        }
    };



    private static void CreateNotification(Context context, String msg) {
        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("Breakdown Assist+")
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pi)
                .setAutoCancel(true).setDefaults(0)
                .setDefaults(Notification.FLAG_INSISTENT);

        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if(manager!=null)manager.notify(73195, builder.build());

        /*PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if(pm!=null){
            boolean isScreenOn = pm.isScreenOn();
            if (!isScreenOn) {
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "NotificationWakeLock");
                wl.acquire(10000);
                PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NotificationCpuLock");
                wl_cpu.acquire(10000);
            }
        }*/
        TurnOnScreen(context);
    }

    private static void TurnOnScreen(Context context){
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if(pm!=null){
            boolean isScreenOn = pm.isScreenOn();
            if (!isScreenOn) {
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "NotificationWakeLock");
                wl.acquire(10000);
                PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NotificationCpuLock");
                wl_cpu.acquire(10000);
            }
        }
    }

    public static void GetBreakdowns(final Context context, String breakdownId) {
        try {
            final SyncRESTService syncRESTService = new SyncRESTService(10);
            Call<List<Breakdown>> call = syncRESTService.getService()
                    .GetBreakdowns("Bearer " + Globals.mToken.access_token,
                            Globals.mToken.user_id, breakdownId);

            call.enqueue(new Callback<List<Breakdown>>() {
                @Override
                public void onResponse(Call<List<Breakdown>> call, Response<List<Breakdown>> response) {
                    if (response.isSuccessful()) {
                        //Log.e("GetNewBreakdowns","Successful");
                        //Log.e("GetNewBreakdowns","Successful"+response.body());
                        List<Breakdown> breakdowns = response.body();
                        //Log.e("GetNewBreakdowns","Number-"+breakdowns.size());

                        if (breakdowns.size() > 0) {
                            String ring = "0";
                            List<FeedbackObject> feedbacks = new ArrayList<FeedbackObject>();
                            long result = 0;
                            for (Breakdown breakdown : breakdowns) {

                                FeedbackObject feedback = new FeedbackObject();
                                feedback.FeedbackType = "NB";
                                feedback.BreakdownId = breakdown.get_Job_No();
                                feedback.StatusId = String.valueOf(breakdown.get_Status());
                                feedback.StatusTime = breakdown.get_Received_Time();
                                feedback.UserId = Globals.mToken.user_id;
                                feedback.AreaId = breakdown.get_AREA();//Globals.mToken.area_id;
                                feedback.EcscId = breakdown.get_ECSC();//Globals.mToken.team_id;
                                feedback.TeamId = Globals.mToken.team_id;
                                feedbacks.add(feedback);

                                breakdown.set_BA_SERVER_SYNCED("1");
                                breakdown.set_JOB_SOURCE("BA");
                                result = Globals.dbHandler.InsertOrUpdateBreakdown(breakdown);
                                //Log.e("breakdown","Job_No-"+breakdown.get_Job_No());
                                //Log.e("breakdown","ParentBreakdownId-"+breakdown.get_ParentBreakdownId());
                                if (breakdown.get_Priority() == 4) ring = "1";
                            }

                            PostFeedbackNew(feedbacks);

                            if (result > 0) {
                                if (ring.equals("1")) {
                                    PlayTone(context, R.raw.buzzer, false);
                                } else {
                                    PlayTone(context, R.raw.ding_ling, false);
                                }


                               // String sIssuedBreakdownID = Globals.dbHandler.getLastBreakdownID();
                                //Informing the Map view about the new bd, then it can add it
                                Intent intent = new Intent();
                                intent.setAction("lk.steps.breakdownassistpluss.MainActivityBroadcastReceiver");
                                //intent.putExtra("_id", sIssuedBreakdownID);
                                intent.putExtra("new_breakdowns", "new_breakdowns");
                                intent.putExtra("new_breakdown_list", new Gson().toJson(breakdowns));
                                context.sendBroadcast(intent);

                                if (breakdowns.size() == 1) {
                                    CreateNotification(context, "New breakdown received.");
                                } else {
                                    CreateNotification(context, "New " + breakdowns.size() + " breakdowns received.");
                                }
                            }
                        }
                    } else if (response.errorBody() != null) {
                        if (response.code() == 401) { //Authentication fail
                            Toast.makeText(context, "Authentication fail..", Toast.LENGTH_SHORT).show();
                            Common.RemoteLoginWithLastCredentials(context,2);
                        } else {
                            Toast.makeText(context, "GetNewBreakdowns\nResponse code =" + response.code(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("GetNewBreakdowns", "onResponse" + response.errorBody() + "*code*" + response.code());
                    }
                    syncRESTService.CloseAllConnections();
                }

                @Override
                public void onFailure(Call<List<Breakdown>> call, Throwable t) {
                    Toast.makeText(context, "Error in network..\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("GetNewBreakdowns", "5-" + t.getMessage());
                    syncRESTService.CloseAllConnections();
                }
            });
        } catch (Exception e) {
            Log.e("GetNewBreakdowns", "" + e.getMessage());
        }
    }

    public static void GetBreakdownGroups(final Context context, String ParentBreakdownId) {
        try {
            final SyncRESTService syncRESTService = new SyncRESTService(10);
            Call<List<BreakdownGroup>> call = syncRESTService.getService()
                    .GetBreakdownGroups("Bearer " + Globals.mToken.access_token,
                            ParentBreakdownId);

            call.enqueue(new Callback<List<BreakdownGroup>>() {
                @Override
                public void onResponse(Call<List<BreakdownGroup>> call, Response<List<BreakdownGroup>> response) {
                    if (response.isSuccessful()) {
                        //Log.e("GetNewBreakdowns","Successful");
                        List<BreakdownGroup> breakdownGroups = response.body();
                        Log.e("GetBreakdownGroups", "Number-" + breakdownGroups.size());
                        Log.e("GetBreakdownGroups", "Number-" + response.body());
                        if (breakdownGroups.size() > 0) {
                            Globals.dbHandler.AddBreakdownGroups(breakdownGroups);
                            List<FeedbackObject> feedbacks = new ArrayList<FeedbackObject>();
                            FeedbackObject feedback = new FeedbackObject();
                            feedback.FeedbackType = "GROUP";
                            feedback.BreakdownId = breakdownGroups.get(0).GetParentBreakdownId();
                            //feedback.StatusId = String.valueOf(breakdown.get_Status());
                            //feedback.StatusTime = breakdown.get_Received_Time();
                            feedback.UserId = Globals.mToken.user_id;
                            feedback.AreaId = Globals.mToken.area_id;
                            feedback.EcscId = Globals.mToken.team_id;
                            feedbacks.add(feedback);

                            PostFeedbackNew(feedbacks);

                            PlayTone(context, R.raw.ching, false);
                            //Informing the Map view about the new bd, then it can add it
                            Intent intent = new Intent();
                            intent.setAction("lk.steps.breakdownassistpluss.MainActivityBroadcastReceiver");
                            intent.putExtra("group_breakdowns", "group_breakdowns");
                            context.sendBroadcast(intent);

                            CreateNotification(context, "Breakdown group created.");
                        }
                    } else if (response.errorBody() != null) {
                        if (response.code() == 401) { //Authentication fail
                            Toast.makeText(context, "Authentication fail..", Toast.LENGTH_SHORT).show();
                            Common.RemoteLoginWithLastCredentials(context,3);
                        } else {
                            Toast.makeText(context, "GetBreakdownGroups\nResponse code =" + response.code(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("GetBreakdownGroups", "onResponse" + response.errorBody() + "*code*" + response.code());
                    }
                    syncRESTService.CloseAllConnections();
                }

                @Override
                public void onFailure(Call<List<BreakdownGroup>> call, Throwable t) {
                    Toast.makeText(context, "Error in network..\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("GetBreakdownGroups", "5-" + t.getMessage());
                    syncRESTService.CloseAllConnections();
                }
            });
        } catch (Exception e) {
            Log.e("GetBreakdownGroups", "" + e.getMessage());
        }
    }

    public static void GetBreakdownsStatusChange(final Context context, String breakdownId) {
        try {
            final SyncRESTService syncRESTService = new SyncRESTService(10);
            Call<List<JobChangeStatus>> call = syncRESTService.getService()
                    .GetBreakdownsStatus("Bearer " + Globals.mToken.access_token, breakdownId);

            call.enqueue(new Callback<List<JobChangeStatus>>() {
                @Override
                public void onResponse(Call<List<JobChangeStatus>> call, Response<List<JobChangeStatus>> response) {
                    if (response.isSuccessful()) {
                        Log.e("GetBreakdownsStatus", "Successful" + new Gson().toJson(response));
                        List<JobChangeStatus> jobChangeStatus = response.body();
                        Log.e("GetBreakdownsStatus", "Number-" + jobChangeStatus.size());
                        if (jobChangeStatus.size() > 0) {
                            List<FeedbackObject> feedbacks = new ArrayList<FeedbackObject>();
                            long result = 0;
                            for (JobChangeStatus jobStatus : jobChangeStatus) {
                                FeedbackObject feedback = new FeedbackObject();
                                feedback.FeedbackType = "BS";
                                feedback.BreakdownId = jobStatus.job_no;
                                feedback.StatusId = jobStatus.status;
                                feedback.StatusTime = jobStatus.change_datetime;
                                feedback.UserId = Globals.mToken.user_id;
                                feedback.AreaId = Globals.mToken.area_id;
                                feedback.EcscId = Globals.mToken.team_id;
                                feedbacks.add(feedback);
                                /*Log.e("GetBreakdownsStatus","JOB_NO-"+jobStatus.job_no);
                                Log.e("GetBreakdownsStatus","change_datetime-"+jobStatus.change_datetime);
                                Log.e("GetBreakdownsStatus","STATUS-"+jobStatus.STATUS);
                                Log.e("GetBreakdownsStatus","type_failure-"+jobStatus.type_failure);
                                Log.e("GetBreakdownsStatus","cause-"+jobStatus.cause);
                                Log.e("GetBreakdownsStatus","detail_reason_code-"+jobStatus.detail_reason_code);*/
                                jobStatus.device_timestamp = jobStatus.change_datetime;
                                jobStatus.synchro_mobile_db = 1;//No need to send to server

                                if (jobStatus.status.equals(String.valueOf(Breakdown.JOB_VISITED))) {//VisitedDialog
                                    Log.e("GetBreakdownsStatus", "VisitedDialog:" + jobStatus.job_no);
                                    //jobStatus.STATUS="V";
                                    result = Globals.dbHandler.addJobStatusChangeRec(jobStatus);
                                    Globals.dbHandler.UpdateBreakdownStatusByJobNo(jobStatus.job_no, "", jobStatus.status);

                                } else if (jobStatus.status.equals(String.valueOf(Breakdown.JOB_ATTENDING))) {//Attending
                                    Log.e("GetBreakdownsStatus", "Attending:" + jobStatus.job_no);
                                    // jobStatus.STATUS="A";
                                    result = Globals.dbHandler.addJobStatusChangeRec(jobStatus);
                                    Globals.dbHandler.UpdateBreakdownStatusByJobNo(jobStatus.job_no, "", jobStatus.status);

                                } else if (jobStatus.status.equals(String.valueOf(Breakdown.JOB_TEMPORARY_COMPLETED))) {//Done
                                    Log.e("GetBreakdownsStatus", "Done:" + jobStatus.job_no);
                                    // jobStatus.STATUS="D";
                                    result = Globals.dbHandler.addJobStatusChangeRec(jobStatus);
                                    Globals.dbHandler.UpdateBreakdownStatusByJobNo(jobStatus.job_no, "", jobStatus.status);

                                } else if (jobStatus.status.equals(String.valueOf(Breakdown.JOB_COMPLETED))) {//Completed
                                    Log.e("GetBreakdownsStatus", "Completed:" + jobStatus.job_no);
                                    // jobStatus.STATUS="C";
                                    JobCompletion jobCompletionRec = new JobCompletion();
                                    jobCompletionRec.JOB_NO = jobStatus.job_no;
                                    jobCompletionRec.job_completed_datetime = jobStatus.change_datetime;
                                    jobCompletionRec.type_failure = jobStatus.type_failure;
                                    jobCompletionRec.cause = jobStatus.cause;
                                    jobCompletionRec.detail_reason_code = jobStatus.detail_reason_code;

                                    result = Globals.dbHandler.addJobCompletionRec(jobCompletionRec);
                                    Globals.dbHandler.UpdateBreakdownStatusByJobNo(jobStatus.job_no, jobStatus.change_datetime, jobStatus.status);
                                } else if (jobStatus.status.equals(String.valueOf(Breakdown.JOB_REJECT)) |//reject
                                        jobStatus.status.equals(String.valueOf(Breakdown.JOB_WITHDRAWN))) {
                                    Log.e("GetBreakdownsStatus", "reject:" + jobStatus.job_no);
                                    // jobStatus.STATUS="R";
                                    result = Globals.dbHandler.addJobStatusChangeRec(jobStatus);
                                    Globals.dbHandler.UpdateBreakdownStatusByJobNo(jobStatus.job_no, jobStatus.change_datetime, jobStatus.status);
                                } else if (jobStatus.status.equals(String.valueOf(Breakdown.JOB_RE_CALLED))) {//Done
                                    Log.e("GetBreakdownsStatus", "JOB_RE_CALLED:" + jobStatus.job_no);
                                    // jobStatus.STATUS="D";
                                    result = Globals.dbHandler.addJobStatusChangeRec(jobStatus);
                                    Globals.dbHandler.UpdateBreakdownStatusByJobNo(jobStatus.job_no, "", jobStatus.status);

                                }
                                Log.e("GetBreakdownsStatus", "jobStatus.STATUS:" + jobStatus.status);
                            }

                            PostFeedbackNew(feedbacks);

                            if (result > 0) {

                                PlayTone(context, R.raw.ching, false);

                                //Informing the Map view about the new bd, then it can add it
                                Intent intent = new Intent();
                                intent.setAction("lk.steps.breakdownassistpluss.MainActivityBroadcastReceiver");
                                intent.putExtra("job_status_changed", "refresh_list");
                                intent.putExtra("updated_breakdowns", new Gson().toJson(jobChangeStatus));
                                context.sendBroadcast(intent);

                                if (jobChangeStatus.size() == 1) {
                                    CreateNotification(context, "One breakdown has updated.");
                                } else {
                                    CreateNotification(context, jobChangeStatus.size() + " breakdowns has updated.");
                                }
                            }
                        }
                    } else if (response.errorBody() != null) {
                        if (response.code() == 401) { //Authentication fail
                            Toast.makeText(context, "Authentication fail..", Toast.LENGTH_SHORT).show();
                            Common.RemoteLoginWithLastCredentials(context,4);
                        } else {
                            Toast.makeText(context, "GetNewBreakdowns\nResponse code =" + response.code(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("GetNewBreakdowns", "onResponse" + response.errorBody() + "*code*" + response.code());
                    }
                    syncRESTService.CloseAllConnections();
                }

                @Override
                public void onFailure(Call<List<JobChangeStatus>> call, Throwable t) {
                    Toast.makeText(context, "Error in network..\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("GetNewBreakdowns", "5-" + t.getMessage());
                    syncRESTService.CloseAllConnections();
                }
            });

        } catch (Exception e) {
            Log.e("GetNewBreakdowns", "" + e.getMessage());
            //Log.e("SignalRTEST","6");
        }
    }


    private static void PostFeedbackNew(List<FeedbackObject> data) {

        final SyncRESTService syncRESTService = new SyncRESTService(10);
        Call<FeedbackObject> call = syncRESTService.getService()
                .PostFeedback("Bearer " + Globals.mToken.access_token, data);

        call.enqueue(new Callback<FeedbackObject>() {

            @Override
            public void onResponse(Call<FeedbackObject> call, Response<FeedbackObject> response) {
                if (response.isSuccessful()) {
                } else if (response.errorBody() != null) {
                }
                syncRESTService.CloseAllConnections();
            }

            @Override
            public void onFailure(Call<FeedbackObject> call, Throwable t) {
                syncRESTService.CloseAllConnections();
            }
        });

    }

}

