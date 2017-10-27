package lk.steps.breakdownassistpluss.Sync;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Chirantha on 18/06/2017.
 */

public class SyncService extends Service {
    SyncRESTService syncRESTService;
    Timer timer;
    MyTimerTask myTimerTask;
    String area_id ;
    String team_id ;
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
        timer = new Timer();
        myTimerTask = new MyTimerTask();

        //delay 1000ms, repeat in 5000ms
        timer.schedule(myTimerTask, 1000, 10000);
        //GetAuthToken();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }

    public static void PostBreakdownStatusChange(final Context context){
        List<JobChangeStatus> JobChangeStatusList = Globals.dbHandler.getBreakdownStatusChange();
        if(JobChangeStatusList.size()<1)return;
        for (final JobChangeStatus obj: JobChangeStatusList)
        {
            Log.e("StatusChange","*"+ obj.job_no+","+obj.change_datetime+", "+ obj.status);
            SyncObject syncObject = new SyncObject();

            Breakdown breakdown= Globals.dbHandler.ReadBreakdown_by_JonNo(obj.job_no);

            syncObject.StatusId=obj.status;
            syncObject.BreakdownId = Globals.dbHandler.GetNewJobNumber(obj.job_no);
            syncObject.StatusTime = obj.change_datetime;
            syncObject.UserId = MainActivity.mToken.user_id;
            syncObject.Sin=breakdown.get_SUB();
            //TODO : if program crashes then this particular record may not be updated, hence use another task or change the
            //query to update state==0 OR (update_state==-1 AND currentTimestamp-update_timestamp>2min
            Globals.dbHandler.UpdateSyncState_JobStatusChangeObj(obj, -1);//Uploading Started

            final SyncRESTService syncRESTService = new SyncRESTService(10);
            Call<SyncObject> call = syncRESTService.getService()
                    .UpdateBreakdownStatus( "Bearer "+ MainActivity.mToken.access_token, syncObject);

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
                    Toast.makeText(context,"SyncBreakdownStatus-Failure\n"+t, Toast.LENGTH_SHORT).show();
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
        if(breakdowns.size()<1)return;

        final String area_id = ReadStringPreferences(context, "area_id","");
        final String team_id = ReadStringPreferences(context, "team_id","");
        final String user_id = ReadStringPreferences(context, "user_id","");
        for (final Breakdown breakdown: breakdowns)
        {
            breakdown.set_ECSC(team_id);
            breakdown.set_Area(area_id);
            breakdown.set_TeamId(team_id);
            breakdown.set_UserId(user_id);

            Globals.dbHandler.UpdateSyncState_NewBreakdown(breakdown, -1);//Uploading Started
            final SyncRESTService syncRESTService = new SyncRESTService(10);
            Call<String> call = syncRESTService.getService()
                    .CreateBreakdown( "Bearer "+ MainActivity.mToken.access_token, breakdown);

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
                    Toast.makeText(context,"PostBreakdowns-Failure\n"+t, Toast.LENGTH_SHORT).show();
                    Log.e("PostBreakdowns","onResponse" + t);
                    syncRESTService.CloseAllConnections();
                }
            });


        }
    }

    public static void PostBreakdownCompletion(final Context context){
        List<JobCompletion> JobCompletionList = Globals.dbHandler.getBreakdownCompletion();
        if(JobCompletionList.size()<1)return;
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
            syncObject.UserId= MainActivity.mToken.user_id;
            syncObject.Sin=breakdown.get_SUB();
            Globals.dbHandler.UpdateSyncState_JobCompletionObj(obj, -1);//Uploading Started
            final SyncRESTService syncRESTService = new SyncRESTService(10);
            Call<SyncObject> call = syncRESTService.getService()
                    .UpdateBreakdownStatus( "Bearer "+ MainActivity.mToken.access_token, syncObject);

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
                    Toast.makeText(context,"PostBreakdownCompletion-Failure\n"+t, Toast.LENGTH_SHORT).show();
                    syncRESTService.CloseAllConnections();
                }
            });
        }
    }

    public void GetNewBreakdowns(){
        try{
            final SyncRESTService syncRESTService = new SyncRESTService(10);
            Call<List<Breakdown>> call = syncRESTService.getService()
                    .getNewBreakdowns( "Bearer "+ MainActivity.mToken.access_token,
                            MainActivity.mToken.user_id,area_id,team_id);

            call.enqueue(new Callback<List<Breakdown>>(){
                @Override
                public void onResponse(Call<List<Breakdown>> call, Response<List<Breakdown>> response) {
                    if (response.isSuccessful()) {
                        Globals.serverConnected = true;
                        //Log.e("GetNewBreakdowns","Successful");
                        List<Breakdown> breakdowns = response.body();
                        Log.e("GetNewBreakdowns","Number-"+breakdowns.size());

                        if(breakdowns.size()>0){
                            String ring = "0";
                            for (Breakdown breakdown :breakdowns) {
                                breakdown.set_BA_SERVER_SYNCED("1");
                                breakdown.set_JOB_SOURCE("BA");
                                Globals.dbHandler.addBreakdown2(breakdown);
                                Log.e("breakdown","breakdown-"+breakdown.get_Job_No());
                                if(breakdown.get_Priority() == 4) ring = "1";
                            }

                            String sIssuedBreakdownID=Globals.dbHandler.getLastBreakdownID();

                            /*if(ring.equals("1")){
                                Globals.mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.buzzer);
                                Globals.mediaPlayer.setLooping(true);
                            }else{
                                Globals.mediaPlayer= MediaPlayer.create(getApplicationContext(), R.raw.fb_sound);
                            }
                            Globals.mediaPlayer.setVolume(100,100);
                            Globals.mediaPlayer.start();*/

                            //Informing the Map view about the new bd, then it can add it
                            Intent myintent=new Intent();
                            myintent.setAction("lk.steps.breakdownassistpluss.NewBreakdownBroadcast");
                            myintent.putExtra("_id",sIssuedBreakdownID);
                            myintent.putExtra("new_breakdowns",new Gson().toJson(breakdowns));
                            //myintent.putExtra("ring",ring);
                            getApplicationContext().sendBroadcast(myintent);

                            if(breakdowns.size() == 1){
                                CreateNotification("New breakdown received.");
                            }else{
                                CreateNotification("New "+breakdowns.size()+" breakdowns received.");
                            }
                        }
                    } else if (response.errorBody() != null) {
                        Globals.serverConnected = false;
                        if(response.code() == 401) { //Authentication fail
                            Toast.makeText(getApplicationContext(), "Authentication fail..", Toast.LENGTH_SHORT).show();
                            MainActivity.ReLoginRequired=true;
                        }else{
                            Toast.makeText(getApplicationContext(), "GetNewBreakdowns\nResponse code ="+response.code(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("GetNewBreakdowns","onResponse" + response.errorBody()+"*code*"+response.code());
                    }
                    syncRESTService.CloseAllConnections();
                }

                @Override
                public void onFailure(Call<List<Breakdown>> call, Throwable t) {
                    Globals.serverConnected = false;
                    Toast.makeText(getApplicationContext(), "Error in network..\n"+ t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("GetNewBreakdowns","5-" + t.getMessage());
                    syncRESTService.CloseAllConnections();
                }
            });
        }catch(Exception e){
            Log.e("GetNewBreakdowns","" + e.getMessage());
        }
    }

    public void GetBreakdownsStatusChange(){
        try{
            final SyncRESTService syncRESTService = new SyncRESTService(10);
            Call<List<JobChangeStatus>> call = syncRESTService.getService()
                    .GetBreakdownsStatus2( "Bearer "+ MainActivity.mToken.access_token,area_id+"_"+team_id);

            call.enqueue(new Callback<List<JobChangeStatus>>(){
                @Override
                public void onResponse(Call<List<JobChangeStatus>> call, Response<List<JobChangeStatus>> response) {
                    if (response.isSuccessful()) {
                        Log.e("GetBreakdownsStatus","Successful"+new Gson().toJson(response));
                        List<JobChangeStatus> jobChangeStatus = response.body();

                        if(jobChangeStatus.size()>0){
                            Log.e("GetBreakdownsStatus","Number-"+jobChangeStatus.size());
                            for (JobChangeStatus jobStatus :jobChangeStatus) {
                                //Log.e("GetBreakdownsStatus","JOB_NO-"+jobStatus.job_no);
                                //Log.e("GetBreakdownsStatus","change_datetime-"+jobStatus.change_datetime);
                                //Log.e("GetBreakdownsStatus","status-"+jobStatus.status);
                                //Log.e("GetBreakdownsStatus","type_failure-"+jobStatus.type_failure);
                                //Log.e("GetBreakdownsStatus","cause-"+jobStatus.cause);
                                //Log.e("GetBreakdownsStatus","detail_reason_code-"+jobStatus.detail_reason_code);
                                jobStatus.device_timestamp=jobStatus.change_datetime;
                                jobStatus.synchro_mobile_db=1;//No need to send to server

                                if(jobStatus.status.equals(String.valueOf(Breakdown.JOB_VISITED))){//Visited
                                    Log.e("GetBreakdownsStatus","Visited:"+jobStatus.job_no);
                                    jobStatus.st_code="V";
                                    Globals.dbHandler.addJobStatusChangeRec(jobStatus);
                                    Globals.dbHandler.UpdateBreakdownStatusByJobNo(jobStatus.job_no,"", jobStatus.status);

                                }else if(jobStatus.status.equals(String.valueOf(Breakdown.JOB_ATTENDING))){//Attending
                                    Log.e("GetBreakdownsStatus","Attending:"+jobStatus.job_no);
                                    jobStatus.st_code="A";
                                    Globals.dbHandler.addJobStatusChangeRec(jobStatus);
                                    Globals.dbHandler.UpdateBreakdownStatusByJobNo(jobStatus.job_no,"", jobStatus.status);

                                }else if(jobStatus.status.equals(String.valueOf(Breakdown.JOB_DONE))){//Done
                                    Log.e("GetBreakdownsStatus","Done:"+jobStatus.job_no);
                                    jobStatus.st_code="D";
                                    Globals.dbHandler.addJobStatusChangeRec(jobStatus);
                                    Globals.dbHandler.UpdateBreakdownStatusByJobNo(jobStatus.job_no,"", jobStatus.status);

                                }else if(jobStatus.status.equals(String.valueOf(Breakdown.JOB_COMPLETED))){//Completed
                                    Log.e("GetBreakdownsStatus","Completed:"+jobStatus.job_no);
                                    jobStatus.st_code="C";
                                    JobCompletion jobCompletionRec = new JobCompletion();
                                    jobCompletionRec.JOB_NO = jobStatus.job_no;
                                    jobCompletionRec.job_completed_datetime = jobStatus.change_datetime;
                                    jobCompletionRec.type_failure = jobStatus.type_failure;
                                    jobCompletionRec.cause = jobStatus.cause;
                                    jobCompletionRec.detail_reason_code = jobStatus.detail_reason_code;

                                    Globals.dbHandler.addJobCompletionRec(jobCompletionRec);
                                    Globals.dbHandler.UpdateBreakdownStatusByJobNo(jobStatus.job_no,jobStatus.change_datetime, jobStatus.status);
                                }

                            }
                            /*Globals.mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.iphone);
                            Globals.mediaPlayer.setVolume(1.0f , 1.0f);
                            Globals.mediaPlayer.start();*/


                            //Informing the Map view about the new bd, then it can add it
                            Intent myintent=new Intent();
                            myintent.setAction("lk.steps.breakdownassistpluss.NewBreakdownBroadcast");
                            myintent.putExtra("job_status_changed","refresh_list");
                            // myintent.putExtra("_id",sIssuedBreakdownID);
                            //  myintent.putExtra("new_breakdowns",new Gson().toJson(breakdowns));
                            // myintent.putExtra("ring",ring);
                            getApplicationContext().sendBroadcast(myintent);

                            if(jobChangeStatus.size() == 1){
                                CreateNotification("One breakdown has updated.");
                            }else{
                                CreateNotification(jobChangeStatus.size()+" breakdowns has updated.");
                            }
                        }

                    } else if (response.errorBody() != null) {
                        if(response.code() == 401) { //Authentication fail
                            Toast.makeText(getApplicationContext(), "Authentication fail..", Toast.LENGTH_SHORT).show();
                            MainActivity.ReLoginRequired=true;
                        }else{
                            Toast.makeText(getApplicationContext(), "GetNewBreakdowns\nResponse code ="+response.code(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("GetNewBreakdowns","onResponse" + response.errorBody()+"*code*"+response.code());
                    }
                    syncRESTService.CloseAllConnections();
                }

                @Override
                public void onFailure(Call<List<JobChangeStatus>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Error in network..\n"+ t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("GetNewBreakdowns","5-" + t.getMessage());
                    syncRESTService.CloseAllConnections();
                }
            });
        }catch(Exception e){
            Log.e("GetNewBreakdowns","" + e.getMessage());
        }
    }

    public static void PostMaterials(final Context context){
        final List<SyncMaterialObject> list = Globals.dbHandler.getNotSyncMaterials();
        if(list.size()< 1)return;

        final SyncRESTService syncRESTService = new SyncRESTService(10);
        Call<SyncMaterialObject> call = syncRESTService.getService()
                .PushMaterials( "Bearer "+ MainActivity.mToken.access_token, list);

        call.enqueue(new Callback<SyncMaterialObject>(){

            @Override
            public void onResponse(Call<SyncMaterialObject> call, Response<SyncMaterialObject> response) {
                if (response.isSuccessful()) {
                    Globals.serverConnected = true;
                    Log.e("PostTrackingData","Successfull");
                    for (final SyncMaterialObject obj: list) {
                        Globals.dbHandler.UpdateMaterials(obj);//Successfully done
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
            public void onFailure(Call<SyncMaterialObject> call, Throwable t) {
                Globals.serverConnected = false;
                // dbHandler.UpdateSyncState_JobCompletionObj(obj, 0);//Not Uploaded due to no network
                Toast.makeText(context,"PostTrackingData-Failure\n"+t, Toast.LENGTH_SHORT).show();
                syncRESTService.CloseAllConnections();
            }
        });

    }

    private static void PostTrackingData(final Context context){
        final List<TrackerObject> list = Globals.dbHandler.getNotSyncTrackingData();
        if(list.size()< 1)return;

        final SyncRESTService syncRESTService = new SyncRESTService(10);
        Call<TrackerObject> call = syncRESTService.getService()
                .PushTrackingData( "Bearer "+ MainActivity.mToken.access_token, list);

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
                Toast.makeText(context,"PostTrackingData-Failure\n"+t, Toast.LENGTH_SHORT).show();
                syncRESTService.CloseAllConnections();
            }
        });

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
            if(i==0){
                i=1;
                Log.e("Sync","PostBreakdowns");
                PostBreakdowns(getApplicationContext());
            }else if(i==1){
                i=2;
                Log.e("Sync","PostBreakdownCompletion");
                PostBreakdownCompletion(getApplicationContext());
            }else if(i==2){
                i=3;
                Log.e("Sync","PostTrackingData");
                PostTrackingData(getApplicationContext());
            }else if(i==3){
                i=4;
                Log.e("Sync","PostBreakdownStatusChange");
                PostBreakdownStatusChange(getApplicationContext());
            }else if(i==4){
                i=0;
                Log.e("Sync","PostMaterials");
                PostMaterials(getApplicationContext());
            }
            /*else if(i==4){
                i=5;
                Log.e("Sync","GetNewBreakdowns");
                GetNewBreakdowns();
            }else if(i==5){
                i=0;
                Log.e("Sync","GetBreakdownsStatusChange");
                GetBreakdownsStatusChange();
            }*/
        }

    }
}
