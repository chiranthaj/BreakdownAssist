package lk.steps.breakdownassist.Sync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import lk.steps.breakdownassist.MainActivity;
import lk.steps.breakdownassist.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Chirantha on 18/06/2017.
 */

public class BackgroundService extends Service {
    DBHandler dbHandler;
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
        syncRESTService = new SyncRESTService();
        Toast.makeText(this, "Sync Service Started", Toast.LENGTH_SHORT).show();

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

    private void SyncBreakdownStatusChange(){
        List<JobChangeStatus> JobChangeStatusList = dbHandler.getBreakdownStatusChange();
        for (final JobChangeStatus obj: JobChangeStatusList)
        {
            Log.e("StatusChange","*"+ obj.job_no+","+obj.change_datetime+", "+ obj.status);
            SyncObject syncObject = new SyncObject();

            if(obj.status.equals(Integer.toString(Breakdown.Status_JOB_VISITED))) syncObject.StatusId="1";
            else if(obj.status.equals(Integer.toString(Breakdown.Status_JOB_ATTENDING))) syncObject.StatusId="2";
            else if(obj.status.equals(Integer.toString(Breakdown.Status_JOB_DONE))) syncObject.StatusId="3";
            else if(obj.status.equals(Integer.toString(Breakdown.Status_JOB_COMPLETED))) syncObject.StatusId="4";
            else syncObject.StatusId="2";

            syncObject.BreakdownId=obj.job_no;
            syncObject.StatusTime=obj.change_datetime;
            syncObject.UserId = MainActivity.mToken.user_id;

            //TODO : if program crashes then this particular record may not be updated, hence use another task or change the
            //query to update state==0 OR (update_state==-1 AND currentTimestamp-update_timestamp>2min
            dbHandler.UpdateSyncState_JobStatusChangeObj(obj, -1);//Uploading Started

            SyncRESTService syncRESTService = new SyncRESTService();
            Call<SyncObject> call = syncRESTService.getService()
                    .UpdateBreakdownStatus( "Bearer "+ MainActivity.mToken.access_token, syncObject);

            call.enqueue(new Callback<SyncObject>(){
                @Override
                public void onResponse(Call<SyncObject> call, Response<SyncObject> response) {
                    if (response.isSuccessful()) {
                        Log.e("SyncStatusChange","successful");
                        dbHandler.UpdateSyncState_JobStatusChangeObj(obj,1);//Successfully done
                    } else if (response.errorBody() != null) {
                        Log.e("SyncStatusChange","onResponse"+response.errorBody());
                        dbHandler.UpdateSyncState_JobStatusChangeObj(obj, 0); //ToDo : Change this for each reason
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
                    }
                }

                @Override
                public void onFailure(Call<SyncObject> call, Throwable t) {
                    Log.e("SyncStatusChange","onFailure "+t);
                    dbHandler.UpdateSyncState_JobStatusChangeObj(obj, 0);//Not Uploaded due to no network
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
            syncObject.UserId= MainActivity.mToken.user_id;

            dbHandler.UpdateSyncState_JobCompletionObj(obj, -1);//Uploading Started
            SyncRESTService syncRESTService = new SyncRESTService();
            Call<SyncObject> call = syncRESTService.getService()
                    .UpdateBreakdownStatus( "Bearer "+ MainActivity.mToken.access_token, syncObject);

            call.enqueue(new Callback<SyncObject>(){

                @Override
                public void onResponse(Call<SyncObject> call, Response<SyncObject> response) {
                    if (response.isSuccessful()) {
                        Log.e("SyncStatusChange","Successfull");
                        dbHandler.UpdateSyncState_JobCompletionObj(obj,1);//Successfully done
                    } else if (response.errorBody() != null) {
                        Log.e("SyncStatusChange","Error"+response.errorBody());
                        dbHandler.UpdateSyncState_JobCompletionObj(obj, 0);//Not Uploaded due to no network
                        /*if (error.getKind() != RetrofitError.Kind.NETWORK | error.getResponse() != null) {
                            if (error.getResponse().getStatus()==409){
                                dbHandler.UpdateSyncState_JobCompletionObj(obj, 1); //Already record is there, may be due to timeout
                            }else {
                                dbHandler.UpdateSyncState_JobCompletionObj(obj, -5); //To avoid retry again and again
                            }
                        }else {
                            dbHandler.UpdateSyncState_JobCompletionObj(obj, 0);//Not Uploaded due to no network
                        }*/
                    }
                }

                @Override
                public void onFailure(Call<SyncObject> call, Throwable t) {
                    dbHandler.UpdateSyncState_JobCompletionObj(obj, 0);//Not Uploaded due to no network
                }
            });
        }
    }


    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            SyncBreakdownStatusChange();
            SyncBreakdownCompletion();
        }
    }
}
