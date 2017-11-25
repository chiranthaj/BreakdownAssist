package lk.steps.breakdownassistpluss.Sync;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
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
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.Fragments.JobListFragment;
import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.GpsTracker.GpsTrackerAlarmReceiver;
import lk.steps.breakdownassistpluss.GpsTracker.TrackerObject;
import lk.steps.breakdownassistpluss.JobChangeStatus;
import lk.steps.breakdownassistpluss.JobCompletion;
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
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;
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
    MyTimerTask myTimerTask;
    private static MediaPlayer mediaPlayer;
    //private boolean ServerConnected=false;
    private boolean RetryTimerStarted=false;
    private boolean mBound = false;
    SyncRESTService syncRESTService;
    private static String area_id;
    private static String team_id;

    public SignalRService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());
        try{
            registerReceiver(broadcastReceiver, new IntentFilter("lk.steps.breakdownassistpluss.stopmediaplayer"));
            if(!ReadBooleanPreferences("server",false))SelectorActivity.GetIpAddress();
        }catch(Exception e){
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        syncRESTService = new SyncRESTService(10);
        //Toast.makeText(this, "SignalR Service Started", Toast.LENGTH_SHORT).show();

        StartRetryTimer();
       // return result;
        return START_STICKY;
    }

    private void StartRetryTimer(){
        if(RetryTimerStarted)return;
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 1000, 20000);
        RetryTimerStarted=true;
    }
    private void StopRetryTimer(){
        if(!RetryTimerStarted)return;
        timer.cancel();
        RetryTimerStarted=false;
    }
    @Override
    public void onDestroy() {
        mHubConnection.stop();
        //Log.e("SignalRService","501");
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
       // if(!ServerConnected)
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

        //Restart the service once it has been killed android

        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +100, restartServicePI);
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
        String SERVER_METHOD_SEND_TO = "PostGpsLocation";
        String groupId = area_id+"_"+team_id;

        if(!Globals.serverConnected) return;
        try{
            mHubProxy.invoke(SERVER_METHOD_SEND_TO,
                    mHubConnection.getConnectionId(),
                    Globals.mToken.user_id,
                    groupId,Timestamp, lat, lon, accu );
        }catch(Exception e){
        }
    }


    private void startSignalR() {
        Log.e("SignalR","startSignalR()");
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

       String user_id = ReadStringPreferences(getApplicationContext(), "user_id","non");
        String CONNECTION_QUERYSTRING = "userId="+user_id;
        mHubConnection = new HubConnection(Globals.serverUrl,CONNECTION_QUERYSTRING, true, new Logger() {
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
        area_id = ReadStringPreferences("area_id","");
        team_id = ReadStringPreferences("team_id","");
        Log.e("SignalR ", "*groupid*"+area_id+"_"+team_id);
        mHubConnection.setGroupsToken(area_id + "_" + team_id);

        try {
            signalRFuture.get();
            StopRetryTimer();
            Globals.serverConnected = true;
            Log.e("SignalR ", "*Connected*");
            Log.e("SignalR ", "ID="+mHubConnection.getConnectionId());

            //Log.e("SimpleSignalR", "ServerConnected=true");
        } catch (InterruptedException | ExecutionException e) {
            Globals.serverConnected = false;
            Log.e("SignalR", e.toString());
            //ServerConnected=false;
            StartRetryTimer();
            return;
        }
       // sendMessage("Hello from BNK!");


       /* String CLIENT_METHOD_BROADAST_MESSAGE = "broadcastMessage";
        mHubProxy.on(CLIENT_METHOD_BROADAST_MESSAGE,
                new SubscriptionHandler2<String, String>() {
                    @Override
                    public void run(final String name, final String msg) {
                        //Log.e("SimpleSignalR", "4569"+msg+name);
                        final String finalMsg =  msg;
                        // display Toast message
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), finalMsg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                , String.class,String.class);*/

        // Subscribe to the received event
        mHubConnection.received(new MessageReceivedHandler() {

            @Override
            public void onMessageReceived(final JsonElement json) {

                Log.e("onMessageReceived ", json.toString());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        SignalRObject msg = gson.fromJson(json, SignalRObject.class);
                       // Log.e("onMessageReceived ", msg.toString());
                        if(msg.M!=null) {
                            if(!msg.M.equals("PostFeedback")){//Post feedback not required running activity
                                StartMainActivityIfRequired(getApplicationContext(), msg);
                            }
                            HandleMsg(getApplicationContext(), msg.M, msg.A);
                        }
                    }
                });
            }
        });

        mHubConnection.error(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                Globals.serverConnected = false;
                error.printStackTrace();
                StartRetryTimer();
            }
        });
        mHubConnection.connected(new Runnable() {
            @Override
            public void run() {
                Globals.serverConnected = true;
                Log.e("SignalR", "CONNECTED");
            }
        });

        mHubConnection.reconnected(new Runnable() {
            @Override
            public void run() {
                Globals.serverConnected = true;
                Log.e("SignalR", "RECONNECTED");
            }
        });
        // Subscribe to the closed event
        mHubConnection.closed(new Runnable() {
            @Override
            public void run() {
                Globals.serverConnected = false;
                Log.e("SignalR", "DISCONNECTED");
                StartRetryTimer();
            }
        });
    }

    public static void HandleMsg(Context context,String method, List<String> data){
        if(method.equals("GetNewBreakdowns")){
            Log.e("SignalR", "GetNewBreakdowns");
            Toast.makeText(context,"SignalR NewBreakdowns request received", Toast.LENGTH_SHORT).show();
            String breakdownId = data.get(1);
            GetBreakdowns(context, breakdownId);
        }else if(method.equals("GetBreakdownStatusUpdate")){
            Log.e("SignalR", "GetBreakdownStatusUpdate");
            Toast.makeText(context,"SignalR BreakdownStatusUpdate request received", Toast.LENGTH_SHORT).show();
            String breakdownId = data.get(1);
            GetBreakdownsStatusChange(context,breakdownId);
        }else if(method.equals("GetBreakdownGroups")){
            Log.e("SignalR", "GetBreakdownGroups");
            Toast.makeText(context,"SignalR BreakdownGroups request received", Toast.LENGTH_SHORT).show();
            String breakdownId = data.get(1);
            GetBreakdownGroups(context,breakdownId);
        }else if(method.equals("PostFeedback")){
            Log.e("SignalR", "PostFeedback");
            Toast.makeText(context,"SignalR PostFeedback received", Toast.LENGTH_SHORT).show();
            String fbType = data.get(1);
            if(fbType.equals("GpsPoint")){
                String timestamp = data.get(2);
                Globals.dbHandler.UpdateTrackingDataByTimeStamp(timestamp);//Successfully done
            }
        }
    }



    private void StartMainActivityIfRequired(Context context, final SignalRObject msg){
        if(!isForeground()){
            Toast.makeText(context,"SignalR starting MainActivity", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    Intent myIntent = new Intent(SignalRService.this, MainActivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myIntent.putExtra("SignalR-msg.M",msg.M);
                   // myIntent.putExtra("SignalR-msg.A",msg.A.get(1));
                    myIntent.putStringArrayListExtra("SignalR-msg.A",msg.A);
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


    public static void GetBreakdowns(final Context context, String breakdownId){
        try{
            final SyncRESTService syncRESTService = new SyncRESTService(10);
            Call<List<Breakdown>> call = syncRESTService.getService()
                    .GetBreakdowns( "Bearer "+ Globals.mToken.access_token,
                            Globals.mToken.user_id,breakdownId);

            call.enqueue(new Callback<List<Breakdown>>(){
                @Override
                public void onResponse(Call<List<Breakdown>> call, Response<List<Breakdown>> response) {
                    if (response.isSuccessful()) {
                        Globals.serverConnected = true;
                        //Log.e("GetNewBreakdowns","Successful");
                        Log.e("GetNewBreakdowns","Successful"+response.body());
                        List<Breakdown> breakdowns = response.body();
                        Log.e("GetNewBreakdowns","Number-"+breakdowns.size());

                        if(breakdowns.size()>0){
                            String ring = "0";
                            List<FeedbackObject> feedbacks = new ArrayList<FeedbackObject>();
                            long result = 0;
                            for (Breakdown breakdown :breakdowns) {

                                FeedbackObject feedback = new FeedbackObject();
                                feedback.FeedbackType="NB";
                                feedback.BreakdownId = breakdown.get_Job_No();
                                feedback.StatusId = String.valueOf(breakdown.get_Status());
                                feedback.StatusTime = breakdown.get_Received_Time();
                                feedback.UserId=Globals.mToken.user_id;
                                feedback.AreaId= breakdown.get_AREA();//Globals.mToken.area_id;
                                feedback.EcscId= breakdown.get_ECSC();//Globals.mToken.team_id;
                                feedback.TeamId=Globals.mToken.team_id;
                                feedbacks.add(feedback);

                                breakdown.set_BA_SERVER_SYNCED("1");
                                breakdown.set_JOB_SOURCE("BA");
                                result=Globals.dbHandler.addBreakdown2(breakdown);
                                //Log.e("breakdown","Job_No-"+breakdown.get_Job_No());
                                //Log.e("breakdown","ParentBreakdownId-"+breakdown.get_ParentBreakdownId());
                                if(breakdown.get_Priority() == 4) ring = "1";
                            }

                            PostFeedbackNew(feedbacks);

                            if(result >0){
                                if(mediaPlayer==null){
                                    if(ring.equals("1")){
                                        mediaPlayer = MediaPlayer.create(context, R.raw.buzzer);
                                        mediaPlayer.setLooping(true);
                                    }else{
                                        mediaPlayer= MediaPlayer.create(context, R.raw.fb_sound);
                                    }
                                    mediaPlayer.setVolume(100,100);
                                    mediaPlayer.start();
                                }else if(!mediaPlayer.isPlaying()){
                                    if(ring.equals("1")){
                                        mediaPlayer = MediaPlayer.create(context, R.raw.buzzer);
                                        mediaPlayer.setLooping(true);
                                    }else{
                                        mediaPlayer= MediaPlayer.create(context, R.raw.fb_sound);
                                    }
                                    mediaPlayer.setVolume(100,100);
                                    mediaPlayer.start();
                                }
                                String sIssuedBreakdownID = Globals.dbHandler.getLastBreakdownID();
                                //Informing the Map view about the new bd, then it can add it
                                Intent myintent=new Intent();
                                myintent.setAction("lk.steps.breakdownassistpluss.NewBreakdownBroadcast");
                                myintent.putExtra("_id",sIssuedBreakdownID);
                                myintent.putExtra("new_breakdowns","new_breakdowns");
                                myintent.putExtra("new_breakdown_list",new Gson().toJson(breakdowns));
                                //myintent.putExtra("ring",ring);
                                context.sendBroadcast(myintent);

                                if(breakdowns.size() == 1){
                                    CreateNotification(context, "New breakdown received.");
                                }else{
                                    CreateNotification(context, "New "+breakdowns.size()+" breakdowns received.");
                                }
                            }
                        }
                    } else if (response.errorBody() != null) {
                        Globals.serverConnected = false;
                        if(response.code() == 401) { //Authentication fail
                            Toast.makeText(context, "Authentication fail..", Toast.LENGTH_SHORT).show();
                            MainActivity.ReLoginRequired=true;
                        }else{
                            Toast.makeText(context, "GetNewBreakdowns\nResponse code ="+response.code(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("GetNewBreakdowns","onResponse" + response.errorBody()+"*code*"+response.code());
                    }
                    syncRESTService.CloseAllConnections();
                }

                @Override
                public void onFailure(Call<List<Breakdown>> call, Throwable t) {
                    Globals.serverConnected = false;
                    Toast.makeText(context, "Error in network..\n"+ t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("GetNewBreakdowns","5-" + t.getMessage());
                    syncRESTService.CloseAllConnections();
                }
            });
        }catch(Exception e){
            Globals.serverConnected = false;
            Log.e("GetNewBreakdowns","" + e.getMessage());
        }
    }

    public static void GetBreakdownGroups(final Context context, String ParentBreakdownId){
        try{
            final SyncRESTService syncRESTService = new SyncRESTService(10);
            Call<List<BreakdownGroup>> call = syncRESTService.getService()
                    .GetBreakdownGroups( "Bearer "+ Globals.mToken.access_token,
                            ParentBreakdownId);

            call.enqueue(new Callback<List<BreakdownGroup>>(){
                @Override
                public void onResponse(Call<List<BreakdownGroup>> call, Response<List<BreakdownGroup>> response) {
                    if (response.isSuccessful()) {
                        Globals.serverConnected = true;
                        //Log.e("GetNewBreakdowns","Successful");
                        List<BreakdownGroup> breakdownGroups = response.body();
                        Log.e("GetBreakdownGroups","Number-"+breakdownGroups.size());
                        Log.e("GetBreakdownGroups","Number-"+response.body());
                        if(breakdownGroups.size()>0){
                            Globals.dbHandler.AddBreakdownGroups(breakdownGroups);
                            List<FeedbackObject> feedbacks = new ArrayList<FeedbackObject>();
                            FeedbackObject feedback = new FeedbackObject();
                            feedback.FeedbackType="BG";
                            feedback.BreakdownId = breakdownGroups.get(0).GetParentBreakdownId();
                            //feedback.StatusId = String.valueOf(breakdown.get_Status());
                            //feedback.StatusTime = breakdown.get_Received_Time();
                            feedback.UserId=Globals.mToken.user_id;
                            feedback.AreaId= Globals.mToken.area_id;
                            feedback.EcscId=Globals.mToken.team_id;
                            feedbacks.add(feedback);

                            PostFeedbackNew(feedbacks);
                            if(mediaPlayer==null){
                                mediaPlayer = MediaPlayer.create(context, R.raw.iphone);
                                mediaPlayer.setVolume(1.0f , 1.0f);
                                mediaPlayer.start();
                            }else if(!mediaPlayer.isPlaying()){
                                mediaPlayer = MediaPlayer.create(context, R.raw.iphone);
                                mediaPlayer.setVolume(1.0f , 1.0f);
                                mediaPlayer.start();
                            }
                            //Informing the Map view about the new bd, then it can add it
                            Intent intent=new Intent();
                            intent.setAction("lk.steps.breakdownassistpluss.NewBreakdownBroadcast");
                            intent.putExtra("group_breakdowns","group_breakdowns");
                            context.sendBroadcast(intent);

                            CreateNotification(context,"Breakdown group created.");
                        }
                    } else if (response.errorBody() != null) {
                        Globals.serverConnected = false;
                        if(response.code() == 401) { //Authentication fail
                            Toast.makeText(context, "Authentication fail..", Toast.LENGTH_SHORT).show();
                            MainActivity.ReLoginRequired=true;
                        }else{
                            Toast.makeText(context, "GetBreakdownGroups\nResponse code ="+response.code(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("GetBreakdownGroups","onResponse" + response.errorBody()+"*code*"+response.code());
                    }
                    syncRESTService.CloseAllConnections();
                }

                @Override
                public void onFailure(Call<List<BreakdownGroup>> call, Throwable t) {
                    Globals.serverConnected = false;
                    Toast.makeText(context, "Error in network..\n"+ t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("GetBreakdownGroups","5-" + t.getMessage());
                    syncRESTService.CloseAllConnections();
                }
            });
        }catch(Exception e){
            Globals.serverConnected = false;
            Log.e("GetBreakdownGroups","" + e.getMessage());
        }
    }

    public static void GetBreakdownsStatusChange(final Context context, String breakdownId){
        try{
            final SyncRESTService syncRESTService = new SyncRESTService(10);
            Call<List<JobChangeStatus>> call = syncRESTService.getService()
                    .GetBreakdownsStatus( "Bearer "+ Globals.mToken.access_token,breakdownId);

            call.enqueue(new Callback<List<JobChangeStatus>>(){
                @Override
                public void onResponse(Call<List<JobChangeStatus>> call, Response<List<JobChangeStatus>> response) {
                    if (response.isSuccessful()) {
                        Globals.serverConnected = true;
                        Log.e("GetBreakdownsStatus","Successful"+new Gson().toJson(response));
                        List<JobChangeStatus> jobChangeStatus = response.body();
                        Log.e("GetBreakdownsStatus","Number-"+jobChangeStatus.size());
                        if(jobChangeStatus.size()>0){
                            List<FeedbackObject> feedbacks = new ArrayList<FeedbackObject>();
                            long result = 0;
                            for (JobChangeStatus jobStatus :jobChangeStatus) {
                                FeedbackObject feedback = new FeedbackObject();
                                feedback.FeedbackType="BS";
                                feedback.BreakdownId = jobStatus.job_no;
                                feedback.StatusId = jobStatus.status;
                                feedback.StatusTime = jobStatus.change_datetime;
                                feedback.UserId=Globals.mToken.user_id;
                                feedback.AreaId= Globals.mToken.area_id;
                                feedback.EcscId=Globals.mToken.team_id;
                                feedbacks.add(feedback);
                                /*Log.e("GetBreakdownsStatus","JOB_NO-"+jobStatus.job_no);
                                Log.e("GetBreakdownsStatus","change_datetime-"+jobStatus.change_datetime);
                                Log.e("GetBreakdownsStatus","STATUS-"+jobStatus.STATUS);
                                Log.e("GetBreakdownsStatus","type_failure-"+jobStatus.type_failure);
                                Log.e("GetBreakdownsStatus","cause-"+jobStatus.cause);
                                Log.e("GetBreakdownsStatus","detail_reason_code-"+jobStatus.detail_reason_code);*/
                                jobStatus.device_timestamp=jobStatus.change_datetime;
                                jobStatus.synchro_mobile_db=1;//No need to send to server

                                if(jobStatus.status.equals(String.valueOf(Breakdown.JOB_VISITED))){//Visited
                                    Log.e("GetBreakdownsStatus","Visited:"+jobStatus.job_no);
                                    //jobStatus.STATUS="V";
                                    result=Globals.dbHandler.addJobStatusChangeRec(jobStatus);
                                    Globals.dbHandler.UpdateBreakdownStatusByJobNo(jobStatus.job_no,"", jobStatus.status);

                                }else if(jobStatus.status.equals(String.valueOf(Breakdown.JOB_ATTENDING))){//Attending
                                    Log.e("GetBreakdownsStatus","Attending:"+jobStatus.job_no);
                                   // jobStatus.STATUS="A";
                                    result=Globals.dbHandler.addJobStatusChangeRec(jobStatus);
                                    Globals.dbHandler.UpdateBreakdownStatusByJobNo(jobStatus.job_no,"", jobStatus.status);

                                }else if(jobStatus.status.equals(String.valueOf(Breakdown.JOB_TEMPORARY_COMPLETED))){//Done
                                    Log.e("GetBreakdownsStatus","Done:"+jobStatus.job_no);
                                   // jobStatus.STATUS="D";
                                    result=Globals.dbHandler.addJobStatusChangeRec(jobStatus);
                                    Globals.dbHandler.UpdateBreakdownStatusByJobNo(jobStatus.job_no,"", jobStatus.status);

                                }else if(jobStatus.status.equals(String.valueOf(Breakdown.JOB_COMPLETED))){//Completed
                                    Log.e("GetBreakdownsStatus","Completed:"+jobStatus.job_no);
                                   // jobStatus.STATUS="C";
                                    JobCompletion jobCompletionRec = new JobCompletion();
                                    jobCompletionRec.JOB_NO = jobStatus.job_no;
                                    jobCompletionRec.job_completed_datetime = jobStatus.change_datetime;
                                    jobCompletionRec.type_failure = jobStatus.type_failure;
                                    jobCompletionRec.cause = jobStatus.cause;
                                    jobCompletionRec.detail_reason_code = jobStatus.detail_reason_code;

                                    result=Globals.dbHandler.addJobCompletionRec(jobCompletionRec);
                                    Globals.dbHandler.UpdateBreakdownStatusByJobNo(jobStatus.job_no,jobStatus.change_datetime, jobStatus.status);
                                }else if(jobStatus.status.equals(String.valueOf(Breakdown.JOB_REJECT)) |//reject
                                        jobStatus.status.equals(String.valueOf(Breakdown.JOB_WITHDRAWN))){
                                    Log.e("GetBreakdownsStatus","reject:"+jobStatus.job_no);
                                   // jobStatus.STATUS="R";
                                    result=Globals.dbHandler.addJobStatusChangeRec(jobStatus);
                                    Globals.dbHandler.UpdateBreakdownStatusByJobNo(jobStatus.job_no,jobStatus.change_datetime, jobStatus.status);
                                }
                                Log.e("GetBreakdownsStatus","jobStatus.STATUS:"+jobStatus.status);
                            }

                            PostFeedbackNew(feedbacks);

                            if(result > 0){
                                if(mediaPlayer==null){
                                    mediaPlayer = MediaPlayer.create(context, R.raw.iphone);
                                    mediaPlayer.setVolume(1.0f , 1.0f);
                                    mediaPlayer.start();
                                }else if(!mediaPlayer.isPlaying()){
                                    mediaPlayer = MediaPlayer.create(context, R.raw.iphone);
                                    mediaPlayer.setVolume(1.0f , 1.0f);
                                    mediaPlayer.start();
                                }

                                //Informing the Map view about the new bd, then it can add it
                                Intent intent=new Intent();
                                intent.setAction("lk.steps.breakdownassistpluss.NewBreakdownBroadcast");
                                intent.putExtra("job_status_changed","refresh_list");
                                intent.putExtra("updated_breakdowns",new Gson().toJson(jobChangeStatus));
                                context.sendBroadcast(intent);

                                if(jobChangeStatus.size() == 1){
                                    CreateNotification(context,"One breakdown has updated.");
                                }else{
                                    CreateNotification(context,jobChangeStatus.size()+" breakdowns has updated.");
                                }
                            }
                        }
                    } else if (response.errorBody() != null) {
                        if(response.code() == 401) { //Authentication fail
                            Toast.makeText(context, "Authentication fail..", Toast.LENGTH_SHORT).show();
                            MainActivity.ReLoginRequired=true;
                        }else{
                            Toast.makeText(context, "GetNewBreakdowns\nResponse code ="+response.code(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("GetNewBreakdowns","onResponse" + response.errorBody()+"*code*"+response.code());
                    }
                    syncRESTService.CloseAllConnections();
                }

                @Override
                public void onFailure(Call<List<JobChangeStatus>> call, Throwable t) {
                    Globals.serverConnected = false;
                    Toast.makeText(context, "Error in network..\n"+ t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("GetNewBreakdowns","5-" + t.getMessage());
                    syncRESTService.CloseAllConnections();
                }
            });

        }catch(Exception e){
            Globals.serverConnected = false;
            Log.e("GetNewBreakdowns","" + e.getMessage());
            Log.e("SignalRTEST","6");
        }
    }

    /*
    private static void PostFeedback(String type, List<String> data){

        data.add(0,type);
        data.add(1,Globals.mToken.user_id);
        data.add(2,Globals.mToken.area_id);
        data.add(3,Globals.mToken.team_id);

        final SyncRESTService syncRESTService = new SyncRESTService(10);
        Call<String> call = syncRESTService.getService()
                .PostFeedback( "Bearer "+ Globals.mToken.access_token, data);

        call.enqueue(new Callback<String>(){

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Globals.serverConnected = true;

                } else if (response.errorBody() != null) {
                    Globals.serverConnected = false;

                }
                syncRESTService.CloseAllConnections();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Globals.serverConnected = false;
                syncRESTService.CloseAllConnections();
            }
        });

    }
*/
    private static void PostFeedbackNew(List<FeedbackObject> data){

        final SyncRESTService syncRESTService = new SyncRESTService(10);
        Call<FeedbackObject> call = syncRESTService.getService()
                .PostFeedback( "Bearer "+ Globals.mToken.access_token, data);

        call.enqueue(new Callback<FeedbackObject>(){

            @Override
            public void onResponse(Call<FeedbackObject> call, Response<FeedbackObject> response) {
                if (response.isSuccessful()) {
                    Globals.serverConnected = true;

                } else if (response.errorBody() != null) {
                    Globals.serverConnected = false;

                }
                syncRESTService.CloseAllConnections();
            }

            @Override
            public void onFailure(Call<FeedbackObject> call, Throwable t) {
                Globals.serverConnected = false;
                syncRESTService.CloseAllConnections();
            }
        });

    }
    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
                Log.e("SignalR ", "*NOT*Connected*");
                if(!ReadBooleanPreferences("server",false))SelectorActivity.GetIpAddress();
                startSignalR();
        }
    }

    private static void CreateNotification(Context context, String msg){
        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("Breakdown Assist+")
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        //MediaPlayer mp= MediaPlayer.create(getBaseContext(), R.raw.fb_sound);
        //mp.start();
        manager.notify(73195, builder.build());

        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if(!isScreenOn)
        {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.ON_AFTER_RELEASE,"MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
            wl_cpu.acquire(10000);
        }
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                    if(mediaPlayer!=null) {
                        if(mediaPlayer.isPlaying())mediaPlayer.stop();
                        //Globals.mediaPlayer.pause();
                       // Toast.makeText(getApplicationContext(), "003", Toast.LENGTH_SHORT).show();

                       // Toast.makeText(getApplicationContext(), "004", Toast.LENGTH_SHORT).show();
                    }
        }
    };

    private boolean ReadBooleanPreferences(String key, boolean defaultValue){
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences prfs = getPreferences(Context.MODE_PRIVATE);
        return prfs.getBoolean(key, defaultValue);
    }
    private static String ReadStringPreferences(Context context, String key, String defaultValue){
        SharedPreferences prfs = context.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        return prfs.getString(key, defaultValue);
    }
    private String ReadStringPreferences(String key, String defaultValue){
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        return prfs.getString(key, defaultValue);
    }
}

