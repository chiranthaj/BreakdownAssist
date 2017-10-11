package lk.steps.breakdownassistpluss.Sync;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.GpsTracker.TrackerObject;
import lk.steps.breakdownassistpluss.JobChangeStatus;
import lk.steps.breakdownassistpluss.JobCompletion;
import lk.steps.breakdownassistpluss.MainActivity;
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        syncRESTService = new SyncRESTService();
        //Toast.makeText(this, "Sync Service Started", Toast.LENGTH_SHORT).show();

        timer = new Timer();
        myTimerTask = new MyTimerTask();

        //delay 1000ms, repeat in 5000ms
        timer.schedule(myTimerTask, 1000, 30000);
        //GetAuthToken();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }

    public static void SyncBreakdownStatusChange(final Context context){
        List<JobChangeStatus> JobChangeStatusList = Globals.dbHandler.getBreakdownStatusChange();
        if(JobChangeStatusList.size()<1)return;
        for (final JobChangeStatus obj: JobChangeStatusList)
        {
            Log.e("StatusChange","*"+ obj.job_no+","+obj.change_datetime+", "+ obj.status);
            SyncObject syncObject = new SyncObject();

            syncObject.StatusId=obj.status;
            syncObject.BreakdownId = Globals.dbHandler.GetNewJobNumber(obj.job_no);
            syncObject.StatusTime = obj.change_datetime;
            syncObject.UserId = MainActivity.mToken.user_id;

            //TODO : if program crashes then this particular record may not be updated, hence use another task or change the
            //query to update state==0 OR (update_state==-1 AND currentTimestamp-update_timestamp>2min
            Globals.dbHandler.UpdateSyncState_JobStatusChangeObj(obj, -1);//Uploading Started

            SyncRESTService syncRESTService = new SyncRESTService();
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
                }

                @Override
                public void onFailure(Call<SyncObject> call, Throwable t) {
                    Globals.serverConnected = false;
                    Toast.makeText(context,"SyncBreakdownStatus-Failure\n"+t, Toast.LENGTH_SHORT).show();
                    Log.e("SyncStatusChange","onFailure "+t);
                    Globals.dbHandler.UpdateSyncState_JobStatusChangeObj(obj, 0);//Not Uploaded due to no network
                }
            });
        }
    }

    public static void PushBreakdowns(final Context context){
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
            SyncRESTService syncRESTService = new SyncRESTService();
            Call<String> call = syncRESTService.getService()
                    .CreateBreakdown( "Bearer "+ MainActivity.mToken.access_token, breakdown);

            call.enqueue(new Callback<String>(){

                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        Globals.serverConnected = true;
                        //Log.e("PushBreakdowns","Successfull"+new Gson().toJson(response));
                        //Log.e("PushBreakdowns","Successfull"+response.body());
                        Log.e("PushBreakdowns","Successfull");
                       // dbHandler.UpdateSyncState_NewBreakdown(breakdown,1);//Successfully done
                        Globals.dbHandler.UpdateNewJobNumber(breakdown.get_Job_No(),response.body());//Successfully done

                        Intent myintent=new Intent();
                        myintent.setAction("lk.steps.breakdownassistpluss.NewBreakdownBroadcast");
                        myintent.putExtra("job_status_changed","refresh_list");
                        context.sendBroadcast(myintent);

                    } else if (response.errorBody() != null) {
                        Globals.serverConnected = false;
                        Log.e("PushBreakdowns","Error"+response.errorBody());
                        Toast.makeText(context,"PushBreakdowns-Error\n"+response.errorBody(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(context, "PushBreakdowns\nResponse code ="+response.code(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("PushBreakdowns","onResponse" + response.errorBody()+"*code*"+response.code());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Globals.serverConnected = false;
                    Globals.dbHandler.UpdateSyncState_NewBreakdown(breakdown, 0);//Not Uploaded due to no network
                    Toast.makeText(context,"PushBreakdowns-Failure\n"+t, Toast.LENGTH_SHORT).show();
                    Log.e("PushBreakdowns","onResponse" + t);
                }
            });


        }
    }

    public static void SyncBreakdownCompletion(final Context context){
        List<JobCompletion> JobCompletionList = Globals.dbHandler.getBreakdownCompletion();
        if(JobCompletionList.size()<1)return;
        for (final JobCompletion obj: JobCompletionList)
        {
            SyncObject syncObject = new SyncObject();
            syncObject.BreakdownId=obj.JOB_NO;
            syncObject.StatusId=String.valueOf(Breakdown.JOB_COMPLETED);
            syncObject.StatusTime=obj.job_completed_datetime;
            syncObject.FailureTypeId=obj.type_failure;
            syncObject.FailureNatureId=obj.detail_reason_code;
            syncObject.FailureCauseId=obj.cause;
            syncObject.UserId= MainActivity.mToken.user_id;

            Globals.dbHandler.UpdateSyncState_JobCompletionObj(obj, -1);//Uploading Started
            SyncRESTService syncRESTService = new SyncRESTService();
            Call<SyncObject> call = syncRESTService.getService()
                    .UpdateBreakdownStatus( "Bearer "+ MainActivity.mToken.access_token, syncObject);

            call.enqueue(new Callback<SyncObject>(){

                @Override
                public void onResponse(Call<SyncObject> call, Response<SyncObject> response) {
                    if (response.isSuccessful()) {
                        Globals.serverConnected = true;
                        Log.e("SyncBreakdownCompletion","Successfull");
                        Globals.dbHandler.UpdateSyncState_JobCompletionObj(obj,1);//Successfully done
                    } else if (response.errorBody() != null) {
                        Globals.serverConnected = false;
                        Log.e("SyncBreakdownCompletion","Error"+response.errorBody());
                        Toast.makeText(context,"SyncBreakdownCompletion-Error\n"+response.errorBody(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(context, "SyncBreakdownCompletion\nResponse code ="+response.code(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("SyncBreakdownCompletion","onResponse" + response.errorBody()+"*code*"+response.code());
                    }
                }

                @Override
                public void onFailure(Call<SyncObject> call, Throwable t) {
                    Globals.serverConnected = false;
                    Globals.dbHandler.UpdateSyncState_JobCompletionObj(obj, 0);//Not Uploaded due to no network
                    Toast.makeText(context,"SyncBreakdownCompletion-Failure\n"+t, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private static void SyncTrackingData(final Context context){
        final List<TrackerObject> list = Globals.dbHandler.getNotSyncTrackingData();
        if(list.size()< 1)return;

        SyncRESTService syncRESTService = new SyncRESTService();
        Call<TrackerObject> call = syncRESTService.getService()
                .PushTrackingData( "Bearer "+ MainActivity.mToken.access_token, list);

        call.enqueue(new Callback<TrackerObject>(){

            @Override
            public void onResponse(Call<TrackerObject> call, Response<TrackerObject> response) {
                if (response.isSuccessful()) {
                    Globals.serverConnected = true;
                    Log.e("SyncTrackingData","Successfull");
                    for (final TrackerObject obj: list) {
                        Globals.dbHandler.UpdateTrackingData(obj);//Successfully done
                    }
                } else if (response.errorBody() != null) {
                    Globals.serverConnected = false;
                    Log.e("SyncTrackingData","Error"+response.errorBody());
                    Toast.makeText(context,"SyncTrackingData-Error\n"+response.errorBody(), Toast.LENGTH_SHORT).show();
                    //dbHandler.UpdateSyncState_JobCompletionObj(obj, 0);//Not Uploaded due to no network
                    if(response.code() == 401) { //Authentication fail
                        Toast.makeText(context, "Authentication fail..", Toast.LENGTH_SHORT).show();
                        MainActivity.ReLoginRequired=true;
                    }else{
                        Toast.makeText(context, "SyncTrackingData\nResponse code ="+response.code(), Toast.LENGTH_SHORT).show();
                    }
                    Log.e("SyncTrackingData","onResponse" + response.errorBody()+"*code*"+response.code());
                }
            }

            @Override
            public void onFailure(Call<TrackerObject> call, Throwable t) {
                Globals.serverConnected = false;
               // dbHandler.UpdateSyncState_JobCompletionObj(obj, 0);//Not Uploaded due to no network
                Toast.makeText(context,"SyncTrackingData-Failure\n"+t, Toast.LENGTH_SHORT).show();
            }
        });



        /*for (final TrackerObject obj: list)
        {
            SyncObject syncObject = new SyncObject();
            syncObject.BreakdownId=obj.JOB_NO;
            syncObject.StatusId=String.valueOf(Breakdown.JOB_COMPLETED);
            syncObject.StatusTime=obj.job_completed_datetime;
            syncObject.FailureTypeId=obj.type_failure;
            syncObject.FailureNatureId=obj.cause;
            syncObject.FailureCauseId=obj.detail_reason_code;
            syncObject.UserId= MainActivity.mToken.user_id;

            dbHandler.UpdateSyncState_JobCompletionObj(obj, -1);//Uploading Started

        }*/
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
                PushBreakdowns(getApplicationContext());
            }else if(i==1){
                i=2;
                SyncBreakdownCompletion(getApplicationContext());
            }else if(i==2){
                i=3;
                SyncTrackingData(getApplicationContext());
            }else if(i==3){
                i=0;
                SyncBreakdownStatusChange(getApplicationContext());
            }
        }

    }
}
