package lk.steps.breakdownassist.Sync;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lk.steps.breakdownassist.Breakdown;
import lk.steps.breakdownassist.DBHandler;
import lk.steps.breakdownassist.JobChangeStatus;
import lk.steps.breakdownassist.JobCompletion;
import lk.steps.breakdownassist.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Chirantha on 18/06/2017.
 */

public class BackgroundService extends Service {
    DBHandler dbHandler;
    JobStatusChangesRESTService myJobStatusChangesRESTService;
    JobCompletionRESTService myJobCompletionRESTService;
    SyncRESTService syncRESTService;
    Timer timer;
    MyTimerTask myTimerTask;
    boolean bTimerRuning=false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        dbHandler = new DBHandler(this,null,null,1);
        myJobStatusChangesRESTService = new JobStatusChangesRESTService();
        myJobCompletionRESTService = new JobCompletionRESTService();
        syncRESTService = new SyncRESTService();
        Toast.makeText(this, "Sync Service Started", Toast.LENGTH_SHORT).show();

        timer = new Timer();
        myTimerTask = new MyTimerTask();

        //delay 1000ms, repeat in 5000ms
        timer.schedule(myTimerTask, 1000, 10000);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }

    private void Sync_JobStatusChangeObj_to_Server(){
        //Always make sure to upload all the JobChangeStatus records prior to uploading JobCompletion records to avoid abnormalities in informix DB
        List<JobChangeStatus> JobChangeStatusList = dbHandler.getJobStatusChangeObjNotSync_List();
        for (final JobChangeStatus obj: JobChangeStatusList)
        {
            //TODO : if program crashes then this particular record may not be updated, hence use another task or change the
            //query to update state==0 OR (update_state==-1 AND currentTimestamp-update_timestamp>2min
            dbHandler.UpdateSyncState_JobStatusChangeObj(obj, -1);//Uploading Started
            myJobStatusChangesRESTService.getService().addJobStatusRec(obj, new Callback<JobChangeStatus>() {
                @Override
                public void success(JobChangeStatus job, Response response) {
                    dbHandler.UpdateSyncState_JobStatusChangeObj(obj,1);//Successfully done
                }

                @Override
                public void failure(RetrofitError error) {
                    //Toast.makeText(getApplicationContext()," RetrofitError " + error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    if (!error.isNetworkError()) {
                        if (error.getResponse().getStatus()==409){
                            dbHandler.UpdateSyncState_JobStatusChangeObj(obj, 1); //Already record is there, may be due to timeout
                        }else {
                            dbHandler.UpdateSyncState_JobStatusChangeObj(obj, -5); //To avoid retry again and again
                        }
                    }else if(error.isNetworkError()){
                        dbHandler.UpdateSyncState_JobStatusChangeObj(obj, 0);//Not Uploaded due to no network
                    }
                }
            });
        }

        List<JobCompletion> JobCompletionList = dbHandler.getJobCompletionObjNotSync_List();
        for (final JobCompletion obj: JobCompletionList)
        {
            dbHandler.UpdateSyncState_JobCompletionObj(obj, -1);//Uploading Started
            myJobCompletionRESTService.getService().addJob_CompletionRec(obj, new Callback<JobCompletion>() {

                @Override
                public void success(JobCompletion jobCompletion, Response response) {
                    dbHandler.UpdateSyncState_JobCompletionObj(obj,1);//Successfully done
                }

                @Override
                public void failure(RetrofitError error) {
                    //Toast.makeText(getApplicationContext(),"RetrofitError " + error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    if (!error.isNetworkError()) {
                        if (error.getResponse().getStatus()==409){
                            dbHandler.UpdateSyncState_JobCompletionObj(obj, 1); //Already record is there, may be due to timeout
                        }else {
                            dbHandler.UpdateSyncState_JobCompletionObj(obj, -5); //To avoid retry again and again
                        }
                    }else if(error.isNetworkError()){
                        dbHandler.UpdateSyncState_JobCompletionObj(obj, 0);//Not Uploaded due to no network
                    }
                }
            });
        }
    }

    public void GetNewBreakdownsFromServer(){
        syncRESTService.getService().getNewBreakdowns("111","77","S","3", new Callback<List<Breakdown>>() {
            @Override
            public void success(List<Breakdown> breakdowns, Response response) {
                Log.d("API-","OK");
                Log.d("breakdowns-",""+breakdowns.size());
                DBHandler dbHandler = new DBHandler(getApplicationContext(), null, null, 1);
                boolean playTone = false;
                for (Breakdown breakdown :breakdowns) {
                    dbHandler.addBreakdown2(
                            breakdown.get_Received_Time(),
                            breakdown.get_Acct_Num(),
                            breakdown.get_Full_Description(),
                            breakdown.get_Job_No(),
                            breakdown.get_Contact_No(),
                            breakdown.get_ADDRESS(),
                            1);
                    playTone = true;
                }
                String sIssuedBreakdownID=dbHandler.getLastBreakdownID();
                dbHandler.close();

                //Informing the Map view about the new bd, then it can add it
                Intent myintent=new Intent();
                myintent.setAction("lk.steps.breakdownassist.NewBreakdownBroadcast");
                myintent.putExtra("_id",sIssuedBreakdownID);
                getApplicationContext().sendBroadcast(myintent);
                if(playTone){
                    MediaPlayer mPlayer2;
                    mPlayer2= MediaPlayer.create(getApplicationContext(), R.raw.fb_sound);
                    mPlayer2.start();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("API-","ERROR-"+error.getMessage());
            }

        });
    }

    private void SyncBreakdownStatusChange(){
        List<JobChangeStatus> JobChangeStatusList = dbHandler.getBreakdownStatusChange();
        for (final JobChangeStatus obj: JobChangeStatusList)
        {
            Log.e("StatusChange","*"+ obj.job_no+","+obj.change_datetime+", "+ obj.status);
            SyncObject syncObject = new SyncObject();

            Log.e("1724800007","*"+ obj.status+","+obj.change_datetime);
            if(obj.status.equals(Integer.toString(Breakdown.Status_JOB_VISITED))) syncObject.StatusId="1";
            else if(obj.status.equals(Integer.toString(Breakdown.Status_JOB_ATTENDING))) syncObject.StatusId="2";
            else if(obj.status.equals(Integer.toString(Breakdown.Status_JOB_DONE))) syncObject.StatusId="3";
            else if(obj.status.equals(Integer.toString(Breakdown.Status_JOB_COMPLETED))) syncObject.StatusId="4";
            else syncObject.StatusId="2";

            syncObject.BreakdownId=obj.job_no;
            syncObject.StatusTime=obj.change_datetime;
            syncObject.UserId="111";

            //TODO : if program crashes then this particular record may not be updated, hence use another task or change the
            //query to update state==0 OR (update_state==-1 AND currentTimestamp-update_timestamp>2min
            dbHandler.UpdateSyncState_JobStatusChangeObj(obj, -1);//Uploading Started
            syncRESTService.getService().UpdateBreakdownStatus(syncObject, new Callback<SyncObject>() {
                @Override
                public void success(SyncObject job, Response response) {
                    Log.e("StatusChange","0");
                    dbHandler.UpdateSyncState_JobStatusChangeObj(obj,1);//Successfully done
                }

                @Override
                public void failure(RetrofitError error) {
                    //Toast.makeText(getApplicationContext()," RetrofitError " + error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    if (!error.isNetworkError()) {
                        if (error.getResponse().getStatus()==409){
                            Log.e("StatusChange","1");
                            dbHandler.UpdateSyncState_JobStatusChangeObj(obj, 1); //Already record is there, may be due to timeout
                        }else {
                            Log.e("StatusChange","2");
                            dbHandler.UpdateSyncState_JobStatusChangeObj(obj, -5); //To avoid retry again and again
                        }
                    }else if(error.isNetworkError()){
                        Log.e("StatusChange","3");
                        dbHandler.UpdateSyncState_JobStatusChangeObj(obj, 0);//Not Uploaded due to no network
                    }
                }
            });
        }
    }

    private void SyncBreakdownCompletion(){
        List<JobCompletion> JobCompletionList = dbHandler.getBreakdownCompletion();
        for (final JobCompletion obj: JobCompletionList)
        {
            SyncObject syncObject = new SyncObject();
            syncObject.BreakdownId=obj.job_no;
            syncObject.StatusId="4";
            syncObject.StatusTime=obj.job_completed_datetime;
            syncObject.FailureTypeId=obj.type_failure;
            syncObject.FailureNatureId=obj.cause;
            syncObject.FailureCauseId=obj.detail_reason_code;
            syncObject.UserId="111";

            dbHandler.UpdateSyncState_JobCompletionObj(obj, -1);//Uploading Started
            syncRESTService.getService().UpdateBreakdownStatus(syncObject, new Callback<SyncObject>() {

                @Override
                public void success(SyncObject jobCompletion, Response response) {
                    dbHandler.UpdateSyncState_JobCompletionObj(obj,1);//Successfully done
                }

                @Override
                public void failure(RetrofitError error) {
                    //Toast.makeText(getApplicationContext(),"RetrofitError " + error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    if (!error.isNetworkError()) {
                        if (error.getResponse().getStatus()==409){
                            dbHandler.UpdateSyncState_JobCompletionObj(obj, 1); //Already record is there, may be due to timeout
                        }else {
                            dbHandler.UpdateSyncState_JobCompletionObj(obj, -5); //To avoid retry again and again
                        }
                    }else if(error.isNetworkError()){
                        dbHandler.UpdateSyncState_JobCompletionObj(obj, 0);//Not Uploaded due to no network
                    }
                }
            });
        }
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            /*if (!bTimerRuning){
                bTimerRuning=true;*/
            //Sync_JobStatusChangeObj_to_Server();
            SyncBreakdownStatusChange();
            SyncBreakdownCompletion();
              /*  bTimerRuning=false;
            }*/
        }
    }



}
