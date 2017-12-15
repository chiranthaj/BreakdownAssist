package lk.steps.breakdownassistpluss.Sync;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.GpsTracker.TrackerObject;
import lk.steps.breakdownassistpluss.JobChangeStatus;
import lk.steps.breakdownassistpluss.JobCompletion;
import lk.steps.breakdownassistpluss.MainActivity;
import lk.steps.breakdownassistpluss.R;
import lk.steps.breakdownassistpluss.SelectorActivity;
import lk.steps.breakdownassistpluss.StartUpTasks;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Chirantha on 18/06/2017.
 */

public class SyncService extends Service {
    SyncRESTService syncRESTService;
   // Timer timer;

    //MyTimerTask myTimerTask;
    String area_id ;
    String team_id ;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Sync","onCreate");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        syncRESTService = new SyncRESTService(10);
        //Toast.makeText(this, "Sync Service Started", Toast.LENGTH_SHORT).show();
        area_id = ReadStringPreferences(getApplicationContext(),"area_id","");
        team_id = ReadStringPreferences(getApplicationContext(), "team_id","");
      //  timer = new Timer();
      //  myTimerTask = new MyTimerTask();

        //delay 1000ms, repeat in 5000ms
       // timer.schedule(myTimerTask, 1000, 20000);
        //GetAuthToken();
        Log.e("Sync","onStartCommand");
        StartSync();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Sync","*onDestroy*");
        Toast.makeText(this, "Sync Service Destroyed", Toast.LENGTH_SHORT).show();
    }

    /*@Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartService = new Intent(getApplicationContext(), this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +100, restartServicePI);
    }*/

    public static void PostBreakdownStatusChange(final Context context){
        List<JobChangeStatus> JobChangeStatusList = Globals.dbHandler.getBreakdownStatusChange();
        Log.e("Sync","PostBreakdownStatusChange->"+JobChangeStatusList.size());
        if(JobChangeStatusList.size()<1){
            return;
        }
        for (final JobChangeStatus obj: JobChangeStatusList)
        {
            Log.e("StatusChange","*"+ obj.job_no+","+obj.change_datetime+", "+ obj.status);
            SyncObject syncObject = new SyncObject();

            Breakdown breakdown= Globals.dbHandler.ReadBreakdown_by_JonNo(obj.job_no);

            syncObject.StatusId=obj.status;
            syncObject.AreaId=obj.AreaId;
            syncObject.EcscId=obj.EcscId;
            syncObject.TeamId= Globals.mToken.team_id;
            syncObject.BreakdownId = Globals.dbHandler.GetNewJobNumber(obj.job_no);
            syncObject.StatusTime = obj.change_datetime;
            syncObject.Note=obj.comment;
            syncObject.UserId = Globals.mToken.user_id;
            syncObject.Sin=breakdown.get_SUB();
            //TODO : if program crashes then this particular record may not be updated, hence use another task or change the
            //query to update state==0 OR (update_state==-1 AND currentTimestamp-update_timestamp>2min
            Globals.dbHandler.UpdateSyncState_JobStatusChangeObj(obj, -1);//Uploading Started

            final SyncRESTService syncRESTService = new SyncRESTService(10);
            Call<SyncObject> call = syncRESTService.getService()
                    .UpdateBreakdownStatus( "Bearer "+ Globals.mToken.access_token, syncObject);

            call.enqueue(new Callback<SyncObject>(){
                @Override
                public void onResponse(Call<SyncObject> call, Response<SyncObject> response) {
                    if (response.isSuccessful()) {
                        Globals.serverConnected = true;
                        //Log.e("SyncStatusChange","successful1");
                        if(Globals.dbHandler.UpdateSyncState_JobStatusChangeObj(obj,1)==1){//Successfully done
                            Log.e("SyncStatusChange","successful");
                        }
                    } else if (response.errorBody() != null) {
                        Globals.serverConnected = false;
                        Toast.makeText(context,"SyncBreakdownStatus-Error\n"+response.errorBody(), Toast.LENGTH_SHORT).show();
                        Log.e("SyncStatusChange","onResponse"+response.errorBody());
                        Globals.dbHandler.UpdateSyncState_JobStatusChangeObj(obj, 0); //ToDo : Change this for each reason
                        /*if (error.getResponse().getStatus()==409){
                            Log.e("SyncStatusChange","409");
                            dbHandler.UpdateSyncState_JobStatusChangeObj(obj, 1); //Already record is there, may be due to timeout
                        }else if (error.getResponse().getStatus()==401){
                            Log.e("SyncStatusChange","Unauthorized");
                            dbHandler.UpdateSyncState_JobStatusChangeObj(obj, 0); //Not Uploaded due to Unauthorized, need to refresh jwt
                        }else {
                            Log.e("SyncStatusChange","already synced");
                            dbHandler.UpdateSyncState_JobStatusChangeObj(obj, -5); //To avoid retry again and again
                        }*/
                        if(response.code() == 401) { //Authentication fail
                            Toast.makeText(context, "Authentication fail..", Toast.LENGTH_SHORT).show();
                            MainActivity.ReLoginRequired=true;
                        }else{
                            Toast.makeText(context, "SyncStatusChange\nResponse code ="+response.code(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("SyncStatusChange","onResponse" + response.errorBody()+"*code*"+response.code());
                    }
                    syncRESTService.CloseAllConnections();
                }

                @Override
                public void onFailure(Call<SyncObject> call, Throwable t) {
                    Globals.serverConnected = false;
                    Toast.makeText(context,"SyncBreakdownStatus-Strings\n"+t, Toast.LENGTH_SHORT).show();
                    Log.e("SyncStatusChange","onFailure "+t);
                    Globals.dbHandler.UpdateSyncState_JobStatusChangeObj(obj, 0);//Not Uploaded due to no network
                    syncRESTService.CloseAllConnections();
                }
            });
        }
    }

    public static void PostBreakdowns(final Context context){
        List<Breakdown> breakdowns = Globals.dbHandler.getNewBreakdowns();
        if(breakdowns == null)return;
        Log.e("Sync","PostBreakdowns->"+breakdowns.size());
        if(breakdowns.size()<1){
            return;
        }

        final String area_id = ReadStringPreferences(context, "area_id","");
        final String team_id = ReadStringPreferences(context, "team_id","");
        final String user_id = ReadStringPreferences(context, "user_id","");

        if(area_id == null | team_id == null | user_id == null) return;
        if(area_id.isEmpty() | team_id.isEmpty() | user_id.isEmpty()) return;

        for (final Breakdown breakdown: breakdowns)
        {
            if(breakdown.get_Name()==null | breakdown.get_ADDRESS()==null) continue;
            if(breakdown.get_Name().isEmpty() | breakdown.get_ADDRESS().isEmpty()) continue;

            breakdown.set_ECSC(team_id);
            breakdown.set_Area(area_id);
            breakdown.set_TeamId(team_id);
            breakdown.set_UserId(user_id);

            Globals.dbHandler.UpdateSyncState_NewBreakdown(breakdown, -1);//Uploading Started
            final SyncRESTService syncRESTService = new SyncRESTService(10);
            Call<String> call = syncRESTService.getService()
                    .CreateBreakdown( "Bearer "+ Globals.mToken.access_token, breakdown);

            call.enqueue(new Callback<String>(){

                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        Globals.serverConnected = true;
                        //Log.e("PostBreakdowns","Successfull"+new Gson().toJson(response));
                        //Log.e("PostBreakdowns","Successfull"+response.body());
                        Log.e("PostBreakdowns","Successfull");
                       // dbHandler.UpdateSyncState_NewBreakdown(breakdown,1);//Successfully done
                        Globals.dbHandler.UpdateNewJobNumber(breakdown.get_Job_No(),response.body());//Successfully done

                        Intent myintent=new Intent();
                        myintent.setAction("lk.steps.breakdownassistpluss.NewBreakdownBroadcast");
                        myintent.putExtra("job_status_changed","refresh_list");
                        context.sendBroadcast(myintent);

                    } else if (response.errorBody() != null) {
                        Globals.serverConnected = false;
                        Log.e("PostBreakdowns","Error"+response.errorBody());
                        Toast.makeText(context,"PostBreakdowns-Error\n"+response.errorBody(), Toast.LENGTH_SHORT).show();
                        Globals.dbHandler.UpdateSyncState_NewBreakdown(breakdown, 0);//Not Uploaded due to no network
                        /*if (error.getKind() != RetrofitError.Kind.NETWORK | error.getResponse() != null) {
                            if (error.getResponse().getStatus()==409){
                                dbHandler.UpdateSyncState_JobCompletionObj(obj, 1); //Already record is there, may be due to timeout
                            }else {
                                dbHandler.UpdateSyncState_JobCompletionObj(obj, -5); //To avoid retry again and again
                            }
                        }else {
                            dbHandler.UpdateSyncState_JobCompletionObj(obj, 0);//Not Uploaded due to no network
                        }*/
                        if(response.code() == 401) { //Authentication fail
                            Toast.makeText(context, "Authentication fail..", Toast.LENGTH_SHORT).show();
                            MainActivity.ReLoginRequired=true;
                        }else{
                            Toast.makeText(context, "PostBreakdowns\nResponse code ="+response.code(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("PostBreakdowns","onResponse" + response.errorBody()+"*code*"+response.code());
                    }
                    syncRESTService.CloseAllConnections();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Globals.serverConnected = false;
                    Globals.dbHandler.UpdateSyncState_NewBreakdown(breakdown, 0);//Not Uploaded due to no network
                    Toast.makeText(context,"PostBreakdowns-Strings\n"+t, Toast.LENGTH_SHORT).show();
                    Log.e("PostBreakdowns","onResponse" + t);
                    syncRESTService.CloseAllConnections();
                }
            });
        }
    }

    public static void PostBreakdownCompletion(final Context context){
        List<JobCompletion> JobCompletionList = Globals.dbHandler.getBreakdownCompletion();
       // final String area_id = ReadStringPreferences(context, "area_id","");
       // final String team_id = ReadStringPreferences(context, "team_id","");
        Log.e("Sync","PostBreakdownCompletion->"+JobCompletionList.size());
        if(JobCompletionList.size()<1){
            return;
        }
        for (final JobCompletion obj: JobCompletionList)
        {
            Breakdown breakdown= Globals.dbHandler.ReadBreakdown_by_JonNo(obj.JOB_NO);

            SyncObject syncObject = new SyncObject();
            syncObject.BreakdownId=obj.JOB_NO;
            syncObject.StatusId=String.valueOf(Breakdown.JOB_COMPLETED);
            syncObject.StatusTime=obj.job_completed_datetime;
            syncObject.FailureTypeId=obj.type_failure;
            syncObject.FailureNatureId=obj.detail_reason_code;
            syncObject.FailureCauseId=obj.cause;
            syncObject.UserId= Globals.mToken.user_id;
            syncObject.AreaId=obj.AreaId;
            syncObject.EcscId=obj.EcscId;
            if(breakdown!=null){
                syncObject.Sin=breakdown.get_SUB();
            }


            Globals.dbHandler.UpdateSyncState_JobCompletionObj(obj, -1);//Uploading Started
            final SyncRESTService syncRESTService = new SyncRESTService(10);
            Call<SyncObject> call = syncRESTService.getService()
                    .UpdateBreakdownStatus( "Bearer "+ Globals.mToken.access_token, syncObject);

            call.enqueue(new Callback<SyncObject>(){

                @Override
                public void onResponse(Call<SyncObject> call, Response<SyncObject> response) {
                    if (response.isSuccessful()) {
                        Globals.serverConnected = true;
                        Log.e("PostBreakdownCompletion","Successfull");
                        Globals.dbHandler.UpdateSyncState_JobCompletionObj(obj,1);//Successfully done
                    } else if (response.errorBody() != null) {
                        Globals.serverConnected = false;
                        Log.e("PostBreakdownCompletion","Error"+response.errorBody());
                        Toast.makeText(context,"PostBreakdownCompletion-Error\n"+response.errorBody(), Toast.LENGTH_SHORT).show();
                        Globals.dbHandler.UpdateSyncState_JobCompletionObj(obj, 0);//Not Uploaded due to no network
                        /*if (error.getKind() != RetrofitError.Kind.NETWORK | error.getResponse() != null) {
                            if (error.getResponse().getStatus()==409){
                                dbHandler.UpdateSyncState_JobCompletionObj(obj, 1); //Already record is there, may be due to timeout
                            }else {
                                dbHandler.UpdateSyncState_JobCompletionObj(obj, -5); //To avoid retry again and again
                            }
                        }else {
                            dbHandler.UpdateSyncState_JobCompletionObj(obj, 0);//Not Uploaded due to no network
                        }*/
                        if(response.code() == 401) { //Authentication fail
                            Toast.makeText(context, "Authentication fail..", Toast.LENGTH_SHORT).show();
                            MainActivity.ReLoginRequired=true;
                        }else{
                            Toast.makeText(context, "PostBreakdownCompletion\nResponse code ="+response.code(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("PostBreakdownCompletion","onResponse" + response.errorBody()+"*code*"+response.code());
                    }
                    syncRESTService.CloseAllConnections();
                }

                @Override
                public void onFailure(Call<SyncObject> call, Throwable t) {
                    Globals.serverConnected = false;
                    Globals.dbHandler.UpdateSyncState_JobCompletionObj(obj, 0);//Not Uploaded due to no network
                    Toast.makeText(context,"PostBreakdownCompletion-Strings\n"+t, Toast.LENGTH_SHORT).show();
                    syncRESTService.CloseAllConnections();
                }
            });
        }
    }


    public static void PostMaterials(final Context context){
        final List<SyncMaterialObject> list = Globals.dbHandler.getNotSyncMaterials();
        Log.e("Sync","PostMaterials->"+list.size());
        if(list.size()< 1){

            return;
        }

        final SyncRESTService syncRESTService = new SyncRESTService(10);
        Call<SyncMaterialObject> call = syncRESTService.getService()
                .PushMaterials( "Bearer "+ Globals.mToken.access_token, list);

        call.enqueue(new Callback<SyncMaterialObject>(){

            @Override
            public void onResponse(Call<SyncMaterialObject> call, Response<SyncMaterialObject> response) {
                if (response.isSuccessful()) {
                    Globals.serverConnected = true;
                    Log.e("PostMaterials","Successfull");
                    for (final SyncMaterialObject obj: list) {
                        Globals.dbHandler.UpdateMaterials(obj);//Successfully done
                    }
                } else if (response.errorBody() != null) {
                    Globals.serverConnected = false;
                    Log.e("PostMaterials","Error"+response.errorBody());
                    Toast.makeText(context,"PostMaterials-Error\n"+response.errorBody(), Toast.LENGTH_SHORT).show();
                    //dbHandler.UpdateSyncState_JobCompletionObj(obj, 0);//Not Uploaded due to no network
                    if(response.code() == 401) { //Authentication fail
                        Toast.makeText(context, "Authentication fail..", Toast.LENGTH_SHORT).show();
                        MainActivity.ReLoginRequired=true;
                    }else{
                        Toast.makeText(context, "PostMaterials\nResponse code ="+response.code(), Toast.LENGTH_SHORT).show();
                    }
                    Log.e("PostMaterials","onResponse" + response.errorBody()+"*code*"+response.code());
                }
                syncRESTService.CloseAllConnections();
            }

            @Override
            public void onFailure(Call<SyncMaterialObject> call, Throwable t) {
                Globals.serverConnected = false;
                // dbHandler.UpdateSyncState_JobCompletionObj(obj, 0);//Not Uploaded due to no network
                Toast.makeText(context,"PostTrackingData-Strings\n"+t, Toast.LENGTH_SHORT).show();
                syncRESTService.CloseAllConnections();
            }
        });

    }

    private static void PostTrackingData(final Context context){
        final List<TrackerObject> list = Globals.dbHandler.getNotSyncTrackingData();
        Log.e("Sync","PostTrackingData->"+list.size());
        if(list.size()< 1){

            return;
        }

        final SyncRESTService syncRESTService = new SyncRESTService(10);
        Call<TrackerObject> call = syncRESTService.getService()
                .PushTrackingData( "Bearer "+ Globals.mToken.access_token, list);

        call.enqueue(new Callback<TrackerObject>(){

            @Override
            public void onResponse(Call<TrackerObject> call, Response<TrackerObject> response) {
                if (response.isSuccessful()) {
                    Globals.serverConnected = true;
                    Log.e("PostTrackingData","Successfull");
                    for (final TrackerObject obj: list) {
                        Globals.dbHandler.UpdateTrackingData(obj);//Successfully done
                    }
                } else if (response.errorBody() != null) {
                    Globals.serverConnected = false;
                    Log.e("PostTrackingData","Error"+response.errorBody());
                    Toast.makeText(context,"PostTrackingData-Error\n"+response.errorBody(), Toast.LENGTH_SHORT).show();
                    //dbHandler.UpdateSyncState_JobCompletionObj(obj, 0);//Not Uploaded due to no network
                    if(response.code() == 401) { //Authentication fail
                        Toast.makeText(context, "Authentication fail..", Toast.LENGTH_SHORT).show();
                        MainActivity.ReLoginRequired=true;
                    }else{
                        Toast.makeText(context, "PostTrackingData\nResponse code ="+response.code(), Toast.LENGTH_SHORT).show();
                    }
                    Log.e("PostTrackingData","onResponse" + response.errorBody()+"*code*"+response.code());
                }
                syncRESTService.CloseAllConnections();
            }

            @Override
            public void onFailure(Call<TrackerObject> call, Throwable t) {
                Globals.serverConnected = false;
               // dbHandler.UpdateSyncState_JobCompletionObj(obj, 0);//Not Uploaded due to no network
                Toast.makeText(context,"PostTrackingData-Strings\n"+t, Toast.LENGTH_SHORT).show();
                syncRESTService.CloseAllConnections();
            }
        });

    }

    public static void PostGroups(final Context context){
        final List<BreakdownGroup> list = Globals.dbHandler.getNotSyncGroups();
        Log.e("Sync","PostGroups->"+list.size());
        if(list.size()< 1){
            return;
        }

        final SyncRESTService syncRESTService = new SyncRESTService(10);
        Call<BreakdownGroup> call = syncRESTService.getService()
                .PostGroups( "Bearer "+ Globals.mToken.access_token, list);

        call.enqueue(new Callback<BreakdownGroup>(){

            @Override
            public void onResponse(Call<BreakdownGroup> call, Response<BreakdownGroup> response) {
                if (response.isSuccessful()) {
                    Globals.serverConnected = true;
                    Log.e("PostGroups","Successfull");
                    for (final BreakdownGroup obj: list) {
                        Globals.dbHandler.UpdateGroupSynced(obj);//Successfully done
                    }
                } else if (response.errorBody() != null) {
                    Globals.serverConnected = false;
                    Log.e("PostGroups","Error"+response.errorBody());
                    Toast.makeText(context,"PostGroups-Error\n"+response.errorBody(), Toast.LENGTH_SHORT).show();
                    //dbHandler.UpdateSyncState_JobCompletionObj(obj, 0);//Not Uploaded due to no network
                    if(response.code() == 401) { //Authentication fail
                        Toast.makeText(context, "Authentication fail..", Toast.LENGTH_SHORT).show();
                        MainActivity.ReLoginRequired=true;
                    }else{
                        Toast.makeText(context, "PostGroups\nResponse code ="+response.code(), Toast.LENGTH_SHORT).show();
                    }
                    Log.e("PostGroups","onResponse" + response.errorBody()+"*code*"+response.code());
                }
                syncRESTService.CloseAllConnections();
            }

            @Override
            public void onFailure(Call<BreakdownGroup> call, Throwable t) {
                Globals.serverConnected = false;
                // dbHandler.UpdateSyncState_JobCompletionObj(obj, 0);//Not Uploaded due to no network
                Toast.makeText(context,"PostGroups-Strings\n"+t, Toast.LENGTH_SHORT).show();
                syncRESTService.CloseAllConnections();
            }
        });
    }


    public void DownloadApk() {

        final SyncRESTService syncRESTService = new SyncRESTService(10);
        Call<ResponseBody> call = syncRESTService.getService()
                .GetApk( "Bearer "+ Globals.mToken.access_token, Globals.VERSION_CODE);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("DownloadApk", "Got the body for the file");

                    new AsyncTask<Void, Long, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            saveApk(response.body());
                            return null;
                        }
                    }.execute();

                } else {
                    Log.d("DownloadApk", "Connection failed " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.e("DownloadApk", "-"+t.getMessage());
            }
        });
    }

    public void saveApk(ResponseBody body) {
        try {
            String destinationPath = Environment.getExternalStorageDirectory()+"/apk";
            new File(destinationPath).mkdir();
          //  File destinationFile = new File("/data/data/" + getPackageName() + "/games/gameplay3d.zip");
            File destinationFile = new File(destinationPath+"/app-release.apk");
            if(destinationFile.exists())
                destinationFile.delete();

            Log.d("saveApk", "destinationPath=" +destinationPath);
            InputStream is = null;
            OutputStream os = null;

            try {
                Log.d("saveApk", "File Size=" + body.contentLength());

                is = body.byteStream();
                os = new FileOutputStream(destinationFile);

                byte data[] = new byte[4096];
                int count;
                int progress = 0;
                while ((count = is.read(data)) != -1) {
                    os.write(data, 0, count);
                    progress +=count;
                    Log.d("saveApk", "Progress: " + progress + "/" + body.contentLength() + " >>>> " + (float) progress/body.contentLength());
                }

                os.flush();

                Log.d("saveApk", "File saved successfully!");
                InstallApk(destinationFile);

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("saveApk", "Failed to save the file!");

            } finally {
                if (is != null) is.close();
                if (os != null) os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("saveApk", "Failed to save the file!");

        }
    }

    private void InstallApk(File apkFile){
        Intent myintent=new Intent();
        myintent.setAction("lk.steps.breakdownassistpluss.NewBreakdownBroadcast");
        myintent.putExtra("finish_app_req","finish_app_req");
        getApplicationContext().sendBroadcast(myintent);


        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void CreateNotification(String msg){
        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Breakdown Assist+")
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //MediaPlayer mp= MediaPlayer.create(getBaseContext(), R.raw.fb_sound);
        //mp.start();
        manager.notify(73195, builder.build());

        PowerManager pm = (PowerManager)getBaseContext().getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if(!isScreenOn)
        {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.ON_AFTER_RELEASE,"MyLock");
            wl.acquire(20000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
            wl_cpu.acquire(10000);
        }
    }
    private static String ReadStringPreferences(Context context, String key, String defaultValue){
        SharedPreferences prfs = context.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        return prfs.getString(key, defaultValue);
    }

    int i = 0;
    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {

            if(Globals.mToken == null | !Globals.serverConnected){
                RemoteLoginWithLastCredentials();
                StartUpTasks.InitVariables(getApplicationContext());
                return;
            }

            if(i==0){
                i=1;
                //Log.e("Sync","PostBreakdowns");
                PostBreakdowns(getApplicationContext());
            }else if(i==1){
                i=2;
                //Log.e("Sync","PostBreakdownCompletion");
                PostBreakdownCompletion(getApplicationContext());
            }else if(i==2){
                i=3;
                //Log.e("Sync","PostGroups");
                PostGroups(getApplicationContext());
            }else if(i==3){
                i=4;
                //Log.e("Sync","PostBreakdownStatusChange");
                PostBreakdownStatusChange(getApplicationContext());
            }else if(i==4){
                i=5;
                //Log.e("Sync","PostMaterials");
                PostMaterials(getApplicationContext());
            }else if(i==5){
                i=0;
                //Log.e("Sync","PostTrackingData");
                PostTrackingData(getApplicationContext());
            }
            /*else if(i==6){
                i=0;
                Log.e("Sync","DownloadApk");
                DownloadApk();
            }*/
        }
    }


    private void StartSync(){
        if(Globals.mToken == null | !Globals.serverConnected){
            RemoteLoginWithLastCredentials();
            StartUpTasks.InitVariables(getApplicationContext());
            return;
        }
        PostBreakdowns(getApplicationContext());
        PostBreakdownCompletion(getApplicationContext());
        PostBreakdownStatusChange(getApplicationContext());
        PostGroups(getApplicationContext());
        PostMaterials(getApplicationContext());
        PostTrackingData(getApplicationContext());
    }

    public void RemoteLoginWithLastCredentials(){
        final String lastUsername = ReadStringPreferences("last_username", "");
        final String lastPassword = ReadStringPreferences("last_password", "");
        final SyncRESTService syncAuthService = new SyncRESTService(2);
        Call<Token> call = syncAuthService.getService().GetJwt(lastUsername,lastPassword);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    Log.e("GetAuthToken","Authorized");

                    Token token = response.body();
                    Log.e("area_name",token.area_name);
                    //SaveToken(token);
                    WriteStringPreferences("user_id",token.user_id);
                    WriteStringPreferences("area_id",token.area_id);
                    WriteStringPreferences("area_name",token.area_name);
                    WriteStringPreferences("team_id",token.team_id);
                    WriteLongPreferences("expires_in",token.expires_in);
                    WriteStringPreferences("access_token",token.access_token);
                    WriteStringPreferences("group_token",token.group_token);
                    WriteStringPreferences("last_username",lastUsername);
                    WriteStringPreferences("last_password",lastPassword);
                    Globals.mToken = token;
                    Globals.serverConnected = true;

                } else if (response.errorBody() != null) {
                    Globals.serverConnected = false;
                }
                syncAuthService.CloseAllConnections();
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.e("Login","Remote login onFailure"+t.getMessage());//Remote login
                syncAuthService.CloseAllConnections();
            }

        });
    }

    private void WriteLongPreferences(String key, long value){
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prfs.edit();
        editor.putLong(key,value).apply();
    }

    private void WriteStringPreferences(String key, String value){
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prfs.edit();
        editor.putString(key,value).apply();
    }

    private String ReadStringPreferences(String key, String defaultValue){
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        return prfs.getString(key, defaultValue);
    }
}
